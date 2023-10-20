package com.example.somewhere.viewModel

import androidx.lifecycle.ViewModel
import com.example.somewhere.model.Date
import com.example.somewhere.model.Trip
import com.example.somewhere.enumUtils.SpotTypeGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DateWithBoolean(
    val date: Date,
    val isShown: Boolean
)

data class SpotTypeGroupWithBoolean(
    val spotTypeGroup: SpotTypeGroup,
    val isShown: Boolean
)

data class TripMapUiState(
    val currentDateIndex: Int = 0,
    val oneDateShown: Boolean = false,

    val currentSpotIndex: Int = 0,
    val oneSpotShown: Boolean = false,

    val focusOnToSpotEnabled: Boolean = true,

    val dateWithShownMarkerList: List<DateWithBoolean> = listOf(),
    val spotTypeGroupWithShownMarkerList: List<SpotTypeGroupWithBoolean> = listOf()
)

class TripMapViewModel(
    private val trip: Trip
): ViewModel() {

    private val _tripMapUiState = MutableStateFlow(TripMapUiState())
    val tripMapUiState: StateFlow<TripMapUiState> = _tripMapUiState.asStateFlow()



    init{
        initDateListWithShownMarkerList()
        initSpotTypeGroupWithShownMarkerList()
        checkOnlyOneCurrentDateShown()
    }


    private fun initDateListWithShownMarkerList(){
        val newList: MutableList<DateWithBoolean> = mutableListOf()

        for (date in trip.dateList){
            newList.add(DateWithBoolean(date, true))
        }

        _tripMapUiState.update {
            it.copy(dateWithShownMarkerList = newList.toList())
        }
    }

    private fun initSpotTypeGroupWithShownMarkerList() {
        val newList: MutableList<SpotTypeGroupWithBoolean> = mutableListOf()

        val spotTypeGroupList = enumValues<SpotTypeGroup>()

        for (spotTypeGroup in spotTypeGroupList) {
            if (spotTypeGroup != SpotTypeGroup.MOVE)    //not include MOVE
                newList.add(SpotTypeGroupWithBoolean(spotTypeGroup, true))
        }

        _tripMapUiState.update {
            it.copy(spotTypeGroupWithShownMarkerList = newList.toList())
        }
    }








    private fun checkFocusOnToSpotEnabled() {
        val dateWithShownMarkerList = tripMapUiState.value.dateWithShownMarkerList
        val spotTypeGroupWithShownMarkerList = tripMapUiState.value.spotTypeGroupWithShownMarkerList

        for (dateWithBoolean in dateWithShownMarkerList){
            if (dateWithBoolean.isShown) {
                for (spot in dateWithBoolean.date.spotList) {
                    if (SpotTypeGroupWithBoolean(spot.spotType.group, true or false) in spotTypeGroupWithShownMarkerList &&
                        spot.location != null){

                        _tripMapUiState.update {
                            it.copy(focusOnToSpotEnabled = true)
                        }
                        return
                    }
                }
            }
        }

        _tripMapUiState.update {
            it.copy(focusOnToSpotEnabled = false)
        }
    }

    private fun checkOnlyOneCurrentDateShown(){
        val dateWithShownMarkerList = tripMapUiState.value.dateWithShownMarkerList
        val currentDateIndex = tripMapUiState.value.currentDateIndex

        for (dateWithBoolean in dateWithShownMarkerList){
            if (dateWithShownMarkerList.indexOf(dateWithBoolean) == currentDateIndex && !dateWithBoolean.isShown) {
                _tripMapUiState.update {
                    it.copy(oneDateShown = false)
                }
                return
            }
            else if (dateWithShownMarkerList.indexOf(dateWithBoolean) != currentDateIndex && dateWithBoolean.isShown){
                _tripMapUiState.update {
                    it.copy(oneDateShown = false)
                }
                return
            }
        }

        _tripMapUiState.update {
            it.copy(oneDateShown = true)
        }
    }

    private fun checkOnlyOneCurrentSpotShown(){
        val dateWithShownMarkerList = tripMapUiState.value.dateWithShownMarkerList
        val currentDateIndex = tripMapUiState.value.currentDateIndex

        for (dateWithBoolean in dateWithShownMarkerList){
            if (dateWithShownMarkerList.indexOf(dateWithBoolean) == currentDateIndex && !dateWithBoolean.isShown) {
                _tripMapUiState.update {
                    it.copy(oneDateShown = false)
                }
                return
            }
            else if (dateWithShownMarkerList.indexOf(dateWithBoolean) != currentDateIndex && dateWithBoolean.isShown){
                _tripMapUiState.update {
                    it.copy(oneDateShown = false)
                }
                return
            }
        }

        _tripMapUiState.update {
            it.copy(oneDateShown = true)
        }
    }




    fun toggleOneDateShown(date: Date){
        val dateWithShownMarkerList = tripMapUiState.value.dateWithShownMarkerList

        val newList = dateWithShownMarkerList.map {
            if (it.date == date) DateWithBoolean(it.date, !it.isShown)
            else it
        }

        _tripMapUiState.update {
            it.copy(dateWithShownMarkerList = newList.toList())
        }

        //check is only one date shown
        val isShownList = newList.filter { it.isShown }

        if (isShownList.size == 1){
            val newCurrentDateIndex = newList.indexOf(isShownList.first())

            _tripMapUiState.update {
                it.copy(currentDateIndex = newCurrentDateIndex)
            }
        }

        checkFocusOnToSpotEnabled()
        checkOnlyOneCurrentDateShown()
    }

    fun toggleSpotTypeGroupWithShownMarkerList(spotTypeGroup: SpotTypeGroup){
        val spotTypeGroupWithShownMarkerList = tripMapUiState.value.spotTypeGroupWithShownMarkerList

        val newList = spotTypeGroupWithShownMarkerList.map {
            if (it.spotTypeGroup == spotTypeGroup) SpotTypeGroupWithBoolean(it.spotTypeGroup, !it.isShown)
            else it
        }

        _tripMapUiState.update {
            it.copy(spotTypeGroupWithShownMarkerList = newList.toList())
        }

        checkFocusOnToSpotEnabled()
    }

    fun updateDateWithShownMarkerListToCurrentDate(): List<DateWithBoolean> {
        val dateWithShownMarkerList = tripMapUiState.value.dateWithShownMarkerList
        val currentDateIndex = tripMapUiState.value.currentDateIndex

        val newList = dateWithShownMarkerList.map {
            if (dateWithShownMarkerList.indexOf(it) == currentDateIndex)
                DateWithBoolean(it.date, true)
            else DateWithBoolean(it.date, false)
        }

        _tripMapUiState.update {
            it.copy(dateWithShownMarkerList = newList.toList())
        }

        checkFocusOnToSpotEnabled()
        checkOnlyOneCurrentDateShown()

        return newList
    }

    fun currentDateIndexToPrevious(): Int{
        val dateWithShownMarkerList = tripMapUiState.value.dateWithShownMarkerList
        val currentDateIndex = tripMapUiState.value.currentDateIndex


        val newCurrentDateIndex =
            if (currentDateIndex == 0)
                dateWithShownMarkerList.indexOf(dateWithShownMarkerList.last())
            else
                currentDateIndex - 1


        _tripMapUiState.update {
            it.copy(currentDateIndex = newCurrentDateIndex)
        }

        return newCurrentDateIndex
    }

    fun currentDateIndexToNext(): Int{
        val dateWithShownMarkerList = tripMapUiState.value.dateWithShownMarkerList
        val currentDateIndex = tripMapUiState.value.currentDateIndex


        val newCurrentDateIndex =
            if (currentDateIndex == dateWithShownMarkerList.indexOf(dateWithShownMarkerList.last()))
                0
            else
                currentDateIndex + 1


        _tripMapUiState.update {
            it.copy(currentDateIndex = newCurrentDateIndex)
        }

        return newCurrentDateIndex
    }

}