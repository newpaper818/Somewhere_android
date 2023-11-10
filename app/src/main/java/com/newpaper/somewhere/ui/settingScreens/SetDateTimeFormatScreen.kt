package com.newpaper.somewhere.ui.settingScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.enumUtils.DateFormat
import com.newpaper.somewhere.enumUtils.TimeFormat
import com.newpaper.somewhere.ui.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.settingScreenUtils.ItemWithRadioButton
import com.newpaper.somewhere.ui.settingScreenUtils.ItemDivider
import com.newpaper.somewhere.ui.settingScreenUtils.ItemWithSwitch
import com.newpaper.somewhere.ui.settingScreenUtils.ListGroupCard
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.utils.getDateText
import com.newpaper.somewhere.utils.getTimeText
import com.newpaper.somewhere.viewModel.DateTimeFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

object SetDateFormatDestination: NavigationDestination {
    override val route = "setDateTimeFormat"
    override var title = ""
}

@Composable
fun SetDateTimeFormatScreen(
    dateTimeFormat: DateTimeFormat,

    saveUserPreferences: (dateFormat: DateFormat?, useMonthName: Boolean?, includeDayOfWeek: Boolean?, timeFormat: TimeFormat?) -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){
    val locale = LocalConfiguration.current.locales[0]

    var dateFormat by rememberSaveable { mutableStateOf(dateTimeFormat.dateFormat) }
    var useMonthName by rememberSaveable { mutableStateOf(dateTimeFormat.useMonthName) }
    var includeDayOfWeek by rememberSaveable { mutableStateOf(dateTimeFormat.includeDayOfWeek) }
    var timeFormat by rememberSaveable { mutableStateOf(dateTimeFormat.timeFormat) }

    var dateText by rememberSaveable { mutableStateOf(getFixedDateText(locale, dateTimeFormat)) }
    var timeText by rememberSaveable { mutableStateOf(getFixedTimeText(dateTimeFormat)) }

    
    Scaffold(
        topBar = {
            SomewhereTopAppBar(
                title = stringResource(id = R.string.date_time_format),
                navigationIcon = MyIcons.back,
                navigationIconOnClick = { navigateUp() }
            )
        }
    ){ paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            //current date
            item {
                UpperDateTimeExampleBox(dateText, timeText)
            }

            //select time format 12h/24h
            item {
                ListGroupCard(title = stringResource(id = R.string.time_format)) {
                    SelectTimeFormat(
                        selectedTimeFormat = timeFormat,
                        onTimeFormatClick = {
                            if (timeFormat != it) {
                                timeFormat = it
                                timeText = getFixedTimeText(
                                    DateTimeFormat(
                                        dateFormat,
                                        useMonthName,
                                        includeDayOfWeek,
                                        it
                                    )
                                )
                                saveUserPreferences(null, null, null, it)
                            }
                        }
                    )
                }
            }


            item {

                //use month name
                ListGroupCard(title = stringResource(id = R.string.date_format)) {
                    ItemWithSwitch(
                        text = stringResource(id = R.string.use_month_name),
                        checked = useMonthName,
                        onCheckedChange = {
                            if (useMonthName != it) {
                                useMonthName = it
                                dateText = getFixedDateText(
                                    locale,
                                    DateTimeFormat(dateFormat, it, includeDayOfWeek, timeFormat)
                                )
                                saveUserPreferences(null, it, null, null)
                            }
                        }
                    )

                    //include day of month
                    ItemWithSwitch(
                        text = stringResource(id = R.string.include_day_of_week),
                        checked = includeDayOfWeek,
                        onCheckedChange = {
                            if (includeDayOfWeek != it) {
                                includeDayOfWeek = it
                                dateText = getFixedDateText(
                                    locale,
                                    DateTimeFormat(dateFormat, useMonthName, it, timeFormat)
                                )
                                saveUserPreferences(null, null, it, null)
                            }
                        }
                    )


                    ItemDivider()

                    //select date format list / radio button
                    SelectDateFormat(
                        selectedDateFormat = dateFormat,
                        onDateFormatChange = {
                            if (dateFormat != it) {
                                dateFormat = it
                                dateText = getFixedDateText(locale, DateTimeFormat(it, useMonthName, includeDayOfWeek, timeFormat))
                                saveUserPreferences(it, null, null, null)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun getFixedDateText(
    locale: Locale,
    dateTimeFormat: DateTimeFormat
): String{
    //app release date
    val localDate = LocalDate.of(2023,10,22)
    return getDateText(localDate, dateTimeFormat, true)
}

private fun getFixedTimeText(
    dateTimeFormat: DateTimeFormat
): String{
    val localTime = LocalTime.of(21,24)
    return getTimeText(localTime, dateTimeFormat.timeFormat)
}



@Composable
private fun UpperDateTimeExampleBox(
    dateText: String,
    timeText: String,
    textStyle: TextStyle = getTextStyle(TextType.MAIN)
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
    selectedTimeFormat: TimeFormat,
    onTimeFormatClick: (timeFormat: TimeFormat) -> Unit,
){
    val timeFormatList = rememberSaveable { enumValues<TimeFormat>() }

    Row(modifier = Modifier.padding(8.dp)) {

        for (timeFormat in timeFormatList){
            OneTimeFormat(
                timeFormat = timeFormat,
                isSelected = selectedTimeFormat == timeFormat,
                onClick = {
                    onTimeFormatClick(timeFormat)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OneTimeFormat(
    timeFormat: TimeFormat,
    isSelected: Boolean,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    unSelectedTextStyle: TextStyle = getTextStyle(TextType.GROUP_CARD_ITEM_BODY1),
    selectedTextStyle: TextStyle = getTextStyle(TextType.GROUP_CARD_ITEM_BODY1).copy(color = MaterialTheme.colorScheme.onPrimary)
){
    val cardColor = if (isSelected) MaterialTheme.colorScheme.primary
//        getColor(ColorType.CARD_SELECTED)
                    else            Color.Transparent

    val textStyle = if (isSelected) selectedTextStyle
                    else unSelectedTextStyle

    val haptic = LocalHapticFeedback.current

    ClickableBox(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        containerColor = cardColor,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
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
private fun SelectDateFormat(
    selectedDateFormat: DateFormat,
    onDateFormatChange: (DateFormat) -> Unit
){
    val dateFormatList = rememberSaveable { enumValues<DateFormat>() }

    for (dateFormat in dateFormatList){
        ItemWithRadioButton(
            isSelected = selectedDateFormat == dateFormat,
            text = stringResource(id = dateFormat.textId),
            onItemClick = {
                onDateFormatChange(dateFormat)
            }
        )
    }
}