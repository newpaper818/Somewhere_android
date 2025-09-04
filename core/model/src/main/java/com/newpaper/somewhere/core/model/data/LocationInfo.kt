package com.newpaper.somewhere.core.model.data

import com.google.android.gms.maps.model.LatLng

data class LocationInfo(
    val title: String,
    val address: String,
    val placeId: String,
    val location: LatLng? = null,
    val mapMarkerIndex: String? = null
)