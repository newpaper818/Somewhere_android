package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MapButtonIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import java.text.DecimalFormat

@Composable
fun FocusOnToSpotButtonUi(
    focusOnToSpotEnabled: Boolean,
    onClickEnabled: () -> Unit,
    onClickDisabled: () -> Unit
){
    MyPlainTooltipBox(
        tooltipText = if (focusOnToSpotEnabled) stringResource(id = MapButtonIcon.focusOnToTarget.descriptionTextId!!)
            else stringResource(id = MapButtonIcon.disabledFocusOnToTarget.descriptionTextId!!)
    ) {
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
                if (focusOnToSpotEnabled) MapButtonIcon.focusOnToTarget
                else MapButtonIcon.disabledFocusOnToTarget
            )
        }
    }
}

@Composable
fun UserLocationButtonUi(
    userLocationPermissionGranted: Boolean,
    onClickLocationPermissionGranted: () -> Unit,
    onClickLocationPermissionNotGranted: () -> Unit
){
    MyPlainTooltipBox(
        tooltipText = if (userLocationPermissionGranted) stringResource(id = MapButtonIcon.myLocation.descriptionTextId!!)
                        else stringResource(id = MapButtonIcon.disabledMyLocation.descriptionTextId!!)
    ) {
        IconButton(
            onClick = {
                if (userLocationPermissionGranted) onClickLocationPermissionGranted()
                else onClickLocationPermissionNotGranted()
            }
        ) {
            DisplayIcon(
                icon =
                if (userLocationPermissionGranted) MapButtonIcon.myLocation
                else MapButtonIcon.disabledMyLocation
            )
        }
    }
}

@Composable
fun ZoomButtonsUi(
    zoomLevel: Float,
    onClickZoomOut: () -> Unit,
    onClickZoomOutLess: () -> Unit,
    onClickZoomInLess: () -> Unit,
    onClickZoomIn: () -> Unit,
){

    val zoomLevelText = DecimalFormat("0.0").format(zoomLevel)

    MyCard(
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.zoomOut.descriptionTextId!!)) {
                IconButton(onClick = onClickZoomOut) {
                    DisplayIcon(icon = MapButtonIcon.zoomOut)
                }
            }

            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.zoomOutLess.descriptionTextId!!)) {
                IconButton(onClick = onClickZoomOutLess) {
                    DisplayIcon(icon = MapButtonIcon.zoomOutLess)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(50.dp)
                    .semantics {
                        contentDescription = zoomLevelText
                    }
            ) {
                zoomLevelText.forEach {
                    Text(
                        text = it.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = if (it.toString() == ".") Modifier
                                    else Modifier.width(10.4.dp)
                    )
                }
            }

            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.zoomInLess.descriptionTextId!!)) {
                IconButton(onClick = onClickZoomInLess) {
                    DisplayIcon(icon = MapButtonIcon.zoomInLess)
                }
            }

            MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.zoomIn.descriptionTextId!!)) {
                IconButton(onClick = onClickZoomIn) {
                    DisplayIcon(icon = MapButtonIcon.zoomIn)
                }
            }
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
                FocusOnToSpotButtonUi(
                    focusOnToSpotEnabled = true,
                    onClickEnabled = { },
                    onClickDisabled = { }
                )
            }

            MySpacerRow(width = 16.dp)

            MyCard(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                FocusOnToSpotButtonUi(
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
                UserLocationButtonUi(
                    userLocationPermissionGranted = true,
                    onClickLocationPermissionGranted = { },
                    onClickLocationPermissionNotGranted = { }
                )
            }

            MySpacerRow(width = 16.dp)

            MyCard(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                UserLocationButtonUi(
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
private fun ZoomButtonsPreview(){
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
                ZoomButtonsUi(
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