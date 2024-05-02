package com.newpaper.somewhere.core.firebase_authentication.dataSource.di

import com.newpaper.somewhere.core.firebase_authentication.dataSource.UserAuthenticationApi
import com.newpaper.somewhere.core.firebase_authentication.dataSource.UserRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserRemoteModule {
    @Binds
    internal abstract fun bindAuthenticationDataSource(
        userAuthenticationApi: UserAuthenticationApi
    ): UserRemoteDataSource
}