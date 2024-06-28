package com.newpaper.somewhere.feature.dialog.setTime

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_WIDTH
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog
import java.time.LocalTime

//TODO separate timePickerState?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTimeDialog(
    initialTime: LocalTime,
    timeFormat: TimeFormat,
    isSetStartTime: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (newTime: LocalTime) -> Unit
){
    val configuration = LocalConfiguration.current
    val screenHeightLong = configuration.screenHeightDp > 600

    val title = if (isSetStartTime) stringResource(id = R.string.set_start_time)
                else    stringResource(id = R.string.set_end_time)

    var isTouchInput by rememberSaveable { mutableStateOf(screenHeightLong) }

    val layoutType = TimePickerDefaults.layoutType()

    val width = if (layoutType == TimePickerLayoutType.Horizontal)   570.dp
                else DIALOG_DEFAULT_WIDTH

    val maxHeight = if (layoutType == TimePickerLayoutType.Horizontal && isTouchInput) 26.dp + 16.dp + 280.dp + 48.dp
                else if (layoutType == TimePickerLayoutType.Vertical && isTouchInput) 26.dp + 16.dp + 396.dp + 48.dp
                else                                                                    26.dp + 16.dp + 91.dp + 48.dp

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = timeFormat == TimeFormat.T24H
    )

    val colors = TimePickerDefaults.colors(
        clockDialColor = MaterialTheme.colorScheme.surfaceBright,
        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceBright,
        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface
    )

    MyDialog(
        onDismissRequest = onDismissRequest,
        width = width,
        setMaxHeight = true,
        maxHeight = maxHeight,

        titleText = title,
        setBodySpacer = false,
        bodyContent = {
            if (isTouchInput) TimePicker(state = timePickerState, colors = colors)
            else              TimeInput(state = timePickerState, colors = colors)
        },
        buttonContent = {
            Row {
                //switch to touch/text input icon button
                if (screenHeightLong) {
                    IconButton(onClick = { isTouchInput = !isTouchInput }) {
                        if (isTouchInput) DisplayIcon(icon = MyIcons.switchToTextInput)
                        else DisplayIcon(icon = MyIcons.switchToTouchInput)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }

                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.button_ok),
                    onClick = {
                        onConfirm(
                            LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        )
                    }
                )
            }
        }
    )
}
























@Composable
@PreviewLightDark
private fun Preview_SetTimeDialog(){
    SomewhereTheme {
        MyScaffold {
            SetTimeDialog(
                initialTime = LocalTime.now(),
                timeFormat = TimeFormat.T12H,
                isSetStartTime = true,
                onDismissRequest = {},
                onConfirm = {_->}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_SetTimeDialog_24h(){
    SomewhereTheme {
        MyScaffold {
            SetTimeDialog(
                initialTime = LocalTime.now(),
                timeFormat = TimeFormat.T24H,
                isSetStartTime = true,
                onDismissRequest = {},
                onConfirm = {_->}
            )
        }
    }
}