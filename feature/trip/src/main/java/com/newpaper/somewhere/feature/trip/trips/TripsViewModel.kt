package com.newpaper.somewhere.feature.trip.trips

import android.util.Log
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.core.data.repository.trip.TripsRepository
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.getTripId
import com.newpaper.somewhere.feature.trip.CommonTripUiStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject

private const val TRIPS_VIEWMODEL_TAG = "Trips-ViewModel"

data class Glance(
    val visible: Boolean = false,
    val trip: Trip? = null,
    val date: Date? = null,
    val spot: Spot? = null
)

data class TripsUiState(
    val glance: Glance = Glance(),

    val loadingTrips: Boolean = true,

    val isShowingDialog: Boolean = false,
    val showTripCreationOptionsDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val selectedTrip: Trip? = null, //for delete trip

    val deletedTripIds: List<Int> = listOf(),
    val deletedSharedTrips: List<Trip> = listOf(),
)


@HiltViewModel
class TripsViewModel @Inject constructor(
    private val commonTripUiStateRepository: CommonTripUiStateRepository,
    private val tripsRepository: TripsRepository,
    private val commonImageRepository: CommonImageRepository,
): ViewModel()  {
    private val _tripsUiState: MutableStateFlow<TripsUiState> =
        MutableStateFlow(
            TripsUiState()
        )

    val tripsUiState = _tripsUiState.asStateFlow()

    private val commonTripUiState = commonTripUiStateRepository.commonTripUiState

    init {
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(trip = null, tempTrip = null)
            )
        }
    }


    //==============================================================================================
    //set uiState value ============================================================================
    fun setLoadingTrips(
        isLoadingTrips: Boolean
    ){
        _tripsUiState.update {
            it.copy(loadingTrips = isLoadingTrips)
        }
    }

    private fun setIsShowingDialog(){
        val isShowingDialog =
            _tripsUiState.value.showTripCreationOptionsDialog ||
            _tripsUiState.value.showExitDialog ||
            _tripsUiState.value.showDeleteDialog

        _tripsUiState.update {
            it.copy(isShowingDialog = isShowingDialog)
        }
    }

    fun setShowTripCreationOptionsDialog(showTripCreationOptionsDialog: Boolean){
        _tripsUiState.update {
            it.copy(showTripCreationOptionsDialog = showTripCreationOptionsDialog)
        }
        setIsShowingDialog()
    }

    fun setShowExitDialog(showExitDialog: Boolean){
        _tripsUiState.update {
            it.copy(showExitDialog = showExitDialog)
        }
        setIsShowingDialog()
    }

    fun setShowDeleteDialog(showDeleteDialog: Boolean){
        _tripsUiState.update {
            it.copy(showDeleteDialog = showDeleteDialog)
        }
        setIsShowingDialog()
    }

    fun setSelectedTrip(selectedTrip: Trip?){
        _tripsUiState.update {
            it.copy(selectedTrip = selectedTrip)
        }
    }

    fun initGlanceInfo(

    ){
        _tripsUiState.update {
            it.copy(
                glance = Glance(
                    visible = false,
                    trip = null,
                    date = null,
                    spot = null
                )
            )
        }
    }




    //==============================================================================================
    // ============================================================================

    /** update trips from remote db*/
    suspend fun updateTrips(
        internetEnabled: Boolean,
        appUserId: String
    ){
        val newTripList = tripsRepository.getMyTrips(internetEnabled, appUserId)
        val newSharedTripList = tripsRepository.getSharedTrips(internetEnabled, appUserId)

        //set orderId of shared trip
        newSharedTripList.forEachIndexed { index, sharedTrip ->
            sharedTrip.orderId = index
        }

        //update newTripList's dateList from tripUiState.value.tripList's dateList
        //because newTripList's dateList is empty list (get trip data except dateList)

        // -> at app open and user enter TripScreen, user see empty dateList,
        //    after load dateList, dateList will be show to user and dateList will save in viewModel (even user go back to MyTripsScreen)
        //    when user enter TripScreen(again), old dateList will show (not empty list)
        //    after load dateList, user can see new dateList

        // all trip data(not really all, user have to go to TripScreen) will be saved before execute app
        for (trip in newTripList) {
            val matchingOldTrip = commonTripUiState.value.tripInfo.trips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }

        //sharedTrip
        for (trip in newSharedTripList) {
            val matchingOldTrip = commonTripUiState.value.tripInfo.sharedTrips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }


        //delete unused image files
        commonImageRepository.deleteUnusedImageFilesForAllTrips(
            allTrips = newTripList + newSharedTripList
        )

        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    trips = newTripList, tempTrips = newTripList,
                    sharedTrips = newSharedTripList, tempSharedTrips = newSharedTripList

                )
            )
        }
    }

    /** update glance spot info */
    fun findCurrentDateTripAndUpdateGlanceTrip(

    ): Trip? {
        Log.d("aaaa", "aaaa")

        initGlanceInfo()

        //get current date time
        val currentDateTime = LocalDateTime.now()
        val currentDate = currentDateTime.toLocalDate()

        val tripList: List<Trip> = (commonTripUiState.value.tripInfo.trips ?: listOf()) +
                (commonTripUiState.value.tripInfo.sharedTrips ?: listOf())

        //find trip that include current date
        for (trip in tripList) {
            //if trip's date is include in current date]
            if (trip.startDate == null || trip.endDate == null)
                continue

            val tripStartDate = LocalDate.parse(trip.startDate)
            val tripEndDate = LocalDate.parse(trip.endDate)

            if (currentDate in tripStartDate..tripEndDate) {
                _tripsUiState.update {
                    it.copy(
                        glance = it.glance.copy(
                            trip = trip
                        )
                    )
                }
                return trip
            }
        }
        return null
    }

    fun updateGlanceSpotInfo(
        glanceTrip: Trip?
    ){
        //get current date time
        val currentDateTime = LocalDateTime.now()
        val currentDate = currentDateTime.toLocalDate()
        val currentTime = currentDateTime.toLocalTime()

        if (_tripsUiState.value.glance.trip != null) {
            if (glanceTrip != null) {
                _tripsUiState.update {
                    it.copy(
                        glance = it.glance.copy(
                            trip = glanceTrip
                        )
                    )
                }

                //find date
                for (date in glanceTrip.dateList){
                    if (date.date == currentDate){
                        _tripsUiState.update {
                            it.copy(
                                glance = it.glance.copy(
                                    date = date
                                )
                            )
                        }
                        break
                    }
                }
            }
        }

        if (_tripsUiState.value.glance.date != null) {
            val spotList = _tripsUiState.value.glance.date!!.spotList

            if (spotList.isEmpty()){
                _tripsUiState.update {
                    it.copy(
                        glance = it.glance.copy(
                            visible = false,
                            spot = null
                        )
                    )
                }
            }

            //find spot
            for (spot in _tripsUiState.value.glance.date!!.spotList) {
                var glanceSpot: Spot? = null

                val startTime = spot.startTime
                val endTime = spot.endTime

                val isFirstSpot = spot == _tripsUiState.value.glance.date!!.spotList.first()
                val isLastSpot = spot == _tripsUiState.value.glance.date!!.spotList.last()

                val nextSpot = _tripsUiState.value.glance.date!!.spotList.getOrNull(spot.index + 1)
                val nextSpotStartTime = nextSpot?.startTime

                // current < start(first spot)
                if (isFirstSpot && startTime != null && currentTime < startTime) {
                    glanceSpot = spot
                }

                // start < current < end  (time)
                else if (startTime != null && endTime != null && currentTime in startTime..endTime) {
                    glanceSpot = spot
                }

                // end < current < start(next spot)
                if (
                    endTime != null && nextSpotStartTime != null &&
                    currentTime in endTime .. nextSpotStartTime
                ){
                    glanceSpot = nextSpot
                }

                // start < current < null && last spot of the day
                else if (
                    startTime != null && endTime == null && startTime <= currentTime &&
                    isLastSpot
                ){
                    glanceSpot = spot
                }

                // start < current < null (not last spot of day)
                else if (
                    startTime != null && endTime == null && startTime <= currentTime &&
                    !isLastSpot &&
                    nextSpotStartTime != null && currentTime < nextSpotStartTime
                ){
                    glanceSpot = spot
                }

                // null < current < end
                else if (
                    startTime == null && endTime != null && currentTime < endTime
                ){
                    glanceSpot = spot
                }

                // null < current < null && last spot of the day
                else if (
                    startTime == null && endTime == null &&
                    isLastSpot
                ){
                    glanceSpot = spot
                }

                // null < current < null (not last spot of day)
                else if (
                    startTime == null && endTime == null &&
                    !isLastSpot &&
                    nextSpotStartTime != null && currentTime < nextSpotStartTime
                ){
                    glanceSpot = spot
                }



                //update glance spot
                if (glanceSpot != null) {
                    _tripsUiState.update {
                        it.copy(
                            glance = it.glance.copy(
                                visible = true,
                                spot = glanceSpot
                            )
                        )
                    }
                    break
                }


                if (spot == _tripsUiState.value.glance.date!!.spotList.last()){
                    _tripsUiState.update {
                        it.copy(
                            glance = it.glance.copy(
                                visible = false
                            )
                        )
                    }
                }
            }
        }
    }

    fun addAndGetNewTrip(
        appUserId: String
    ): Trip{
        val nowTime = ZonedDateTime.now(ZoneOffset.UTC)
        val newTrip = getNewTrip(
            appUserId = appUserId,
            nowTime = nowTime
        )

        //update tripUiState tempTripList
        val tempTripList = (commonTripUiState.value.tripInfo.tempTrips ?: listOf<Trip>()) .toMutableList()
        tempTripList.add(newTrip)

        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    tempTrips = tempTripList
                )
            )
        }

        return newTrip
    }

    private fun getNewTrip(
        appUserId: String,
        nowTime: ZonedDateTime
    ): Trip{
        //get last orderId
        var newOrderId = 0
        val lastTrip = commonTripUiState.value.tripInfo.trips?.lastOrNull()
        if (lastTrip != null) { newOrderId = lastTrip.orderId + 1 }

        val newId = getTripId(
            managerId = appUserId,
            firstCreatedTripTime = nowTime
        )

        //new trip
        return Trip(
            id = newId,
            orderId = newOrderId,
            managerId = appUserId,
            editable = true,
            firstCreatedTime = nowTime,
            lastModifiedTime = nowTime
        )
    }

    /** delete given [trip]*/
    fun deleteTrip(
        trip: Trip,
        appUserId: String
    ){
        if (trip.managerId == appUserId)
            deleteTripFromTempTrip(trip)
        else
            deleteTripFromTempSharedTrip(trip)
    }

    /** delete given [trip] from tempTripList */
    private fun deleteTripFromTempTrip(trip: Trip){
        commonTripUiState.value.tripInfo.tempTrips
        if (commonTripUiState.value.tripInfo.tempTrips != null) {
            val newTempTripList = commonTripUiState.value.tripInfo.tempTrips!!.toMutableList()
            newTempTripList.remove(trip)

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempTrips = newTempTripList.toList()
                    )
                )
            }

            val newDeletedTripIdList = tripsUiState.value.deletedTripIds + listOf(trip.id)

            _tripsUiState.update {
                it.copy(
                    deletedTripIds = newDeletedTripIdList
                )
            }
        }
    }

    /** delete given [trip] from tempSharedTripList */
    fun deleteTripFromTempSharedTrip(trip: Trip){
        if (commonTripUiState.value.tripInfo.tempSharedTrips != null) {
            val newTempSharedTripList = commonTripUiState.value.tripInfo.tempSharedTrips!!.toMutableList()
            newTempSharedTripList.removeIf {
                it.id == trip.id && it.managerId == trip.managerId
            }

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempSharedTrips = newTempSharedTripList.toList()
                    )
                )
            }

            val newDeletedSharedTripList = tripsUiState.value.deletedSharedTrips + listOf(trip)

            _tripsUiState.update {
                it.copy(
                    deletedSharedTrips = newDeletedSharedTripList
                )
            }
        }
    }

    fun updateTripListAndShardTripList(){
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    trips = it.tripInfo.tempTrips,
                    sharedTrips = it.tripInfo.tempSharedTrips
                )
            )
        }
    }

    fun initDeletedTripsIdListAndDeletedSharedTripList(){
        _tripsUiState.update {
            it.copy(
                deletedTripIds = listOf(),
                deletedSharedTrips = listOf()
            )
        }
    }


    /** when click save button,
     * save my trips and shared trips to remote db and uiState */
    suspend fun saveTrips(
        appUserId: String
    ){
        //update commonTripUiState tripList & sharedTripList
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                isEditMode = false,
                tripInfo = it.tripInfo.copy(
                    trips = it.tripInfo.tempTrips,
                    sharedTrips = it.tripInfo.tempSharedTrips
                )
            )
        }

        //save myTrips to firestore
        tripsRepository.saveTrips(
            appUserId = appUserId,
            myTrips = commonTripUiState.value.tripInfo.tempTrips ?: listOf(),
            deletedTripsIds = tripsUiState.value.deletedTripIds,
            deletedSharedTrips = tripsUiState.value.deletedSharedTrips
        )

        //update sharedTrips order to firestore
        tripsRepository.updateSharedTripsOrder(
            appUserId = appUserId,
            sharedTripList = commonTripUiState.value.tripInfo.tempSharedTrips ?: listOf()
        )

        //init deletedTripsIdList
        _tripsUiState.update {
            it.copy(
                deletedTripIds = listOf(),
                deletedSharedTrips = listOf()
            )
        }
    }


    fun reorderTempTrips(
        isSharedTripList: Boolean,
        currentIndex: Int,
        destinationIndex: Int
    ){
        commonTripUiState.value.tripInfo.tempTrips
        //only at edit mode
        if (!isSharedTripList && commonTripUiState.value.tripInfo.tempTrips != null) {
            val newTrips = commonTripUiState.value.tripInfo.tempTrips!!.toMutableList()
            val trip = newTrips[currentIndex]
            newTrips.removeAt(currentIndex)
            newTrips.add(destinationIndex, trip)

            newTrips.forEach {
                it.orderId = newTrips.indexOf(it)
            }

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempTrips = newTrips.toList()
                    )
                )
            }
        }
        else if (isSharedTripList && commonTripUiState.value.tripInfo.tempSharedTrips != null){
            val newTrips = commonTripUiState.value.tripInfo.tempSharedTrips!!.toMutableList()
            val trip = newTrips[currentIndex]
            newTrips.removeAt(currentIndex)
            newTrips.add(destinationIndex, trip)

            newTrips.forEach {
                it.orderId = newTrips.indexOf(it)
            }

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempSharedTrips = newTrips.toList()
                    )
                )
            }
        }
    }
}

