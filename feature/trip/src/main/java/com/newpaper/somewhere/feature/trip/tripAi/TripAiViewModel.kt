package com.newpaper.somewhere.feature.trip.tripAi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class TripAiUiState(
    val test: Boolean = false,
    val trip: String? = null
)

@HiltViewModel
class TripAiViewModel @Inject constructor(
    private val aiRepository: AiRepository
): ViewModel() {
    private val _tripAiUiState: MutableStateFlow<TripAiUiState> =
        MutableStateFlow(
            TripAiUiState()
        )

    val tripAiUiState = _tripAiUiState.asStateFlow()

    fun setAiCreatedTrip(
        trip: String?
    ){
        _tripAiUiState.update {
            it.copy(trip = trip)
        }
    }

    fun getAiCreatedTrip(

    ){
        viewModelScope.launch{
            val aiCreatedTrip = aiRepository.getAiCreatedTrip()
            setAiCreatedTrip(aiCreatedTrip)
        }
    }
}