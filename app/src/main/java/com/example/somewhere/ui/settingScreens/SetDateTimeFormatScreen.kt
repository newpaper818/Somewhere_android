package com.example.somewhere.ui.settingScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.enumUtils.DateFormat
import com.example.somewhere.enumUtils.TimeFormat
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MyRadioButton
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screenUtils.MySwitch
import com.example.somewhere.ui.screens.SomewhereTopAppBar
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.utils.getDateText
import com.example.somewhere.utils.getTimeText
import com.example.somewhere.viewModel.DateTimeFormat
import java.time.LocalDate
import java.time.LocalTime

object SetDateFormatDestination: NavigationDestination {
    override val route = "setDateTimeFormat"
    override var title = "Date and Time"
}

@Composable
fun SetDateTimeFormatScreen(
    dateTimeFormat: DateTimeFormat,

    saveUserPreferences: (dateFormat: DateFormat?, useMonthName: Boolean?, includeDayOfWeek: Boolean?, timeFormat: TimeFormat?) -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){
    
    var dateFormat by rememberSaveable { mutableStateOf(dateTimeFormat.dateFormat) }
    var useMonthName by rememberSaveable { mutableStateOf(dateTimeFormat.useMonthName) }
    var includeDayOfWeek by rememberSaveable { mutableStateOf(dateTimeFormat.includeDayOfWeek) }
    var timeFormat by rememberSaveable { mutableStateOf(dateTimeFormat.timeFormat) }

    val dateFormatList by rememberSaveable { mutableStateOf(enumValues<DateFormat>()) }

    var dateText by rememberSaveable { mutableStateOf(getFixedDateText(dateTimeFormat)) }
    var timeText by rememberSaveable { mutableStateOf(getFixedTimeText(dateTimeFormat)) }

    
    Scaffold(
        topBar = {
            SomewhereTopAppBar(
                title = SetDateFormatDestination.title,
                navigationIcon = MyIcons.back,
                navigationIconOnClick = { navigateUp() }
            )
        }
    ){ paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
                .background(MaterialTheme.colors.background)
        ) {
            //current date
            item {
                UpperDateExampleBox(dateText, timeText)
            }

            item {
                //select time format 12h/24h
                SelectTimeFormat(
                    currentTimeFormat = timeFormat,
                    onTimeFormatClick = {
                        if (timeFormat != it) {
                            timeFormat = it
                            timeText = getFixedTimeText(DateTimeFormat(dateFormat, useMonthName, includeDayOfWeek, it))
                            saveUserPreferences(null, null, null, it)
                        }
                    }
                )
            }

            item {
                Card(
                    elevation = 0.dp,
                    backgroundColor = getColor(ColorType.CARD),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        //use month name / include day of month
                        TwoSwitch(
                            isMonthString = useMonthName,
                            includeDayOfWeek = includeDayOfWeek,
                            onUseMonthNameChange = {
                                if (useMonthName != it) {
                                    useMonthName = it
                                    dateText = getFixedDateText(DateTimeFormat(dateFormat, it, includeDayOfWeek, timeFormat))
                                    saveUserPreferences(null, it, null, null)
                                }
                            },
                            onIncludeDayOfWeekChange = {
                                if (includeDayOfWeek != it) {
                                    includeDayOfWeek = it
                                    dateText = getFixedDateText(DateTimeFormat(dateFormat, useMonthName, it, timeFormat))
                                    saveUserPreferences(null, null, it, null)
                                }
                            }
                        )

                        MyDivider()

                        //select date format list / radio button
                        SelectDateFormatColumn(
                            currentDateFormat = dateFormat,
                            dateFormatList = dateFormatList,
                            onDateFormatChange = {
                                if (dateFormat != it) {
                                    dateFormat = it
                                    dateText = getFixedDateText(DateTimeFormat(it, useMonthName, includeDayOfWeek, timeFormat))
                                    saveUserPreferences(it, null, null, null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getFixedDateText(
    dateTimeFormat: DateTimeFormat
): String{
    //app release date
    val localDate = LocalDate.of(2023,10,20)
    return getDateText(localDate, dateTimeFormat, true)
}

private fun getFixedTimeText(
    dateTimeFormat: DateTimeFormat
): String{
    val localTime = LocalTime.of(14,0)
    return getTimeText(localTime, dateTimeFormat.timeFormat)
}

@Composable
fun MyDivider(

){
    Row {
        MySpacerRow(width = 16.dp)
        Divider(modifier = Modifier.weight(1f))
        MySpacerRow(width = 16.dp)
    }
}

@Composable
private fun UpperDateExampleBox(
    dateText: String,
    timeText: String,
    textStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE)
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Row {
            Text(
                text = dateText,
                style = textStyle,
                modifier = Modifier.widthIn(min = 190.dp),
                textAlign = TextAlign.Start
            )

            MySpacerRow(width = 8.dp)

            Text(
                text = timeText,
                style = textStyle,
                modifier = Modifier.widthIn(min = 100.dp),
                textAlign = TextAlign.Start
            )
        }

    }
}

@Composable
private fun SelectTimeFormat(
    currentTimeFormat: TimeFormat,
    onTimeFormatClick: (timeFormat: TimeFormat) -> Unit,
){
    Card(
        elevation = 0.dp,
        backgroundColor = getColor(ColorType.CARD),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {

            OneTimeFormat(
                timeFormat = TimeFormat.T12H,
                isSelected = currentTimeFormat == TimeFormat.T12H,
                onClick = {
                    onTimeFormatClick(TimeFormat.T12H)
                },
                modifier = Modifier.weight(1f)
            )

            OneTimeFormat(
                timeFormat = TimeFormat.T24H,
                isSelected = currentTimeFormat == TimeFormat.T24H,
                onClick = {
                    onTimeFormatClick(TimeFormat.T24H)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OneTimeFormat(
    timeFormat: TimeFormat,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY).copy(color = MaterialTheme.colors.onSurface)
){
    val cardColor = if(isSelected) getColor(ColorType.CARD_SELECTED)
                    else            getColor(ColorType.CARD)

    val haptic = LocalHapticFeedback.current

    Card(
        elevation = 0.dp,
        backgroundColor = cardColor,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = stringResource(id = timeFormat.textId),
                style = textStyle
            )
        }
    }
}

@Composable
private fun TwoSwitch(
    isMonthString: Boolean,
    includeDayOfWeek: Boolean,
    onUseMonthNameChange: (Boolean) -> Unit,
    onIncludeDayOfWeekChange: (Boolean) -> Unit
){
   TextWithSwitchRow(
       text = stringResource(id = R.string.use_month_name),
       checked = isMonthString,
       onCheckedChange = onUseMonthNameChange
   )

   TextWithSwitchRow(
       text = stringResource(id = R.string.include_day_of_week),
       checked = includeDayOfWeek,
       onCheckedChange = onIncludeDayOfWeekChange
   )
}

@Composable
private fun TextWithSwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY)
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(16.dp, 0.dp)
    ) {
        Text(
            text = text,
            style = textStyle
        )

        Spacer(modifier = Modifier.weight(1f))

        MySwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SelectDateFormatColumn(
    currentDateFormat: DateFormat,
    dateFormatList: Array<DateFormat>,
    onDateFormatChange: (DateFormat) -> Unit
){
    for (dateFormat in dateFormatList){
        OneDateFormatItem(
            isSelected = currentDateFormat == dateFormat,
            dateFormat = dateFormat,
            onItemClick = {
                onDateFormatChange(dateFormat)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OneDateFormatItem(
    isSelected: Boolean,
    dateFormat: DateFormat,
    onItemClick: () -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY)
){
    Card(
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(60.dp),
        onClick = onItemClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 0.dp)
        ) {
            MyRadioButton(
                selected = isSelected,
                onClick = onItemClick
            )
            
            MySpacerRow(width = 8.dp)

            Text(
                text = stringResource(id = dateFormat.textId),
                style = textStyle
            )
        }
    }
}