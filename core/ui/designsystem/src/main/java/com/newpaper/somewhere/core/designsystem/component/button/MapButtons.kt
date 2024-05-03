package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MapButtonIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R
import java.text.DecimalFormat

@Composable
fun FocusOnToSpotButton(
    focusOnToSpotEnabled: Boolean,
    onClickEnabled: () -> Unit,
    onClickDisabled: () -> Unit
){
    IconButton(
        onClick = {
            if (focusOnToSpotEnabled)
                onClickEnabled()
            else
                onClickDisabled()
        }
    ) {
        DisplayIcon(
            icon =
                if (focusOnToSpotEnabled ) MapButtonIcon.focusOnToTarget
                else MapButtonIcon.disabledFocusOnToTarget
        )
    }
}

@Composable
fun UserLocationButton(
    userLocationPermissionGranted: Boolean,
    onClickLocationPermissionGranted: () -> Unit,
    onClickLocationPermissionNotGranted: () -> Unit
){
    IconButton(
        onClick = {
            if (userLocationPermissionGranted)
                onClickLocationPermissionGranted()
            else
                onClickLocationPermissionNotGranted()
        }
    ) {
        DisplayIcon(
            icon =
                if (userLocationPermissionGranted) MapButtonIcon.myLocation
                else MapButtonIcon.disabledMyLocation
        )
    }
}

@Composable
fun ZoomButtonsForSetLocation(
    zoomLevel: Float,
    onClickZoomOut: () -> Unit,
    onClickZoomOutLess: () -> Unit,
    onClickZoomInLess: () -> Unit,
    onClickZoomIn: () -> Unit,
){
    MyCard(
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClickZoomOut) {
                DisplayIcon(icon = MapButtonIcon.zoomOut)
            }

            IconButton(onClick = onClickZoomOutLess) {
                DisplayIcon(icon = MapButtonIcon.zoomOutLess)
            }

            Text(
                text = DecimalFormat("0.0").format(zoomLevel),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(50.dp)
            )

            IconButton(onClick = onClickZoomInLess) {
                DisplayIcon(icon = MapButtonIcon.zoomInLess)
            }

            IconButton(onClick = onClickZoomIn) {
                DisplayIcon(icon = MapButtonIcon.zoomIn)
            }
        }
    }
}

/**
 * use at set location
 */
@Composable
fun ZoomButtons(
    zoomLevel: Float,
    onClickZoomOut: () -> Unit,
    onClickZoomOutLess: () -> Unit,
    onClickZoomInLess: () -> Unit,
    onClickZoomIn: () -> Unit,

    userLocationPermissionGranted: Boolean,
    onClickLocationPermissionGranted: () -> Unit,
    onClickLocationPermissionNotGranted: () -> Unit
){
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(0.dp, 10.dp)
    ) {
        //set zoom level
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.zoom_card_zoom_level),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 3.dp)
            )

            ZoomButtonsForSetLocation(
                zoomLevel = zoomLevel,
                onClickZoomOut = onClickZoomOut,
                onClickZoomOutLess = onClickZoomOutLess,
                onClickZoomInLess = onClickZoomInLess,
                onClickZoomIn = onClickZoomIn
            )
        }

        MySpacerRow(width = 16.dp)

        //user location button
        MyCard(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            UserLocationButton(
                userLocationPermissionGranted = userLocationPermissionGranted,
                onClickLocationPermissionGranted = onClickLocationPermissionGranted,
                onClickLocationPermissionNotGranted = onClickLocationPermissionNotGranted
            )
        }
    }
}

























@Composable
@PreviewLightDark
private fun FocusOnToSpotButtonPreview(){
    SomewhereTheme {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            MyCard(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                FocusOnToSpotButton(
                    focusOnToSpotEnabled = true,
                    onClickEnabled = { },
                    onClickDisabled = { }
                )
            }

            MySpacerRow(width = 16.dp)

            MyCard(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                FocusOnToSpotButton(
                    focusOnToSpotEnabled = false,
                    onClickEnabled = { },
                    onClickDisabled = { }
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun UserLocationButtonPreview(){
    SomewhereTheme {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            MyCard(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                UserLocationButton(
                    userLocationPermissionGranted = true,
                    onClickLocationPermissionGranted = { },
                    onClickLocationPermissionNotGranted = { }
                )
            }

            MySpacerRow(width = 16.dp)

            MyCard(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                UserLocationButton(
                    userLocationPermissionGranted = false,
                    onClickLocationPermissionGranted = { },
                    onClickLocationPermissionNotGranted = { }
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun ZoomButtonsForSetLocationPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            MyCard(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                ZoomButtonsForSetLocation(
                    zoomLevel = 10.5f,
                    onClickZoomOut = { },
                    onClickZoomOutLess = { },
                    onClickZoomInLess = { },
                    onClickZoomIn = { }
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun ZoomButtonsPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            ZoomButtons(
                zoomLevel = 16.0f,
                onClickZoomOut = { },
                onClickZoomOutLess = { },
                onClickZoomInLess = { },
                onClickZoomIn = { },
                userLocationPermissionGranted = true,
                onClickLocationPermissionGranted = { },
                onClickLocationPermissionNotGranted = { }
            )
        }
    }
}