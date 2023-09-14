package com.example.somewhere.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somewhere.db.TripRepository
import com.example.somewhere.model.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class AppUiState(
    val tripList: List<Trip>? = null,
    val tempTripList: List<Trip>? = null
)

class AppViewModel(
    private val tripsRepository: TripRepository
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _appUiState = MutableStateFlow(AppUiState())
    val appUiState = _appUiState.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateAppUiStateFromRepository()
        }

        viewModelScope.launch {
            while (!_isReady.value) {
                if (_appUiState.value.tripList != null) {
                    updateTripListState(true, _appUiState.value.tripList)
                    _isReady.value = true
                }
                delay(100)
            }
        }
    }

    suspend fun addAndGetNewTrip(): Trip?{
        val nowTime = LocalDateTime.now()
        addNewTrip(nowTime)
        return getTrip(nowTime)
    }

    private suspend fun addNewTrip(nowTime: LocalDateTime){
        //get last orderId
        var lastOrderId = 1
        val lastTrip = _appUiState.value.tripList?.lastOrNull()
        if (lastTrip != null) { lastOrderId = lastTrip.orderId + 1 }

        //add trip to repository
        tripsRepository.insertTrip(Trip(orderId = lastOrderId, firstCreatedTime = nowTime, lastModifiedTime = nowTime))
        val newTrip = getTrip(nowTime)

        //update appUiState
        if (newTrip != null){
            //update appUiState
            val tripList = (_appUiState.value.tripList ?: listOf<Trip>()) .toMutableList()
            tripList.add(newTrip)
            updateTripListState(false, tripList.toList())

            val tempTripList = (_appUiState.value.tempTripList ?: listOf<Trip>()) .toMutableList()
            tempTripList.add(newTrip)
            updateTripListState(false, tempTripList.toList())
        }
    }

    suspend fun getTrip(firstCreatedTime: LocalDateTime): Trip?{
        return tripsRepository.getTripStream(firstCreatedTime)
            .first()
    }

    fun deleteTempTrip(trip: Trip){
        if (_appUiState.value.tempTripList != null) {
            val newTempTripList = _appUiState.value.tempTripList!!.toMutableList()
            newTempTripList.remove(trip)

            updateTripListState(true, newTempTripList.toList())
        }
    }




    fun initTripListState(tripList: List<Trip>?){
        _appUiState.update {
            it.copy(tripList = tripList, tempTripList = tripList)
        }
    }

    fun updateTripListState(toTempTripList: Boolean, tripList: List<Trip>?){
        _appUiState.update {
            if (toTempTripList)     it.copy(tripList = it.tripList, tempTripList = tripList)
            else                    it.copy(tripList = tripList,    tempTripList = it.tempTripList)
        }
    }




    fun reorderTempTripList(currentIndex: Int, destinationIndex: Int){
        //only at edit mode
        if (_appUiState.value.tempTripList != null) {
            val newTripList = _appUiState.value.tempTripList!!.toMutableList()
            val trip = newTripList[currentIndex]
            newTripList.removeAt(currentIndex)
            newTripList.add(destinationIndex, trip)

            newTripList.forEach {
                it.orderId = newTripList.indexOf(it)
            }

            updateTripListState(true, newTripList.toList())
        }
    }

    suspend fun updateTempToRepository(changeEditMode: () -> Unit){
        //save tempTripList to repository
        val tripList = _appUiState.value.tripList ?: listOf()
        val tempTripList = _appUiState.value.tempTripList ?: listOf()

        for (trip in tripList){
            if (trip !in tempTripList){
                tripsRepository.deleteTrip(trip)
            }
        }
        for (tempTrip in tempTripList) {
            tripsRepository.updateTrip(tempTrip)
        }

        updateTripListState(false, _appUiState.value.tempTripList)

        changeEditMode()
    }

    suspend fun updateAppUiStateFromRepository(){
        val newTripList = tripsRepository.getAllTripsStream().firstOrNull()
        initTripListState(newTripList)
    }

    suspend fun updateTempTripId(updateSlideState: (tripId: Int) -> Unit){
        if (_appUiState.value.tempTripList != null) {
            _appUiState.value.tempTripList!!.forEach {
                //???????

                tripsRepository.updateTrip(it)
                updateSlideState(it.id)
            }
        }
    }
}