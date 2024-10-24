package com.newpaper.somewhere.core.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.somewhere.core.designsystem.component.button.FocusOnToSpotButtonUi
import com.newpaper.somewhere.core.designsystem.component.button.UserLocationButtonUi
import com.newpaper.somewhere.core.designsystem.component.button.ZoomButtonsUi
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.ANIMATION_DURATION_MS
import com.newpaper.somewhere.core.utils.USER_LOCATION_PERMISSION_ARRAY
import com.newpaper.somewhere.core.utils.checkUserLocationPermission
import com.newpaper.somewhere.core.utils.focusOnToSpots
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    val snackBarText = stringResource(id = R.string.snackbar_no_location_to_show)

    FocusOnToSpotButtonUi(
        focusOnToSpotEnabled = focusOnToSpotEnabled,
        onClickEnabled = {
            coroutineScope.launch {
                focusOnToSpots(cameraPositionState, mapSize, spotList, density)
            }
        },
        onClickDisabled = {
            showSnackBar(snackBarText, null, SnackbarDuration.Short) {}
        }
    )
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

    var userLocationPermissionGranted: Boolean by rememberSaveable {
        mutableStateOf(
            checkUserLocationPermission(context)
        )
    }

    //snackbar
    val snackBarNoUserLocation = stringResource(id = R.string.snackbar_no_user_location)
    val snackBarLocationPermissionDenied =
        stringResource(id = R.string.snackbar_location_permission_denied)
    val snackBarSettings = stringResource(id = R.string.snackbar_settings)

    val noLocationSnackbar = {
        showSnackBar(snackBarNoUserLocation, null, SnackbarDuration.Short){ }
    }

    val permissionsDeniedSnackbar = {
        showSnackBar(snackBarLocationPermissionDenied, snackBarSettings, SnackbarDuration.Short) {
            //go to app settings
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    }



    //get user location & focus to location
    val getUserLocationAndAnimateToUserLocation = {
        //get user location
        getUserLocation(
            context = context,
            fusedLocationClient = fusedLocationClient,
            onPermissionsDenied = {
                permissionsDeniedSnackbar()
            },
            onLocationChanged = { userLocation ->
                //focus to user location
                if (userLocation != null) {
                    coroutineScope.launch {
                        focusOnUserLocation(cameraPositionState, userLocation)
                    }
                }

                //user location is null
                else {
                    noLocationSnackbar()
                }

            }
        )
    }


    //permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        //after end permission dialog
        userLocationPermissionGranted = checkUserLocationPermission(context)

        if (userLocationPermissionGranted) {
            setUserLocationEnabled(true)
            getUserLocationAndAnimateToUserLocation()
        } else {
            setUserLocationEnabled(false)
            permissionsDeniedSnackbar()
        }
    }


    //user location icon ui
    UserLocationButtonUi(
        userLocationPermissionGranted = userLocationPermissionGranted,
        onClickLocationPermissionGranted = {
            getUserLocationAndAnimateToUserLocation()
        },
        onClickLocationPermissionNotGranted = {
            //request permissions & check granted & get location & animate to location
            permissionLauncher.launch(userLocationPermissions)
        }
    )
}

@Composable
fun ZoomButtonsForSetLocation(
    zoomLevel: Float,
    mapZoomTo: (zoomLevel: Float) -> Unit,

    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
){
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(0.dp, 10.dp)
    ) {
        //set zoom level
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.zoom_card_zoom_level),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 3.dp)
            )

            ZoomButtonsUi(
                zoomLevel = zoomLevel,
                onClickZoomOut = { mapZoomTo(roundZoomLevel(zoomLevel - 1.0f)) },
                onClickZoomOutLess = { mapZoomTo(roundZoomLevel(zoomLevel - 0.5f)) },
                onClickZoomInLess = { mapZoomTo(roundZoomLevel(zoomLevel + 0.5f)) },
                onClickZoomIn = { mapZoomTo(roundZoomLevel(zoomLevel + 1.0f)) }
            )
        }

        MySpacerRow(width = 16.dp)

        //user location button
        MyCard(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            UserLocationButton(
                fusedLocationClient,
                cameraPositionState,
                setUserLocationEnabled,
                showSnackBar
            )
        }
    }
}











private fun roundZoomLevel(zoomLevel: Float): Float{
    val newZoomLevel = (zoomLevel * 2).roundToInt() / 2.0f

    return if (newZoomLevel < 0)
        0.0f
    else if (newZoomLevel > 21)
        21.0f
    else
        newZoomLevel
}

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
    onPermissionsDenied: () -> Unit,
    onLocationChanged: (userLocation: LatLng?) -> Unit
) {
    //if permissions denied
    if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionsDenied()
        return
    }

    //if permissions granted - get user location
    else {
        //get user last location
        fusedLocationClient.lastLocation.addOnSuccessListener { userLocation1 ->
            if (userLocation1 != null) {
                val lat = userLocation1.latitude
                val lng = userLocation1.longitude
                onLocationChanged(LatLng(lat, lng))
            } else {
                onLocationChanged(null)
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
        ), ANIMATION_DURATION_MS
    )
}




