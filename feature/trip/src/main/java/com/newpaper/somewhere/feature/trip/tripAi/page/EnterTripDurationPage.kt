package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.utils.getDateText
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.core.utils.millisToLocalDate
import com.newpaper.somewhere.feature.trip.R
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EnterTripDurationPage(
    dateTimeFormat: DateTimeFormat,
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    setStartDate: (LocalDate?) -> Unit,
    setEndDate: (LocalDate?) -> Unit,
    onErrorTextVisible: (Boolean) -> Unit
){
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartDate?.atStartOfDay()
            ?.toInstant(ZoneOffset.UTC)?.toEpochMilli(),
        initialSelectedEndDateMillis = initialEndDate?.atStartOfDay()
            ?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    )

    val startDate =
        if (dateRangePickerState.selectedStartDateMillis != null)
            millisToLocalDate(dateRangePickerState.selectedStartDateMillis!!)
        else null

    val endDate =
        if (dateRangePickerState.selectedEndDateMillis != null)
            millisToLocalDate(dateRangePickerState.selectedEndDateMillis!!)
        else null

    val errorTextVisible =
        startDate != null
        && endDate != null
        && startDate.plusDays(15) <= endDate

    LaunchedEffect(errorTextVisible) {
        onErrorTextVisible(errorTextVisible)
    }

    //set start, end date
    LaunchedEffect(dateRangePickerState.selectedStartDateMillis) {
        setStartDate(
            if (dateRangePickerState.selectedStartDateMillis != null)
                millisToLocalDate(dateRangePickerState.selectedStartDateMillis!!)
            else null
        )
    }

    LaunchedEffect(dateRangePickerState.selectedEndDateMillis) {
        setEndDate(
            if (dateRangePickerState.selectedEndDateMillis != null)
                millisToLocalDate(dateRangePickerState.selectedEndDateMillis!!)
            else null
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 35.dp),
    ) {
        item {
            TripAiPage(
                title = stringResource(id = R.string.when_are_you_going),
                subTitle = stringResource(id = R.string.choose_a_date_range),
                content = {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 600.dp)
                            .widthIn(max = itemMaxWidth)
                    ) {
                        MyDateRangePicker(
                            dateTimeFormat = dateTimeFormat,
                            dateRangePickerState = dateRangePickerState,
                            modifier =
                            if (dateRangePickerState.displayMode == DisplayMode.Picker)
                                Modifier.weight(1f)
                            else Modifier
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangeText(
    dateTimeFormat: DateTimeFormat,
    dateRangePickerState: DateRangePickerState
){
    val startDateMillis = dateRangePickerState.selectedStartDateMillis
    val startDateText = if (startDateMillis != null) getDateText(startDateMillis, dateTimeFormat)
        else stringResource(id = R.string.starts)

    val endDateMillis = dateRangePickerState.selectedEndDateMillis
    val endDateText = if (endDateMillis != null) getDateText(endDateMillis, dateTimeFormat)
        else stringResource(id = R.string.ends)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.date_range, startDateText, endDateText),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyDateRangePicker(
    dateTimeFormat: DateTimeFormat,
    dateRangePickerState: DateRangePickerState,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceBright)
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = null,
            headline = {
                DateRangeText(
                    dateTimeFormat = dateTimeFormat,
                    dateRangePickerState = dateRangePickerState
                )
            },
            showModeToggle = true,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent,
                headlineContentColor = MaterialTheme.colorScheme.onSurface,
                subheadContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}