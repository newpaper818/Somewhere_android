package com.newpaper.somewhere.core.data.moshi.di

import com.newpaper.somewhere.core.data.moshi.MoshiApi
import com.newpaper.somewhere.core.data.moshi.SerializationDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class SerializationModule {
    @Binds
    internal abstract fun bindSerializationDataSource(
        moshiApi: MoshiApi
    ): SerializationDataSource
}