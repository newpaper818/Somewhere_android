package com.newpaper.somewhere.core.google_map_places.dataSource.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.newpaper.somewhere.core.data.google_map_places.BuildConfig
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesGoogleMapPlacesApi
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
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
abstract class PlacesRemoteModule {
    @Binds
    internal abstract fun bindCommonDataSource(
        googleMapFindPlacesApi: PlacesGoogleMapPlacesApi
    ): PlacesRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object PlacesClientModule {
    @Provides
    @Singleton
    fun providePlacesClient(
        @ApplicationContext context: Context
    ): PlacesClient {
        Places.initialize(context, BuildConfig.MAPS_API_KEY)
        return Places.createClient(context)
    }
}


