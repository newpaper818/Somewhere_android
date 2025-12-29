package com.newpaper.somewhere.core.ui.card.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.convert.getEndTimeText
import com.newpaper.somewhere.core.utils.convert.getStartTimeText
import com.newpaper.somewhere.core.utils.enterVerticallyScaleIn
import com.newpaper.somewhere.core.utils.exitVerticallyScaleOut
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DateTimeCard(
    date: Date,
    spot: Spot,
    isEditMode: Boolean,
    dateTimeFormat: DateTimeFormat,

    onClickDate: () -> Unit,
    onClickTime: (isStart: Boolean) -> Unit,
    onClickDeleteTime: (isStart: Boolean) -> Unit,

    modifier: Modifier = Modifier,
){
    AnimatedVisibility(
        visible = isEditMode || spot.startTime != null || spot.endTime != null,
        enter = enterVerticallyScaleIn,
        exit = exitVerticallyScaleOut
    ) {
        Column {

            MyCard(
                modifier = modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    OneDateRow(
                        dateTimeFormat = dateTimeFormat,
                        isEditMode = isEditMode,
                        currentDate = date,
                        currentSpot = spot,
                        onClickDate = onClickDate
                    )

                    TwoTimesRow(
                        timeFormat = dateTimeFormat.timeFormat,
                        spot = spot,
                        isEditMode = isEditMode,
                        onClickTime = onClickTime,
                        onClickDeleteTime = onClickDeleteTime
                    )
                }
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
private fun OneDateRow(
    dateTimeFormat: DateTimeFormat,
    isEditMode: Boolean,
    currentDate: Date,
    currentSpot: Spot,

    onClickDate: () -> Unit
){
    val dateTitle = currentDate.titleText

    val dateText =
        if (dateTitle == null)
            currentSpot.getDateText(dateTimeFormat, includeYear = true)
        else
            stringResource(id = R.string.date_time_card_date_text, currentSpot.getDateText(dateTimeFormat, includeYear = true), dateTitle)

    IconTextRow(
        isVisible = isEditMode,
        isClickable = true,
        informationItem = dateItem.copy(
            text = dateText,
            onClick = onClickDate
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TwoTimesRow(
    timeFormat: TimeFormat,
    isEditMode: Boolean,
    spot: Spot,

    onClickTime: (isStart: Boolean) -> Unit,
    onClickDeleteTime: (isStart: Boolean) -> Unit
){
    var rowHeight by rememberSaveable { mutableIntStateOf(40) }
    val density = LocalDensity.current

    FlowRow(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .onSizeChanged { rowHeight = with(density){it.height.toDp()}.value.toInt() }
            .semantics(mergeDescendants = true) { }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(42.dp)
        ) {
            MySpacerRow(width = 8.dp)

            //time icon
            Box(
                modifier = Modifier.width(30.dp),
                contentAlignment = Alignment.Center
            ) {
                DisplayIcon(icon = MyIcons.time)
            }

            MySpacerRow(width = 8.dp)

            if (isEditMode || spot.startTime != null) {
                OneTimeRow(
                    spot = spot,
                    isStart = true,
                    isEditMode = isEditMode,
                    timeFormat = timeFormat,
                    onClickSetTime = { onClickTime(true) },
                    onClickDeleteTime = { onClickDeleteTime(true) }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(42.dp)
        ) {
            if (rowHeight > 50){
                MySpacerRow(width = 50.dp)
            }

            if (isEditMode || spot.endTime != null) {
                MySpacerRow(width = 4.dp)

                DisplayIcon(icon = MyIcons.rightArrowTo)

                MySpacerRow(width = 4.dp)

                OneTimeRow(
                    timeFormat = timeFormat,
                    isEditMode = isEditMode,
                    spot = spot,
                    isStart = false,
                    onClickSetTime = { onClickTime(false) },
                    onClickDeleteTime = { onClickDeleteTime(false) }
                )
            }
        }
    }
}

@Composable
private fun OneTimeRow(
    timeFormat: TimeFormat,
    isEditMode: Boolean,
    spot: Spot,
    isStart: Boolean,

    onClickSetTime: () -> Unit,
    onClickDeleteTime: () -> Unit
){
    val timeText = if (isStart) spot.getStartTimeText(timeFormat)
                    else         spot.getEndTimeText(timeFormat)

    val timeTextStyle = if (timeText == null) MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        else                  MaterialTheme.typography.bodyLarge

    val timeText1 = timeText ?: if (isStart) stringResource(id = R.string.date_time_card_starts)
                                else         stringResource(id = R.string.date_time_card_ends)

    val modifier =
        if (isEditMode){
            Modifier
                .clip(SmoothRoundedCornerShape(8.dp))
                .clickable {
                    onClickSetTime()
                }
        }
        else Modifier.clip(SmoothRoundedCornerShape(8.dp))

    MyCard(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            //time text
            Text(
                text = timeText1,
                style = timeTextStyle,
                letterSpacing = 0.7.sp
            )

            //delete icon
            AnimatedVisibility(
                visible = isEditMode,
                enter = expandHorizontally(tween(400)),
                exit = shrinkHorizontally(tween(400))
            ) {
                Row {
                    MySpacerRow(width = 8.dp)

                    MyPlainTooltipBox(
                        tooltipText = if (isStart) stringResource(id = MyIcons.deleteStartTime.descriptionTextId!!)
                                        else stringResource(id = MyIcons.deleteEndTime.descriptionTextId!!)
                    ) {
                        IconButton(
                            onClick = onClickDeleteTime,
                            modifier = Modifier.size(21.dp)
                        ) {
                            DisplayIcon(
                                icon = if (isStart) MyIcons.deleteStartTime
                                else MyIcons.deleteEndTime
                            )
                        }
                    }
                }
            }
        }
    }
}


























@Composable
@PreviewLightDark
private fun DateTimeCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            DateTimeCard(
                date = Date(
                    date = LocalDate.now(),
                    spotList = listOf(
                        Spot(id = 0, date = LocalDate.now())
                    )
                ),
                spot = Spot(
                    id = 0, date = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                ),
                isEditMode = false,
                dateTimeFormat = DateTimeFormat(),
                onClickDate = {},
                onClickTime = {},
                onClickDeleteTime = {}
            )
        }

    }
}

@Composable
@PreviewLightDark
private fun DateTimeCardPreviewEdit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            DateTimeCard(
                date = Date(
                    date = LocalDate.now(),
                    spotList = listOf(
                        Spot(id = 0, date = LocalDate.now())
                    )
                ),
                spot = Spot(
                    id = 0, date = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                ),
                isEditMode = true,
                dateTimeFormat = DateTimeFormat(),
                onClickDate = {},
                onClickTime = {},
                onClickDeleteTime = {}
            )
        }
    }
}
