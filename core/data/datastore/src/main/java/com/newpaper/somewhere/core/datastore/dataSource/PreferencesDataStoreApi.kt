package com.newpaper.somewhere.core.datastore.dataSource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.TimeFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val DATA_STORE_TAG = "DataStore"

class PreferencesDataStoreApi @Inject constructor(
    private val dataStore: DataStore<Preferences>
): PreferencesLocalDataSource {
    private companion object {
        val USE_BLUR_EFFECT = booleanPreferencesKey("use_blur_effect")
        val APP_THEME = intPreferencesKey("app_theme")
        val MAP_THEME = intPreferencesKey("map_theme")

        val DATE_FORMAT = intPreferencesKey("date_format")
        val DATE_USE_MONTH_NAME = booleanPreferencesKey("date_use_month_name")
        val DATE_INCLUDE_DAY_OF_WEEK = booleanPreferencesKey("date_include_day_of_week")
        val TIME_FORMAT = intPreferencesKey("time_format")
    }

    private val useBlurEffect: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(DATA_STORE_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[USE_BLUR_EFFECT] ?: true
        }

    private val appTheme: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(DATA_STORE_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[APP_THEME] ?: AppTheme.AUTO.ordinal
        }

    private val mapTheme: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(DATA_STORE_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[MAP_THEME] ?: MapTheme.AUTO.ordinal
        }

    private val dateFormat: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(DATA_STORE_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DATE_FORMAT] ?: DateFormat.YMD.ordinal
        }

    private val dateUseMonthName: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(DATA_STORE_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DATE_USE_MONTH_NAME] ?: false
        }

    private val dateIncludeDayOfWeek: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(DATA_STORE_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DATE_INCLUDE_DAY_OF_WEEK] ?: false
        }

    private val timeFormat: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(DATA_STORE_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[TIME_FORMAT] ?: TimeFormat.T24H.ordinal
        }


















    override suspend fun getAppPreferencesValue(
        onGet: (Theme, DateTimeFormat) -> Unit
    ){

        //get data from dataStore
        //theme
        val useBlurEffect = useBlurEffect.firstOrNull() ?: true
        val appTheme = AppTheme.get(appTheme.firstOrNull() ?: AppTheme.AUTO.ordinal)
        val mapTheme = MapTheme.get(mapTheme.firstOrNull() ?: MapTheme.AUTO.ordinal)

        //date time format
        val dateFormat = DateFormat.get(dateFormat.firstOrNull() ?: DateFormat.YMD.ordinal)
        val useMonthName = dateUseMonthName.firstOrNull() ?: false
        val includeDayOfWeek = dateIncludeDayOfWeek.firstOrNull() ?: false
        val timeFormat = TimeFormat.get(timeFormat.firstOrNull() ?: TimeFormat.T24H.ordinal)

        onGet(
            Theme(useBlurEffect, appTheme, mapTheme),
            DateTimeFormat(timeFormat, useMonthName, includeDayOfWeek, dateFormat)
        )
    }


    override suspend fun saveUseBlurEffectPreference(useBlurEffect: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_BLUR_EFFECT] = useBlurEffect
        }
    }

    override suspend fun saveAppThemePreference(appTheme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[APP_THEME] = appTheme.ordinal
        }
    }

    override suspend fun saveMapThemePreference(mapTheme: MapTheme) {
        dataStore.edit { preferences ->
            preferences[MAP_THEME] = mapTheme.ordinal
        }
    }


    override suspend fun saveDateFormatPreference(dateFormat: DateFormat) {
        dataStore.edit { preferences ->
            preferences[DATE_FORMAT] = dateFormat.ordinal
        }
    }

    override suspend fun saveDateUseMonthNamePreference(useMonthName: Boolean) {
        dataStore.edit { preferences ->
            preferences[DATE_USE_MONTH_NAME] = useMonthName
        }
    }

    override suspend fun saveDateIncludeDayOfWeekPreference(includeDayOfWeek: Boolean) {
        dataStore.edit { preferences ->
            preferences[DATE_INCLUDE_DAY_OF_WEEK] = includeDayOfWeek
        }
    }

    override suspend fun saveTimeFormatPreference(timeFormat: TimeFormat) {
        dataStore.edit { preferences ->
            preferences[TIME_FORMAT] = timeFormat.ordinal
        }
    }
}