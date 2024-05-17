package com.newpaper.somewhere.core.data.repository

import com.newpaper.somewhere.core.datastore.dataSource.SettingLocalDataSource
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.TimeFormat
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val settingLocalDataSource: SettingLocalDataSource
) {
    suspend fun getAppPreferencesValue(
        onGet: (Theme, DateTimeFormat) -> Unit
    ) {
        settingLocalDataSource.getAppPreferencesValue(onGet = onGet)
    }


    suspend fun saveAppThemePreference(
        appTheme: AppTheme
    ) {
        settingLocalDataSource.saveAppThemePreference(appTheme = appTheme)
    }

    suspend fun saveMapThemePreference(
        mapTheme: MapTheme
    ) {
        settingLocalDataSource.saveMapThemePreference(mapTheme = mapTheme)
    }



    suspend fun saveDateFormatPreference(
        dateFormat: DateFormat
    ) {
        settingLocalDataSource.saveDateFormatPreference(dateFormat = dateFormat)
    }

    suspend fun saveDateUseMonthNamePreference(
        useMonthName: Boolean
    ) {
        settingLocalDataSource.saveDateUseMonthNamePreference(useMonthName = useMonthName)
    }

    suspend fun saveDateIncludeDayOfWeekPreference(
        includeDayOfWeek: Boolean
    ) {
        settingLocalDataSource.saveDateIncludeDayOfWeekPreference(includeDayOfWeek = includeDayOfWeek)
    }

    suspend fun saveTimeFormatPreference(
        timeFormat: TimeFormat
    ) {
        settingLocalDataSource.saveTimeFormatPreference(timeFormat = timeFormat)
    }

}