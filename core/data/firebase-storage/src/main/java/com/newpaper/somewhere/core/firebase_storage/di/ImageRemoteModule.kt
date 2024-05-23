package com.newpaper.somewhere.core.firebase_storage.di

import com.google.firebase.storage.FirebaseStorage
import com.newpaper.somewhere.core.firebase_storage.dataSource.ImageRemoteDataSource
import com.newpaper.somewhere.core.firebase_storage.dataSource.ImageStorageApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class ImageRemoteModule {
    @Binds
    internal abstract fun bindCommonDataSource(
        imageStorageApi: ImageStorageApi
    ): ImageRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseStorageModule {
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

}