package com.newpaper.somewhere.core.data.repository

import com.newpaper.somewhere.core.datastore.dataSource.PreferencesLocalDataSource
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.TimeFormat
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    private val preferencesLocalDataSource: PreferencesLocalDataSource
) {
    suspend fun getAppPreferencesValue(
        onGet: (Theme, DateTimeFormat) -> Unit
    ) {
        preferencesLocalDataSource.getAppPreferencesValue(onGet = onGet)
    }



    suspend fun saveUseBlurEffectPreference(
        useBlurEffect: Boolean
    ){
        preferencesLocalDataSource.saveUseBlurEffectPreference(useBlurEffect = useBlurEffect)
    }

    suspend fun saveAppThemePreference(
        appTheme: AppTheme
    ) {
        preferencesLocalDataSource.saveAppThemePreference(appTheme = appTheme)
    }

    suspend fun saveMapThemePreference(
        mapTheme: MapTheme
    ) {
        preferencesLocalDataSource.saveMapThemePreference(mapTheme = mapTheme)
    }

    suspend fun saveDateFormatPreference(
        dateFormat: DateFormat
    ) {
        preferencesLocalDataSource.saveDateFormatPreference(dateFormat = dateFormat)
    }

    suspend fun saveDateUseMonthNamePreference(
        useMonthName: Boolean
    ) {
        preferencesLocalDataSource.saveDateUseMonthNamePreference(useMonthName = useMonthName)
    }

    suspend fun saveDateIncludeDayOfWeekPreference(
        includeDayOfWeek: Boolean
    ) {
        preferencesLocalDataSource.saveDateIncludeDayOfWeekPreference(includeDayOfWeek = includeDayOfWeek)
    }

    suspend fun saveTimeFormatPreference(
        timeFormat: TimeFormat
    ) {
        preferencesLocalDataSource.saveTimeFormatPreference(timeFormat = timeFormat)
    }

}