package com.newpaper.somewhere.feature.trip.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.trip.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripUiState(
    val loadingTrip: Boolean = true,
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
//            tripRepository.trip57Data.collect { tripData ->
//                _tripUiState.update {
//                    it.copy(
//                        trip = tripData,
//                        tempTrip = tripData
//                    )
//                }
//            }
        }
    }






    //==============================================================================================
    //set UiState ==================================================================================
    fun setLoadingTrip(
        isLoading: Boolean
    ){
        _tripUiState.update {
            it.copy(
                loadingTrip = isLoading
            )
        }
    }




    //==============================================================================================
    // ==================================================================================

    fun updateTrip(

    ){

    }
}