package com.example.somewhere.ui.screens.date

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somewhere.db.TripRepository
import com.example.somewhere.model.Date
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.screens.trip.TripDestination
import com.example.somewhere.ui.screens.trip.TripUiState
import com.example.somewhere.ui.screens.trip.toTripUiState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DateUiState(
    val date: Date = Date(date = LocalDate.now()),
    val tempDate: Date = Date(date = LocalDate.now())
)

class DateViewModel(
    savedStateHandle: SavedStateHandle,
    private val tripsRepository: TripRepository,
) : ViewModel() {

    private val dateId: Int = checkNotNull(savedStateHandle[TripDestination.tripIdArg])

    var dateUiState by mutableStateOf(DateUiState())
        private set

    init{
        viewModelScope.launch{
            dateUiState = tripsRepository.getTripStream(dateId)
                .first()
                //if null make new Trip()
                ?.toTripUiState() ?: Trip().toTripUiState()
        }
    }



}

