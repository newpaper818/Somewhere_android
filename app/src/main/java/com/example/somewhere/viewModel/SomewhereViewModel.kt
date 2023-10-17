package com.example.somewhere.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.somewhere.db.TripRepository
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.mainScreens.MyTripsDestination
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.tripScreenUtils.cards.deleteFilesFromInternalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime

data class SomewhereUiState(
    val isEditMode: Boolean = false,
    val currentMainNavigationDestination: NavigationDestination = MyTripsDestination,
    val currentScreen: NavigationDestination = MyTripsDestination,
    val isNewTrip: Boolean = false,

    val tripId: Int? = null,
    val dateId: Int? = null,
    val spotId: Int? = null,

    val trip: Trip? = null,
    val tempTrip: Trip? = null,

    val addedImages: List<String> = listOf(),
    val deletedImages: List<String> = listOf(),
)

class SomewhereViewModel(
    private val tripsRepository: TripRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(
        SomewhereUiState()
    )

    val uiState: StateFlow <SomewhereUiState> = _uiState


    fun updateCurrentMainNavDestination(screenTo: NavigationDestination){
        _uiState.update {
            it.copy(currentMainNavigationDestination = screenTo)
        }
    }

    fun updateCurrentScreen(screenTo: NavigationDestination) {
        _uiState.update {
            it.copy(currentScreen = screenTo)
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

    fun updateId(
        tripId: Int? = null,
        dateId: Int? = null,
        spotId: Int? = null,
    ){
        if (tripId != null)
            _uiState.update {
                it.copy(tripId = tripId)
            }

        if (dateId != null)
            _uiState.update {
                it.copy(dateId = dateId)
            }

        if (spotId != null)
            _uiState.update {
                it.copy(spotId = spotId)
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
                        dateList = _uiState.value.tempTrip!!.dateList.filter { it.id >= 0 })
                else
                    _uiState.value.tempTrip!!.copy(lastModifiedTime = LocalDateTime.now())


            _uiState.update {
                it.copy(trip = tempTrip, tempTrip = tempTrip)
            }
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

                while (currDate != endDate.plusDays(1)) {

                    //add Date in dateList
                    dateList.add(Date(id = id, date = currDate))
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
                var id = 0

                for (date in dateList){

                    val enabled = if (currDate <= endDate) 1
                                    else -1
                    date.id = id * enabled
                    date.date = currDate

                    currDate = currDate.plusDays(1)
                    id++
                }

                while (currDate <= endDate){
                    dateList.add(Date(id = id, date = currDate))

                    currDate = currDate.plusDays(1)
                    id++
                }

                updateTripState(toTempTrip, currentTrip.copy(dateList = dateList))
            }
        }
    }

    suspend fun deleteTrip(trip: Trip){
        tripsRepository.deleteTrip(trip)
    }


    // use at Date Screen ==========================================================================
    fun addNewSpot(dateId: Int, toTempTrip: Boolean = true){

        if(_uiState.value.tempTrip != null){
            val newSpotList = _uiState.value.tempTrip!!.dateList[dateId].spotList.toMutableList()

            val date = _uiState.value.tempTrip!!.dateList[dateId].date

            //set id, orderId
            val lastId =
                if (newSpotList.isNotEmpty()) newSpotList.last().id
                else -1

            val lastOrderId =
                if (newSpotList.isNotEmpty()) newSpotList.last().iconText
                else 0

            val lastIndex =
                if (newSpotList.isNotEmpty()) newSpotList.last().index
                else 0

            newSpotList.add(Spot(id = lastId + 1, iconText = lastOrderId + 1, index = lastIndex + 1, date = date))

            //update
            val newDateList = _uiState.value.tempTrip!!.dateList.toMutableList()

            val newDate = newDateList[dateId].copy(spotList = newSpotList)

            newDateList[dateId] = newDate

            val currentTrip = _uiState.value.tempTrip!!

            updateTripState(true, currentTrip.copy(dateList = newDateList))
        }
    }

    fun deleteSpot(dateId: Int, spotId: Int, toTempTrip: Boolean = true){
        if(_uiState.value.tempTrip != null){

            //tempTrip's spotList
            val newSpotList = _uiState.value.tempTrip!!.clone().dateList[dateId].spotList.toMutableList()

            //delete spot
            newSpotList.removeAt(spotId)

            //set id, orderId
            var newIconText = if (spotId == 0) 0
                            else newSpotList[spotId - 1].iconText

            for (i in spotId until newSpotList.size){
                newSpotList[i].index = i

                if(newSpotList[i].spotType.isNotMove())
                    newIconText++

                newSpotList[i].iconText = newIconText
            }


            //tempTrip's dateList
            val newDateList = _uiState.value.tempTrip!!.clone().dateList.toMutableList()
            val newDate = newDateList[dateId].copy(spotList = newSpotList.toList())
            newDateList[dateId] = newDate

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

    fun reorderDateList(currentIndex: Int, destinationIndex: Int){
        if (_uiState.value.tempTrip?.dateList != null){

            val newDateList = _uiState.value.tempTrip!!.dateList.toMutableList()

            var currDate = newDateList.first().date

            //reorder
            val date = newDateList[currentIndex]
            newDateList.removeAt(currentIndex)
            newDateList.add(destinationIndex, date)

            //update orderId, date
            newDateList.forEach{
                if (it.id >= 0) {
                    it.orderId = newDateList.indexOf(it)
                    it.date = currDate
                }
                currDate = currDate.plusDays(1)
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

            //update orderId
            newSpotList.forEach{
                it.iconText = newSpotList.indexOf(it)
            }

            var newOrderId = 0
            for (i in 0 until newSpotList.size){
                if (newSpotList[i].spotType.isNotMove())
                    newOrderId++
                newSpotList[i].iconText = newOrderId
                newSpotList[i].index = i
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

