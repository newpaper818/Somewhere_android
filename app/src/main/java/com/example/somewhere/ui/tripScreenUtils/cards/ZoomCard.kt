package com.example.somewhere.ui.tripScreenUtils.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.tripScreenUtils.UserLocationButton
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.CameraPositionState
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun ZoomCard(
    zoomLevel: Float,
    mapZoomTo: (zoomLevel: Float) -> Unit,

    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit
){
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
//            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
    ) {
        //set zoom level
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.zoom_level),
                style = getTextStyle(TextType.CARD__TITLE),
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 3.dp)
            )

            Card(
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { mapZoomTo(roundZoomLevel(zoomLevel - 1.0f)) }) {
                        DisplayIcon(icon = MyIcons.zoomOutMore)
                    }

                    IconButton(onClick = { mapZoomTo(roundZoomLevel(zoomLevel - 0.5f)) }) {
                        DisplayIcon(icon = MyIcons.zoomOut)
                    }

                    val df1 = DecimalFormat("0.0")
                    Text(
                        text = df1.format(zoomLevel),
                        style = getTextStyle(TextType.CARD__BODY),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(50.dp)
                    )

                    IconButton(onClick = { mapZoomTo(roundZoomLevel(zoomLevel + 0.5f)) }) {
                        DisplayIcon(icon = MyIcons.zoomIn)
                    }

                    IconButton(onClick = { mapZoomTo(roundZoomLevel(zoomLevel + 1.0f)) }) {
                        DisplayIcon(icon = MyIcons.zoomInMore)
                    }
                }
            }
        }

        MySpacerRow(width = 16.dp)

        //user location button
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            UserLocationButton(fusedLocationClient, cameraPositionState, setUserLocationEnabled, showSnackBar)
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