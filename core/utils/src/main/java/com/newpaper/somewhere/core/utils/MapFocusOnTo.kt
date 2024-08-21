package com.newpaper.somewhere.core.utils

import androidx.compose.ui.unit.IntSize
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.somewhere.core.model.tripData.Spot
import kotlin.math.min

/**
 * focus on to spotList's spot.
 * if one spot in spotList, focus to this.
 * if 2 or more in spotList, focus to all spots
 *
 * @param cameraPositionState
 * @param mapSize
 * @param spotList
 * @param density
 */
suspend fun focusOnToSpots(
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    spotList: List<Spot>,
    density: Float? = null
){
    if (spotList.size == 1){
        val spot = spotList.first()

        if (spot.location != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(spot.location!!, spot.zoomLevel ?: DEFAULT_ZOOM_LEVEL), ANIMATION_DURATION_MS
            )
        }
    }
    else if (spotList.size > 1){
        val latLngBounds = LatLngBounds.Builder()
        var spotCnt = 0

        for(spot in spotList){
            if (spot.location != null){
                spotCnt++
                latLngBounds.include(spot.location!!)
            }
        }

        if (spotCnt >= 2) {
            //button height + button bottom padding + icon height / 2 + padding
            val bottomButtonHeight = 52 + 16 + 15 + 2
            val defaultPadding = if (density != null) (bottomButtonHeight * density * 2).toInt()
                                    else                230 * 2
            val mapHeight = if (density != null) (mapSize.height - (bottomButtonHeight * density * 2)).toInt()
                            else                 mapSize.height

            var padding = min(min(mapHeight, mapSize.width) / 2, defaultPadding) / 2

            if (density != null && padding < defaultPadding / 2) padding = defaultPadding / 2

            //if map screen is not small
            if (padding > 10) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding), ANIMATION_DURATION_MS
                )
            }
        }
    }
}

suspend fun focusOnToLatLng(
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    locationList: List<LatLng?>,
    zoomLevel: Float = 13f
){
    val newLocationList: List<LatLng> = locationList.filterNotNull()

    if (newLocationList.size == 1){
        val location = newLocationList.first()

        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(location, zoomLevel), ANIMATION_DURATION_MS
        )
    }
    else if (newLocationList.size > 1){
        val latLngBounds = LatLngBounds.Builder()


        for(location in newLocationList){
            latLngBounds.include(location)
        }

        val mapSizeMin = min(mapSize.width, mapSize.height)

        val padding =
            if (mapSizeMin * 0.2 > LAT_LNG_BOUND_PADDING) LAT_LNG_BOUND_PADDING
            else                                            (mapSizeMin * 0.2).toInt()

        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding), ANIMATION_DURATION_MS
        )
    }
}

suspend fun focusOnToSpotForTripMap(
    mapSize: IntSize,   //exclude map padding
    cameraPositionState: CameraPositionState,
    spotList: List<Spot>
){
    //focus to 1 spot
    if (spotList.size == 1){
        val spot = spotList.first()

        if (spot.location != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(spot.location!!, spot.zoomLevel ?: DEFAULT_ZOOM_LEVEL), ANIMATION_DURATION_MS
            )
        }
    }

    //focus to 2 or more spot
    else if (spotList.size > 1){
        val latLngBounds = LatLngBounds.Builder()
        var spotCnt = 0

        for(spot in spotList){
            if (spot.location != null){
                spotCnt++
                latLngBounds.include(spot.location!!)
            }
        }

        if (spotCnt >= 2) {
            //calc padding using map size
            val mapSizeMin = min(mapSize.width, mapSize.height)

            val padding =
                if (mapSizeMin * 0.2 > LAT_LNG_BOUND_PADDING) LAT_LNG_BOUND_PADDING
                else                                            (mapSizeMin * 0.2).toInt()

            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding), ANIMATION_DURATION_MS
            )
        }
    }
}



