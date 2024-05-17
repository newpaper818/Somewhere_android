package com.newpaper.somewhere.feature.trip.trips

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.ImageRepository
import com.newpaper.somewhere.core.data.repository.trip.TripRepository
import com.newpaper.somewhere.core.data.repository.trip.TripsRepository
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

private const val TRIP_VIEWMODEL_TAG = "Trip-ViewModel"

data class Trips(
    val trips: List<Trip>? = null,
    val tempTrips: List<Trip>? = null,
    val sharedTrips: List<Trip>? = null,
    val tempSharedTrips: List<Trip>? = null
)

data class Glance(
    val visible: Boolean = false,
    val trip: Trip? = null,
    val date: Date? = null,
    val spot: Spot? = null
)

data class TripsUiState(
    val trips: Trips = Trips(),
    val glance: Glance = Glance(),

    val addedImages: List<String> = listOf(),
    val deletedImages: List<String> = listOf(),

    val deletedTripIds: List<Int> = listOf(),
    val deletedSharedTrips: List<Trip> = listOf(),
)


@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripsRepository: TripsRepository,
    private val tripRepository: TripRepository,
    private val imageRepository: ImageRepository
): ViewModel()  {
    private val _tripsUiState: MutableStateFlow<TripsUiState> =
        MutableStateFlow(
            TripsUiState(
                trips = Trips(
                    trips = emptyList(),
                    tempTrips = emptyList()
                )
            )
        )

    val tripsUiState = _tripsUiState.asStateFlow()













    private fun initGlance() {
        _tripsUiState.update {
            it.copy(
                glance = Glance()
            )
        }
    }

    fun updateTripsAndShardTrips(){
        _tripsUiState.update {
            it.copy(
                trips = it.trips.copy(
                    trips = _tripsUiState.value.trips.tempTrips,
                    sharedTrips = _tripsUiState.value.trips.tempSharedTrips
                )
            )
        }
    }

    fun initDeletedTripsIdsAndDeletedSharedTrips(){
        _tripsUiState.update {
            it.copy(
                deletedTripIds = listOf(),
                deletedSharedTrips = listOf()
            )
        }
    }

    fun unSaveTempTrips(){
        _tripsUiState.update {
            it.copy(
                trips = it.trips.copy(
                    tempTrips = _tripsUiState.value.trips.trips,
                    tempSharedTrips = _tripsUiState.value.trips.sharedTrips
                )
            )
        }
    }






    //image

    private fun initAddedDeletedImages() {
        _tripsUiState.update {
            it.copy(addedImages = listOf(), deletedImages = listOf())
        }
    }

    fun addAddedImages(
        newAddedImages: List<String>
    ){
        _tripsUiState.update {
            it.copy(addedImages = it.addedImages + newAddedImages)
        }
    }

    fun addDeletedImages(
        newDeletedImages: List<String>
    ){
        _tripsUiState.update {
            it.copy(deletedImages = it.deletedImages + newDeletedImages)
        }
    }

    fun organizeAddedDeletedImages(
        tripManagerId: String,
        context: Context,
        isClickSave: Boolean, //save: true / cancel: false
        isInTripsScreen: Boolean = false
    ) {
        Log.d(TRIP_VIEWMODEL_TAG, "added images: ${_tripsUiState.value.addedImages}")
        Log.d(TRIP_VIEWMODEL_TAG, "deleted images: ${_tripsUiState.value.deletedImages}")

        //when edit mode, user add images and cancel edit
        // -> delete here (delete internal storage)

        //save: delete(delete internal storage) deletedImages
        if (isClickSave) {
            //delete deletedImages
            imageRepository.deleteImagesFromInternalStorage(context, _tripsUiState.value.deletedImages)

            //upload addedImages to firebase storage
            imageRepository.uploadImagesToRemote(
                tripManagerId = tripManagerId,
                imagePaths = _tripsUiState.value.addedImages

            )

            //if in tripsScreen, don't do it.  because...
            //  myTrips: already delete image form firebase-functions
            //  sharedTrips: exit from trip, not delete trip - do not have to delete
            if (!isInTripsScreen) {
                imageRepository.deleteImagesFromRemote(
                    tripManagerId = tripManagerId,
                    imagePaths = _tripsUiState.value.deletedImages
                )
            }
        }
        //cancel: delete(delete internal storage) addedImages
        else{
            imageRepository.deleteImagesFromInternalStorage(context, _tripsUiState.value.addedImages)
        }

        //init
        initAddedDeletedImages()
    }




    suspend fun updateTrips(
        internetEnabled: Boolean,
        appUserId: String
    ){
        val newTrips = tripsRepository.getMyTrips(internetEnabled, appUserId)
        val newSharedTrips = tripsRepository.getSharedTrips(internetEnabled, appUserId)

        //set orderId of shared trip
        newSharedTrips.forEachIndexed { index, sharedTrip ->
            sharedTrip.orderId = index
        }

        //update newTripList's dateList from tripUiState.value.tripList's dateList
        //because newTripList's dateList is empty list (get trip data except dateList)

        // -> at app open and user enter TripScreen, user see empty dateList,
        //    after load dateList, dateList will be show to user and dateList will save in viewModel (even user go back to MyTripsScreen)
        //    when user enter TripScreen(again), old dateList will show (not empty list)
        //    after load dateList, user can see new dateList

        // all trip data(not really all, user have to go to TripScreen) will be saved before execute app
        for (trip in newTrips) {
            val matchingOldTrip = tripsUiState.value.trips.trips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }

        //sharedTrip
        for (trip in newSharedTrips) {
            val matchingOldTrip = tripsUiState.value.trips.sharedTrips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }


        _tripsUiState.update {
            it.copy(
                trips = it.trips.copy(
                    trips = newTrips,
                    tempTrips = newTrips,
                    sharedTrips = newSharedTrips,
                    tempSharedTrips = newSharedTrips
                )
            )
        }
    }

    suspend fun updateGlance(
        internetEnabled: Boolean,
        appUserId: String,
    ){
        //get current date and time
        val currentDateTime = LocalDateTime.now()
        val currentDate = currentDateTime.toLocalDate()
        val currentTime = currentDateTime.toLocalTime()

        val tripList: List<Trip> = (_tripsUiState.value.trips.trips ?: listOf()) +
                (_tripsUiState.value.trips.sharedTrips ?: listOf())


        if (tripList.isEmpty()){
            initGlance()
        }

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
                break
            }
        }

        if (_tripsUiState.value.glance.trip != null) {

            var trip = _tripsUiState.value.glance.trip

            //update trip info (only at empty date list - load once)
            if (_tripsUiState.value.glance.trip!!.dateList.isEmpty()) {
                trip = updateTrip(
                    internetEnabled = internetEnabled,
                    appUserId = appUserId,
                    tripWithEmptyDateList = _tripsUiState.value.glance.trip!!
                )
            }

            if (trip != null) {
                _tripsUiState.update {
                    it.copy(
                        glance = it.glance.copy(trip = trip)
                    )
                }

                //find date
                for (date in trip.dateList){
                    if (date.date == currentDate){
                        _tripsUiState.update {
                            it.copy(
                                glance = it.glance.copy(date = date)
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
                            visible = false, spot = null
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
                                visible = true, spot = glanceSpot
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

    fun deleteTrip(
      trip: Trip,
      appUserId: String
    ) {
        if (trip.managerId == appUserId){
            if (_tripsUiState.value.trips.tempTrips != null) {
                val newTempTripList = _tripsUiState.value.trips.tempTrips!!.toMutableList()
                newTempTripList.remove(trip)

                _tripsUiState.update {
                    it.copy(trips = it.trips.copy(tempTrips = newTempTripList.toList()))
                }

                val newDeletedTripIdList = tripsUiState.value.deletedTripIds + listOf(trip.id)

                _tripsUiState.update {
                    it.copy(deletedTripIds = newDeletedTripIdList)
                }
            }
        }
        else{
            if (_tripsUiState.value.trips.tempSharedTrips != null) {
                val newTempSharedTripList = _tripsUiState.value.trips.tempSharedTrips!!.toMutableList()
                newTempSharedTripList.removeIf {
                    it.id == trip.id && it.managerId == trip.managerId
                }

                _tripsUiState.update {
                    it.copy(trips = it.trips.copy(tempSharedTrips = newTempSharedTripList.toList()))
                }

                val newDeletedSharedTripList = _tripsUiState.value.deletedSharedTrips + listOf(trip)

                _tripsUiState.update {
                    it.copy(deletedSharedTrips = newDeletedSharedTripList)
                }
            }
        }
    }




    suspend fun saveTrips(
        appUserId: String,
        editModeToFalse: () -> Unit
    ) {
        updateTripsAndShardTrips()

        //edit mode to false
        editModeToFalse()

        //save myTrips to remote
        tripsRepository.saveTrips(
            appUserId = appUserId,
            myTrips = tripsUiState.value.trips.tempTrips ?: listOf(),
            deletedTripsIds = tripsUiState.value.deletedTripIds,
            deletedSharedTrips = tripsUiState.value.deletedSharedTrips
        )

        //update sharedTrips order to firestore
        tripsRepository.updateSharedTripsOrder(
            appUserId = appUserId,
            sharedTripList = tripsUiState.value.trips.tempSharedTrips ?: listOf()
        )

        //init deletedTripsIdList
        initDeletedTripsIdsAndDeletedSharedTrips()
    }






    fun reorderTempTrips(
        isSharedTripList: Boolean,
        currentIndex: Int,
        destinationIndex: Int
    ){
        //only at edit mode
        if (!isSharedTripList && _tripsUiState.value.trips.tempTrips != null) {
            val newTrips = _tripsUiState.value.trips.tempTrips!!.toMutableList()
            val trip = newTrips[currentIndex]
            newTrips.removeAt(currentIndex)
            newTrips.add(destinationIndex, trip)

            newTrips.forEach {
                it.orderId = newTrips.indexOf(it)
            }

            _tripsUiState.update {
                it.copy(
                    trips = it.trips.copy(tempTrips = newTrips.toList())
                )
            }
        }
        else if (isSharedTripList && _tripsUiState.value.trips.tempSharedTrips != null){
            val newTrips = _tripsUiState.value.trips.tempSharedTrips!!.toMutableList()
            val trip = newTrips[currentIndex]
            newTrips.removeAt(currentIndex)
            newTrips.add(destinationIndex, trip)

            newTrips.forEach {
                it.orderId = newTrips.indexOf(it)
            }

            _tripsUiState.update {
                it.copy(
                    trips = it.trips.copy(tempSharedTrips = newTrips.toList())
                )
            }
        }
    }




















    /**
     * get trip from remote
     * for glance
     */
    private suspend fun updateTrip(
        internetEnabled: Boolean,
        appUserId: String,
        tripWithEmptyDateList: Trip,
    ): Trip? {
        val trip = tripRepository.getTrip(
            internetEnabled = internetEnabled,
            appUserId = appUserId,
            tripWithEmptyDateList = tripWithEmptyDateList
        )

        if (trip != null) {

            //update tripList / sharedTripList
            val isInSharedTrips = trip.managerId != appUserId

            //FIXME: is it fine to use tempSharedTrips? (not original shared trips?)
            val indexOfTrip = if (!isInSharedTrips)
                    _tripsUiState.value.trips.trips?.indexOfFirst { it.id == trip.id }
                else
                    _tripsUiState.value.trips.tempSharedTrips?.indexOfFirst { it.id == trip.id }

            val newTrips = if (!isInSharedTrips)
                    _tripsUiState.value.trips.trips?.toMutableList()
                else
                    _tripsUiState.value.trips.tempSharedTrips?.toMutableList()

            if (indexOfTrip != null && indexOfTrip != -1) {
                newTrips?.set(indexOfTrip, trip)
            }

            _tripsUiState.update {
                if (!isInSharedTrips)
                    it.copy(
                        trips = it.trips.copy(
                            trips = newTrips, tempTrips = newTrips
                        )
                    )
                else
                    it.copy(
                        trips = it.trips.copy(
                            sharedTrips = newTrips, tempSharedTrips = newTrips
                        )
                    )
            }
        }

        return trip
    }
}