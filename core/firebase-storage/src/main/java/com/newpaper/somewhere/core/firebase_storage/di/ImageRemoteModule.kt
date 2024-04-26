package com.newpaper.somewhere.core.firebase_storage.di

import com.newpaper.somewhere.core.firebase_storage.dataSource.ImageRemoteDataSource
import com.newpaper.somewhere.core.firebase_storage.dataSource.ImageStorageApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ImageRemoteModule {
    @Binds
    internal abstract fun bindCommonDataSource(
        imageStorageApi: ImageStorageApi
    ): ImageRemoteDataSource
}