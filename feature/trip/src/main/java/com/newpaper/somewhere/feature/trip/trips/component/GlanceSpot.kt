package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.convert.getEndTimeText
import com.newpaper.somewhere.core.utils.convert.getStartTimeText
import com.newpaper.somewhere.core.utils.convert.isFirstSpot
import com.newpaper.somewhere.core.utils.convert.isLastSpot
import com.newpaper.somewhere.feature.trip.R

private val DUMMY_SPACE_HEIGHT: Dp = 10.dp
private val MIN_CARD_HEIGHT: Dp = 40.dp
private val ADDITIONAL_HEIGHT: Dp = 22.dp
private val POINT_CIRCLE_SIZE: Dp = 24.dp
private val LINE_WIDTH: Dp = 7.dp
@Composable
internal fun GlanceSpot(
    visible: Boolean,
    dateTimeFormat: DateTimeFormat,
    trip: Trip,
    date: Date,
    spot: Spot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val tripTitleText = if (trip.titleText == null || trip.titleText == "") stringResource(id = R.string.no_title)
                        else trip.titleText!!
    val tripTitleStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)

    val dateTitleText = date.titleText ?: date.getDateText(dateTimeFormat, false)
    val dateTimeStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)


    val spotTitleText = if (spot.titleText == null || spot.titleText == "") stringResource(id = R.string.no_title)
                        else spot.titleText!!
    val spotTitleStyle = if (spot.titleText == null || spot.titleText == "") MaterialTheme.typography.bodyLarge.copy(
                                MaterialTheme.colorScheme.onSurfaceVariant)
                        else MaterialTheme.typography.bodyLarge

    val startTimeText = spot.getStartTimeText(dateTimeFormat.timeFormat)
    val endTimeText = spot.getEndTimeText(dateTimeFormat.timeFormat)

    val upperLineColor =
        if (spot.isFirstSpot(
                dateId = date.id,
                dateList = trip.dateList,
                spotList = date.spotList
            )) Color.Transparent
        else MaterialTheme.colorScheme.outline

    val lowerLineColor =
        if (spot.isLastSpot(
                dateId = date.id,
                dateList = trip.dateList,
                spotList = date.spotList
            )) Color.Transparent
        else MaterialTheme.colorScheme.outline


    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { (it * 2.5f).toInt() }),
        exit = slideOutVertically(
            animationSpec = tween(500),
            targetOffsetY = { (it * 2.5f).toInt() })
    ) {
        ClickableBox(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = modifier
                .height(70.dp)
                .widthIn(max = 500.dp)
                .padding(horizontal = 16.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
        ) {
            Row {
                //line point
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        //upper line
                        Box(
                            modifier = Modifier
                                .width(LINE_WIDTH)
                                .weight(1f)
                                .background(upperLineColor)
                        )

                        //lower line
                        Box(
                            modifier = Modifier
                                .width(LINE_WIDTH)
                                .weight(1f)
                                .background(lowerLineColor)
                        )
                    }

                    //point
                    if (spot.spotType.isNotMove()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(POINT_CIRCLE_SIZE)
                                .clip(CircleShape)
                                .background(Color(spot.spotType.group.color.color))
                        ) {
                            val textStyle =
                                MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    .copy(
                                        color = Color(spot.spotType.group.color.onColor)
                                    )
                            Text(
                                text = spot.iconText.toString(),
                                style = textStyle
                            )

                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(POINT_CIRCLE_SIZE)
                                .clip(CircleShape)
                        )
                    }
                }


                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp, 8.dp, 16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //text
                    Column {
                        Text(
                            text = spotTitleText,
                            style = spotTitleStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (!(startTimeText == null && endTimeText == null)) {
                            MySpacerColumn(height = 4.dp)

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (startTimeText != null) {
                                    Text(
                                        text = startTimeText,
                                        style = dateTimeStyle,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (endTimeText != null) {
                                    MySpacerRow(width = 4.dp)

                                    DisplayIcon(icon = MyIcons.rightArrowToSmall)

                                    MySpacerRow(width = 4.dp)

                                    Text(
                                        text = endTimeText,
                                        style = dateTimeStyle,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                        }
                    }

                    MySpacerRow(width = 8.dp)
                    Spacer(modifier = Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = tripTitleText,
                            style = tripTitleStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        MySpacerColumn(height = 4.dp)

                        Text(
                            text = dateTitleText ?: "a",
                            style = dateTimeStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}