package com.newpaper.somewhere.core.firebase_authentication.dataSource.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.newpaper.somewhere.core.firebase_authentication.dataSource.UserAuthenticationApi
import com.newpaper.somewhere.core.firebase_authentication.dataSource.UserRemoteDataSource
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
abstract class UserRemoteModule {
    @Binds
    internal abstract fun bindAuthenticationDataSource(
        userAuthenticationApi: UserAuthenticationApi
    ): UserRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(

    ): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideSignInClient(
        @ApplicationContext context: Context
    ): SignInClient {
        return Identity.getSignInClient(context)
    }
}