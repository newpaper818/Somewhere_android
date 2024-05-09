package com.newpaper.somewhere.feature.dialog.dateRange

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.utils.getDateText
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_MAX_HEIGHT
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import java.time.LocalDate
import java.time.ZoneOffset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeDialog(
    defaultDateRange: ClosedRange<LocalDate>,
    dateTimeFormat: DateTimeFormat,
    onDismissRequest: () -> Unit,
    onConfirm: (startDateMillis: Long?, endDateMillis: Long?) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeightLong = configuration.screenHeightDp > 600

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = defaultDateRange.start.atStartOfDay()
            .toInstant(ZoneOffset.UTC).toEpochMilli(),
        initialSelectedEndDateMillis = defaultDateRange.endInclusive.atStartOfDay().toInstant(
            ZoneOffset.UTC
        ).toEpochMilli(),
        initialDisplayMode = if (screenHeightLong) DisplayMode.Picker else DisplayMode.Input
    )

    val startDateMillis = dateRangePickerState.selectedStartDateMillis
    val startDateText = if (startDateMillis != null) getDateText(startDateMillis, dateTimeFormat)
    else stringResource(id = R.string.starts)

    val endDateMillis = dateRangePickerState.selectedEndDateMillis
    val endDateText = if (endDateMillis != null) getDateText(endDateMillis, dateTimeFormat)
    else stringResource(id = R.string.ends)

    dateRangePickerState.displayMode

    val maxHeight = if (dateRangePickerState.displayMode == DisplayMode.Picker) DIALOG_DEFAULT_MAX_HEIGHT + 100.dp
    else if (screenHeightLong)  26.dp + 16.dp + 166.dp + 12.dp + 48.dp
    else                        26.dp + 16.dp + 130.dp + 12.dp + 48.dp
    //166 131
    MyDialogForDatePicker(
        onDismissRequest = onDismissRequest,
        maxHeight = maxHeight,

        titleText = stringResource(id = R.string.set_date_range),
        bodyContent = {
            Column(
                modifier = Modifier.heightIn(min = 0.dp)
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    title = null,
                    headline = {
                        Row {
                            MySpacerRow(width = 16.dp)
                            Text(
                                text = stringResource(id = R.string.date_range_headline, startDateText, endDateText),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 2
                            )
                        }
                    },
                    showModeToggle = screenHeightLong,
                    colors = DatePickerDefaults.colors(
                        headlineContentColor = MaterialTheme.colorScheme.onSurface,
                        subheadContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.button_ok),
                    enabled = (dateRangePickerState.selectedStartDateMillis != null &&
                            dateRangePickerState.selectedEndDateMillis != null),
                    onClick = {
                        onConfirm(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    }
                )
            }
        }
    )
}

//no horizontal padding
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyDialogForDatePicker(
    onDismissRequest: () -> Unit,
    maxHeight: Dp,

    modifier: Modifier = Modifier,
    titleText: String? = null,
    bodyContent: @Composable() (() -> Unit)? = null,
    buttonContent: @Composable() (() -> Unit)? = null,
){
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.wrapContentHeight(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = modifier
                    .padding(0.dp, 16.dp)
                    .heightIn(max = maxHeight),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                //title text / align center
                if (titleText != null) {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleSmall
                    )

                    MySpacerColumn(height = 16.dp)
                }

                //body
                if (bodyContent != null) {
                    Column(Modifier.weight(1f)) {
                        bodyContent()
                    }

                    MySpacerColumn(height = 12.dp)
                }

                //buttons
                if (buttonContent != null) {
                    buttonContent()
                }
            }
        }
    }
}
























@Composable
@PreviewLightDark
private fun Preview_DateRangeDialog(){
    SomewhereTheme {
        MyScaffold {
//            DateRangeDialog(
//                defaultDateRange = LocalDate.now().plusDays(2) .. LocalDate.now().plusDays(5),
//                dateTimeFormat = DateTimeFormat(),
//                onDismissRequest = {},
//                onConfirm = {_, _->}
//            )
        }
    }
}