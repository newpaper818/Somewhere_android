package com.newpaper.somewhere.feature.trip

import android.util.Log
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.core.data.repository.image.GetImageRepository
import com.newpaper.somewhere.core.data.repository.trip.TripRepository
import com.newpaper.somewhere.core.data.repository.trip.TripsRepository
import com.newpaper.somewhere.core.model.tripData.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject

private const val COMMON_TRIP_VIEWMODEL_TAG = "Common-Trip-ViewModel"

@HiltViewModel
class CommonTripViewModel @Inject constructor(
    private val commonTripUiStateRepository: CommonTripUiStateRepository,
    private val commonImageRepository: CommonImageRepository,
    private val tripsRepository: TripsRepository,
    private val tripRepository: TripRepository,
    private val getImageRepository: GetImageRepository,
): ViewModel() {
    val commonTripUiState = commonTripUiStateRepository.commonTripUiState

    fun setIsEditMode(
        isEditMode: Boolean?
    ){
        commonTripUiStateRepository._commonTripUiState.update {
            if (isEditMode == null)
                it.copy(isEditMode = !it.isEditMode)
            else
                it.copy(isEditMode = isEditMode)
        }
    }

    fun setIsNewTrip(
        isNewTrip: Boolean?
    ){
        commonTripUiStateRepository._commonTripUiState.update {
            if (isNewTrip == null)
                it.copy(isNewTrip = !it.isEditMode)
            else
                it.copy(isNewTrip = isNewTrip)
        }
    }

    fun setTrip(
        trip: Trip?
    ){
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(tripInfo = it.tripInfo.copy(
                trip = trip, tempTrip = trip
            ))
        }
    }

    fun getImage(
        imagePath: String,
        imageUserId: String,
        result: (Boolean) -> Unit
    ) {
        getImageRepository.getImage(
            imagePath = imagePath,
            imageUserId = imageUserId,
            result = result
        )
    }

    fun updateTripState(toTempTrip: Boolean, trip: Trip){
        commonTripUiStateRepository._commonTripUiState.update {
            if (toTempTrip)     it.copy(tripInfo = it.tripInfo.copy(tempTrip = trip) )
            else                it.copy(tripInfo = it.tripInfo.copy(trip = trip))
        }
    }

    /** get trip's all data(include date list and spot list)
     *  and update trip to commonTripUiState
     * */
    suspend fun updateTrip(
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

            //update trip
            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(trip = trip.clone(), tempTrip = trip.clone())
                )
            }

            //update tripList / sharedTripList
            val isInSharedTripList = trip.managerId != appUserId

            val indexOfTrip = if (!isInSharedTripList)
                commonTripUiState.value.tripInfo.trips?.indexOfFirst { it.id == trip.id }
            else
                commonTripUiState.value.tripInfo.tempSharedTrips?.indexOfFirst { it.id == trip.id }

            val newTripList = if (!isInSharedTripList)
                commonTripUiState.value.tripInfo.trips?.toMutableList()
            else
                commonTripUiState.value.tripInfo.tempSharedTrips?.toMutableList()

            if (indexOfTrip != null && indexOfTrip != -1) {
                newTripList?.set(indexOfTrip, trip)
            }

            if (!isInSharedTripList)
                commonTripUiStateRepository._commonTripUiState.update {
                    it.copy(
                        tripInfo = it.tripInfo.copy(trips = newTripList?.toList(), tempTrips = newTripList?.toList())
                    )
                }
            else
                commonTripUiStateRepository._commonTripUiState.update {
                    it.copy(
                        tripInfo = it.tripInfo.copy(sharedTrips = newTripList?.toList(), tempSharedTrips = newTripList?.toList())
                    )
                }

        }

        return trip
    }

    /**
     * save Trip form tempTrip
     *
     * @param appUserId not managerId
     * @param deleteNotEnabledDate
     * @return tempTrip.dateList 's size (include not enabled Date)
     */
    fun saveTrip(
        appUserId: String,
        deleteNotEnabledDate: Boolean = false
    ): Int?{
        val tempTrip = commonTripUiState.value.tripInfo.tempTrip

        if (tempTrip != null){

            val tempTripDateListLastIndex = tempTrip.dateList.size - 1

            val newTempTrip =
                if (deleteNotEnabledDate)
                    tempTrip.copy(
                        lastModifiedTime = ZonedDateTime.now(ZoneOffset.UTC),
                        dateList = tempTrip.dateList.filter { it.enabled }
                    )
                else
                    tempTrip.copy(
                        lastModifiedTime = ZonedDateTime.now(ZoneOffset.UTC)
                    )

            //update trip state
            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(tripInfo = it.tripInfo.copy(
                    trip = newTempTrip,
                    tempTrip = newTempTrip
                ))
            }

            //update tripList or sharedTripList
            //get temp trip is in tripList or sharedTripList
            val isInSharedTripList = newTempTrip.managerId != appUserId

            val indexOfTempTrip = if (!isInSharedTripList)
                commonTripUiState.value.tripInfo.trips?.indexOfFirst { it.id == newTempTrip.id }
            else
                commonTripUiState.value.tripInfo.tempSharedTrips?.indexOfFirst { it.id == newTempTrip.id }

            val newTripList = if (!isInSharedTripList)
                commonTripUiState.value.tripInfo.trips?.toMutableList()
            else
                commonTripUiState.value.tripInfo.tempSharedTrips?.toMutableList()


            if (indexOfTempTrip != null && indexOfTempTrip != -1) {
                newTripList?.set(indexOfTempTrip, newTempTrip)
            }
            else if (indexOfTempTrip == -1) {
                newTripList?.add(newTempTrip)
            }

            commonTripUiStateRepository._commonTripUiState.update {
                if (!isInSharedTripList)
                    it.copy(tripInfo = it.tripInfo.copy(
                        trips = newTripList, tempTrips = newTripList
                    ))
                else
                    it.copy(tripInfo = it.tripInfo.copy(
                        sharedTrips = newTripList, tempSharedTrips = newTripList
                    ))
            }

            //return tempTrip.dateList 's size
            return tempTripDateListLastIndex
        }
        return null
    }


    /** when click cancel button,
     *
     *  temp trips to trips(original trip data)
     *
     *  temp shared trips to shared trips(original shared trip data) */
    fun unSaveTempTrips(

    ){
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(
                    tempTrips = it.tripInfo.trips,
                    tempSharedTrips = it.tripInfo.tempSharedTrips
                )
            )
        }
    }

    suspend fun saveTripAndAllDates(
        trip: Trip,
        tempTripDateListLastIndex: Int? = null
    ){
        tripRepository.saveTripAndAllDates(
            trip = trip,
            tempTripDateListLastIndex = tempTripDateListLastIndex
        )
    }

    fun addAddedImages(
        newAddedImages: List<String>
    ){
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                addedImages = it.addedImages + newAddedImages
            )
        }
    }

    fun addDeletedImages(
        newDeletedImages: List<String>
    ){
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                deletedImages = it.deletedImages + newDeletedImages
            )
        }
    }

    fun organizeAddedDeletedImages(
        tripManagerId: String,
        isClickSave: Boolean, //save: true / cancel: false
        isInTripsScreen: Boolean = false
    ) {
        Log.d(COMMON_TRIP_VIEWMODEL_TAG, "added images: ${commonTripUiState.value.addedImages}")
        Log.d(COMMON_TRIP_VIEWMODEL_TAG, "deleted images: ${commonTripUiState.value.deletedImages}")

        //when edit mode, user add images and cancel edit
        // -> delete here (delete internal storage)

        //save: delete(delete internal storage) deletedImages
        if (isClickSave){
            //delete deletedImages
            commonImageRepository.deleteImagesFromInternalStorage(commonTripUiState.value.deletedImages)

            //upload / delete addedImages to firebase storage
            commonImageRepository.uploadImagesToRemote(
                tripManagerId = tripManagerId,
                imagePaths = commonTripUiState.value.addedImages
            )

            //if in tripsScreen, don't do it.  because...
            //  myTrips: already delete image form firebase-functions
            //  sharedTrips: exit from trip, not delete trip - do not have to delete
            if (!isInTripsScreen) {
                commonImageRepository.deleteImagesFromRemote(
                    tripManagerId = tripManagerId,
                    imagePaths = commonTripUiState.value.deletedImages
                )
            }
        }

        //cancel: delete(delete internal storage) addedImages
        else{
            commonImageRepository.deleteImagesFromInternalStorage(commonTripUiState.value.addedImages)
        }

        //init
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(addedImages = listOf(), deletedImages = listOf())
        }
    }

    fun deleteTempTrip(trip: Trip){
        val newTempTripList = commonTripUiState.value.tripInfo.tempTrips?.toMutableList()
        newTempTripList?.remove(trip)
        commonTripUiStateRepository._commonTripUiState.update {
            it.copy(
                tripInfo = it.tripInfo.copy(tempTrips = newTempTripList)
            )
        }
    }
}
