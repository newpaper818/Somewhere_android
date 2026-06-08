package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newpaper.somewhere.core.designsystem.component.button.ToGoogleMapsButton
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
import com.newpaper.somewhere.feature.trip.trips.GlanceSpot
import com.newpaper.somewhere.feature.trip.trips.GlanceSpots
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

private val DUMMY_SPACE_HEIGHT: Dp = 10.dp
private val MIN_CARD_HEIGHT: Dp = 40.dp
private val ADDITIONAL_HEIGHT: Dp = 22.dp
private val POINT_CIRCLE_SIZE: Dp = 24.dp
private val LINE_WIDTH: Dp = 7.dp


@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun GlanceSpotGroup(
    showTopTripTitleWithDate: Boolean,

    uesLongWidth: Boolean,
    visible: Boolean,
    dateTimeFormat: DateTimeFormat,
    glanceSpots: GlanceSpots,

    onClickGlanceSpot: (GlanceSpot) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,

    enterAnimation: EnterTransition = slideInVertically(
        animationSpec = tween(500),
        initialOffsetY = { (it * 2.5f).toInt() }),
    exitAnimation: ExitTransition = slideOutVertically(
        animationSpec = tween(500),
        targetOffsetY = { (it * 2.5f).toInt() })
){
    val containerColor = if (hazeState == null) MaterialTheme.colorScheme.surfaceBright
                        else Color.Transparent

    val hazeTintColor = MaterialTheme.colorScheme.surfaceBright

    val glanceSpotModifier = modifier
        .navigationBarsPadding()
        .widthIn(max = if (uesLongWidth) 600.dp else 400.dp)
        .padding(horizontal = 16.dp)
        .clip(MaterialTheme.shapes.large)

    val glanceSpotHazeModifier = if (hazeState == null) glanceSpotModifier
        else glanceSpotModifier.hazeEffect(state = hazeState) {
            blurRadius = 16.dp
            tints = listOf(HazeTint(hazeTintColor.copy(alpha = 0.8f)))
            inputScale = HazeInputScale.Fixed(0.5f)
        }




    AnimatedVisibility(
        visible = visible,
        enter = enterAnimation,
        exit = exitAnimation
    ) {
        Column(
            modifier = glanceSpotHazeModifier
                .background(containerColor)
        ) {
            // trip title with date
            if (showTopTripTitleWithDate && glanceSpots.spots.isNotEmpty()){
                val trip = glanceSpots.spots[0].trip
                val date = glanceSpots.spots[0].date

                val tripTitleText = if (trip.titleText == null || trip.titleText == "") stringResource(id = R.string.no_title)
                                    else trip.titleText!!

                val dateTitleText = date.titleText ?: date.getDateText(dateTimeFormat, false)

                TripTitleWithDay(
                    tripTitle = tripTitleText,
                    dateTitle = dateTitleText
                )
            }

            // spots
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceDim, MaterialTheme.shapes.large)
            ) {
                glanceSpots.spots.forEach { glanceSpot ->
                    GlanceSpotItem(
                        showTripTitleWithDate = !showTopTripTitleWithDate,
                        dateTimeFormat = dateTimeFormat,
                        trip = glanceSpot.trip,
                        date = glanceSpot.date,
                        spot = glanceSpot.spot,
                        onClick = { onClickGlanceSpot(glanceSpot) }
                    )
                }
            }
        }
    }
}


@Composable
private fun TripTitleWithDay(
    tripTitle: String,
    dateTitle: String
){
    val textStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(24.dp, 8.dp, 24.dp, 8.dp)
            .height(20.dp)
    ) {
        Text(
            text = tripTitle,
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        MySpacerRow(4.dp)
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = dateTitle,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}




@OptIn(ExperimentalHazeApi::class)
@Composable
private fun GlanceSpotItem(
    showTripTitleWithDate: Boolean,

    dateTimeFormat: DateTimeFormat,
    trip: Trip,
    date: Date,
    spot: Spot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val tripTitleText = if (trip.titleText == null || trip.titleText == "") stringResource(id = R.string.no_title)
                        else trip.titleText!!

    val dateTitleText = date.titleText ?: date.getDateText(dateTimeFormat, false)

    val spotTitleText = if (spot.titleText == null || spot.titleText == "") stringResource(id = R.string.no_title)
                        else spot.titleText!!

    val startTimeText = spot.getStartTimeText(dateTimeFormat.timeFormat)
    val endTimeText = spot.getEndTimeText(dateTimeFormat.timeFormat)


    val tripTitleStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)

    val dateTimeStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)

    val spotTitleStyle = if (spot.titleText == null || spot.titleText == "")
        MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onSurfaceVariant)
    else MaterialTheme.typography.bodyLarge

    val upperLineColor =
        if (spot.isFirstSpot(
                dateIndex = date.id,
                dateList = trip.dateList,
                spotList = date.spotList
            )) Color.Transparent
        else MaterialTheme.colorScheme.outline

    val lowerLineColor =
        if (spot.isLastSpot(
                dateIndex = date.id,
                dateList = trip.dateList,
                spotList = date.spotList
            )) Color.Transparent
        else MaterialTheme.colorScheme.outline






    ClickableBox(
        onClick = onClick,
        shape = RectangleShape,
        modifier = modifier
            .height(70.dp)
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
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(10.dp, 8.dp, 8.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //spot title / time text
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
                                    overflow = TextOverflow.Ellipsis,
                                    letterSpacing = 0.7.sp
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
                                    overflow = TextOverflow.Ellipsis,
                                    letterSpacing = 0.7.sp
                                )
                            }
                        }

                    }
                }

                MySpacerRow(width = 8.dp)
                Spacer(modifier = Modifier.weight(1f))

                //trip title / date text
                if (showTripTitleWithDate) {
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
                            text = dateTitleText,
                            style = dateTimeStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }


            }

            //google maps button
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 16.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!((spot.titleText == null || spot.titleText == "")
                       && (spot.googleMapsPlacesId == null || spot.googleMapsPlacesId == ""))){
                    ToGoogleMapsButton(
                        enabled = true,
                        googleMapsUrl = spot.getGoogleMapsUrl(),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceBright)
                            .border(1.dp, MaterialTheme.colorScheme.surfaceDim, CircleShape)
                    )
                }
            }
        }
    }
}