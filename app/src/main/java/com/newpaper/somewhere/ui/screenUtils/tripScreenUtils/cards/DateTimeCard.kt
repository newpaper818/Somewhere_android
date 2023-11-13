package com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.enumUtils.TimeFormat
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Spot
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.SelectDateDialog
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.SetTimeDialog
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.viewModel.DateTimeFormat
import java.time.LocalTime

@Composable
fun DateTimeCard(
    setUseImePadding: (useImePadding: Boolean) -> Unit,
    dateList: List<Date>,
    date: Date,
    spot: Spot,
    isEditMode: Boolean,

    dateTimeFormat: DateTimeFormat,

    setShowBottomSaveCancelBar: (Boolean) -> Unit,
    changeDate: (dateId: Int) -> Unit,
    setStartTime: (startTime: LocalTime?) -> Unit,
    setEndTime: (endTime: LocalTime?) -> Unit,

    modifier: Modifier = Modifier,
){
    AnimatedVisibility(
        visible = isEditMode || spot.startTime != null || spot.endTime != null,
        enter = scaleIn(animationSpec = tween(300))
                + expandVertically(animationSpec = tween(300))
                + fadeIn(animationSpec = tween(300)),
        exit = scaleOut(animationSpec = tween(300))
                + shrinkVertically(animationSpec = tween(300))
                + fadeOut(animationSpec = tween(300))
    ) {
        Column {
            Card(
                modifier = modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    OneDateRow(
                        setUseImePadding = setUseImePadding,
                        setShowBottomSaveCancelBar = setShowBottomSaveCancelBar,
                        dateList = dateList,
                        currentDate = date,
                        currentSpot = spot,
                        dateTimeFormat = dateTimeFormat,
                        isEditMode = isEditMode,
                        changeDate = changeDate
                    )

                    TwoTimesRow(
                        setUseImePadding = setUseImePadding,
                        setShowBottomSaveCancelBar = setShowBottomSaveCancelBar,
                        spot = spot,
                        isEditMode = isEditMode,
                        setStartTime = setStartTime,
                        setEndTime = setEndTime,
                        timeFormat = dateTimeFormat.timeFormat
                    )
                }
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
private fun OneDateRow(
    setUseImePadding: (useImePadding: Boolean) -> Unit,
    setShowBottomSaveCancelBar: (Boolean) -> Unit,
    dateList: List<Date>,
    currentDate: Date,
    currentSpot: Spot,
    dateTimeFormat: DateTimeFormat,
    isEditMode: Boolean,
    changeDate: (dateId: Int) -> Unit,
){
    val dateTitle = currentDate.titleText

    val dateText =
        if (dateTitle == null)
            currentSpot.getDateText(dateTimeFormat, includeYear = true)
        else
            stringResource(id = R.string.date_time_card_date_text, currentSpot.getDateText(dateTimeFormat, includeYear = true), dateTitle)

    var showSelDateDialog by rememberSaveable { mutableStateOf(false) }

    if (showSelDateDialog){
        setUseImePadding(false)
        SelectDateDialog(
            dateTimeFormat = dateTimeFormat,
            initialDate = currentDate,
            dateList = dateList,
            onOkClick = { dateId ->
                changeDate(dateId)
                showSelDateDialog = false
                setShowBottomSaveCancelBar(true)
            },
            onDismissRequest = {
                showSelDateDialog = false
                setShowBottomSaveCancelBar(true)
            }
        )
    }

    IconTextRow(
        isVisible = isEditMode,
        isClickable = true,
        informationItem = dateItem.copy(text = dateText,
            onClick = {
                showSelDateDialog = true
                setShowBottomSaveCancelBar(false)
            })
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TwoTimesRow(
    setUseImePadding: (useImePadding: Boolean) -> Unit,
    setShowBottomSaveCancelBar: (Boolean) -> Unit,
    spot: Spot,
    isEditMode: Boolean,
    setStartTime: (startTime: LocalTime?) -> Unit,
    setEndTime: (startTime: LocalTime?) -> Unit,
    timeFormat: TimeFormat
){
    var rowHeight by rememberSaveable { mutableIntStateOf(40) }
    val density = LocalDensity.current

    FlowRow(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.onSizeChanged { rowHeight = with(density){it.height.toDp()}.value.toInt() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            MySpacerRow(width = 8.dp)

            //time icon
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                DisplayIcon(icon = MyIcons.time)
            }

            MySpacerRow(width = 8.dp)

            if (isEditMode || spot.startTime != null) {
                OneTimeRow(
                    setUseImePadding = setUseImePadding,
                    setShowBottomSaveCancelBar = setShowBottomSaveCancelBar,
                    spot = spot,
                    isStart = true,
                    isEditMode = isEditMode,
                    setTime = { startTime ->
                        setStartTime(startTime)
                    },
                    timeFormat = timeFormat
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (rowHeight > 50){
                MySpacerRow(width = 50.dp)
            }

            if (isEditMode || spot.startTime != null && spot.endTime != null) {
                MySpacerRow(width = 4.dp)

                DisplayIcon(icon = MyIcons.rightArrowTo)

                MySpacerRow(width = 4.dp)
            }

            if (isEditMode || spot.endTime != null) {
                OneTimeRow(
                    setUseImePadding = setUseImePadding,
                    setShowBottomSaveCancelBar = setShowBottomSaveCancelBar,
                    spot = spot,
                    isStart = false,
                    isEditMode = isEditMode,
                    setTime = { endTime ->
                        setEndTime(endTime)
                    },
                    timeFormat = timeFormat
                )
            }
        }
    }
}

@Composable
private fun OneTimeRow(
    setUseImePadding: (useImePadding: Boolean) -> Unit,
    setShowBottomSaveCancelBar: (Boolean) -> Unit,
    spot: Spot,
    isStart: Boolean,
    isEditMode: Boolean,
    setTime: (time: LocalTime?) -> Unit,
    timeFormat: TimeFormat,

    defaultTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    nullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    val timeText = if (isStart) spot.getStartTimeText(timeFormat)
                    else         spot.getEndTimeText(timeFormat)

    val timeTextStyle = if (timeText == null) nullTextStyle
                        else                  defaultTextStyle

    val timeText1 = timeText ?: if (isStart) stringResource(id = R.string.date_time_card_starts)
                                else         stringResource(id = R.string.date_time_card_ends)

    val initialTime = if  (isStart) spot.startTime ?: LocalTime.of(12,0)
                        else        spot.endTime ?: LocalTime.of(12,0)


    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    if (showTimePicker){
        setUseImePadding(false)
        SetTimeDialog(
            initialTime = initialTime,
            timeFormat = timeFormat,
            isSetStartTime = isStart,
            onDismissRequest = {
                showTimePicker = false
                setShowBottomSaveCancelBar(true)
            },
            onConfirm = {newTime_ ->
                setTime(newTime_)
                showTimePicker = false
                setShowBottomSaveCancelBar(true)
            }
        )
    }

    val modifier =
        if (isEditMode){
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    showTimePicker = true
                    setShowBottomSaveCancelBar(false)
                }
        }
        else Modifier.clip(RoundedCornerShape(8.dp))

    Card(
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
            )

            //delete icon
            AnimatedVisibility(
                visible = isEditMode,
                enter = expandHorizontally(tween(400)),
                exit = shrinkHorizontally(tween(400))
            ) {
                Row {
                    MySpacerRow(width = 8.dp)

                    IconButton(
                        onClick = { setTime(null) },
                        modifier = Modifier.size(22.dp)
                    ) {
                        DisplayIcon(icon = MyIcons.delete)
                    }
                }
            }
        }
    }
}