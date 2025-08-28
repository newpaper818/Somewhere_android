package com.newpaper.somewhere.feature.more.setTheme

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.PreferencesRepository
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.MapTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetThemeViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {

    suspend fun saveThemePreferences(
        useBlurEffect: Boolean?,
        appTheme: AppTheme?,
        mapTheme: MapTheme?
    ){
        if (useBlurEffect != null) {
            preferencesRepository.saveUseBlurEffectPreference(useBlurEffect)
        }
        else if (appTheme != null) {
            preferencesRepository.saveAppThemePreference(appTheme)
        }
        else if (mapTheme != null) {
            preferencesRepository.saveMapThemePreference(mapTheme)
        }
    }
}