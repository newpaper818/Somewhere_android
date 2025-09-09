package com.newpaper.somewhere.feature.trip.spot.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.somewhere.core.designsystem.component.button.ToGoogleMapsButton
import com.newpaper.somewhere.core.designsystem.component.map.MapForSpot
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MapButtonIcon
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.ui.FitBoundsToMarkersButton
import com.newpaper.somewhere.core.ui.UserLocationButton
import com.newpaper.somewhere.core.utils.convert.nextSpotOrDateIsExist
import com.newpaper.somewhere.core.utils.convert.prevSpotOrDateIsExist
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun SpotMapCard(
    useBlurEffect: Boolean,
    isEditMode: Boolean,
    isMapExpand: Boolean,
    expandHeight: Int,
    mapSize: IntSize,
    onMapLoaded: () -> Unit,

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

    openInGoogleMapEnabled: Boolean,

    modifier: Modifier = Modifier,

    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
    use2Panes: Boolean = false,
    spotFrom: Spot? = null,
    spotTo: Spot? = null,
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

    val mapButtonHazeState = if(useBlurEffect) rememberHazeState() else null


    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = boxModifier
    ) {
        //map for spot
        MapForSpot(
            modifier = if (mapButtonHazeState != null) Modifier.hazeSource(state = mapButtonHazeState)
                        else Modifier,
            isDarkMapTheme = isDarkMapTheme,
            cameraPositionState = cameraPositionState,
            userLocationEnabled = userLocationEnabled,
            onMapLoaded = onMapLoaded,
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

                openInGoogleMapEnabled = openInGoogleMapEnabled,

                showSnackBar = showSnackBar,
                hazeState = mapButtonHazeState
            )

            MySpacerColumn(height = 16.dp)

            AnimatedVisibility(
                visible = isMapExpand && !use2Panes,
                enter = expandVertically(tween(300)),
                exit = shrinkVertically(tween(300))
            ) {
                Box(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun MapSpotMapButtons(
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

    openInGoogleMapEnabled: Boolean,

    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
    hazeState: HazeState?,
){
    val fitBoundsToMarkersEnabled =
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
                showFullScreenButton = showFullScreenButton,
                hazeState = hazeState
            )
        }
        else {
            EditAndDeleteLocationButtons(
                editEnabled = spot?.spotType?.isNotMove() == true,
                deleteEnabled = deleteEnabled,
                toggleIsEditLocation = toggleIsEditLocation,
                deleteLocation = deleteLocation,
                hazeState = hazeState
            )
        }

        MySpacerRow(width = 16.dp)

        SpotNavigateWithFitBoundsToMarkersButtons(
            toPrevSpotEnabled = toPrevSpotEnabled,
            toNextSpotEnabled = toNextSpotEnabled,
            toPrevSpot = toPrevSpot,
            toNextSpot = toNextSpot,
            mapSize = mapSize,
            fitBoundsToMarkersEnabled = fitBoundsToMarkersEnabled,
            cameraPositionState = cameraPositionState,
            spotList = spotList,
            showSnackBar = showSnackBar,
            hazeState = hazeState
        )

        MySpacerRow(width = 16.dp)

        ToGoogleMapButton(
            enabled = openInGoogleMapEnabled,
            googleMapsPlacesId = spot?.googleMapsPlacesId ?: "",
            hazeState = hazeState
        )
    }
}

//user location / full screen buttons
@Composable
private fun UserLocationAndFullScreenButtons(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    onFullScreenClicked: () -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
    showFullScreenButton: Boolean = true,
    hazeState: HazeState?,
){
    MapButtonsRow(hazeState = hazeState) {
        //user location
        UserLocationButton(fusedLocationClient, cameraPositionState, setUserLocationEnabled, showSnackBar)

        //fullscreen map
        if (showFullScreenButton) {
            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.fullscreen.descriptionTextId!!)) {
                IconButton(onClick = onFullScreenClicked) {
                    DisplayIcon(icon = MapButtonIcon.fullscreen)
                }
            }
        }
    }
}

// edit location / delete location buttons
@Composable
private fun EditAndDeleteLocationButtons(
    editEnabled: Boolean,
    deleteEnabled: Boolean,
    toggleIsEditLocation: () -> Unit,
    deleteLocation: () -> Unit,
    hazeState: HazeState?,
){
    MapButtonsRow(hazeState = hazeState) {
        //edit location
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.editLocation.descriptionTextId!!)) {
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
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.deleteLocation.descriptionTextId!!)) {
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
private fun SpotNavigateWithFitBoundsToMarkersButtons(
    toPrevSpotEnabled: Boolean,
    toNextSpotEnabled: Boolean,
    toPrevSpot: () -> Unit,
    toNextSpot: () -> Unit,

    mapSize: IntSize,
    fitBoundsToMarkersEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    spotList: List<Spot>,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
    hazeState: HazeState?,
){
    MapButtonsRow(hazeState = hazeState) {
        //to prev spot
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.prevSpot.descriptionTextId!!)) {
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
        FitBoundsToMarkersButton(mapSize, fitBoundsToMarkersEnabled, cameraPositionState, spotList, showSnackBar)


        //to next spot
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.nextSpot.descriptionTextId!!)) {
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
private fun ToGoogleMapButton(
    enabled: Boolean,
    googleMapsPlacesId: String,
    hazeState: HazeState?,
){
    MapButtonsRow(hazeState = hazeState) {
        ToGoogleMapsButton(
            enabled = enabled,
            googleMapsPlacesId = googleMapsPlacesId
        )
    }
}


@OptIn(ExperimentalHazeApi::class)
@Composable
private fun MapButtonsRow(
    hazeState: HazeState?,
    buttonsContent: @Composable () -> Unit,
){
    val containerColor = if (hazeState == null) MaterialTheme.colorScheme.surface
                            else Color.Transparent

    val hazeTintColor = MaterialTheme.colorScheme.surface

    val rowModifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceTint, CircleShape)

    val hazeModifier = if (hazeState == null) rowModifier.background(containerColor)
                        else rowModifier
                            .background(containerColor)
                            .hazeEffect(state = hazeState) {
                                blurRadius = 16.dp
                                tints = listOf(HazeTint(hazeTintColor.copy(alpha = 0.8f)))
                                inputScale = HazeInputScale.Fixed(0.5f)
                            }

    Row(
        modifier = hazeModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttonsContent()
    }
}