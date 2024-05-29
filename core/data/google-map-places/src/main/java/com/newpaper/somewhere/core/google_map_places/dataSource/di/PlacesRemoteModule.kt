package com.newpaper.somewhere.core.google_map_places.dataSource.di

import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesGoogleMapPlacesApi
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class PlacesRemoteModule {
    @Binds
    internal abstract fun bindCommonDataSource(
        googleMapFindPlacesApi: PlacesGoogleMapPlacesApi
    ): PlacesRemoteDataSource
}