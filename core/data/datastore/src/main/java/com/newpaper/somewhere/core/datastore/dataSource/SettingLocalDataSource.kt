package com.newpaper.somewhere.core.datastore.dataSource

import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.TimeFormat

interface SettingLocalDataSource {

    /**
     * update app setting values - at app start
     */
    suspend fun getAppPreferencesValue(
        onGet: (Theme, DateTimeFormat) -> Unit
    )




    suspend fun saveAppThemePreference(appTheme: AppTheme)

    suspend fun saveMapThemePreference(mapTheme: MapTheme)


    suspend fun saveDateFormatPreference(dateFormat: DateFormat)

    suspend fun saveDateUseMonthNamePreference(useMonthName: Boolean)

    suspend fun saveDateIncludeDayOfWeekPreference(includeDayOfWeek: Boolean)

    suspend fun saveTimeFormatPreference(timeFormat: TimeFormat)
}