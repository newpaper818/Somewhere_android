package com.newpaper.somewhere.feature.more.setTheme

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.SettingRepository
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.MapTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetThemeViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
): ViewModel() {
    //==============================================================================================
    //update and save setting values ===============================================================
    suspend fun saveThemeUserPreferences(
        appTheme: AppTheme?,
        mapTheme: MapTheme?
    ){
        if (appTheme != null) {
            settingRepository.saveAppThemePreference(appTheme)
        }
        else if (mapTheme != null) {
            settingRepository.saveMapThemePreference(mapTheme)
        }
    }
}