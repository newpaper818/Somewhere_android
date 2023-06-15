package com.example.somewhere.ui.screenUtils

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.model.Trip
import com.example.somewhere.typeUtils.SpotType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch

@Composable
fun MapForTrip(
    firstLaunch: Boolean,
    context: Context,
    cameraPositionState: CameraPositionState,
    uiSettings: MapUiSettings,
    properties: MapProperties,

    trip: Trip,
    dateListWithShownIconList: List<Pair<Date, Boolean>>,
    spotTypeWithShownIconList: List<Pair<SpotType, Boolean>>,

    modifier: Modifier = Modifier
){
    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ){
        var locationCnt = 0
        var location = LatLng(0.0, 0.0)

        val latLngBounds = LatLngBounds.Builder()

        for (date in dateListWithShownIconList){
            if (date.second){
                for (spot in date.first.spotList){

                    if (spot.location != null &&
                        spot.spotType != SpotType.MOVE &&
                        Pair(spot.spotType, true) in spotTypeWithShownIconList
                    ) {

                        locationCnt++
                        location = spot.location

                        latLngBounds.include(spot.location)

                        MapMarker(
                            context = context,
                            location = spot.location,
                            title = spot.titleText ?: "no title",

                            drawBackground = true,
                            iconId = spot.iconId ?: R.drawable.icon_circle_hole,
                            iconColor = date.first.iconColor,

                            backgroundIconId = R.drawable.icon_background_circle,
                            backgroundColor = 0xffffffff.toInt()
                        )
                    }
                }
            }
        }

        //at first launch
        if (false){
            when (locationCnt) {
                0 -> { }

                1 -> {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(location, 13f)
                    )
                }

                else -> {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 500)
                    )
                }
            }
        }
        //after first launch
        else {
            when (locationCnt) {
                0 -> { }

                1 -> {
                    LaunchedEffect(true){
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(location, 13f), 500
                        )
                    }

                }

                else -> {
                    LaunchedEffect(true) {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 230), 500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapForSpot(
    context: Context,
    cameraPositionState: CameraPositionState,
    uiSettings: MapUiSettings,
    properties: MapProperties,

    date: Date,
    spot: Spot,
    isFullScreen: Boolean,

    modifier: Modifier = Modifier
){
    val coroutineScope = rememberCoroutineScope()

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapLoaded = {
            if (spot.spotType == SpotType.MOVE) {
                val latLngBounds = LatLngBounds.Builder()
                    .include(spot.spotFrom!!.location!!)
                    .include(spot.spotTo!!.location!!)
                    .build()

                val padding = if (isFullScreen) 450
                else 200

                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(latLngBounds, padding),500
                    )
                }

            }
            else {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(spot.location!!, 13f),500
                    )
                }
            }
        }
    ){
        //show today's around marker
        MapMarkerAround(
            context = context,
            spotList = date.spotList,
            currentSpot = spot
        )

        if (spot.spotType == SpotType.MOVE) {
            MapMarkersMove(
                context = context,
                cameraPositionState = cameraPositionState,
                spotFrom = spot.spotFrom!!,
                spotTo = spot.spotTo!!,
                isFullScreen = isFullScreen
            )
        }
        else{
            MapMarkerSpot(
                context = context,
                location = spot.location!!,
                spot = spot
            )
        }
    }
}


@Composable
fun MapMarkerSpot(
    context: Context,
    location: LatLng,
    spot: Spot
){
    MapMarker(
        context = context,
        location = location,
        title = spot.titleText ?: "no title",
        iconId = R.drawable.icon_spot,
        drawBackground = false
    )
}


@Composable
fun MapMarkersMove(
    context: Context,
    cameraPositionState: CameraPositionState,
    spotFrom: Spot,
    spotTo: Spot,
    isFullScreen: Boolean,
){
//    val latLngBounds = LatLngBounds.Builder()
//        .include(spotFrom.location!!)
//        .include(spotTo.location!!)
//        .build()

//    val padding = if (isFullScreen) 450
//                    else 200

    MapMarker(
        context = context,
        location = spotFrom.location!!,
        title = spotFrom.titleText ?: "From",
        iconId = R.drawable.icon_from,
        drawBackground = false
    )

    MapMarker(
        context = context,
        location = spotTo.location!!,
        title = spotTo.titleText ?: "to",
        iconId = R.drawable.icon_to,
        drawBackground = false
    )

    //cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding))
}

@Composable
fun MapMarkerAround(
    context: Context,
    spotList: List<Spot>,
    currentSpot: Spot
){
    for(spot in spotList){
        if (
            (
                    //when move
                    currentSpot.spotType == SpotType.MOVE &&
                            currentSpot.spotFrom != spot && currentSpot.spotTo != spot

                            //when not move
                            || currentSpot.spotType != SpotType.MOVE &&
                            currentSpot != spot
                    )
            && spot.location != null
        )
            MapMarker(
                context = context,

                location = spot.location,
                title = spot.titleText ?: "no title",

                drawBackground = true,

                iconId = spot.iconId ?: R.drawable.icon_around,
                iconColor = spot.iconColor,
            )
    }
}

@Composable
private fun MapMarker(
    context: Context,

    location: LatLng,
    title: String,

    drawBackground: Boolean,

    @DrawableRes iconId: Int,
    @ColorInt iconColor: Int? = null,

    @DrawableRes backgroundIconId: Int = R.drawable.icon_circle,
    @ColorInt backgroundColor: Int? = null
){
    Marker(
        state = MarkerState(position = location),
        title = title,
        icon = bitmapDescriptor(
            context = context,

            drawBackground = drawBackground,

            vectorResId = iconId,
            color = iconColor,

            backgroundVectorResId = backgroundIconId,
            backgroundColor = backgroundColor
        ),
        anchor = Offset(0.5f,0.5f)
    )
}

private fun bitmapDescriptor(
    context: Context,

    drawBackground: Boolean,

    @DrawableRes vectorResId: Int,
    @ColorInt color: Int? = null,

    @DrawableRes backgroundVectorResId: Int = R.drawable.icon_circle,
    @ColorInt backgroundColor: Int? = null

): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    if (color != null)
        drawable.setTint(color)

    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)

    if (drawBackground){
        val backgroundDrawable = ContextCompat.getDrawable(context, backgroundVectorResId) ?: return null
        backgroundDrawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        if (backgroundColor != null)
            backgroundDrawable.setTint(backgroundColor)

        backgroundDrawable.draw(canvas)
    }

    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (LatLng?) -> Unit
){
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            val userLocation = LatLng(latitude, longitude)
            onLocationReceived(userLocation)
        }
        else{
            onLocationReceived(null)
        }
    }
}