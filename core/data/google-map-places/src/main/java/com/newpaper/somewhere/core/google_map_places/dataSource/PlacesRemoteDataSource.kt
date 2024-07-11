package com.newpaper.somewhere.core.google_map_places.dataSource

import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.model.data.LocationInfo

interface PlacesRemoteDataSource {
    suspend fun searchPlaces(
        query: String
    ): List<LocationInfo>?

    suspend fun getLatLngFromPlaceId(
        placeId: String
    ): LatLng?
}