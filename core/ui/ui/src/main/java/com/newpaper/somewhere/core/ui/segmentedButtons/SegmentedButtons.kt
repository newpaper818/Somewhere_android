package com.newpaper.somewhere.core.ui.segmentedButtons

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySegmentedButtons
import com.newpaper.somewhere.core.designsystem.component.utils.SegmentedButtonItem
import com.newpaper.somewhere.core.designsystem.icon.SelectSwitchIcon
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.itemMaxWidth

@Composable
fun AllowEditViewSegmentedButtons(
    isEditable: Boolean,
    setIsAllowEdit: (isAllowEdit: Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    MySegmentedButtons(
        modifier = modifier.widthIn(max = 330.dp),
        initSelectedItemIndex = if (isEditable) 0 else 1,
        itemList = listOf(
            //allow edit
            SegmentedButtonItem(
                icon = SelectSwitchIcon.allowEdit,
                text = stringResource(id = R.string.allow_edit),
                onClick = { setIsAllowEdit(true) }
            ),
            //view only
            SegmentedButtonItem(
                icon = SelectSwitchIcon.viewOnly,
                text = stringResource(id = R.string.view_only),
                onClick = { setIsAllowEdit(false) }
            )
        )
    )
}

@Composable
fun TimeFormatSegmentedButtons(
    is24h: Boolean,
    setTimeFormat: (timeFormat: TimeFormat) -> Unit
){

    MySegmentedButtons(
        modifier = Modifier.widthIn(max = itemMaxWidth),
        initSelectedItemIndex = if (!is24h) 0 else 1,
        itemList = listOf(
            SegmentedButtonItem(
                text = stringResource(id = R.string._12h),
                onClick = { setTimeFormat(TimeFormat.T12H) }
            ),
            SegmentedButtonItem(
                text = stringResource(id = R.string._24h),
                onClick = { setTimeFormat(TimeFormat.T24H) }
            )
        )
    )
}