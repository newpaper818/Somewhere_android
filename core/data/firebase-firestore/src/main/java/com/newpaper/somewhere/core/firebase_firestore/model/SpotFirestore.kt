package com.newpaper.somewhere.core.firebase_firestore.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.newpaper.somewhere.core.firebase_firestore.localTimeToString
import com.newpaper.somewhere.core.firebase_firestore.stringToLocalTime
import com.newpaper.somewhere.core.firebase_firestore.stringToSpotType
import com.newpaper.somewhere.core.model.tripData.Spot
import java.time.LocalDate

internal data class SpotFirestore(
    val id: Int = 0,
    val googleMapsPlacesId: String? = null,
    var index: Int = 0,
    var iconText: Int = 0,

    val spotType: String = "TOUR",

    var date: String = LocalDate.now().toString(),

    val location: GeoPoint? = null,
    val zoomLevel: Float? = null,

    val titleText: String? = null,
    val imagePathList: List<String> = listOf(),

    val startTime: String? = null,
    val endTime: String? = null,

    val budget: Double = 0.0,
    val travelDistance: Float = 0.0f,
    val memoText: String? = null
){
    fun toSpot(): Spot {
        return Spot(
            id = id,
            googleMapsPlacesId = googleMapsPlacesId,
            index = index,
            iconText = iconText,
            spotType = stringToSpotType(spotType),
            date = LocalDate.parse(date),
            location = location?.let { LatLng(it.latitude, it.longitude) },
            zoomLevel = zoomLevel,
            titleText = titleText,
            imagePathList = imagePathList,
            startTime = stringToLocalTime(startTime),
            endTime = stringToLocalTime(endTime),
            budget = budget,
            travelDistance = travelDistance,
            memoText = memoText
        )
    }
}

internal fun Spot.toSpotFirestore(): SpotFirestore =
    SpotFirestore(
        id = id,
        googleMapsPlacesId = googleMapsPlacesId,
        index = index,
        iconText = iconText,
        spotType = spotType.toString(),
        date = date.toString(),
        location = location?.let { GeoPoint(it.latitude, it.longitude) },
        zoomLevel = zoomLevel,
        titleText = titleText,
        imagePathList = imagePathList,
        startTime = localTimeToString(startTime),
        endTime = localTimeToString(endTime),
        budget = budget,
        travelDistance = travelDistance,
        memoText = memoText
    )




