package com.example.somewhere.ui.tripScreenUtils.cards

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.somewhere.model.Spot
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.tripScreenUtils.FocusOnToSpotButton
import com.example.somewhere.ui.tripScreenUtils.MapForSpot
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.tripScreenUtils.UserLocationButton
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.Shapes
import com.example.somewhere.ui.theme.getColor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.CameraPositionState

@Composable
fun MapCard(
    isEditMode: Boolean,
    isMapExpand: Boolean,
    expandHeight: Int,
    mapSize: IntSize,

    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    cameraPositionState: CameraPositionState,

    spotList: List<Spot>,
    currentSpot: Spot,
    onFullScreenClicked: () -> Unit,

    toggleIsEditLocation: () -> Unit,
    deleteLocation: () -> Unit,
    setMapSize: (IntSize) -> Unit,

    modifier: Modifier = Modifier,

    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit,


    spotFrom: Spot? = null,
    spotTo: Spot? = null
) {
    val context = LocalContext.current

    val cardHeight by animateIntAsState(
        targetValue = if (isMapExpand)  expandHeight
                        else            300,
        animationSpec = tween(300)
    )


    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight.dp)
            .onSizeChanged {
                setMapSize(it)
            }
    ) {
        //map for spot
        MapForSpot(
            context = context,
            isDarkMapTheme = isDarkMapTheme,
            cameraPositionState = cameraPositionState,
            userLocationEnabled = userLocationEnabled,
            mapSize = mapSize,
            spotList = spotList,
            spot = currentSpot,
            spotFrom = spotFrom,
            spotTo = spotTo
        )

        Column() {
            Spacer(modifier = Modifier.weight(1f))
            
            //buttons
            MapButtons(
                isEditMode = isEditMode,
                spot = currentSpot,
                spotFrom = spotFrom,
                spotTo = spotTo,
                fusedLocationClient = fusedLocationClient,
                mapSize = mapSize,
                cameraPositionState = cameraPositionState,
                setUserLocationEnabled = setUserLocationEnabled,
                onFullScreenClicked = onFullScreenClicked,
                toggleIsEditLocation = toggleIsEditLocation,
                deleteLocation = deleteLocation,
                showSnackBar = showSnackBar
            )
            
            MySpacerColumn(height = 16.dp)
        }
    }








    //=====================

//    Card(
//        modifier = modifier
//            .clip(RoundedCornerShape(16.dp))
//            .fillMaxWidth(),
//        elevation = 0.dp
//    ) {
//        Column(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//
//            Row {
//                //Location title
//                AnimatedVisibility(
//                    visible = isEditMode,
//                    enter = expandHorizontally(tween(400)),
//                    exit = shrinkHorizontally(tween(400))
//                ) {
//                    Text(
//                        text = stringResource(id = R.string.location_card_title),
//                        style = titleTextStyle,
//                        modifier = Modifier.padding(16.dp, 14.dp, 50.dp, 13.dp)
//                    )
//                }
//
//                //map buttons
//                MapButtons(
//                    isEditMode = isEditMode,
//
//                    spot = currentSpot,
//                    spotFrom = spotFrom,
//                    spotTo = spotTo,
//                    cameraPositionState = cameraPositionState,
//                    onMapClicked = onMapClicked,
//
//                    toggleIsEditLocation = toggleIsEditLocation,
//                    setLocationNotMove = setLocation
//                )
//            }
//
//            //map
//            if(currentSpot.spotType.isNotMove() && currentSpot.location != null
//                || (currentSpot.spotType.isMove() && spotFrom?.location != null && spotTo?.location != null)) {
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(390.dp)
//                ) {
//                    //map for spot
//                    MapForSpot(
//                        context = context,
//                        cameraPositionState = cameraPositionState,
//                        date = currentDate,
//                        spot = currentSpot,
//                        isFullScreen = false,
//                        spotFrom = spotFrom,
//                        spotTo = spotTo
//                    )
//                }
//            }
//            else if (!isEditMode){
//                Text(
//                    text = "No location",
//                    style = bodyNullTextStyle,
//                    modifier = Modifier.padding(16.dp, 14.dp)
//                )
//            }
//        }
//
//    }
}

@Composable
fun MapButtons(
    isEditMode: Boolean,

    spot: Spot,
    spotFrom: Spot?,
    spotTo: Spot?,

    fusedLocationClient: FusedLocationProviderClient,
    mapSize: IntSize,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    onFullScreenClicked: () -> Unit,
    toggleIsEditLocation: () -> Unit,
    deleteLocation: () -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit
){

    if (!(isEditMode && spot.spotType.isMove())) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = getColor(ColorType.BUTTON__MAP))
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (isEditMode) {
                    if (spot.spotType.isNotMove())
                        MapButtonsEditNotMove(
                            deleteEnabled = spot.location != null,
                            toggleIsEditLocation = toggleIsEditLocation,
                            deleteLocation = deleteLocation
                        )
                } else {
                    MapButtonsNotEdit(spot, spotFrom, spotTo, fusedLocationClient, mapSize, cameraPositionState, setUserLocationEnabled, onFullScreenClicked, showSnackBar)
                }
            }
        }
    }
}

@Composable
//map buttons at [not edit]
fun MapButtonsNotEdit(
    spot: Spot,
    spotFrom: Spot?,
    spotTo: Spot?,

    fusedLocationClient: FusedLocationProviderClient,
    mapSize: IntSize,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    onFullScreenClicked: () -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit
){
    val focusOnToSpotEnabled = (spot.spotType.isMove() && spotFrom?.location != null && spotTo?.location != null
                                    || spot.spotType.isNotMove() && spot.location != null)

    val spotList = if (spot.spotType.isMove() && spotFrom != null && spotTo != null)  listOf(spotFrom, spotTo)
                    else                                                                listOf(spot)

    //user location
    UserLocationButton(fusedLocationClient, cameraPositionState, setUserLocationEnabled, showSnackBar)

    //focus on to spot
    FocusOnToSpotButton(mapSize, focusOnToSpotEnabled, cameraPositionState, spotList, showSnackBar)

    //fullscreen map
    IconButton(onClick = onFullScreenClicked) {
        DisplayIcon(icon = MyIcons.fullscreen)
    }
}

@Composable
//map buttons at [edit] [not move]
fun MapButtonsEditNotMove(
    deleteEnabled: Boolean,
    toggleIsEditLocation: () -> Unit,
    deleteLocation: () -> Unit,
){
    //edit location
    IconButton(onClick = toggleIsEditLocation) {
        DisplayIcon(icon = MyIcons.editLocation)
    }

    //delete location
    IconButton(
        enabled = deleteEnabled,
        onClick = deleteLocation
    ) {
        DisplayIcon(icon = MyIcons.deleteLocation, enabled = deleteEnabled)
    }
}