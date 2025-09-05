package com.newpaper.somewhere.feature.trip.tripMap

import androidx.compose.ui.unit.IntSize
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.somewhere.core.model.data.DateWithBoolean
import com.newpaper.somewhere.core.model.data.SpotTypeGroupWithBoolean
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.utils.fitBoundsToMarkersForTripMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun fitBoundsToMarkers(
    mapSize: IntSize,   //map size without padding
    coroutineScope: CoroutineScope,
    dateListWithShownMarkerList: List<DateWithBoolean>,
    spotTypeWithShownMarkerList: List<SpotTypeGroupWithBoolean>,
    cameraPositionState: CameraPositionState,
){
    //get spot list
    val spotList: MutableList<Spot> = mutableListOf()

    for (dateWithBoolean in dateListWithShownMarkerList) {
        if (dateWithBoolean.isShown) {
            for (spot in dateWithBoolean.date.spotList) {
                if (SpotTypeGroupWithBoolean(spot.spotType.group, true or false) in spotTypeWithShownMarkerList &&
                    spot.location != null
                ) {
                    spotList.add(spot)
                }
            }
        }
    }

    //fit bounds to markers
    coroutineScope.launch {
        fitBoundsToMarkersForTripMap(
            mapSize, cameraPositionState, spotList.toList()
        )
    }
}