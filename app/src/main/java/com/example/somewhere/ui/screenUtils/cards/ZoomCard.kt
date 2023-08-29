package com.example.somewhere.ui.screenUtils.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun ZoomCard(
    zoomLevel: Float,
    mapZoomTo: (zoomLevel: Float) -> Unit
){
    //set zoom level
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(
                text = "Zoom Level",
                style = getTextStyle(TextType.CARD__BODY),
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 3.dp)
            )

            Spacer(modifier = Modifier.weight(1f))


            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colors.surface)
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