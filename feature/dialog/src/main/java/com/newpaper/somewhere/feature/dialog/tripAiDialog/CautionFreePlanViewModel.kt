package com.newpaper.somewhere.feature.dialog.tripAiDialog

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CautionFreePlanUiState(
    val isCheckedIUnderstand: Boolean = false,
    val showLoading: Boolean = false
)

@HiltViewModel
class CautionFreePlanViewModel @Inject constructor(

): ViewModel() {
    private val _cautionFreePlanUiState: MutableStateFlow<CautionFreePlanUiState> =
        MutableStateFlow(
            CautionFreePlanUiState()
        )

    val cautionFreePlanUiState = _cautionFreePlanUiState.asStateFlow()

    fun toggleCheckedIUnderstand(){
        _cautionFreePlanUiState.update {
            it.copy(
                isCheckedIUnderstand = !it.isCheckedIUnderstand
            )
        }
    }

    fun setShowLoading(showLoading: Boolean) {
        _cautionFreePlanUiState.update {
            it.copy(
                showLoading = showLoading
            )
        }
    }
}