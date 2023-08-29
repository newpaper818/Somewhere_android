package com.example.somewhere.ui.screenUtils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.typeUtils.SpotTypeGroup
import com.example.somewhere.utils.USER_LOCATION_PERMISSION_LIST
import com.example.somewhere.utils.checkAndRequestPermissionForUserLocation
import com.example.somewhere.utils.checkPermissionUserLocation
import com.example.somewhere.utils.checkPermissionsIsGranted
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
import kotlin.math.min

//map style dark / light
fun getMapStyle(
    isDarkTheme: Boolean
): Int {
    return  if (isDarkTheme)  R.raw.map_style_dark
            else              R.raw.map_style_light
}

//==================================================================================================
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapForTrip(
    context: Context,
    userLocationEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    dateListWithShownMarkerList: List<Pair<Date, Boolean>>,
    spotTypeGroupWithShownMarkerList: List<Pair<SpotTypeGroup, Boolean>>,
    firstFocusOnToSpot: () -> Unit
){
//    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

//    var locationCnt by rememberSaveable { mutableStateOf(0) }
//    var location by rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }
//    val latLngBounds = LatLngBounds.Builder()


    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = true,
                isMyLocationEnabled = userLocationEnabled,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context, getMapStyle(isDarkTheme)
                )
            )
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapLoaded = {
            firstFocusOnToSpot()
        }
    ){
        for (date in dateListWithShownMarkerList){
            if (date.second){
                for (spot in date.first.spotList){

                    if (spot.location != null &&
                        spot.spotType.isNotMove() &&
                        Pair(spot.spotType.group, true) in spotTypeGroupWithShownMarkerList
                    ) {
                        //show map marker
                        MapMarker(
                            context = context,
                            location = spot.location,
                            title = spot.titleText ?: stringResource(id = R.string.no_title),

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
    }
}

@Composable
fun MapForSpot(
    context: Context,
    cameraPositionState: CameraPositionState,

    userLocationEnabled: Boolean,

    date: Date,
    spot: Spot,

    spotFrom: Spot? = null,
    spotTo: Spot? = null
){
    val isDarkTheme = isSystemInDarkTheme()

    val coroutineScope = rememberCoroutineScope()

    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = true,
                isMyLocationEnabled = userLocationEnabled,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context, getMapStyle(isDarkTheme)
                ),
            )
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapLoaded = {
            //focus on to spot
            if (spot.spotType.isMove() && spotFrom?.location != null && spotTo?.location != null) {
                val latLngBounds = LatLngBounds.Builder()
                    .include(spotFrom.location)
                    .include(spotTo.location)
                    .build()

                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(latLngBounds, 200),500
                    )
                }
            }
            else {
                if(spot.location != null)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(spot.location, spot.zoomLevel ?: 13f),500
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

        //show spot map marker
        if (spot.spotType.isMove()) {
            MapMarkersMove(
                context = context,
                spotFrom = spotFrom,
                spotTo = spotTo
            )
        }
        else{
            MapMarkerSpot(
                context = context,
                spot = spot
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapChooseLocation(
    context: Context,
    cameraPositionState: CameraPositionState,

    date: Date,
    spot: Spot,

    onLocationChange: (LatLng) -> Unit,
    onZoomChange: (Float) -> Unit
){
    val isDarkTheme = isSystemInDarkTheme()

    val userLocationPermissionState =
        rememberMultiplePermissionsState(permissions = USER_LOCATION_PERMISSION_LIST)

    val myLocationEnabled = checkPermissionsIsGranted(userLocationPermissionState)


    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = true,
                isMyLocationEnabled = false,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context, getMapStyle(isDarkTheme)
                )
        ))
    }

    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = myLocationEnabled, zoomControlsEnabled = false)
    }


    //get center location(LatLng)
    LaunchedEffect(cameraPositionState.isMoving){
        onLocationChange(cameraPositionState.position.target)
        onZoomChange(cameraPositionState.position.zoom)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapLoaded = {
//            coroutineScope.launch {
//                cameraPositionState.animate(
//                    //FIXME set location to lat lng bounds
//                    CameraUpdateFactory.newLatLngZoom(LatLng(37.532600, 127.024600), 13f),500
//                )
//            }

        }
    ){
        //show today's around marker
        MapMarkerAround(
            context = context,
            spotList = date.spotList,
            currentSpot = spot
        )
    }
}

//map marker =======================================================================================
@Composable
private fun MapMarkerSpot(
    context: Context,
    spot: Spot
){
    //map marker at spot / if null don't show
    if (spot.location != null)
        MapMarker(
            context = context,
            location = spot.location,
            title = spot.titleText ?: stringResource(id = R.string.no_title),
            iconId = R.drawable.icon_spot,
            drawBackground = false
        )
}


@Composable
private fun MapMarkersMove(
    context: Context,
    spotFrom: Spot?,
    spotTo: Spot?,
){
    //2 map marker for move / if null don't show
    if (spotFrom?.location != null)
        MapMarker(
            context = context,
            location = spotFrom.location,
            title = spotFrom.titleText ?: stringResource(id = R.string.to),
            iconId = R.drawable.icon_from,
            drawBackground = false
        )

    if (spotTo?.location != null)
        MapMarker(
            context = context,
            location = spotTo.location,
            title = spotTo.titleText ?: stringResource(id = R.string.from),
            iconId = R.drawable.icon_to,
            drawBackground = false
        )
}

@Composable
private fun MapMarkerAround(
    context: Context,
    spotList: List<Spot>,
    currentSpot: Spot,

    spotFrom: Spot? = null,
    spotTo: Spot? = null
){
    //show map markers around spot
    for(spot in spotList){
        if (
            (
                //when move
                currentSpot.spotType.isMove() &&
                        spotFrom != spot && spotTo != spot

                        //when not move
                        || currentSpot.spotType.isNotMove() &&
                        currentSpot != spot
                )
            && spot.location != null
        )
            MapMarker(
                context = context,

                location = spot.location,
                title = spot.titleText ?: stringResource(id = R.string.no_title),

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
    //draw marker on map
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

//map button =======================================================================================
@Composable
fun FocusOnToSpotButton(
    mapSize: IntSize,
    focusOnToSpotEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    spotList: List<Spot>
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (focusOnToSpotEnabled){
        IconButton(
            onClick = {
                coroutineScope.launch {
                    focusOnToSpot(cameraPositionState, mapSize, spotList)
                }
            }
        ) {
            DisplayIcon(icon = MyIcons.focusOnToTarget)
        }
    }
    else{
        IconButton(
            onClick = {
                Toast.makeText(context, "No Spot to show", Toast.LENGTH_SHORT).show()
            }
        ) {
            DisplayIcon(icon = MyIcons.disabledFocusOnToTarget)
        }
    }
}

@Composable
fun UserLocationButton(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    context: Context = LocalContext.current
){
    val coroutineScope = rememberCoroutineScope()

    var permissionRequestEnd by rememberSaveable{ mutableStateOf(checkPermissionUserLocation(context)) }
    var userLocationButtonEnabled by rememberSaveable{ mutableStateOf(checkPermissionUserLocation(context)) }

    if (permissionRequestEnd) {
        val userLocationPermissionGranted = checkPermissionUserLocation(context)
        userLocationButtonEnabled = userLocationPermissionGranted
        setUserLocationEnabled(userLocationPermissionGranted)
    }

    var userLocationPermissionGranted: Boolean? by rememberSaveable {
        mutableStateOf(checkPermissionUserLocation(context))
    }

    var userLocation: LatLng? by rememberSaveable{ mutableStateOf(null) }

    //permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            Log.d("test", "agreeeeeeeeeeeeeeeee")
            permissionRequestEnd = true
        }
        else {
            Log.d("test", "denyyyyyyyyyyyyyyyyy")
            permissionRequestEnd = true
        }
    }
    //Log.d("test", "request end = $permissionRequestEnd")



    IconButton(
        onClick = {
            getUserLocation(context, fusedLocationClient, permissionLauncher, permissionRequestEnd,
                ){ userLocationPermissionGranted_, userLocation_ ->
                    userLocationPermissionGranted = userLocationPermissionGranted_
                    userLocation = userLocation_
                }

//            Log.d("test", "1- $userLocationPermissionGranted")
//            Log.d("test", "2- $userLocation")

            //focus to user location
            if (userLocationPermissionGranted == true){
                if (userLocation != null) {
                    //focus to user location
                    coroutineScope.launch {
                        focusOnUserLocation(cameraPositionState, userLocation!!)
                    }
                }
                else {
                    //user location is null
                    val toastText = R.string.toast_no_user_location
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                }
            }

            //user location permission denied show toast
            else if (userLocationPermissionGranted == false){
                val toastText = R.string.toast_location_permission_denied
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        if (userLocationButtonEnabled)  DisplayIcon(icon = MyIcons.myLocation)
        else                            DisplayIcon(icon = MyIcons.disabledMyLocation)
    }
}

//==================================================================================================
private fun getUserLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    permissionRequestEnd: Boolean,
    onLocationResult: (userLocationPermissionGranted: Boolean?, userLocation: LatLng?) -> Unit
) {
    val userLocationPermissionGranted = checkAndRequestPermissionForUserLocation(context, permissionLauncher, permissionRequestEnd)

    if (userLocationPermissionGranted == true) {

//        var locationRequest: LocationRequest =
//            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10*1000)
//                .setMinUpdateDistanceMeters(0f)
//                .build()
//
//        val currentLocationRequest = CurrentLocationRequest.Builder()
//            .setDurationMillis(5 * 1000)
//            .setMaxUpdateAgeMillis(5 * 1000)
//            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//            .build()
//
//        val cancellationToken = object : CancellationToken(){
//            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken =
//                CancellationTokenSource().token
//
//            override fun isCancellationRequested(): Boolean = false
//        }
//
//        val locationCallBack  = object : LocationCallback(){
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                locationResult.let { locationResult_ ->
//                    val lastLocation = locationResult_.lastLocation
//                    lastLocation?.let { lastLocation_ ->
//                        var latitude = lastLocation_.latitude
//                        var longitude = lastLocation_.longitude
//                        var altitude = lastLocation_.altitude
//                    }
//                }
//            }
//        }



//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//        }

//        //update user location
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest, locationCallBack, Looper.getMainLooper()
//        )

        //FIXME can't find location when first tap
        //get user last location
        fusedLocationClient.lastLocation.addOnSuccessListener { userLocation_ ->
            if (userLocation_ != null){
                val lat = userLocation_.latitude
                val lng = userLocation_.longitude

                onLocationResult(true, LatLng(lat, lng))
            }
            else{
                onLocationResult(true, null)
            }
        }
    }
    else if (userLocationPermissionGranted == false){
        onLocationResult(false, null)
    }
    else{
        onLocationResult(null, null)
    }
}

suspend fun focusOnToSpot(
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    spotList: List<Spot>
){
    if (spotList.size == 1){
        val spot = spotList.first()

        if (spot.location != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(spot.location, spot.zoomLevel ?: 13f), 300
            )
        }
    }
    else if (spotList.size > 1){
        val latLngBounds = LatLngBounds.Builder()
        var spotCnt = 0

        for(spot in spotList){
            if (spot.location != null){
                spotCnt++
                latLngBounds.include(spot.location)
            }
        }

        if (spotCnt >= 2) {
            val padding = min(min(mapSize.height, mapSize.width) / 2, 460) / 2
            if (padding > 10) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding), 300
                )
            }
        }
    }
}

private suspend fun focusOnUserLocation(
    cameraPositionState: CameraPositionState,
    userLocation: LatLng,
){
    cameraPositionState.animate(
        CameraUpdateFactory.newLatLngZoom(
            userLocation, 13f
        ), 300
    )
}












