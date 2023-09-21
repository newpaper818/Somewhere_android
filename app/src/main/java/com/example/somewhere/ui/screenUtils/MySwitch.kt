package com.example.somewhere.ui.screenUtils

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.somewhere.ui.theme.gray

@Composable
fun MySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Switch(
        checked = checked,
        onCheckedChange = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCheckedChange(it)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colors.onPrimary,
            checkedTrackColor = MaterialTheme.colors.primary,
            uncheckedThumbColor = MaterialTheme.colors.onPrimary,
            uncheckedTrackColor = gray
        )
    )
}