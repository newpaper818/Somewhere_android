package com.newpaper.somewhere.core.google_map_places.dataSource

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.newpaper.somewhere.core.model.data.LocationInfo

interface PlacesRemoteDataSource {
    suspend fun searchPlaces(
        query: String
    ): List<LocationInfo>?

    suspend fun getLatLngFromPlaceId(
        placeId: String
    ): LatLng?

    suspend fun getPlacesInfo(
        places: Set<String>
    ): Set<Pair<String, Place>>?
}