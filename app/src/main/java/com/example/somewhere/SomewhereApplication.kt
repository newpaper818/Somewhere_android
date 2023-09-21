package com.example.somewhere

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.somewhere.db.AppContainer
import com.example.somewhere.db.AppDataContainer
import com.example.somewhere.db.UserPreferencesRepository


private const val USER_PREFERENCE_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCE_NAME
)

class SomewhereApplication : Application() {

    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}
