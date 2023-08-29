package com.example.somewhere.ui.screenUtils.cards

import android.widget.Toast
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
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.FocusOnToSpotButton
import com.example.somewhere.ui.screenUtils.MapForSpot
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.UserLocationButton
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.getColor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapCard(
    isEditMode: Boolean,
    isMapExpand: Boolean,
    expandHeight: Int,
    mapSize: IntSize,

    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    cameraPositionState: CameraPositionState,

    currentDate: Date,
    currentSpot: Spot,
    onFullScreenClicked: () -> Unit,

    toggleIsEditLocation: () -> Unit,
    setLocation: (newLocation: LatLng?) -> Unit,
    setMapSize: (IntSize) -> Unit,

    modifier: Modifier = Modifier,

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
            cameraPositionState = cameraPositionState,
            userLocationEnabled = userLocationEnabled,
            date = currentDate,
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
                setLocationNotMove = setLocation
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
    setLocationNotMove: (newLocation: LatLng?) -> Unit
){

    if (!(isEditMode && spot.spotType.isMove())) {
        Card(
            modifier = Modifier.clip(RoundedCornerShape(30.dp)),
            backgroundColor = getColor(ColorType.BUTTON__MAP),
            elevation = 0.dp
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
                            setLocation = setLocationNotMove
                        )
                } else {
                    MapButtonsNotEdit(spot, spotFrom, spotTo, fusedLocationClient, mapSize, cameraPositionState, setUserLocationEnabled, onFullScreenClicked)
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
    onFullScreenClicked: () -> Unit
){
    val focusOnToSpotEnabled = (spot.spotType.isMove() && spotFrom != null && spotTo != null
                                    || spot.spotType.isNotMove() && spot.location != null)

    val spotList = if (spot.spotType.isMove())  listOf(spotFrom!!, spotTo!!)
                    else                        listOf(spot)

    //user location
    UserLocationButton(fusedLocationClient, cameraPositionState, setUserLocationEnabled)

    //focus on to spot
    FocusOnToSpotButton(mapSize, focusOnToSpotEnabled, cameraPositionState, spotList
    )

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
    setLocation: (newLocation: LatLng?) -> Unit,
){
    //edit location
    IconButton(onClick = toggleIsEditLocation) {
        DisplayIcon(icon = MyIcons.editLocation)
    }

    //delete location
    IconButton(
        enabled = deleteEnabled,
        onClick = { setLocation(null) }
    ) {
        DisplayIcon(icon = MyIcons.deleteLocation, enabled = deleteEnabled)
    }
}