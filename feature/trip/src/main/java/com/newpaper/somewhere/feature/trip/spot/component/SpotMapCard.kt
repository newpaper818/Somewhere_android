package com.newpaper.somewhere.feature.trip.spot.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.somewhere.core.designsystem.component.map.MapForSpot
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MapButtonIcon
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.ui.FocusOnToSpotButton
import com.newpaper.somewhere.core.ui.UserLocationButton
import com.newpaper.somewhere.core.utils.convert.nextSpotOrDateIsExist
import com.newpaper.somewhere.core.utils.convert.prevSpotOrDateIsExist

@Composable
fun SpotMapCard(
    isEditMode: Boolean,
    isMapExpand: Boolean,
    expandHeight: Int,
    mapSize: IntSize,

    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    cameraPositionState: CameraPositionState,

    dateList: List<Date>,
    dateIndex: Int,
    spotList: List<Spot>,
    currentSpot: Spot?,
    onFullScreenClicked: () -> Unit,

    toPrevSpot: () -> Unit,
    toNextSpot: () -> Unit,

    toggleIsEditLocation: () -> Unit,
    deleteLocation: () -> Unit,
    setMapSize: (IntSize) -> Unit,

    modifier: Modifier = Modifier,

    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
    use2Panes: Boolean = false,
    spotFrom: Spot? = null,
    spotTo: Spot? = null
) {
    val cardHeight by animateIntAsState(
        targetValue = if (isMapExpand)  expandHeight
                        else            300,
        animationSpec = tween(300),
        label = "card height"
    )

    val toPrevSpotEnabled =
        currentSpot?.prevSpotOrDateIsExist(dateIndex) ?: (dateIndex > 0)

    val toNextSpotEnabled =
        currentSpot?.nextSpotOrDateIsExist(spotList, dateList, dateIndex) ?: (dateIndex < dateList.lastIndex)

    val boxModifier = if (!use2Panes) modifier
        .fillMaxWidth()
        .height(cardHeight.dp)
        .onSizeChanged {
            setMapSize(it)
        }
    else modifier
        .fillMaxSize()
        .onSizeChanged {
            setMapSize(it)
        }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = boxModifier
    ) {
        //map for spot
        MapForSpot(
            isDarkMapTheme = isDarkMapTheme,
            cameraPositionState = cameraPositionState,
            userLocationEnabled = userLocationEnabled,
            dateList = dateList,
            dateIndex = dateIndex,
            spotList = spotList,
            currentSpot = currentSpot,
            spotFrom = spotFrom,
            spotTo = spotTo
        )

        Column() {
            Spacer(modifier = Modifier.weight(1f))

            //buttons
            MapSpotMapButtons(
                isEditMode = isEditMode,
                spot = currentSpot,
                spotFrom = spotFrom,
                spotTo = spotTo,
                fusedLocationClient = fusedLocationClient,
                mapSize = mapSize,
                cameraPositionState = cameraPositionState,
                setUserLocationEnabled = setUserLocationEnabled,

                toPrevSpotEnabled = toPrevSpotEnabled,
                toNextSpotEnabled = toNextSpotEnabled,
                toPrevSpot = toPrevSpot,
                toNextSpot = toNextSpot,

                showFullScreenButton = !use2Panes,
                onFullScreenClicked = onFullScreenClicked,
                toggleIsEditLocation = toggleIsEditLocation,
                deleteLocation = deleteLocation,
                showSnackBar = showSnackBar
            )

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
fun MapSpotMapButtons(
    isEditMode: Boolean,

    spot: Spot?,
    spotFrom: Spot?,
    spotTo: Spot?,

    fusedLocationClient: FusedLocationProviderClient,
    mapSize: IntSize,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    toPrevSpotEnabled: Boolean,
    toNextSpotEnabled: Boolean,
    toPrevSpot: () -> Unit,
    toNextSpot: () -> Unit,

    showFullScreenButton: Boolean,
    onFullScreenClicked: () -> Unit,
    toggleIsEditLocation: () -> Unit,
    deleteLocation: () -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
){
    val focusOnToSpotEnabled =
        if (spot == null) false
        else
            spot.spotType.isMove() && spotFrom?.location != null && spotTo?.location != null
                    || spot.spotType.isNotMove() && spot.location != null

    val deleteEnabled =
        if (spot == null) false
        else spot.location != null

    val spotList =
        if (spot == null) listOf()
        else if (spot.spotType.isMove() && spotFrom != null && spotTo != null)  listOf(spotFrom, spotTo)
        else listOf(spot)


    Row {
        if (!isEditMode){
            UserLocationAndFullScreenButtons(
                fusedLocationClient = fusedLocationClient,
                cameraPositionState = cameraPositionState,
                setUserLocationEnabled = setUserLocationEnabled,
                onFullScreenClicked = onFullScreenClicked,
                showSnackBar = showSnackBar,
                showFullScreenButton = showFullScreenButton
            )
        }
        else {
            EditAndDeleteLocationButtons(
                editEnabled = spot?.spotType?.isNotMove() == true,
                deleteEnabled = deleteEnabled,
                toggleIsEditLocation = toggleIsEditLocation,
                deleteLocation = deleteLocation
            )
        }

        MySpacerRow(width = 16.dp)

        SpotNavigateWithFocusOnToSpotButtons(
            toPrevSpotEnabled = toPrevSpotEnabled,
            toNextSpotEnabled = toNextSpotEnabled,
            toPrevSpot = toPrevSpot,
            toNextSpot = toNextSpot,
            mapSize = mapSize,
            focusOnToSpotEnabled = focusOnToSpotEnabled,
            cameraPositionState = cameraPositionState,
            spotList = spotList,
            showSnackBar = showSnackBar
        )
    }
}

//user location / full screen buttons
@Composable
fun UserLocationAndFullScreenButtons(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    onFullScreenClicked: () -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
    showFullScreenButton: Boolean = true
){
    MapButtonsRow {
        //user location
        UserLocationButton(fusedLocationClient, cameraPositionState, setUserLocationEnabled, showSnackBar)

        //fullscreen map
        if (showFullScreenButton) {
            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.fullscreen.descriptionTextId)) {
                IconButton(onClick = onFullScreenClicked) {
                    DisplayIcon(icon = MapButtonIcon.fullscreen)
                }
            }
        }
    }
}

// edit location / delete location buttons
@Composable
fun EditAndDeleteLocationButtons(
    editEnabled: Boolean,
    deleteEnabled: Boolean,
    toggleIsEditLocation: () -> Unit,
    deleteLocation: () -> Unit,
){
    MapButtonsRow {
        //edit location
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.editLocation.descriptionTextId)) {
            IconButton(
                enabled = editEnabled,
                onClick = toggleIsEditLocation
            ) {
                DisplayIcon(
                    icon = MapButtonIcon.editLocation,
                    enabled = editEnabled
                )
            }
        }

        //delete location
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.deleteLocation.descriptionTextId)) {
            IconButton(
                enabled = deleteEnabled,
                onClick = deleteLocation
            ) {
                DisplayIcon(
                    icon = MapButtonIcon.deleteLocation,
                    enabled = deleteEnabled
                )
            }
        }
    }
}

// < spot >
@Composable
fun SpotNavigateWithFocusOnToSpotButtons(
    toPrevSpotEnabled: Boolean,
    toNextSpotEnabled: Boolean,
    toPrevSpot: () -> Unit,
    toNextSpot: () -> Unit,

    mapSize: IntSize,
    focusOnToSpotEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    spotList: List<Spot>,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
){
    MapButtonsRow {
        //to prev spot
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.prevSpot.descriptionTextId)) {
            IconButton(
                enabled = toPrevSpotEnabled,
                onClick = toPrevSpot
            ) {
                DisplayIcon(
                    icon = MapButtonIcon.prevSpot,
                    enabled = toPrevSpotEnabled
                )
            }
        }

        //focus on to spot
        FocusOnToSpotButton(mapSize, focusOnToSpotEnabled, cameraPositionState, spotList, showSnackBar)


        //to next spot
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.nextSpot.descriptionTextId)) {
            IconButton(
                enabled = toNextSpotEnabled,
                onClick = toNextSpot
            ) {
                DisplayIcon(
                    icon = MapButtonIcon.nextSpot,
                    enabled = toNextSpotEnabled
                )
            }
        }
    }
}

@Composable
fun MapButtonsRow(
    buttonsContent: @Composable () -> Unit
){
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(0.85f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttonsContent()
    }
}