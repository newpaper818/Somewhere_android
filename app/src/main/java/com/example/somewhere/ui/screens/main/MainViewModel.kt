package com.example.somewhere.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somewhere.db.TripRepository
import com.example.somewhere.model.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

data class MainUiState(
    val tripList: List<Trip> = listOf()
)

class MainViewModel(
    private val tripsRepository: TripRepository
) : ViewModel() {
    val mainUiState: StateFlow<MainUiState> =
        tripsRepository.getAllTripsStream()
            .map { MainUiState(tripList = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MainUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


    suspend fun addAndGetNewTrip(): Trip?{
        val nowTime = LocalDateTime.now()
        addNewTrip(nowTime)
        return getTrip(nowTime)
    }

    suspend fun addNewTrip(nowTime: LocalDateTime){
        tripsRepository.insertTrip(Trip(firstCreatedTime = nowTime, lastModifiedTime = nowTime))
    }

    suspend fun getTrip(firstCreatedTime: LocalDateTime): Trip?{
        return tripsRepository.getTripStream(firstCreatedTime)
            .first()
    }

    suspend fun deleteTrip(trip: Trip){
        tripsRepository.deleteTrip(trip)
    }
}

