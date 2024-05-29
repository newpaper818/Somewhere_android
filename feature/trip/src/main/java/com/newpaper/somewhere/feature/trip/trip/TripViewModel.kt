package com.newpaper.somewhere.feature.trip.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.trip.TripRepository
import com.newpaper.somewhere.core.model.tripData.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripUiState(
    val trip: Trip = Trip(
        id = 0,
        managerId = ""
    ),
    val tempTrip: Trip = Trip(
        id = 0,
        managerId = ""
    )
)

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
): ViewModel() {

    private val _tripUiState: MutableStateFlow<TripUiState> =
        MutableStateFlow(
            TripUiState()
        )

    val tripUiState = _tripUiState.asStateFlow()

    init {
        viewModelScope.launch {
//            tripRepository.tripData.collect { tripData ->
//                _tripUiState.update {
//                    it.copy(
//                        trip = tripData,
//                        tempTrip = tripData
//                    )
//                }
//            }
        }
    }



    fun saveTrip(

    ){
//        tripRepository.saveToOriginalTrip()
    }
}