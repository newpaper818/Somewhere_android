package com.newpaper.somewhere.db

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.newpaper.somewhere.enumUtils.AppTheme
import com.newpaper.somewhere.enumUtils.DateFormat
import com.newpaper.somewhere.enumUtils.MapTheme
import com.newpaper.somewhere.enumUtils.TimeFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val APP_THEME = intPreferencesKey("app_theme")
        val MAP_THEME = intPreferencesKey("map_theme")

        val DATE_FORMAT = intPreferencesKey("date_format")
        val DATE_USE_MONTH_NAME = booleanPreferencesKey("date_use_month_name")
        val DATE_INCLUDE_DAY_OF_WEEK = booleanPreferencesKey("date_include_day_of_week")
        val TIME_FORMAT = intPreferencesKey("time_format")

        const val TAG = "UserPreferencesRepo"
    }


    val appTheme: Flow<Int> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[APP_THEME] ?: AppTheme.AUTO.ordinal
        }

    val mapTheme: Flow<Int> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[MAP_THEME] ?: MapTheme.AUTO.ordinal
        }

    val dateFormat: Flow<Int> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DATE_FORMAT] ?: DateFormat.YMD.ordinal
        }

    val dateUseMonthName: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DATE_USE_MONTH_NAME] ?: false
        }

    val dateIncludeDayOfWeek: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DATE_INCLUDE_DAY_OF_WEEK] ?: false
        }

    val timeFormat: Flow<Int> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[TIME_FORMAT] ?: TimeFormat.T24H.ordinal
        }





    //save =========================================================================================
    suspend fun saveAppThemePreference(appTheme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[APP_THEME] = appTheme.ordinal
        }
    }

    suspend fun saveMapThemePreference(mapTheme: MapTheme) {
        dataStore.edit { preferences ->
            preferences[MAP_THEME] = mapTheme.ordinal
        }
    }

    suspend fun saveDateFormatPreference(dateFormat: DateFormat) {
        dataStore.edit { preferences ->
            preferences[DATE_FORMAT] = dateFormat.ordinal
        }
    }

    suspend fun saveDateUseMonthNamePreference(useMonthName: Boolean) {
        dataStore.edit { preferences ->
            preferences[DATE_USE_MONTH_NAME] = useMonthName
        }
    }

    suspend fun saveDateIncludeDayOfWeekPreference(includeDayOfWeek: Boolean) {
        dataStore.edit { preferences ->
            preferences[DATE_INCLUDE_DAY_OF_WEEK] = includeDayOfWeek
        }
    }

    suspend fun saveTimeFormatPreference(timeFormat: TimeFormat) {
        dataStore.edit { preferences ->
            preferences[TIME_FORMAT] = timeFormat.ordinal
        }
    }
}