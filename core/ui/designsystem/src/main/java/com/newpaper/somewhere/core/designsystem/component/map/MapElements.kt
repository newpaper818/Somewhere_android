package com.newpaper.somewhere.core.designsystem.component.map

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.newpaper.somewhere.core.designsystem.theme.CustomColor

/**
 * Draw map maker on Google map
 *
 * @param location marker's location
 * @param title show title text when user click marker
 * @param isBigMarker big or small marker
 * @param iconText marker icon text
 * @param iconColor icon color
 * @param onIconColor icon text color
 */
@Composable
fun MapMarker(
    location: LatLng,
    title: String,
    isBigMarker: Boolean,
    iconText: String? = null,
    @ColorInt iconColor: Int,
    @ColorInt onIconColor: Int,
){
    //draw marker on map
    Marker(
        state = MarkerState(position = location),
        title = title,
        icon = bitmapDescriptor(isBigMarker, iconText, iconColor, onIconColor),
        anchor = Offset(0.5f, 0.5f),
        zIndex = if (isBigMarker) 2f else 1.5f
    )
}

/**
 * Draw line on Google map
 *
 * @param pointList
 * @param lineColor
 */
@Composable
fun MapLine(
    pointList: List<LatLng>,
    lineColor: Color? = null
){
    val lineWidth = with(LocalDensity.current){
        4.dp.toPx()
    }

    Polyline(
        points = pointList,
        color = lineColor ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        width = lineWidth,
        zIndex = 1.1f
    )
}

/**
 * Draw SpotTypeGroup.MOVE line on Google map
 *
 * @param isTripMap use on trip map screen
 * @param locationFrom line start point location
 * @param locationTo line end point location
 * @param lineColor line color
 */
@Composable
fun MapMoveLine(
    isTripMap: Boolean,
    locationFrom: LatLng,
    locationTo: LatLng,
    lineColor: Color? = null
){
    val lineWidth = with(LocalDensity.current){
        if (isTripMap)  4.dp.toPx()
        else            4.dp.toPx()
    }

    val lineColor1 =
        lineColor ?: CustomColor.spotMapLineMove

    Polyline(
        points = listOf(locationFrom, locationTo),
        color = lineColor1,
        width = lineWidth,
        zIndex = 1.3f
    )
}

/**
 * Draw icon. Use at map marker.
 *
 * @param isBigIcon
 * @param iconText
 * @param iconColor
 * @param onColor
 * @return
 */
@Composable
private fun bitmapDescriptor(
    isBigIcon: Boolean,
    iconText: String?,
    @ColorInt iconColor: Int,
    @ColorInt onColor: Int,
): BitmapDescriptor {

    val density = LocalDensity.current.density

    val iconDp = if(isBigIcon) 30 else 24
    val textDp = if(isBigIcon) 16 else 12

    //bitmap size 30.dp to int
    val bitmapSize = (30 * density).toInt()

    //icon & text size
    val iconSize = (iconDp * density).toInt()
    val textSize = textDp * density

    // Create a bitmap
    val bm = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bm)

    // Draw the circle background
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    circlePaint.color = iconColor
    canvas.drawCircle(bitmapSize / 2f, bitmapSize / 2f, iconSize / 2f, circlePaint)

    // Draw the text
    if (iconText != null){
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = onColor
        textPaint.textSize = textSize
        textPaint.isFakeBoldText = true
        textPaint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        textPaint.getTextBounds(iconText, 0, iconText.length, textBounds)
        val textX = bitmapSize / 2f
        val textY = (bitmapSize + textBounds.height()) / 2f

        canvas.drawText(iconText, textX, textY, textPaint)
    }

    return BitmapDescriptorFactory.fromBitmap(bm)
}