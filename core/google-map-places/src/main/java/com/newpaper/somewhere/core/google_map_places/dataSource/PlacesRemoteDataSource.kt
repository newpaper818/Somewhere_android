package com.newpaper.somewhere.core.google_map_places.dataSource

import android.content.Context
import com.newpaper.somewhere.core.model.data.LocationInfo

interface PlacesRemoteDataSource {
    suspend fun searchPlaces(
        context: Context,
        query: String
    ): List<LocationInfo>?
}