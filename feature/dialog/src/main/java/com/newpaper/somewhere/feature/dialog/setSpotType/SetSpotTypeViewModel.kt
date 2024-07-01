package com.newpaper.somewhere.feature.dialog.setSpotType

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.core.model.enums.getSpotTypeList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SetSpotTypeUiState(
    val currentSpotTypeGroup: SpotTypeGroup = SpotType.TOUR.group,
    val currentSpotType: SpotType = SpotType.TOUR
)

@HiltViewModel
class SetSpotTypeViewModel @Inject constructor(

): ViewModel() {

    private val _setSpotTypeUiState: MutableStateFlow<SetSpotTypeUiState> =
        MutableStateFlow(
            SetSpotTypeUiState(
                currentSpotTypeGroup = SpotType.TOUR.group,
                currentSpotType = SpotType.TOUR
            )
        )

    val setSpotTypeUiState = _setSpotTypeUiState.asStateFlow()







    fun initSetSpotTypeUiState(
        spotType: SpotType
    ){
        _setSpotTypeUiState.update {
            it.copy(
                currentSpotTypeGroup = spotType.group,
                currentSpotType = spotType
            )
        }
    }


    fun updateSpotTypeGroup(spotTypeGroup: SpotTypeGroup){
        _setSpotTypeUiState.update {
            it.copy(
                currentSpotTypeGroup = spotTypeGroup,
                currentSpotType = getSpotTypeList(spotTypeGroup).first()
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