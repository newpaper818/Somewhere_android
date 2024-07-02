package com.newpaper.somewhere.feature.trip.date

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.model.tripData.Spot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class DateUiState(
    val isShowingDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showMemoDialog: Boolean = false,
    val showSetColorDialog: Boolean = false,
    val showSetTimeDialog: Boolean = false,
    val showSetSpotTypeDialog: Boolean = false,

    val selectedSpot: Spot? = null, //for set color dialog

    val totalErrorCount: Int = 0,
    val spotTitleErrorCount: Int = 0,
)

@HiltViewModel
class DateViewModel @Inject constructor(

): ViewModel() {

    private val _dateUiState: MutableStateFlow<DateUiState> =
        MutableStateFlow(DateUiState())

    val dateUiState = _dateUiState.asStateFlow()




    private fun setIsShowingDialog(){
        val isShowingDialog = _dateUiState.value.showExitDialog ||
            _dateUiState.value.showMemoDialog || _dateUiState.value.showSetColorDialog ||
            _dateUiState.value.showSetTimeDialog || _dateUiState.value.showSetSpotTypeDialog

        _dateUiState.update {
            it.copy(isShowingDialog = isShowingDialog)
        }
    }


    //dialog
    fun setShowExitDialog(showExitDialog: Boolean) {
        _dateUiState.update {
            it.copy(showExitDialog = showExitDialog)
        }
        setIsShowingDialog()
    }

    fun setShowMemoDialog(showMemoDialog: Boolean) {
        _dateUiState.update {
            it.copy(showMemoDialog = showMemoDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetColorDialog(showSetColorDialog: Boolean) {
        _dateUiState.update {
            it.copy(showSetColorDialog = showSetColorDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetTimeDialog(showSetTimeDialog: Boolean) {
        _dateUiState.update {
            it.copy(showSetTimeDialog = showSetTimeDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetSpotTypeDialog(showSetSpotTypeDialog: Boolean) {
        _dateUiState.update {
            it.copy(showSetSpotTypeDialog = showSetSpotTypeDialog)
        }
        setIsShowingDialog()
    }

    fun setSelectedSpot(selectedSpot: Spot?) {
        _dateUiState.update {
            it.copy(selectedSpot = selectedSpot)
        }
    }

    //error count
    fun initAllErrorCount(){
        _dateUiState.update {
            it.copy(totalErrorCount = 0, spotTitleErrorCount = 0)
        }
    }

    fun increaseTotalErrorCount(){
        _dateUiState.update {
            it.copy(totalErrorCount = it.totalErrorCount + 1)
        }
    }

    fun decreaseTotalErrorCount(){
        _dateUiState.update {
            it.copy(totalErrorCount = it.totalErrorCount - 1)
        }
    }

    fun increaseSpotTitleErrorCount(){
        _dateUiState.update {
            it.copy(spotTitleErrorCount = it.spotTitleErrorCount + 1)
        }
    }

    fun decreaseSpotTitleErrorCount(){
        _dateUiState.update {
            it.copy(spotTitleErrorCount = it.spotTitleErrorCount - 1)
        }
    }

}