package com.newpaper.somewhere.core.datastore.dataSource.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.newpaper.somewhere.core.datastore.dataSource.PreferencesDataStoreApi
import com.newpaper.somewhere.core.datastore.dataSource.PreferencesLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class PreferencesLocalModule {
    @Binds
    internal abstract fun bindSettingDataSource(
        settingDataStoreApi: PreferencesDataStoreApi
    ): PreferencesLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    @Provides
    @Singleton
    fun provideDatastorePreferences(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> {
        return applicationContext.dataStore
    }
}