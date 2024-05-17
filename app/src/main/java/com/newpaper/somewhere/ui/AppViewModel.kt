package com.newpaper.somewhere.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.core.data.repository.SettingRepository
import com.newpaper.somewhere.core.data.repository.signIn.SignInRepository
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppPreferencesState(
    val theme: Theme = Theme(),
    val dateTimeFormat: DateTimeFormat = DateTimeFormat()
)

data class DestinationState(
    val startScreenDestination: ScreenDestination? = null, //if not null, splash screen will be finish
    val currentTopLevelDestination: TopLevelDestination = TopLevelDestination.TRIPS,
    val currentScreenDestination: ScreenDestination = ScreenDestination.TRIPS_ROUTE
)

data class AppUiState(
    val appUserData: UserData? = null,
    val appPreferences: AppPreferencesState = AppPreferencesState(),
    val screenDestination: DestinationState = DestinationState(),

    val firstLaunch: Boolean = true,
    val isEditMode: Boolean = false,
    val debugMode: Boolean = BuildConfig.DEBUG
)



@HiltViewModel
class AppViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val signInRepository: SignInRepository,
): ViewModel() {
    private val _appUiState = MutableStateFlow(AppUiState())
    val appUiState = _appUiState.asStateFlow()

    //==============================================================================================
    //update app setting values - at app start =====================================================
    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAppPreferencesValue()
        }
    }

    private suspend fun getAppPreferencesValue(){
        settingRepository.getAppPreferencesValue { theme, dateTimeFormat ->
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        theme = theme,
                        dateTimeFormat = dateTimeFormat
                    )
                )
            }
        }
    }









    //==============================================================================================
    //set uiState value ============================================================================
    fun firstLaunchToFalse() {
        _appUiState.update {
            it.copy(firstLaunch = false)
        }
    }

    /**
     *
     * @param editMode if null, toggle isEditMode value
     */
    fun setIsEditMode(
        editMode: Boolean?
    ){
        if (editMode != null){
            _appUiState.update {
                it.copy(isEditMode = editMode)
            }
        }
        else {
            _appUiState.update {
                it.copy(isEditMode = !it.isEditMode)
            }
        }
    }












    //==============================================================================================
    //at app start splash screen ===================================================================
    fun intiUserAndUpdateStartDestination (

    ){
        initSignedInUser(
            onDone = { userDataIsNull ->
                updateStartScreenDestination(userDataIsNull)
            }
        )
    }

    private fun initSignedInUser(
        onDone: (userDataIsNull: Boolean) -> Unit
    ){
        viewModelScope.launch {
            val userData = signInRepository.getSignedInUser()
            _appUiState.update {
                it.copy(appUserData = userData)
            }
            onDone(userData?.email == null)
        }
    }










    //==============================================================================================
    //update screen destination ====================================================================
    private fun updateStartScreenDestination(
        userDataIsNull: Boolean
    ){
        val startScreenDestination =
            if (userDataIsNull) ScreenDestination.SIGN_IN_ROUTE
            else ScreenDestination.TRIPS_ROUTE

        _appUiState.update {
            it.copy(
                screenDestination = it.screenDestination.copy(
                    startScreenDestination = startScreenDestination
                )
            )
        }
    }

    fun updateCurrentTopLevelDestination(topLevelDestination: TopLevelDestination){
        _appUiState.update {
            it.copy(
                screenDestination = it.screenDestination.copy(
                    currentTopLevelDestination = topLevelDestination
                )
            )
        }
    }

    fun updateCurrentScreenDestination(screenDestination: ScreenDestination) {
        _appUiState.update {
            it.copy(
                screenDestination = it.screenDestination.copy(
                    currentScreenDestination = screenDestination
                )
            )
        }
    }












    //==============================================================================================
    //update and save setting values ===============================================================
    fun saveThemeUserPreferences(
        appTheme: AppTheme? = null,
        mapTheme: MapTheme? = null
    ){
        if (appTheme != null && appTheme != appUiState.value.appPreferences.theme.appTheme){
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        theme = it.appPreferences.theme.copy(
                            appTheme = appTheme
                        )
                    )
                )
            }

            viewModelScope.launch {
                settingRepository.saveAppThemePreference(appTheme = appTheme)
            }
        }
        else if (mapTheme != null && mapTheme != appUiState.value.appPreferences.theme.mapTheme){
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        theme = it.appPreferences.theme.copy(
                            mapTheme = mapTheme
                        )
                    )
                )
            }
            viewModelScope.launch {
                settingRepository.saveMapThemePreference(mapTheme = mapTheme)
            }
        }
    }

    fun saveDateTimeFormatUserPreferences(
        dateFormat: DateFormat? = null,
        useMonthName: Boolean? = null,
        includeDayOfWeek: Boolean? = null,
        timeFormat: TimeFormat? = null
    ){
        if (dateFormat != null && dateFormat != appUiState.value.appPreferences.dateTimeFormat.dateFormat) {
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        dateTimeFormat = it.appPreferences.dateTimeFormat.copy(
                            dateFormat = dateFormat
                        )
                    )
                )
            }
            viewModelScope.launch {
                settingRepository.saveDateFormatPreference(dateFormat = dateFormat)
            }
        }
        else if (useMonthName != null && useMonthName != appUiState.value.appPreferences.dateTimeFormat.useMonthName) {
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        dateTimeFormat = it.appPreferences.dateTimeFormat.copy(
                            useMonthName = useMonthName
                        )
                    )
                )
            }
            viewModelScope.launch {
                settingRepository.saveDateUseMonthNamePreference(useMonthName = useMonthName)
            }
        }
        else if (includeDayOfWeek != null && includeDayOfWeek != appUiState.value.appPreferences.dateTimeFormat.includeDayOfWeek) {
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        dateTimeFormat = it.appPreferences.dateTimeFormat.copy(
                            includeDayOfWeek = includeDayOfWeek
                        )
                    )
                )
            }
            viewModelScope.launch {
                settingRepository.saveDateIncludeDayOfWeekPreference(includeDayOfWeek = includeDayOfWeek)
            }
        }
        else if (timeFormat != null && timeFormat != appUiState.value.appPreferences.dateTimeFormat.timeFormat) {
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        dateTimeFormat = it.appPreferences.dateTimeFormat.copy(
                            timeFormat = timeFormat
                        )
                    )
                )
            }
            viewModelScope.launch {
                settingRepository.saveTimeFormatPreference(timeFormat = timeFormat)
            }
        }
    }
}