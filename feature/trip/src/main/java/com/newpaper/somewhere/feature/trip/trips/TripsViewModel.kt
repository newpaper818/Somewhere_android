package com.newpaper.somewhere.feature.trip.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.TripsRepository
import com.newpaper.somewhere.core.model.tripData.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class TripsUiState(
    val trips: List<Trip>,
    val tempTrips: List<Trip>
)

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripsRepository: TripsRepository
): ViewModel()  {
    private val _tripsUiState: MutableStateFlow<TripsUiState> =
        MutableStateFlow(
            TripsUiState(
                trips = emptyList(),
                tempTrips = emptyList()
            )
        )

    val tripsUiState = _tripsUiState.asStateFlow()


    fun getMyTrips(
        internetEnabled: Boolean,
        userId: String
    ){
        viewModelScope.launch {
            val newTrips = tripsRepository.getMyTrips(
                internetEnabled = internetEnabled,
                userId = userId
            )

            if (newTrips != null){
                _tripsUiState.update {
                    it.copy(
                        trips = newTrips, tempTrips = newTrips
                    )
                }
            }
            else {
                //error
            }

        }
    }
}