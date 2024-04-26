package com.newpaper.somewhere.core.datastore.dataSource.di

import com.newpaper.somewhere.core.datastore.dataSource.SettingDataStoreApi
import com.newpaper.somewhere.core.datastore.dataSource.SettingLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SettingLocalModule {
    @Binds
    internal abstract fun bindSettingDataSource(
        settingDataStoreApi: SettingDataStoreApi
    ): SettingLocalDataSource
}