package com.newpaper.somewhere.core.model.tripData

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.model.enums.SpotType
import java.time.LocalDate
import java.time.LocalTime

data class Spot(
    val id: Int,
    val googleMapPlacesId: String? = null,
    var index: Int = 0,
    var iconText: Int = 0,

    val spotType: SpotType = SpotType.TOUR,
    @DrawableRes
    val iconId: Int? = null,
    @ColorInt
    val iconColor: Int? = null,
    @ColorInt
    val iconBackgroundColor: Int = 0xffffffff.toInt(),

    var date: LocalDate,

    val location: LatLng? = null,
    val zoomLevel: Float? = null,

    val titleText: String? = null,
    val imagePathList: List<String> = listOf(),

    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    val budget: Float = 0.0f,
    val travelDistance: Float = 0.0f,
    val memoText: String? = null
): Cloneable {
    public override fun clone(): Spot {
        return Spot(
            id = id,
            googleMapPlacesId = googleMapPlacesId,
            index = index,
            iconText = iconText,
            spotType = spotType,
            iconId = iconId,
            iconColor = iconColor,
            iconBackgroundColor = iconBackgroundColor,
            date = date,
            location = location,
            zoomLevel = zoomLevel,
            titleText= titleText,
            imagePathList = imagePathList,
            startTime = startTime,
            endTime = endTime,
            budget = budget,
            travelDistance = travelDistance,
            memoText = memoText
        )
    }
}