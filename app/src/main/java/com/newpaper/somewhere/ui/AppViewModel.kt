package com.newpaper.somewhere.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.SettingRepository
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.data.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AppUiState(
    val theme: Theme = Theme(),
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),

    val appUserData: UserData? = null,

    //FIXME
//    val startScreenDestination: ScreenDestination? = null,
//    val currentMainNavigationDestination: MainNavigationDestination = MyTripsMainDestination,
//    val currentScreenDestination: ScreenDestination = MyTripsScreenDestination,
)

class AppViewModel @Inject constructor(
    private val settingRepository: SettingRepository
): ViewModel() {
    private val _appUiState = MutableStateFlow(AppUiState())
    val appUiState = _appUiState.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateAppSettingValue()
        }
    }

    //update app setting values - at app start =====================================================
    private suspend fun updateAppSettingValue(){

        settingRepository.updateAppSettingValue { theme, dateTimeFormat ->
            _appUiState.update {
                it.copy(
                    theme = theme,
                    dateTimeFormat = dateTimeFormat
                )
            }
        }
    }

    //TODO save setting value

}