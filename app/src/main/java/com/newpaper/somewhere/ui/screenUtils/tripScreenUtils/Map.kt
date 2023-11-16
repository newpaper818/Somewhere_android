package com.newpaper.somewhere.ui.screenUtils.tripScreenUtils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.newpaper.somewhere.R
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Spot
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.utils.USER_LOCATION_PERMISSION_LIST
import com.newpaper.somewhere.utils.checkPermissionUserLocation
import com.newpaper.somewhere.utils.checkPermissionsIsGranted
import com.newpaper.somewhere.viewModel.DateWithBoolean
import com.newpaper.somewhere.viewModel.SpotTypeGroupWithBoolean
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
import com.google.maps.android.compose.Polyline
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.utils.USER_LOCATION_PERMISSION_ARRAY
import com.newpaper.somewhere.viewModel.LocationInfo
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * seoul location, initial location
 */
val SEOUL_LOCATION = LatLng(37.55, 126.98)

/**
 * 10f, default map zoom level
 */
const val DEFAULT_ZOOM_LEVEL = 10f

/**
 * 21f, max map zoom level
 */
const val MAX_ZOOM_LEVEL = 21f

/**
 * 3f, minimum map zoom level
 */
const val MIN_ZOOM_LEVEL = 3f


/**
 * 300ms, map move spot animation duration
 */
const val ANIMATION_DURATION_MS = 300

/**
 * 200, map LatLng bound padding value
 */
const val LAT_LNG_BOUND_PADDING = 200

/**
 * get map style(dark / light)
 *
 * @param isDarkTheme
 * @return map style(dark / light)
 */
fun getMapStyle(
    isDarkTheme: Boolean
): Int {
    return  if (isDarkTheme)  R.raw.map_style_dark
            else              R.raw.map_style_light
}

//==================================================================================================
@Composable
fun MapForTrip(
    context: Context,
    mapPadding: PaddingValues,
    isDarkMapTheme: Boolean,
    userLocationEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    dateList: List<Date>,
    dateListWithShownMarkerList: List<DateWithBoolean>,
    spotTypeGroupWithShownMarkerList: List<SpotTypeGroupWithBoolean>,
    firstFocusOnToSpot: () -> Unit
){
    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = true,
                isMyLocationEnabled = userLocationEnabled,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context, getMapStyle(isDarkMapTheme)
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
        },
        contentPadding = mapPadding
    ){
        dateListWithShownMarkerList.forEachIndexed { dateIndex, dateWithBoolean ->
            if (dateWithBoolean.isShown){

                val polyLinePointList = mutableListOf<LatLng>()

                for (spot in dateWithBoolean.date.spotList){

                    if (spot.location != null &&
                        spot.spotType.isNotMove() &&
                        SpotTypeGroupWithBoolean(spot.spotType.group, true) in spotTypeGroupWithShownMarkerList
                    ) {
                        MapMarker(
                            location = spot.location,
                            title = spot.titleText ?: stringResource(id = R.string.no_title),
                            isBigMarker = false,
                            iconText = spot.iconText.toString(),
                            iconColor = dateWithBoolean.date.color.color,
                            onIconColor = dateWithBoolean.date.color.onColor
                        )

                        //add poly line point
                        polyLinePointList.add(spot.location)
                    }

                    //move line
                    if(spot.spotType.isMove()) {
                        val spotFrom = spot.getPrevSpot(dateList, dateIndex)
                        val spotTo = spot.getNextSpot(dateList, dateIndex)

                        if (
                            spotFrom?.location != null && spotTo?.location != null
                            && SpotTypeGroupWithBoolean(spotFrom.spotType.group, true) in spotTypeGroupWithShownMarkerList
                            && SpotTypeGroupWithBoolean(spotTo.spotType.group, true) in spotTypeGroupWithShownMarkerList
                        )
                            MapMoveLine(
                                isTripMap = true,
                                locationFrom = spotFrom.location,
                                locationTo = spotTo.location,
                                lineColor = Color(dateList[dateIndex].color.color).copy(alpha = 0.8f)
                            )
                    }
                }

                //draw poly line
                MapLine(
                    pointList = polyLinePointList,
                    lineColor = Color(dateList[dateIndex].color.color).copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun MapForSpot(
    context: Context,
    isDarkMapTheme: Boolean,
    cameraPositionState: CameraPositionState,
    userLocationEnabled: Boolean,
    mapSize: IntSize,

    dateList: List<Date>,
    dateIndex: Int,
    spotList: List<Spot>,
    currentSpot: Spot?,

    spotFrom: Spot? = null,
    spotTo: Spot? = null
){
    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = true,
                isMyLocationEnabled = userLocationEnabled,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context, getMapStyle(isDarkMapTheme)
                ),
            )
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {

            },
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapLoaded = {
//            val spotList1: List<Spot> =
//                if (spot.spotType.isMove() && spotFrom?.location != null && spotTo?.location != null)
//                    listOf(spotFrom, spotTo)
//                else if (spot.spotType.isMove() && spot.location != null)    listOf(spot)
//                else listOf()
//
//            coroutineScope.launch {
//                focusOnToSpot(cameraPositionState, mapSize, spotList1, density)
//            }
        }
    ){
        val polyLinePointList = mutableListOf<LatLng>()

        for(spot in spotList) {
            if (spot.spotType.isNotMove() && spot.location != null) {

                //draw map marker
                MapMarker(
                    location = spot.location,
                    title = spot.titleText ?: stringResource(id = R.string.no_title),
                    isBigMarker = spot == currentSpot || spot == spotFrom || spot == spotTo,
                    iconText = spot.iconText.toString(),
                    iconColor = spot.spotType.group.color.color,
                    onIconColor = spot.spotType.group.color.onColor
                )

                //add poly line point
                polyLinePointList.add(spot.location)
            }
            if(spot.spotType.isMove()) {
                val spotFrom1 = spot.getPrevSpot(dateList, dateIndex)
                val spotTo1 = spot.getNextSpot(dateList, dateIndex)

                if (spotFrom1?.location != null && spotTo1?.location != null)
                    MapMoveLine(
                        isTripMap = false,
                        locationFrom = spotFrom1.location,
                        locationTo = spotTo1.location
                    )
            }
        }

        //draw poly line
        MapLine(pointList = polyLinePointList)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapChooseLocation(
    context: Context,
    mapPadding: PaddingValues,
    isDarkMapTheme: Boolean,
    cameraPositionState: CameraPositionState,

    dateList: List<Date>,
    dateIndex: Int,
    spotList: List<Spot>,
    currentDate: Date,
    currentSpot: Spot,

    showSearchLocationMarker: Boolean,
    searchLocationMarkerList: List<LocationInfo>,

    onMapLoaded: () -> Unit,
    onLocationChange: (LatLng) -> Unit,
    onZoomChange: (Float) -> Unit
){
    val userLocationPermissionState =
        rememberMultiplePermissionsState(permissions = USER_LOCATION_PERMISSION_LIST)

    val myLocationEnabled = checkPermissionsIsGranted(userLocationPermissionState)


    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = true,
                isMyLocationEnabled = myLocationEnabled,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context, getMapStyle(isDarkMapTheme)
                )
        ))
    }

    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
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
        contentPadding = mapPadding,
        onMapLoaded = onMapLoaded
    ){
        val polyLinePointList = mutableListOf<LatLng>()

        //show today's around marker
        for(spot in currentDate.spotList) {
            if (spot.spotType.isNotMove() && spot.location != null && spot != currentSpot) {
                MapMarker(
                    location = spot.location,
                    title = spot.titleText ?: stringResource(id = R.string.no_title),
                    isBigMarker = false,
                    iconText = spot.iconText.toString(),
                    iconColor = spot.spotType.group.color.color,
                    onIconColor = spot.spotType.group.color.onColor
                )

                //add poly line point
                polyLinePointList.add(spot.location)
            }
            if(spot.spotType.isMove()) {
                val spotFrom1 = spot.getPrevSpot(dateList, dateIndex)
                val spotTo1 = spot.getNextSpot(dateList, dateIndex)

                if (spotFrom1?.location != null && spotTo1?.location != null
                    && spotFrom1 != currentSpot && spotTo1 != currentSpot)
                    MapMoveLine(
                        isTripMap = false,
                        locationFrom = spotFrom1.location,
                        locationTo = spotTo1.location
                    )
            }
        }

        //draw poly line
        MapLine(pointList = polyLinePointList)
        
        //draw search location markers
        if (showSearchLocationMarker) {
            for (locationInfo in searchLocationMarkerList) {
                if (locationInfo.location != null) {
                    MapMarker(
                        location = locationInfo.location!!,
                        title = locationInfo.title,
                        iconText = "",
                        isBigMarker = false,
                        iconColor = 0xFFff0000.toInt(),
                        onIconColor = 0xFFff0000.toInt()
                    )
                }
            }
        }
    }
}

@Composable
private fun MapMarker(
    location: LatLng,
    title: String,
    isBigMarker: Boolean,
    iconText: String? = null,
    @ColorInt iconColor: Int,
    @ColorInt onIconColor: Int,
){
    //draw marker on map
    Marker(
        state = MarkerState(position = location),
        title = title,
        icon = bitmapDescriptor(isBigMarker, iconText, iconColor, onIconColor),
        anchor = Offset(0.5f,0.5f),
        zIndex = if (isBigMarker) 2f else 1.5f
    )
}

@Composable
private fun MapLine(
    pointList: List<LatLng>,
    lineColor: Color? = null
){
    val lineWidth = with(LocalDensity.current){
        4.dp.toPx()
    }

    Polyline(
        points = pointList,
        color = lineColor ?: getColor(ColorType.MAP_LINE_DEFAULT),
        width = lineWidth,
        zIndex = 1.1f
    )
}

@Composable
private fun MapMoveLine(
    isTripMap: Boolean,
    locationFrom: LatLng,
    locationTo: LatLng,
    lineColor: Color? = null
){
    val lineWidth = with(LocalDensity.current){
        if (isTripMap)  4.dp.toPx()
        else            6.dp.toPx()
    }

    val lineColor1 =
        lineColor ?: getColor(ColorType.SPOT_MAP_LINE_MOVE)

    Polyline(
        points = listOf(locationFrom, locationTo),
        color = lineColor1,
        width = lineWidth,
        zIndex = 1.3f
    )
}

@Composable
private fun bitmapDescriptor(
    isBig: Boolean,
    text: String?,
    @ColorInt iconColor: Int,
    @ColorInt onColor: Int,
): BitmapDescriptor {

    val density = LocalDensity.current.density

    val iconDp = if(isBig) 30 else 24
    val textDp = if(isBig) 16 else 12

    //bitmap size 30.dp to int
    val bitmapSize = (30 * density).toInt()

    //icon & text size
    val iconSize = (iconDp * density).toInt()
    val textSize = textDp * density

    // Create a bitmap
    val bm = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bm)

    // Draw the circle background
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    circlePaint.color = iconColor
    canvas.drawCircle(bitmapSize / 2f, bitmapSize / 2f, iconSize / 2f, circlePaint)

    // Draw the text
    if (text != null){
        val textPaint = Paint (Paint.ANTI_ALIAS_FLAG)
        textPaint.color = onColor
        textPaint.textSize = textSize
        textPaint.isFakeBoldText = true
        textPaint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val textX = bitmapSize / 2f
        val textY = (bitmapSize + textBounds.height()) / 2f

        canvas.drawText(text, textX, textY, textPaint)
    }

    return BitmapDescriptorFactory.fromBitmap(bm)
}

//map button =======================================================================================
@Composable
fun FocusOnToSpotButton(
    mapSize: IntSize,
    focusOnToSpotEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    spotList: List<Spot>,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
){
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current.density
    val snackBarText = stringResource(id = R.string.snack_bar_no_location_to_show)

    if (focusOnToSpotEnabled){
        IconButton(
            onClick = {
                coroutineScope.launch {
                    focusOnToSpot(cameraPositionState, mapSize, spotList, density)
                }
            }
        ) {
            DisplayIcon(icon = MyIcons.focusOnToTarget)
        }
    }
    else{
        IconButton(
            onClick = {
                showSnackBar(snackBarText, null, SnackbarDuration.Short) {}
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
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
    context: Context = LocalContext.current,
    userLocationPermissions: Array<String> = USER_LOCATION_PERMISSION_ARRAY
){
    val coroutineScope = rememberCoroutineScope()

    var userLocationPermissionGranted: Boolean by rememberSaveable{ mutableStateOf(checkPermissionUserLocation(context)) }

    val snackBarNoUserLocation = stringResource(id = R.string.snack_bar_no_user_location)
    val snackBarLocationPermissionDenied = stringResource(id = R.string.snack_bar_location_permission_denied)
    val snackBarSettings = stringResource(id = R.string.snack_bar_settings)


    val getUserLocationAndAnimateToUserLocation = {
        //get user location
        getUserLocation(
            context = context,
            fusedLocationClient = fusedLocationClient,
            onLocationChanged = { userLocation ->
                //focus to user location
                if (userLocation != null) {
                    coroutineScope.launch {
                        focusOnUserLocation(cameraPositionState, userLocation)
                    }
                }

                //user location is null
                else {
                    showSnackBar(snackBarNoUserLocation, null, SnackbarDuration.Short){ }
                }

            }
        )
    }


    //permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        //after end permission dialog
        userLocationPermissionGranted = checkPermissionUserLocation(context)

        if (userLocationPermissionGranted) {
            setUserLocationEnabled(true)
            getUserLocationAndAnimateToUserLocation()
        }
        else {
            setUserLocationEnabled(false)
            showSnackBar(snackBarLocationPermissionDenied, snackBarSettings, SnackbarDuration.Short) {
                //go to app settings
                val intent = Intent(
                    ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        }
    }


    //icon ui
    IconButton(
        onClick = {
            if (userLocationPermissionGranted){
                getUserLocationAndAnimateToUserLocation()
            }
            else {
                //request permissions & check granted & get location & animate to location
                permissionLauncher.launch(userLocationPermissions)
            }
        }
    ) {
        //icon
        if (userLocationPermissionGranted)  DisplayIcon(icon = MyIcons.myLocation)
        else                            DisplayIcon(icon = MyIcons.disabledMyLocation)
    }
}

//==================================================================================================
/**
 * get user location.
 * check user location permission before use
 *
 * @param fusedLocationClient
 * @return
 */
private fun getUserLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationChanged: (userLocation: LatLng?) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

        && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.

        return
    }

    //get user last location
    fusedLocationClient.lastLocation.addOnSuccessListener { userLocation1 ->
            if (userLocation1 != null){
                val lat = userLocation1.latitude
                val lng = userLocation1.longitude
                onLocationChanged(LatLng(lat, lng))
            } else{
                onLocationChanged(null)
            }
    }
}

suspend fun focusOnToSpot(
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    spotList: List<Spot>,
    density: Float? = null
){
    if (spotList.size == 1){
        val spot = spotList.first()

        if (spot.location != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(spot.location, spot.zoomLevel ?: DEFAULT_ZOOM_LEVEL), 300
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
                    CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding), 300
                )
            }
        }
    }
}

suspend fun focusOnToLatLng(
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    locationList: List<LatLng?>
){
    val newLocationList: List<LatLng> = locationList.filterNotNull()

    if (newLocationList.size == 1){
        val location = newLocationList.first()

        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(location, 16f), 300
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
            CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding), 300
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
                CameraUpdateFactory.newLatLngZoom(spot.location, spot.zoomLevel ?: DEFAULT_ZOOM_LEVEL), ANIMATION_DURATION_MS
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
                latLngBounds.include(spot.location)
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












