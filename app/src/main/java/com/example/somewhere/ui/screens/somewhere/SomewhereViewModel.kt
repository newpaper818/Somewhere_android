package com.example.somewhere.ui.screens.somewhere

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somewhere.db.TripRepository
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screens.main.MainDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class SomewhereUiState(
    val isEditMode: Boolean = false,
    val currentScreen: NavigationDestination = MainDestination,
    val isNewTrip: Boolean = false,

    val tripId: Int? = null,
    val dateId: Int? = null,
    val spotId: Int? = null,

    val trip: Trip? = null,
    val tempTrip: Trip? = null
)

class SomewhereViewModel(
    private val tripsRepository: TripRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(
        SomewhereUiState()
    )

    val uiState: StateFlow <SomewhereUiState> = _uiState


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
    suspend fun saveTrip(){
        if (_uiState.value.tempTrip != null){
            val tempTrip = _uiState.value.tempTrip!!.copy(lastModifiedTime = LocalDateTime.now())

            _uiState.update {
                it.copy(trip = tempTrip, tempTrip = tempTrip)
            }
            tripsRepository.updateTrip(tempTrip)

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

    fun updateDateTitle(toTempTrip: Boolean, dateId: Int, dateTitleText: String){
        if (_uiState.value.trip != null && _uiState.value.tempTrip != null) {
            val newTitleText: String? = if (dateTitleText == "") null
                                        else dateTitleText

            val currentTrip: Trip = if (toTempTrip) _uiState.value.tempTrip!!
                                    else            _uiState.value.trip!!

            val newDate = currentTrip.dateList[dateId].copy(titleText = newTitleText)
            val newDateList = currentTrip.dateList.toMutableList()
            newDateList[dateId] = newDate

            updateTripState(toTempTrip, currentTrip.copy(dateList = newDateList.toList()))
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

            if (currentTrip.dateList.isNotEmpty()) {
                val dateList: MutableList<Date> = currentTrip.dateList.toMutableList()

                val firstListDate = dateList.first().date
                val lastListDate = dateList.last().date

                val firstDate = if (firstListDate < startDate) firstListDate
                else startDate

                val lastDate = if (lastListDate < endDate) endDate
                else lastListDate

                var currDate = firstDate

                while (currDate != lastDate.plusDays(1)) {
                    if (currDate !in startDate..endDate) {
                        //delete
                        for (date in dateList) {
                            if (currDate == date.date) {
                                dateList.remove(date)
                                break
                            }
                        }
                    } else if (currDate !in firstListDate..lastListDate) {
                        //add
                        dateList.add(Date(date = currDate))
                    }

                    currDate = currDate.plusDays(1)
                }

                //sort
                dateList.sortBy { it.date }

                for (id in 0 until dateList.size) {
                    dateList[id].id = id
                }

                updateTripState(toTempTrip, currentTrip.copy(dateList = dateList))
            } else {
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

            val lastId =
                if (newSpotList.isNotEmpty()) newSpotList.last().id
                else -1

            newSpotList.add(Spot(id = lastId + 1, date = date))

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

            //index id
            newSpotList.map {
                it.id = newSpotList.indexOf(it)
                //FIXME delete ======================================================
                //it.titleText = newSpotList.indexOf(it).toString()
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

    fun updateSpotTitle(toTempTrip: Boolean, dateId: Int, spotId: Int, spotTitleText: String){
        if (_uiState.value.trip != null && _uiState.value.tempTrip != null) {
            val newTitleText: String? = if (spotTitleText == "") null
                                        else                     spotTitleText

            val currentTrip: Trip = if (toTempTrip) _uiState.value.tempTrip!!
                                    else            _uiState.value.trip!!

            val newSpot = currentTrip.dateList[dateId].spotList[spotId].copy(titleText = spotTitleText)

            val newDateList = currentTrip.dateList.toMutableList()

            val newSpotList = newDateList[dateId].spotList.toMutableList()

            newSpotList[spotId] = newSpot

            val newDate = newDateList[dateId].copy(spotList = newSpotList)

            newDateList[dateId] = newDate

            updateTripState(toTempTrip, currentTrip.copy(dateList = newDateList.toList()))
        }
    }


}

