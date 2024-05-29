package com.newpaper.somewhere.core.local_image_file.dataSource.di

import com.newpaper.somewhere.core.local_image_file.dataSource.ImageLocalApi
import com.newpaper.somewhere.core.local_image_file.dataSource.ImageLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ImageLocalModule {
    @Binds
    internal abstract fun bindCommonDataSource(
        imageLocalApi: ImageLocalApi
    ): ImageLocalDataSource
}