package com.newpaper.somewhere.feature.trip

import android.util.Log
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.core.data.repository.trip.TripRepository
import com.newpaper.somewhere.core.data.repository.trip.TripsRepository
import com.newpaper.somewhere.core.model.tripData.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject

private const val COMMON_TRIP_VIEWMODEL_TAG = "Common-Trip-ViewModel"

data class TripInfo(
    val trips: List<Trip>? = null,
    val tempTrips: List<Trip>? = null,

    val sharedTrips: List<Trip>? = null,
    val tempSharedTrips: List<Trip>? = null,

    val trip: Trip? = null,
    val tempTrip: Trip? = null,

    val dateIndex: Int? = null,
    val spotIndex: Int? = null,
)

data class CommonTripUiState(
    val isEditMode: Boolean = false,

    val tripInfo: TripInfo = TripInfo(),
    val isNewTrip: Boolean = false,

    val addedImages: List<String> = listOf(),
    val deletedImages: List<String> = listOf(),
)

@HiltViewModel
class CommonTripViewModel @Inject constructor(
    private val commonImageRepository: CommonImageRepository,
    private val tripsRepository: TripsRepository,
    private val tripRepository: TripRepository
): ViewModel() {
    private val _commonTripUiState: MutableStateFlow<CommonTripUiState> =
        MutableStateFlow(
            CommonTripUiState()
        )
    val commonTripUiState = _commonTripUiState.asStateFlow()





    //==============================================================================================
    //set UiState ==================================================================================

    fun setIsNewTrip(
        isNewTrip: Boolean
    ){
        _commonTripUiState.update {
            it.copy(isNewTrip = isNewTrip)
        }
    }

    fun setTrip(
        trip: Trip?
    ){
        _commonTripUiState.update {
            it.copy(tripInfo = it.tripInfo.copy(trip = trip, tempTrip = trip))
        }
    }

    /**
     *
     * @param editMode if null, toggle isEditMode value
     */
    fun setIsEditMode(
        editMode: Boolean?
    ){
        if (editMode != null){
            _commonTripUiState.update {
                it.copy(isEditMode = editMode)
            }
        }
        else {
            _commonTripUiState.update {
                it.copy(isEditMode = !it.isEditMode)
            }
        }
    }


    private fun updateTripsAndShardTrips(){
        _commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    trips = _commonTripUiState.value.tripInfo.tempTrips,
                    sharedTrips = _commonTripUiState.value.tripInfo.tempSharedTrips
                )
            )
        }
    }

    fun unSaveTempTrips(){
        _commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    tempTrips = _commonTripUiState.value.tripInfo.trips,
                    tempSharedTrips = _commonTripUiState.value.tripInfo.sharedTrips
                )
            )
        }
    }


    //==============================================================================================
    //update trip ==================================================================================
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
            val matchingOldTrip = commonTripUiState.value.tripInfo.trips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }

        //sharedTrip
        for (trip in newSharedTrips) {
            val matchingOldTrip = commonTripUiState.value.tripInfo.sharedTrips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }


        _commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    trips = newTrips,
                    tempTrips = newTrips,
                    sharedTrips = newSharedTrips,
                    tempSharedTrips = newSharedTrips
                )
            )
        }
    }



    /**
     * get trip from remote
     * for glance
     */
    suspend fun updateTripForGlance(
        internetEnabled: Boolean,
        appUserId: String,
        tripWithEmptyDateList: Trip
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
                _commonTripUiState.value.tripInfo.trips?.indexOfFirst { it.id == trip.id }
            else
                _commonTripUiState.value.tripInfo.tempSharedTrips?.indexOfFirst { it.id == trip.id }

            val newTrips = if (!isInSharedTrips)
                _commonTripUiState.value.tripInfo.trips?.toMutableList()
            else
                _commonTripUiState.value.tripInfo.tempSharedTrips?.toMutableList()

            if (indexOfTrip != null && indexOfTrip != -1) {
                newTrips?.set(indexOfTrip, trip)
            }

            _commonTripUiState.update {
                if (!isInSharedTrips)
                    it.copy(
                        tripInfo = it.tripInfo.copy(
                            trips = newTrips, tempTrips = newTrips
                        )
                    )
                else
                    it.copy(
                        tripInfo = it.tripInfo.copy(
                            sharedTrips = newTrips, tempSharedTrips = newTrips
                        )
                    )
            }
        }

        return trip
    }


    //==============================================================================================
    //edit trip ====================================================================================
//    fun deleteTrip(
//        trip: Trip,
//        appUserId: String
//    ) {
//        if (trip.managerId == appUserId){
//            if (_commonTripUiState.value.tripInfo.tempTrips != null) {
//                val newTempTripList = _commonTripUiState.value.tripInfo.tempTrips!!.toMutableList()
//                newTempTripList.remove(trip)
//
//                _commonTripUiState.update {
//                    it.copy(
//                        tripInfo = it.tripInfo.copy(
//                            trips = newTempTripList.toList()
//                        )
//                    )
//                }
//
//                val newDeletedTripIdList = tripsUiState.value.deletedTripIds + listOf(trip.id)
//
//                _commonTripUiState.update {
//                    it.copy(deletedTripIds = newDeletedTripIdList)
//                }
//            }
//        }
//        else{
//            if (_commonTripUiState.value.trips.tempSharedTrips != null) {
//                val newTempSharedTripList = _commonTripUiState.value.trips.tempSharedTrips!!.toMutableList()
//                newTempSharedTripList.removeIf {
//                    it.id == trip.id && it.managerId == trip.managerId
//                }
//
//                _commonTripUiState.update {
//                    it.copy(trips = it.trips.copy(tempSharedTrips = newTempSharedTripList.toList()))
//                }
//
//                val newDeletedSharedTripList = _commonTripUiState.value.deletedSharedTrips + listOf(trip)
//
//                _commonTripUiState.update {
//                    it.copy(deletedSharedTrips = newDeletedSharedTripList)
//                }
//            }
//        }
//    }


//    suspend fun saveTrips(
//        appUserId: String,
//        editModeToFalse: () -> Unit
//    ) {
//        updateTripsAndShardTrips()
//
//        //edit mode to false
//        editModeToFalse()
//
//        //save myTrips to remote
//        tripsRepository.saveTrips(
//            appUserId = appUserId,
//            myTrips = tripsUiState.value.trips.tempTrips ?: listOf(),
//            deletedTripsIds = tripsUiState.value.deletedTripIds,
//            deletedSharedTrips = tripsUiState.value.deletedSharedTrips
//        )
//
//        //update sharedTrips order to firestore
//        tripsRepository.updateSharedTripsOrder(
//            appUserId = appUserId,
//            sharedTripList = tripsUiState.value.trips.tempSharedTrips ?: listOf()
//        )
//
//        //init deletedTripsIdList
//        initDeletedTripsIdsAndDeletedSharedTrips()
//    }


//    fun reorderTempTrips(
//        isSharedTripList: Boolean,
//        currentIndex: Int,
//        destinationIndex: Int
//    ){
//        //only at edit mode
//        if (!isSharedTripList && _commonTripUiState.value.trips.tempTrips != null) {
//            val newTrips = _commonTripUiState.value.trips.tempTrips!!.toMutableList()
//            val trip = newTrips[currentIndex]
//            newTrips.removeAt(currentIndex)
//            newTrips.add(destinationIndex, trip)
//
//            newTrips.forEach {
//                it.orderId = newTrips.indexOf(it)
//            }
//
//            _commonTripUiState.update {
//                it.copy(
//                    trips = it.trips.copy(tempTrips = newTrips.toList())
//                )
//            }
//        }
//        else if (isSharedTripList && _commonTripUiState.value.trips.tempSharedTrips != null){
//            val newTrips = _commonTripUiState.value.trips.tempSharedTrips!!.toMutableList()
//            val trip = newTrips[currentIndex]
//            newTrips.removeAt(currentIndex)
//            newTrips.add(destinationIndex, trip)
//
//            newTrips.forEach {
//                it.orderId = newTrips.indexOf(it)
//            }
//
//            _commonTripUiState.update {
//                it.copy(
//                    trips = it.trips.copy(tempSharedTrips = newTrips.toList())
//                )
//            }
//        }
//    }
    
    
    fun saveTrip(
        appUserId: String,
        deleteNotEnabledDate: Boolean = false
    ): Int?{
        if (_commonTripUiState.value.tripInfo.tempTrip != null){

            val tempTripDateListLastIndex = _commonTripUiState.value.tripInfo.tempTrip!!.dateList.size - 1

            val tempTrip =
                if (deleteNotEnabledDate)
                    _commonTripUiState.value.tripInfo.tempTrip!!.copy(
                        lastModifiedTime = ZonedDateTime.now(ZoneOffset.UTC),
                        dateList = _commonTripUiState.value.tripInfo.tempTrip!!.dateList.filter { it.enabled }
                    )
                else
                    _commonTripUiState.value.tripInfo.tempTrip!!.copy(
                        lastModifiedTime = ZonedDateTime.now(ZoneOffset.UTC)
                    )

            //update trip state
            _commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(
                        trip = tempTrip, tempTrip = tempTrip
                    ),
                )
            }


            //update tripList or sharedTripList
            //get temp trip is in tripList or sharedTripList
            val isInSharedTripList = tempTrip.managerId != appUserId

            val indexOfTempTrip = if (!isInSharedTripList)
                _commonTripUiState.value.tripInfo.trips?.indexOfFirst { it.id == tempTrip.id }
            else
                _commonTripUiState.value.tripInfo.tempSharedTrips?.indexOfFirst { it.id == tempTrip.id }

            val newTripList = if (!isInSharedTripList)
                _commonTripUiState.value.tripInfo.trips?.toMutableList()
            else
                _commonTripUiState.value.tripInfo.tempSharedTrips?.toMutableList()


            if (indexOfTempTrip != null && indexOfTempTrip != -1) {
                newTripList?.set(indexOfTempTrip, tempTrip)
            }
            else if (indexOfTempTrip == -1) {
                newTripList?.add(tempTrip)
            }

            _commonTripUiState.update {
                if (!isInSharedTripList)
                    it.copy(
                        tripInfo = it.tripInfo.copy(
                            trips = newTripList, tempTrips = newTripList
                        )
                    )
                else
                    it.copy(
                        tripInfo = it.tripInfo.copy(
                            sharedTrips = newTripList, tempSharedTrips = newTripList
                        )
                    )
            }

            //return tempTrip.dateList 's size
            return tempTripDateListLastIndex
        }
        return null
    }





    //image ========================================================================================
    private fun initAddedDeletedImages() {
        _commonTripUiState.update {
            it.copy(addedImages = listOf(), deletedImages = listOf())
        }
    }

    fun addAddedImages(
        newAddedImages: List<String>
    ){
        _commonTripUiState.update {
            it.copy(addedImages = it.addedImages + newAddedImages)
        }
    }

    fun addDeletedImages(
        newDeletedImages: List<String>
    ){
        _commonTripUiState.update {
            it.copy(deletedImages = it.deletedImages + newDeletedImages)
        }
    }

    fun organizeAddedDeletedImages(
        tripManagerId: String,
        isClickSave: Boolean, //save: true / cancel: false
        isInTripsScreen: Boolean = false
    ) {
        Log.d(COMMON_TRIP_VIEWMODEL_TAG, "added images: ${_commonTripUiState.value.addedImages}")
        Log.d(COMMON_TRIP_VIEWMODEL_TAG, "deleted images: ${_commonTripUiState.value.deletedImages}")

        //when edit mode, user add images and cancel edit
        // -> delete here (delete internal storage)

        //save: delete(delete internal storage) deletedImages
        if (isClickSave) {
            //delete deletedImages
            commonImageRepository.deleteImagesFromInternalStorage(_commonTripUiState.value.deletedImages)

            //upload addedImages to firebase storage
            commonImageRepository.uploadImagesToRemote(
                tripManagerId = tripManagerId,
                imagePaths = _commonTripUiState.value.addedImages

            )

            //if in tripsScreen, don't do it.  because...
            //  myTrips: already delete image form firebase-functions
            //  sharedTrips: exit from trip, not delete trip - do not have to delete
            if (!isInTripsScreen) {
                commonImageRepository.deleteImagesFromRemote(
                    tripManagerId = tripManagerId,
                    imagePaths = _commonTripUiState.value.deletedImages
                )
            }
        }
        //cancel: delete(delete internal storage) addedImages
        else{
            commonImageRepository.deleteImagesFromInternalStorage(_commonTripUiState.value.addedImages)
        }

        //init
        initAddedDeletedImages()
    }





















    
}
