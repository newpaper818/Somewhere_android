package com.newpaper.somewhere.core.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.listItemHeight

@Composable
fun ItemWithSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit
){
    val haptic = LocalHapticFeedback.current
    val toggle = stringResource(id = R.string.toggle)
    val on = stringResource(id = R.string.on)
    val off = stringResource(id = R.string.off)

    ClickableBox(
        onClick = {
            if (!checked) haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
            else haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
            onCheckedChange(!checked)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(listItemHeight)
            .semantics {
                role = Role.Switch
                stateDescription = if (checked) on else off
                onClick(
                    label = toggle,
                    action = {
                        onCheckedChange(!checked)
                        true
                    }
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            MySwitch(
                checked = checked,
                onCheckedChange = {
                    if (it) haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    else haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    onCheckedChange(it)
                },
                modifier = Modifier.clearAndSetSemantics {  }
            )
        }
    }
}

@Composable
private fun MySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
//            checkedThumbColor = MaterialTheme.colorScheme.onTertiaryContainer,
//            checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
//            checkedBorderColor = Color.Transparent,
//
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
            uncheckedTrackColor = Color.Transparent,
            uncheckedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}


















@Composable
@PreviewLightDark
private fun Preview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(250.dp)
        ) {
            ListGroupCard {
                ItemWithSwitch(
                    text = "checked item",
                    checked = true,
                    onCheckedChange = {}
                )

                ItemWithSwitch(
                    text = "unchecked item",
                    checked = false,
                    onCheckedChange = {}
                )
            }
        }
    }
}