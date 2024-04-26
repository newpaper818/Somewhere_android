package com.newpaper.somewhere.core.model.data

import com.google.android.gms.maps.model.LatLng

data class LocationInfo(
    val title: String,
    val address: String,
    val placeId: String,
    var location: LatLng? = null
)