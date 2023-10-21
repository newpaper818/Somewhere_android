package com.newpaper.somewhere.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.db.TripRepository
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Trip
import com.newpaper.somewhere.ui.tripScreens.TripDestination
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class TripUiState(
    val trip: Trip = Trip(),
    val tempTrip: Trip = Trip()
)

//FIXME DELETE this viewModel ?????????????????????????????????????????????????????????????????????????
class TripViewModel(
    savedStateHandle: SavedStateHandle,
    private val tripsRepository: TripRepository,
) : ViewModel(){

    private val tripId: Int = checkNotNull(savedStateHandle[TripDestination.tripIdArg])

    var tripUiState by mutableStateOf(TripUiState())
        private set

    init{
        viewModelScope.launch{
            tripUiState = tripsRepository.getTripStream(tripId)
                .first()
                    //if null make new Trip()
                ?.toTripUiState() ?: Trip().toTripUiState()
        }
    }


    //if edit
    suspend fun updateTrip(){
        tripsRepository.updateTrip(tripUiState.trip)
    }

    //if new trip
    suspend fun insertTrip(){
        tripsRepository.insertTrip(tripUiState.trip)
    }

    suspend fun saveTrip(){
        val tempTrip = tripUiState.tempTrip.copy(lastModifiedTime = LocalDateTime.now())

        tripUiState = TripUiState(trip = tempTrip, tempTrip = tempTrip)
        tripsRepository.updateTrip(tripUiState.trip)
    }

    fun cancelTrip(){
        tripUiState = TripUiState(trip = tripUiState.trip, tempTrip = tripUiState.trip)
    }

    fun updateTripUiState(toTempTrip: Boolean, trip: Trip){
        tripUiState = if (toTempTrip) TripUiState(trip = tripUiState.trip, tempTrip = trip)
                        else TripUiState(trip = trip, tempTrip = tripUiState.tempTrip)
    }

    fun updateDateTitle(toTempTrip: Boolean, id: Int, titleText: String){
        val newTitleText: String? = if (titleText == "") null
                                    else titleText

        val currentTrip = if (toTempTrip) tripUiState.tempTrip
                            else tripUiState.trip

        val newDate = currentTrip.dateList[id].copy(titleText = newTitleText)
        val newDateList = currentTrip.dateList.toMutableList()
        newDateList[id] = newDate

        updateTripUiState(toTempTrip, currentTrip.copy(dateList = newDateList.toList()))
    }

    fun updateTripDurationAndTripUiState(
        toTempTrip: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ){
        val currentTrip = if (toTempTrip) tripUiState.tempTrip
                            else tripUiState.trip

        if (currentTrip.dateList.isNotEmpty()){
            val dateList: MutableList<Date> = currentTrip.dateList.toMutableList()

            val firstListDate = dateList.first().date
            val lastListDate = dateList.last().date

            val firstDate = if(firstListDate < startDate) firstListDate
                            else startDate

            val lastDate = if(lastListDate < endDate) endDate
                            else lastListDate

            var currDate = firstDate

            while (currDate != lastDate.plusDays(1)){
                if (currDate !in startDate..endDate){
                    //delete
                    for (date in dateList){
                        if (currDate == date.date){
                            dateList.remove(date)
                            break
                        }
                    }
                }
                else if (currDate !in firstListDate..lastListDate) {
                    //add
                    dateList.add(Date(date = currDate))
                }

                currDate = currDate.plusDays(1)
            }

            //sort
            dateList.sortBy { it.date }

            for (index in 0 until dateList.size){
                dateList[index].index = index
            }

            updateTripUiState(toTempTrip, currentTrip.copy(dateList = dateList))
        }
        else{
            val dateList: MutableList<Date> = mutableListOf()
            var currDate = startDate
            var id = 0

            while (currDate != endDate.plusDays(1)){

                //add Date in dateList
                dateList.add(Date(id = id, date = currDate))
                id++
                currDate = currDate.plusDays(1)
            }
            updateTripUiState(toTempTrip, currentTrip.copy(dateList = dateList))
        }
    }


}

fun Trip.toTripUiState(): TripUiState = TripUiState(
    trip = this,
    tempTrip = this
)

fun TripUiState.toTrip(): Trip = this.trip
