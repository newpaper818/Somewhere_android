package com.newpaper.somewhere.core.ui.selectSwitch

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.SelectSwitch
import com.newpaper.somewhere.core.designsystem.component.utils.SelectSwitchItem
import com.newpaper.somewhere.core.designsystem.icon.SelectSwitchIcon
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.core.ui.ui.R

@Composable
fun AllowEditViewSelectSwitch(
    isEditable: Boolean,
    setIsAllowEdit: (isAllowEdit: Boolean) -> Unit
){
    SelectSwitch(
        modifier = Modifier.widthIn(max = 330.dp)
    ) {
        SelectSwitchItem(
            modifier = Modifier.weight(1f),
            isSelected = isEditable,
            icon = SelectSwitchIcon.allowEdit,
            text = stringResource(id = R.string.allow_edit),
            onClick = {
                setIsAllowEdit(true)
            }
        )

        SelectSwitchItem(
            modifier = Modifier.weight(1f),
            isSelected = !isEditable,
            icon = SelectSwitchIcon.viewOnly,
            text = stringResource(id = R.string.view_only),
            onClick = {
                setIsAllowEdit(false)
            }
        )
    }
}

@Composable
fun TimeFormatSelectSwitch(
    is24h: Boolean,
    setTimeFormat: (timeFormat: TimeFormat) -> Unit
){

    SelectSwitch {
        SelectSwitchItem(
            modifier = Modifier.weight(1f),
            isSelected = !is24h,
            text = stringResource(id = R.string._12h),
            onClick = {
                setTimeFormat(TimeFormat.T12H)
            }
        )

        SelectSwitchItem(
            modifier = Modifier.weight(1f),
            isSelected = is24h,
            text = stringResource(id = R.string._24h),
            onClick = {
                setTimeFormat(TimeFormat.T24H)
            }
        )
    }
}