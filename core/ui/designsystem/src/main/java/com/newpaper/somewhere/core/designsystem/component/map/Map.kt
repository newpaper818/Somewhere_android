package com.newpaper.somewhere.core.designsystem.component.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.newpaper.somewhere.core.model.data.DateWithBoolean
import com.newpaper.somewhere.core.model.data.LocationInfo
import com.newpaper.somewhere.core.model.data.SpotTypeGroupWithBoolean
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.ui.designsystem.R
import com.newpaper.somewhere.core.utils.USER_LOCATION_PERMISSION_LIST
import com.newpaper.somewhere.core.utils.checkPermissionsIsGranted
import com.newpaper.somewhere.core.utils.convert.getNextSpot
import com.newpaper.somewhere.core.utils.convert.getPrevSpot


/**
 * Google map for trip map screen
 *
 * @param mapPadding
 * @param isDarkMapTheme
 * @param userLocationEnabled
 * @param cameraPositionState
 * @param dateList
 * @param dateListWithShownMarkerList
 * @param spotTypeGroupWithShownMarkerList
 * @param firstFocusOnToSpot
 */
@Composable
fun MapForTripMap(
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
                isMyLocationEnabled = userLocationEnabled
            )
        )
    }

    val mapId = if (isDarkMapTheme) stringResource(id = R.string.dark_map_id)
                else stringResource(id = R.string.light_map_id)


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        googleMapOptionsFactory = { GoogleMapOptions().mapId(mapId) },
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapLoaded = {
            firstFocusOnToSpot()
        },
        contentPadding = mapPadding
    ) {
        dateListWithShownMarkerList.forEachIndexed { dateIndex, dateWithBoolean ->
            if (dateWithBoolean.isShown) {

                val polyLinePointList = mutableListOf<LatLng>()

                for (spot in dateWithBoolean.date.spotList) {

                    if (spot.location != null &&
                        spot.spotType.isNotMove() &&
                        SpotTypeGroupWithBoolean(
                            spot.spotType.group,
                            true
                        ) in spotTypeGroupWithShownMarkerList
                    ) {
                        //draw map marker
                        MapMarker(
                            location = spot.location!!,
                            title = spot.titleText ?: stringResource(id = R.string.no_title),
                            isBigMarker = false,
                            iconText = spot.iconText.toString(),
                            iconColor = dateWithBoolean.date.color.color,
                            onIconColor = dateWithBoolean.date.color.onColor
                        )

                        //add poly line point
                        polyLinePointList.add(spot.location!!)
                    }

                    //move line
                    if (spot.spotType.isMove()) {
                        val spotFrom = spot.getPrevSpot(dateList, dateIndex)
                        val spotTo = spot.getNextSpot(dateList, dateIndex)

                        if (
                            spotFrom?.location != null && spotTo?.location != null
                            && SpotTypeGroupWithBoolean(
                                spotFrom.spotType.group,
                                true
                            ) in spotTypeGroupWithShownMarkerList
                            && SpotTypeGroupWithBoolean(
                                spotTo.spotType.group,
                                true
                            ) in spotTypeGroupWithShownMarkerList
                        )
                            MapMoveLine(
                                isTripMap = true,
                                locationFrom = spotFrom.location!!,
                                locationTo = spotTo.location!!,
                                lineColor = Color(dateList[dateIndex].color.color).copy(alpha = 0.5f)
                            )
                    }
                }

                //draw poly line
                MapLine(
                    pointList = polyLinePointList,
                    lineColor = Color(dateList[dateIndex].color.color).copy(alpha = 0.3f)
                )
            }
        }
    }
}

/**
 * Google map for spot screen
 *
 * @param isDarkMapTheme
 * @param cameraPositionState
 * @param userLocationEnabled
 * @param dateList
 * @param dateIndex
 * @param spotList
 * @param currentSpot
 * @param spotFrom
 * @param spotTo
 */
@Composable
fun MapForSpot(
    isDarkMapTheme: Boolean,
    cameraPositionState: CameraPositionState,
    userLocationEnabled: Boolean,

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
                isMyLocationEnabled = userLocationEnabled
            )
        )
    }

    val mapId = if (isDarkMapTheme) stringResource(id = R.string.dark_map_id)
                else stringResource(id = R.string.light_map_id)


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        googleMapOptionsFactory = { GoogleMapOptions().mapId(mapId) },
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        val polyLinePointList = mutableListOf<LatLng>()

        for (spot in spotList) {
            if (spot.spotType.isNotMove() && spot.location != null) {

                //draw map marker
                MapMarker(
                    location = spot.location!!,
                    title = spot.titleText ?: stringResource(id = R.string.no_title),
                    isBigMarker = spot == currentSpot || spot == spotFrom || spot == spotTo,
                    iconText = spot.iconText.toString(),
                    iconColor = spot.spotType.group.color.color,
                    onIconColor = spot.spotType.group.color.onColor
                )

                //add poly line point
                polyLinePointList.add(spot.location!!)
            }
            if (spot.spotType.isMove()) {
                val spotFrom1 = spot.getPrevSpot(dateList, dateIndex)
                val spotTo1 = spot.getNextSpot(dateList, dateIndex)

                if (spotFrom1?.location != null && spotTo1?.location != null)
                    MapMoveLine(
                        isTripMap = false,
                        locationFrom = spotFrom1.location!!,
                        locationTo = spotTo1.location!!
                    )
            }
        }

        //draw poly line
        MapLine(pointList = polyLinePointList)
    }
}

/**
 * Google map for choose location screen
 *
 * @param mapPadding
 * @param isDarkMapTheme
 * @param cameraPositionState
 * @param dateList
 * @param dateIndex
 * @param currentDate
 * @param currentSpot
 * @param showSearchLocationMarker
 * @param searchLocationMarkerList
 * @param onMapLoaded
 * @param onLocationChange
 * @param onZoomChange
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapForChooseLocation(
    mapPadding: PaddingValues,
    isDarkMapTheme: Boolean,
    cameraPositionState: CameraPositionState,

    dateList: List<Date>,
    dateIndex: Int,
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
                isMyLocationEnabled = myLocationEnabled
            )
        )
    }

    val mapId = if (isDarkMapTheme) stringResource(id = R.string.dark_map_id)
                else stringResource(id = R.string.light_map_id)

    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
    }


    //get center location(LatLng)
    LaunchedEffect(cameraPositionState.isMoving) {
        onLocationChange(cameraPositionState.position.target)
        onZoomChange(cameraPositionState.position.zoom)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        googleMapOptionsFactory = { GoogleMapOptions().mapId(mapId) },
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        contentPadding = mapPadding,
        onMapLoaded = onMapLoaded
    ) {
        val polyLinePointList = mutableListOf<LatLng>()

        //show today's around marker
        for (spot in currentDate.spotList) {
            if (spot.spotType.isNotMove() && spot.location != null && spot != currentSpot) {
                MapMarker(
                    location = spot.location!!,
                    title = spot.titleText ?: stringResource(id = R.string.no_title),
                    isBigMarker = false,
                    iconText = spot.iconText.toString(),
                    iconColor = spot.spotType.group.color.color,
                    onIconColor = spot.spotType.group.color.onColor
                )

                //add poly line point
                polyLinePointList.add(spot.location!!)
            }
            if (spot.spotType.isMove()) {
                val spotFrom1 = spot.getPrevSpot(dateList, dateIndex)
                val spotTo1 = spot.getNextSpot(dateList, dateIndex)

                if (spotFrom1?.location != null && spotTo1?.location != null
                    && spotFrom1 != currentSpot && spotTo1 != currentSpot
                )
                    MapMoveLine(
                        isTripMap = false,
                        locationFrom = spotFrom1.location!!,
                        locationTo = spotTo1.location!!
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