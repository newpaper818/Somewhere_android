package com.example.somewhere.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somewhere.db.TripRepository
import com.example.somewhere.db.UserPreferencesRepository
import com.example.somewhere.enumUtils.DateFormat
import com.example.somewhere.enumUtils.TimeFormat
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

data class DateTimeFormat(
    val dateFormat: DateFormat = DateFormat.YMD,
    val useMonthName: Boolean = false,
    val includeDayOfWeek: Boolean = false,
    val timeFormat: TimeFormat = TimeFormat.T24H,
)

data class AppUiState(
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),

    val tripList: List<Trip>? = null,
    val tempTripList: List<Trip>? = null
)

class AppViewModel(
    private val tripsRepository: TripRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _appUiState = MutableStateFlow(AppUiState())
    val appUiState = _appUiState.asStateFlow()

    //is load repository done
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateAppUiStateFromRepository()
        }

        viewModelScope.launch {
            while (!_isReady.value) {
                if (_appUiState.value.tripList != null) {
                    updateUiState(true, _appUiState.value.tripList)
                    _isReady.value = true
                }
                delay(100)
            }
        }
    }


    //update UiState ===============================================================================
    /**update appUiState [tripList] to tripList and tempTripList*/
    private fun updateUiState(tripList: List<Trip>?){
        _appUiState.update {
            it.copy(tripList = tripList, tempTripList = tripList)
        }
    }

    /**update appUiState [tripList] and [tempTripList] to tripList and tempTripList*/
    private fun updateUiState(tripList: List<Trip>?, tempTripList: List<Trip>?){
        _appUiState.update {
            it.copy(tripList = tripList, tempTripList = tempTripList)
        }
    }

    /**update appUiState [tripList] to tripList or tempTripList*/
    private fun updateUiState(toTempTripList: Boolean, tripList: List<Trip>?){
        _appUiState.update {
            if (toTempTripList)     it.copy(tempTripList = tripList)
            else                    it.copy(tripList = tripList)
        }
    }

    //==============================================================================================

    fun saveUserPreferences(
        dateFormat: DateFormat? = null,
        useMonthName: Boolean? = null,
        includeDayOfWeek: Boolean? = null,
        timeFormat: TimeFormat? = null
    ){
        if (dateFormat != null && dateFormat != appUiState.value.dateTimeFormat.dateFormat) {
            _appUiState.update { it.copy(dateTimeFormat = it.dateTimeFormat.copy(dateFormat = dateFormat)) }
            viewModelScope.launch {
                userPreferencesRepository.saveDateFormatPreference(dateFormat)
            }
        }
        else if (useMonthName != null && useMonthName != appUiState.value.dateTimeFormat.useMonthName) {
            _appUiState.update { it.copy(dateTimeFormat = it.dateTimeFormat.copy(useMonthName = useMonthName)) }
            viewModelScope.launch {
                userPreferencesRepository.saveDateUseMonthNamePreference(useMonthName)
            }
        }
        else if (includeDayOfWeek != null && includeDayOfWeek != appUiState.value.dateTimeFormat.includeDayOfWeek) {
            _appUiState.update { it.copy(dateTimeFormat = it.dateTimeFormat.copy(includeDayOfWeek = includeDayOfWeek)) }
            viewModelScope.launch {
                userPreferencesRepository.saveDateIncludeDayOfWeekPreference(includeDayOfWeek)
            }
        }
        else if (timeFormat != null && timeFormat != appUiState.value.dateTimeFormat.timeFormat) {
            _appUiState.update { it.copy(dateTimeFormat = it.dateTimeFormat.copy(timeFormat = timeFormat)) }
            viewModelScope.launch {
                userPreferencesRepository.saveTimeFormatPreference(timeFormat)
            }
        }
    }

    suspend fun updateAppUiStateFromRepository(){
        val newTripList = tripsRepository.getAllTripsStream().firstOrNull()

        val newDateFormatInt = userPreferencesRepository.dateFormat.firstOrNull() ?: DateFormat.YMD.ordinal
        val newDateFormat = enumValues<DateFormat>().firstOrNull(){ it.ordinal == newDateFormatInt } ?: DateFormat.YMD

        val newUseMonthName = userPreferencesRepository.dateUseMonthName.firstOrNull() ?: false

        val newIncludeDayOfWeek = userPreferencesRepository.dateIncludeDayOfWeek.firstOrNull() ?: false

        val newTimeFormatInt = userPreferencesRepository.timeFormat.firstOrNull() ?: TimeFormat.T24H.ordinal
        val newTimeFormat = enumValues<TimeFormat>().firstOrNull(){ it.ordinal == newTimeFormatInt } ?: TimeFormat.T24H

        _appUiState.update {
            it.copy(
                dateTimeFormat = DateTimeFormat(newDateFormat, newUseMonthName, newIncludeDayOfWeek, newTimeFormat),
                tripList = newTripList,
                tempTripList = newTripList
            )
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

        //auto trip id update
        val newTrip = getTrip(nowTime)

        //update appUiState
        if (newTrip != null){
            //update appUiState
            val tripList = (_appUiState.value.tripList ?: listOf<Trip>()) .toMutableList()
            tripList.add(newTrip)

            val tempTripList = (_appUiState.value.tempTripList ?: listOf<Trip>()) .toMutableList()
            tempTripList.add(newTrip)

            updateUiState(tripList, tempTripList)
        }
    }

    private suspend fun getTrip(firstCreatedTime: LocalDateTime): Trip?{
        return tripsRepository.getTripStream(firstCreatedTime)
            .first()
    }







    //on cancel click
    fun unSaveTempTripList(){
        updateUiState(true, _appUiState.value.tripList)
    }

    //on save click
    suspend fun saveTempTripList(changeEditMode: () -> Unit){
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

        updateUiState(false, _appUiState.value.tempTripList)

        changeEditMode()
    }

    fun deleteTripFromTempTrip(trip: Trip){
        if (_appUiState.value.tempTripList != null) {
            val newTempTripList = _appUiState.value.tempTripList!!.toMutableList()
            newTempTripList.remove(trip)

            updateUiState(true, newTempTripList.toList())
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

            updateUiState(true, newTripList.toList())
        }
    }
}