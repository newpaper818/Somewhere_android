package com.newpaper.somewhere.feature.trip.trips

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.core.data.repository.trip.TripsRepository
import com.newpaper.somewhere.core.model.enums.TripsDisplayMode
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.getTripId
import com.newpaper.somewhere.feature.trip.CommonTripUiStateRepository
import com.newpaper.somewhere.feature.trip.classifyAndConvertToTripsGroup
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


data class GlanceSpot(
    val trip: Trip,
    val date: Date,
    val spot: Spot
)

data class GlanceSpots(
    val visible: Boolean = false,
    val spots: List<GlanceSpot> = listOf()
)

data class TripsUiState(
    val glanceSpots: GlanceSpots = GlanceSpots(),

    val loadingTrips: Boolean = true,

    val tripsDisplayMode: TripsDisplayMode = TripsDisplayMode.ACTIVE,
    val isTripsSortOrderByLatest: Boolean = true,

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
): ViewModel() {
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

    fun setTripsDisplayMode(
        tripsDisplayMode: TripsDisplayMode
    ){
        _tripsUiState.update {
            it.copy(tripsDisplayMode = tripsDisplayMode)
        }
    }

    fun setIsTripsSortOrderByLatest(
        isTripsSortOrderByLatest: Boolean
    ){
        _tripsUiState.update {
            it.copy(isTripsSortOrderByLatest = isTripsSortOrderByLatest)
        }

        //sort order
        val tripInfo = commonTripUiStateRepository._commonTripUiState.value.tripInfo
        val sortedMyTripsGroup = tripInfo.myTripsGroup?.sortOrder(isTripsSortOrderByLatest)
        val sortedTempMyTripsGroup = tripInfo.tempMyTripsGroup?.sortOrder(isTripsSortOrderByLatest)
        val sortedSharedTripsGroup = tripInfo.sharedTripsGroup?.sortOrder(isTripsSortOrderByLatest)
        val sortedTempSharedTripsGroup = tripInfo.tempSharedTripsGroup?.sortOrder(isTripsSortOrderByLatest)

        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    myTripsGroup = sortedMyTripsGroup, tempMyTripsGroup = sortedTempMyTripsGroup,
                    sharedTripsGroup = sortedSharedTripsGroup, tempSharedTripsGroup = sortedTempSharedTripsGroup

                )
            )
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
                glanceSpots = GlanceSpots(
                    visible = false,
                    spots = listOf()
                )
            )
        }
    }




    //==============================================================================================
    // ============================================================================

    /** update trips from remote db*/
    suspend fun updateTrips(
        internetEnabled: Boolean,
        appUserId: String,
        orderByLatest: Boolean
    ){
        val newTripList = tripsRepository.getMyTrips(internetEnabled, appUserId, orderByLatest)
        val newSharedTripList = tripsRepository.getSharedTrips(internetEnabled, appUserId, orderByLatest)

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
            val matchingOldTrip = commonTripUiState.value.tripInfo.myTripsGroup?.all?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }

        //sharedTrip
        for (trip in newSharedTripList) {
            val matchingOldTrip = commonTripUiState.value.tripInfo.sharedTripsGroup?.all?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }


        //delete unused image files
        commonImageRepository.deleteUnusedImageFilesForAllTrips(
            allTrips = newTripList + newSharedTripList
        )

        val myTripsGroup = classifyAndConvertToTripsGroup(newTripList)
        val sharedTripsGroup = classifyAndConvertToTripsGroup(newSharedTripList)

        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    myTripsGroup = myTripsGroup, tempMyTripsGroup = myTripsGroup,
                    sharedTripsGroup = sharedTripsGroup, tempSharedTripsGroup = sharedTripsGroup

                )
            )
        }
    }


    /** update glance spot info */
    fun findCurrentDateTripAndUpdateGlanceTrip(

    ): Trip? {
        initGlanceInfo()

        //get current date time
        val currentDateTime = LocalDateTime.now()
        val currentDate = currentDateTime.toLocalDate()

        val tripList: List<Trip> = (commonTripUiState.value.tripInfo.myTripsGroup?.all ?: listOf()) +
                (commonTripUiState.value.tripInfo.sharedTripsGroup?.all ?: listOf())

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
                        glanceSpots = it.glanceSpots.copy(
                            spots = listOf(GlanceSpot(trip, Date(date = LocalDate.now()), Spot(date = LocalDate.now())))
                        )
                    )
                }
                return trip
            }
        }
        return null
    }

    /**
     * Find the current and upcoming Spot, as well as the next Spot,
     * compared to the current time, and updates the state
     *
     * @param targetTrip
     */
    fun updateGlanceSpotInfo(
        targetTrip: Trip
    ){
        val currentDateTime = LocalDateTime.now()
        val currentDate = currentDateTime.toLocalDate()
        val currentTime = currentDateTime.toLocalTime()

        // 2. find today date's Date
        val targetDate = targetTrip.dateList.find { it.date == currentDate }

        // not found today date' Date or Spot is empty
        if (targetDate == null || targetDate.spotList.isEmpty()) {
            updateGlanceState(visible = false, spots = emptyList())
            return
        }

        // 3. find Spot that include current time
        val targetSpotIndex = findTargetSpotIndex(targetDate.spotList, currentTime)

        // 4. Based on the index, construct a list containing the current Spot and (if present) the next Spot.
        if (targetSpotIndex != -1) {
            val resultSpots = mutableListOf<GlanceSpot>()

            // current/upcoming Spots
            val currentSpot = targetDate.spotList[targetSpotIndex]
            resultSpots.add(GlanceSpot(targetTrip, targetDate, currentSpot))

            // next Spot (if existed)
            val nextSpot = targetDate.spotList.getOrNull(targetSpotIndex + 1)
            if (nextSpot != null) {
                resultSpots.add(GlanceSpot(targetTrip, targetDate, nextSpot))
            }

            // update state
            updateGlanceState(visible = true, spots = resultSpots)
        } else {
            // not found Spot
            updateGlanceState(visible = false, spots = emptyList())
        }
    }

    /**
     * find target spot(which in [currentTime]) index in [spotList]
     *
     * @param spotList
     * @param currentTime
     * @return found spot's index / if not exist -1
     */
    private fun findTargetSpotIndex(
        spotList: List<Spot>,
        currentTime: java.time.LocalTime
    ): Int {
        for (index in spotList.indices) {
            val spot = spotList[index]
            val startTime = spot.startTime
            val endTime = spot.endTime

            val isFirstSpot = (index == 0)
            val isLastSpot = (index == spotList.lastIndex)

            val nextSpotStartTime = spotList.getOrNull(index + 1)?.startTime

            // current time < Spot start (first spot) -> Spot1 index
            // 1. Before the first Spot begins (not yet started) -> Display the first Spot
            if (isFirstSpot && startTime != null && currentTime < startTime) {
                return index
            }

            // Spot1 start < current time < Spot1 end -> Spot1 index
            // 2. Spot in progress (between start and end)
            if (startTime != null && endTime != null && currentTime in startTime..endTime) {
                return index
            }

            // Spot1 end < current time < Spot2 start -> Spot2 index
            // 3. After the current Spot ends - before the next Spot begins (break time) -> Preview the next Spot
            if (endTime != null && nextSpotStartTime != null && currentTime in endTime..nextSpotStartTime) {
                return index + 1
            }

            // 4. Handling cases with no time information (null)

            // 4-1. Only a start time exists, no end time (current time is after the start time)
            // If it's the last Spot, keep the current Spot; otherwise, keep it only until the next Spot starts
            if (startTime != null && endTime == null && startTime <= currentTime) {

                // Spot1(last spot) start < current time -> Spot1 index
                if (isLastSpot) return index

                // Spot1 start < current time < Spot2 start -> Spot1 index
                if (nextSpotStartTime != null && currentTime < nextSpotStartTime) return index
            }

            // current time < Spot1 end -> Spot1 index
            // 4-2. No start time, only an end time (before the end time)
            if (startTime == null && endTime != null && currentTime < endTime) {
                return index
            }

            // 4-3 No start/end time
            // If it's the last Spot, keep it; otherwise, keep it only until the next Spot starts
            if (startTime == null && endTime == null) {
                // Spot1(last spot) -> Spot1 index
                if (isLastSpot) return index

                // current time < Spot2 start -> Spot1 index
                if (nextSpotStartTime != null && currentTime < nextSpotStartTime) return index
            }
        }

        return -1
    }

    private fun updateGlanceState(
        visible: Boolean,
        spots: List<GlanceSpot>
    ) {
        _tripsUiState.update {
            it.copy(
                glanceSpots = GlanceSpots(
                    visible = visible,
                    spots = spots
                )
            )
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
        val tempTripList = (commonTripUiState.value.tripInfo.tempMyTripsGroup?.all ?: listOf<Trip>()).toMutableList()
        tempTripList.add(0, newTrip)

        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    tempMyTripsGroup = classifyAndConvertToTripsGroup(tempTripList)
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
        val lastTrip = commonTripUiState.value.tripInfo.myTripsGroup?.all?.firstOrNull()
        if (lastTrip != null) { newOrderId = lastTrip.orderId - 1 }

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
        if (commonTripUiState.value.tripInfo.tempMyTripsGroup?.all != null) {
            val newTempTripList = commonTripUiState.value.tripInfo.tempMyTripsGroup!!.all.toMutableList()
            newTempTripList.remove(trip)

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempMyTripsGroup = classifyAndConvertToTripsGroup(newTempTripList.toList())
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
        if (commonTripUiState.value.tripInfo.tempSharedTripsGroup?.all != null) {
            val newTempSharedTripList = commonTripUiState.value.tripInfo.tempSharedTripsGroup!!.all.toMutableList()
            newTempSharedTripList.removeIf {
                it.id == trip.id && it.managerId == trip.managerId
            }

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempSharedTripsGroup = classifyAndConvertToTripsGroup(newTempSharedTripList.toList())
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
                    myTripsGroup = it.tripInfo.tempMyTripsGroup,
                    sharedTripsGroup = it.tripInfo.tempSharedTripsGroup
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
                    myTripsGroup = it.tripInfo.tempMyTripsGroup,
                    sharedTripsGroup = it.tripInfo.tempSharedTripsGroup
                )
            )
        }

        //save myTrips to firestore
        tripsRepository.saveTrips(
            appUserId = appUserId,
            myTrips = commonTripUiState.value.tripInfo.tempMyTripsGroup?.all ?: listOf(),
            deletedTripsIds = tripsUiState.value.deletedTripIds,
            deletedSharedTrips = tripsUiState.value.deletedSharedTrips
        )

        //update sharedTrips order to firestore
        tripsRepository.updateSharedTripsOrder(
            appUserId = appUserId,
            sharedTripList = commonTripUiState.value.tripInfo.tempSharedTripsGroup?.all ?: listOf()
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
        //only at edit mode
        if (!isSharedTripList && commonTripUiState.value.tripInfo.tempMyTripsGroup?.all != null) {
            val newTrips = commonTripUiState.value.tripInfo.tempMyTripsGroup!!.all.toMutableList()
            val trip = newTrips[currentIndex]
            newTrips.removeAt(currentIndex)
            newTrips.add(destinationIndex, trip)

            newTrips.forEach {
                it.orderId = newTrips.indexOf(it)
            }

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempMyTripsGroup = classifyAndConvertToTripsGroup(newTrips.toList())
                    )
                )
            }
        }
        else if (isSharedTripList && commonTripUiState.value.tripInfo.tempSharedTripsGroup?.all != null){
            val newTrips = commonTripUiState.value.tripInfo.tempSharedTripsGroup!!.all.toMutableList()
            val trip = newTrips[currentIndex]
            newTrips.removeAt(currentIndex)
            newTrips.add(destinationIndex, trip)

            newTrips.forEach {
                it.orderId = newTrips.indexOf(it)
            }

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        tempSharedTripsGroup = classifyAndConvertToTripsGroup(newTrips.toList())
                    )
                )
            }
        }
    }
}

