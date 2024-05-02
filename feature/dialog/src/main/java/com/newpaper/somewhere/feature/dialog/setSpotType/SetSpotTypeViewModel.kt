package com.newpaper.somewhere.feature.dialog.setSpotType

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.trip.TripRepository
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetSpotTypeUiState(
    val currentSpotTypeGroup: SpotTypeGroup = SpotType.TOUR.group,
    val currentSpotType: SpotType = SpotType.TOUR
)

@HiltViewModel
class SetSpotTypeViewModel @Inject constructor(
    private val tripRepository: TripRepository
): ViewModel() {

    private val _setSpotTypeUiState: MutableStateFlow<SetSpotTypeUiState> =
        MutableStateFlow(
            SetSpotTypeUiState(
                currentSpotTypeGroup = SpotType.TOUR.group,
                currentSpotType = SpotType.TOUR
            )
        )

    val setSpotTypeUiState = _setSpotTypeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            tripRepository.tempTripData.collect { tempTripData ->
                val spotType = tempTripData.dateList[0] ?: SpotType.TOUR

                _setSpotTypeUiState.update {
                    it.copy(
                        currentSpotTypeGroup = spotType.group,
                        currentSpotType = spotType
                    )
                }
            }
        }
    }











    fun updateSpotTypeGroup(spotTypeGroup: SpotTypeGroup){
        _setSpotTypeUiState.update {
            it.copy(
                currentSpotTypeGroup = spotTypeGroup,
                currentSpotType = spotTypeGroup.memberList.first()
            )
        }
    }

    fun updateSpotType(spotType: SpotType){
        _setSpotTypeUiState.update {
            it.copy(
                currentSpotType = spotType
            )
        }
    }
}