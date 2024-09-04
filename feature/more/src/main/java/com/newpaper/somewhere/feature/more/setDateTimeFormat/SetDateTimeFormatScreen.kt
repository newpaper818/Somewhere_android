package com.newpaper.somewhere.feature.more.setDateTimeFormat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.ui.item.ItemWithRadioButton
import com.newpaper.somewhere.core.ui.item.ItemWithSwitch
import com.newpaper.somewhere.core.ui.item.ListGroupCard
import com.newpaper.somewhere.core.ui.selectSwitch.TimeFormatSelectSwitch
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.more.R
import kotlinx.coroutines.launch

@Composable
fun SetDateTimeFormatRoute(
    use2Panes: Boolean,
    spacerValue: Dp,
    dateTimeFormat: DateTimeFormat,
    updatePreferencesValue: () -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,

    setDateTimeFormatViewModel: SetDateTimeFormatViewModel = hiltViewModel()
){
    val setDateTimeFormatUiState by setDateTimeFormatViewModel.setDateTimeFormatUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        setDateTimeFormatViewModel.updateDateTimeExample(dateTimeFormat)
    }

    SetDateTimeFormatScreen(
        startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        endSpacerValue = spacerValue,
        dateTimeFormat = dateTimeFormat,
        dateExampleText = setDateTimeFormatUiState.dateExample,
        timeExampleText = setDateTimeFormatUiState.timeExample,
        saveUserPreferences = { timeFormat, useMonthName, includeDayOfWeek, dateFormat ->
            coroutineScope.launch {
                //save to dataStore
                setDateTimeFormatViewModel.saveDateTimeFormatUserPreferences(
                    dateFormat,
                    useMonthName,
                    includeDayOfWeek,
                    timeFormat
                )

                //update preferences at appViewModel
                updatePreferencesValue()

                //update date time example text
                setDateTimeFormatViewModel.updateDateTimeExample(
                    DateTimeFormat(
                        timeFormat = timeFormat ?: dateTimeFormat.timeFormat,
                        useMonthName = useMonthName ?: dateTimeFormat.useMonthName,
                        includeDayOfWeek = includeDayOfWeek ?: dateTimeFormat.includeDayOfWeek,
                        dateFormat = dateFormat ?: dateTimeFormat.dateFormat
                    )
                )
            }
        },
        navigateUp = navigateUp,
        modifier = modifier,
        use2Panes = use2Panes
    )
}

@Composable
private fun SetDateTimeFormatScreen(
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    dateTimeFormat: DateTimeFormat,

    dateExampleText: String,
    timeExampleText: String,

    saveUserPreferences: (timeFormat: TimeFormat?, useMonthName: Boolean?,
                          includeDayOfWeek: Boolean?, dateFormat: DateFormat?) -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    use2Panes: Boolean
){
    val dateFormat = dateTimeFormat.dateFormat
    val useMonthName = dateTimeFormat.useMonthName
    val includeDayOfWeek = dateTimeFormat.includeDayOfWeek
    val timeFormat = dateTimeFormat.timeFormat

    val dateFormatList = enumValues<DateFormat>()

    val scaffoldModifier = if (use2Panes) modifier
        else modifier.navigationBarsPadding()

    Scaffold(
        modifier = scaffoldModifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.date_time_format),
                navigationIcon = if (!use2Panes) TopAppBarIcon.back else null,
                onClickNavigationIcon = { navigateUp() }
            )
        }
    ){ paddingValues ->

        val itemModifier = Modifier.widthIn(max = itemMaxWidth)

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            //current date
            item {
                UpperDateTimeExampleBox(
                    dateText = dateExampleText,
                    timeText = timeExampleText,
                    modifier = itemModifier
                )
            }

            //select time format 12h/24h
            item {
                ListGroupCard(
                    title = stringResource(id = R.string.time_format),
                    modifier = itemModifier
                ) {
                    TimeFormatSelectSwitch(
                        is24h = timeFormat == TimeFormat.T24H,
                        setTimeFormat = { newTimeFormat ->
                            if (timeFormat != newTimeFormat) {
                                saveUserPreferences(newTimeFormat, null, null, null)
                            }
                        }
                    )
                }
            }


            item {

                ListGroupCard(
                    title = stringResource(id = R.string.date_format),
                    modifier = itemModifier
                ) {
                    //use month name
                    ItemWithSwitch(
                        text = stringResource(id = R.string.use_month_name),
                        checked = useMonthName,
                        onCheckedChange = { newUseMonthName ->
                            if (useMonthName != newUseMonthName) {
                                saveUserPreferences(null, newUseMonthName, null, null)
                            }
                        }
                    )

                    //include day of month
                    ItemWithSwitch(
                        text = stringResource(id = R.string.include_day_of_week),
                        checked = includeDayOfWeek,
                        onCheckedChange = { newIncludeDayOfWeek ->
                            if (includeDayOfWeek != newIncludeDayOfWeek) {
                                saveUserPreferences(null, null, newIncludeDayOfWeek, null)
                            }
                        }
                    )


                    ItemDivider()

                    //select date format list / radio button
                    for (oneDateFormat in dateFormatList){
                        ItemWithRadioButton(
                            isSelected = dateFormat == oneDateFormat,
                            text = stringResource(id = oneDateFormat.textId),
                            onItemClick = {
                                if (dateFormat != oneDateFormat) {
                                    saveUserPreferences(null, null, null, oneDateFormat)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UpperDateTimeExampleBox(
    dateText: String,
    timeText: String,
    modifier: Modifier = Modifier
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(120.dp)
            .padding(16.dp, 0.dp)
    ) {
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .padding(0.dp, 0.dp, 8.dp, 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.widthIn(min = 190.dp),
                    textAlign = TextAlign.Center
                )
            }


            Box(
                modifier = Modifier.height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.widthIn(min = 100.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}