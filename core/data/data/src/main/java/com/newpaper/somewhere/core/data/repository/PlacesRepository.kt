package com.newpaper.somewhere.core.data.repository

import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
import com.newpaper.somewhere.core.model.data.LocationInfo
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val placesRemoteDataSource: PlacesRemoteDataSource
) {
    suspend fun searchPlaces(
        query: String
    ): List<LocationInfo>? {
        return placesRemoteDataSource.searchPlaces(
            query = query
        )
    }

    suspend fun getLatLngFromPlaceId(
        placeId: String
    ): LatLng? {
        return placesRemoteDataSource.getLatLngFromPlaceId(
            placeId = placeId
        )
    }
}