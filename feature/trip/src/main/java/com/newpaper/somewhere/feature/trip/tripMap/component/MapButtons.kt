package com.newpaper.somewhere.feature.trip.tripMap.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MapButtonIcon
import com.newpaper.somewhere.core.ui.UserLocationButton
import com.newpaper.somewhere.core.utils.MAX_ZOOM_LEVEL
import com.newpaper.somewhere.core.utils.MIN_ZOOM_LEVEL
import kotlinx.coroutines.launch

@Composable
internal fun MapButtons(
    paddingValues: PaddingValues,
    cameraPositionState: CameraPositionState,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClicked: () -> Unit) -> Unit,
){
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(paddingValues),
    ) {
        //zoom in, out buttons
        Column(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(0.95f))
                .border(1.dp, MaterialTheme.colorScheme.surfaceTint, CircleShape),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.zoomIn.descriptionTextId!!)) {
                IconButton(
                    enabled = cameraPositionState.position.zoom < MAX_ZOOM_LEVEL,
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomIn()
                            )
                        }
                    }
                ) {
                    DisplayIcon(icon = MapButtonIcon.zoomIn)
                }
            }

            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.zoomOut.descriptionTextId!!)) {
                IconButton(
                    enabled = cameraPositionState.position.zoom > MIN_ZOOM_LEVEL,
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomOut()
                            )
                        }
                    }
                ) {
                    DisplayIcon(icon = MapButtonIcon.zoomOut)
                }
            }
        }

        MySpacerColumn(height = 16.dp)

        //user location button
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(0.95f))
                .border(1.dp, MaterialTheme.colorScheme.surfaceTint, CircleShape),
            verticalAlignment = Alignment . CenterVertically,
        ) {
            //my location button
            UserLocationButton(
                fusedLocationClient,
                cameraPositionState,
                setUserLocationEnabled,
                showSnackBar = showSnackBar
            )
        }
    }
}