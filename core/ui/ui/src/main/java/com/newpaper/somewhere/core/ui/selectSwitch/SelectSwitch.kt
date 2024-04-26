package com.newpaper.somewhere.core.ui.selectSwitch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.newpaper.somewhere.core.designsystem.component.utils.SelectSwitch
import com.newpaper.somewhere.core.designsystem.component.utils.SelectSwitchItem
import com.newpaper.somewhere.core.designsystem.icon.SelectSwitchIcon
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.core.ui.R

@Composable
fun AllowEditViewSelectSwitch(
    isAllowEdit: Boolean,
    setIsAllowEdit: (isAllowEdit: Boolean) -> Unit
){

    SelectSwitch {
        SelectSwitchItem(
            modifier = Modifier.weight(1f),
            isSelected = isAllowEdit,
            icon = SelectSwitchIcon.allowEdit,
            text = stringResource(id = R.string.allow_edit),
            onClick = {
                setIsAllowEdit(true)
            }
        )

        SelectSwitchItem(
            modifier = Modifier.weight(1f),
            isSelected = !isAllowEdit,
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