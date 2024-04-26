package com.newpaper.somewhere.core.firebase_functions.dataSource.di

import com.newpaper.somewhere.core.firebase_functions.dataSource.RecursiveDeleteFunctionsApi
import com.newpaper.somewhere.core.firebase_functions.dataSource.RecursiveDeleteRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RecursiveDeleteRemoteModule {
    @Binds
    internal abstract fun bindCommonDataSource(
        recursiveDeleteFunctionsApi: RecursiveDeleteFunctionsApi
    ): RecursiveDeleteRemoteDataSource
}