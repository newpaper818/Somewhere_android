package com.example.somewhere.ui.tripScreenUtils.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.enumUtils.TimeFormat
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.tripScreenUtils.dialogs.SelectDateDialog
import com.example.somewhere.ui.tripScreenUtils.dialogs.SetTimeDialog
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.viewModel.DateTimeFormat
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
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AnimatedVisibility(
                visible = isEditMode,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
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
            }

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

    defaultTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
){
    val locale = LocalConfiguration.current.locales[0]
    val dateTitle = currentDate.titleText

    val dateText =
        if (dateTitle == null)
            currentSpot.getDateText(locale, dateTimeFormat, includeYear = true)
        else
            currentSpot.getDateText(locale, dateTimeFormat, includeYear = true)+ " - " + dateTitle

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

    val modifier =
        if (isEditMode){
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    showSelDateDialog = true
                    setShowBottomSaveCancelBar(false)
                }
        }
        else Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))


    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ){
                DisplayIcon(icon = MyIcons.date)
            }

            MySpacerRow(width = 16.dp)

            Text(
                text = dateText,
                style = defaultTextStyle
            )
        }
    }
}

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
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        MySpacerRow(width = 8.dp)

        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.Center
        ){
            DisplayIcon(icon = MyIcons.time)
        }


        MySpacerRow(width = 8.dp)

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

        MySpacerRow(width = 4.dp)

        DisplayIcon(icon = MyIcons.rightArrowTo)

        MySpacerRow(width = 4.dp)

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

@OptIn(ExperimentalMaterial3Api::class)
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

    val timeText1 = timeText ?: if (isStart) "Starts"
                                else         "Ends"

    val initialTime = if  (isStart) spot.startTime ?: LocalTime.now()
                        else        spot.endTime ?: LocalTime.now()


    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    if (showTimePicker){
        setUseImePadding(false)
        SetTimeDialog(
            initialTime = initialTime,
            timeFormat = timeFormat,
            title = "Set time",
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