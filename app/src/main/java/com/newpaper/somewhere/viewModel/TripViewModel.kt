package com.newpaper.somewhere.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.db.TripRepository
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Spot
import com.newpaper.somewhere.model.Trip
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.deleteFilesFromInternalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime

data class TripUiState(
    val isEditMode: Boolean = false,
    val isNewTrip: Boolean = false,

    val tripId: Int? = null,
    val dateIndex: Int? = null,
    val spotIndex: Int? = null,

    val trip: Trip? = null,
    val tempTrip: Trip? = null,
    val imageList: List<String>? = null,
    val initialImageIndex: Int = 0,

    val addedImages: List<String> = listOf(),
    val deletedImages: List<String> = listOf(),
)

class TripViewModel(
    private val tripsRepository: TripRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(
        TripUiState()
    )

    val uiState: StateFlow <TripUiState> = _uiState

    fun updateImageListAndInitialImageIndex(newImageList: List<String>, newInitialImageIndex: Int) {
        _uiState.update {
            it.copy(imageList = newImageList, initialImageIndex = newInitialImageIndex)
        }
    }

    fun toggleEditMode(editMode: Boolean? = null) {
        _uiState.update {
            it.copy(isEditMode = editMode ?: !it.isEditMode)
        }
    }

    fun toggleIsNewTrip(isNewTrip: Boolean? = null) {
        _uiState.update {
            it.copy(isNewTrip = isNewTrip ?: !it.isNewTrip)
        }
    }

    fun updateTripId(tripId: Int?){
        _uiState.update {
            it.copy(tripId = tripId)
        }
    }

    fun updateDateIndex(dateIndex: Int?){
        _uiState.update {
            it.copy(dateIndex = dateIndex)
        }
    }

    fun updateSpotIndex(spotIndex: Int?){
        _uiState.update {
            it.copy(spotIndex = spotIndex)
        }
    }

    //use at Main Screen ===========================================================================
    suspend fun changeTrip(tripId: Int){

        var trip: Trip? = null

        trip = tripsRepository.getTripStream(tripId)
            .first()
                //if null make new Trip()
            ?: Trip()

        _uiState.update {
            it.copy(tripId = tripId, trip = trip.clone(), tempTrip = trip.clone())
        }
    }


    //use at Trip Screen ===========================================================================
    suspend fun insertTrip(){
        if (_uiState.value.trip != null)
            tripsRepository.insertTrip(_uiState.value.trip!!)
    }

    //save tempTrip
    suspend fun saveTrip(updateAppUiState: () -> Unit, deleteNotEnabledDate: Boolean = false){
        if (_uiState.value.tempTrip != null){

            val tempTrip =
                if (deleteNotEnabledDate)
                    _uiState.value.tempTrip!!.copy(lastModifiedTime = LocalDateTime.now(),
                        dateList = _uiState.value.tempTrip!!.dateList.filter { it.enabled })
                else
                    _uiState.value.tempTrip!!.copy(lastModifiedTime = LocalDateTime.now())


            //update trip state
            _uiState.update {
                it.copy(trip = tempTrip, tempTrip = tempTrip)
            }

            //save to repository
            tripsRepository.updateTrip(tempTrip)

            //update appUiState tripList
            updateAppUiState()

            toggleEditMode(false)
            toggleIsNewTrip(false)
        }
    }

    fun updateTripState(toTempTrip: Boolean, trip: Trip){
        _uiState.update {
            if (toTempTrip)     it.copy(trip = it.trip, tempTrip = trip)
            else                it.copy(trip = trip,    tempTrip = it.tempTrip)
        }
    }

    fun updateTripState(trip: Trip, tempTrip: Trip){
        _uiState.update {
            it.copy(trip = trip, tempTrip = tempTrip)
        }
    }

    fun updateTripDurationAndTripState(
        toTempTrip: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ){
        if (_uiState.value.trip != null && _uiState.value.tempTrip != null) {

            val currentTrip: Trip = if (toTempTrip) _uiState.value.tempTrip!!
                                    else            _uiState.value.trip!!


            //if dateList is empty (first create)
            if (currentTrip.dateList.isEmpty()){
                val dateList: MutableList<Date> = mutableListOf()
                var currDate = startDate
                var id = 0

                val endDateAfter1Day = endDate.plusDays(1)

                while (currDate != endDateAfter1Day) {

                    //add Date in dateList
                    dateList.add(Date(id = id, index = id, date = currDate))
                    id++
                    currDate = currDate.plusDays(1)
                }
                updateTripState(toTempTrip, currentTrip.copy(dateList = dateList))
            }

            //if same date range with before
            else if (currentTrip.dateList.first().date == startDate && currentTrip.dateList.last().date == endDate)
                return

            //if different date range with before
            else{
                val dateList = currentTrip.dateList.toMutableList()

                var currDate = startDate
                var index = 0
                var maxId = 0

                for (date in dateList){

                    date.enabled = currDate <= endDate
                    date.date = currDate

                    if (maxId <= date.id) maxId = date.id

                    currDate = currDate.plusDays(1)
                    index++
                }

                //create new
                while (currDate <= endDate){
                    maxId++
                    dateList.add(Date(id = maxId, index = index, date = currDate))

                    currDate = currDate.plusDays(1)
                    index++
                }

                updateTripState(toTempTrip, currentTrip.copy(dateList = dateList))
            }
        }
    }

    suspend fun deleteTrip(trip: Trip){
        tripsRepository.deleteTrip(trip)
    }


    // use at Date Screen ==========================================================================
    fun addNewSpot(dateIndex: Int, toTempTrip: Boolean = true){

        if(_uiState.value.tempTrip != null){
            val newSpotList = _uiState.value.tempTrip!!.dateList[dateIndex].spotList.toMutableList()

            val date = _uiState.value.tempTrip!!.dateList[dateIndex].date

            //set id, iconText, index
            val lastId =
                if (newSpotList.isNotEmpty()) newSpotList.last().id
                else -1

            val lastIconText =
                if (newSpotList.isNotEmpty()) newSpotList.last().iconText
                else 0

            val lastIndex =
                if (newSpotList.isNotEmpty()) newSpotList.indexOf(newSpotList.last())
                else -1

            newSpotList.add(Spot(id = lastId + 1, index = lastIndex + 1, iconText = lastIconText + 1, date = date))

            //update
            val newDateList = _uiState.value.tempTrip!!.dateList.toMutableList()

            val newDate = newDateList[dateIndex].copy(spotList = newSpotList)

            newDateList[dateIndex] = newDate

            val currentTrip = _uiState.value.tempTrip!!

            updateTripState(true, currentTrip.copy(dateList = newDateList))
        }
    }

    fun deleteSpot(dateIndex: Int, spotIndex: Int, toTempTrip: Boolean = true){

        if(_uiState.value.tempTrip != null){

            val dateList = _uiState.value.tempTrip!!.clone().dateList

            //tempTrip's spotList
            val newSpotList = _uiState.value.tempTrip!!.clone().dateList[dateIndex].spotList.toMutableList()

            //get prev, next spot
//            val prevSpot = newSpotList[spotIndex].getPrevSpot(dateList, dateIndex)
//            val nextSpot = newSpotList[spotIndex].getNextSpot(dateList, dateIndex)

            //delete spot
            newSpotList.removeAt(spotIndex)

            //set index, iconText
            var newIconText = if (spotIndex == 0) 0
                            else newSpotList[spotIndex - 1].iconText

            for (i in spotIndex until newSpotList.size){
                newSpotList[i].index = i

                if(newSpotList[i].spotType.isNotMove())
                    newIconText++

                newSpotList[i].iconText = newIconText
            }

            //check prev or next spot is MOVE and update travel distance



            //tempTrip's dateList
            val newDateList = _uiState.value.tempTrip!!.clone().dateList.toMutableList()
            val newDate = newDateList[dateIndex].copy(spotList = newSpotList.toList())
            newDateList[dateIndex] = newDate

            val currentTrip = _uiState.value.tempTrip!!

            //update tempTrip
            updateTripState(true, currentTrip.copy(dateList = newDateList.toList()))
        }
    }

    //image
    fun addAddedImages(
        newAddedImages: List<String>
    ){
        _uiState.update {
            it.copy(addedImages = it.addedImages + newAddedImages)
        }
    }

    fun addDeletedImages(
        newDeletedImages: List<String>
    ){
        _uiState.update {
            it.copy(deletedImages = it.deletedImages + newDeletedImages)
        }
    }

    fun organizeAddedDeletedImages(
        context: Context,
        isClickSave: Boolean //save: true / cancel: false
    ){

        //when edit mode, user add images and cancel edit
        // -> delete here (delete internal storage)

        //save: delete(delete internal storage) deletedImages
        if (isClickSave){
            deleteFilesFromInternalStorage(context, _uiState.value.deletedImages)
        }

        //cancel: delete(delete internal storage) addedImages
        else{
            deleteFilesFromInternalStorage(context, _uiState.value.addedImages)
        }

        //init
        initAddedDeletedImages()
    }

    fun reorderTripImageList(currentIndex: Int, destinationIndex: Int){
        val imagePathList = _uiState.value.tempTrip?.imagePathList

        if (imagePathList != null){
            val newImagePathList = imagePathList.toMutableList()

            //reorder
            val imagePath = newImagePathList[currentIndex]
            newImagePathList.removeAt(currentIndex)
            newImagePathList.add(destinationIndex, imagePath)

            //update ui state
            val newTempTrip = _uiState.value.tempTrip!!.copy(imagePathList = newImagePathList)

            _uiState.update {
                it.copy(tempTrip = newTempTrip)
            }
        }
    }

    fun reorderSpotImageList(dateIndex: Int, spotIndex: Int, currentIndex: Int, destinationIndex: Int){
        val imagePathList = _uiState.value.tempTrip?.dateList?.get(dateIndex)?.spotList?.get(spotIndex)?.imagePathList

        if (imagePathList  != null){
            val newImagePathList = imagePathList.toMutableList()

            //reorder
            val imagePath = newImagePathList[currentIndex]
            newImagePathList.removeAt(currentIndex)
            newImagePathList.add(destinationIndex, imagePath)

            //update ui state
            val newTempSpot = _uiState.value.tempTrip!!.dateList[dateIndex].spotList[spotIndex].copy(imagePathList = newImagePathList)
            val newTempSpotList = _uiState.value.tempTrip!!.dateList[dateIndex].spotList.toMutableList()
            newTempSpotList[spotIndex] = newTempSpot
            val newTempDate =_uiState.value.tempTrip!!.dateList[dateIndex].copy(spotList = newTempSpotList)
            val newTempDateList = _uiState.value.tempTrip!!.dateList.toMutableList()
            newTempDateList[dateIndex] = newTempDate
            val newTempTrip = _uiState.value.tempTrip!!.copy(dateList = newTempDateList)

            _uiState.update {
                it.copy(tempTrip = newTempTrip)
            }
        }
    }

    fun reorderDateList(currentIndex: Int, destinationIndex: Int){
        if (_uiState.value.tempTrip?.dateList != null){

            val newDateList = _uiState.value.tempTrip!!.dateList.toMutableList()

            var currDate = newDateList.first().date

            //reorder
            val date = newDateList[currentIndex]
            newDateList.removeAt(currentIndex)
            newDateList.add(destinationIndex, date)

            //update date index
            newDateList.forEach{
                if (it.enabled) {
                    it.index = newDateList.indexOf(it)
                    it.date = currDate
                    currDate = currDate.plusDays(1)
                }
            }

            //update ui state
            val newTempTrip = _uiState.value.tempTrip!!.copy(dateList = newDateList)

            _uiState.update {
                it.copy(tempTrip = newTempTrip)
            }
        }
    }

    fun reorderSpotList(dateId: Int, currentIndex: Int, destinationIndex: Int){
        if (_uiState.value.tempTrip?.dateList?.get(dateId)?.spotList != null){

            val newSpotList = _uiState.value.tempTrip!!.dateList[dateId].spotList.toMutableList()

            //reorder
            val date = newSpotList[currentIndex]
            newSpotList.removeAt(currentIndex)
            newSpotList.add(destinationIndex, date)

            //update index and iconText
            var newIconText = 0
            for (index in 0 until newSpotList.size){
                if (newSpotList[index].spotType.isNotMove())
                    newIconText++
                newSpotList[index].iconText = newIconText
                newSpotList[index].index = index
            }

            //update ui state
            val newDate = _uiState.value.tempTrip!!.dateList[dateId].copy(spotList = newSpotList)
            val newDateList = _uiState.value.tempTrip!!.dateList.toMutableList()
            newDateList[dateId] = newDate
            val newTempTrip = _uiState.value.tempTrip!!.copy(dateList = newDateList)

            _uiState.update {
                it.copy(tempTrip = newTempTrip)
            }
        }
    }

    private fun initAddedDeletedImages() {
        _uiState.update {
            it.copy(addedImages = listOf(), deletedImages = listOf())
        }
    }
}

