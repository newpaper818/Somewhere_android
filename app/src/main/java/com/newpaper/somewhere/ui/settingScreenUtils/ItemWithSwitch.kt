package com.newpaper.somewhere.ui.settingScreenUtils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

/*
      ---------------------------------------------------------------
    /                                                                 \
    |   text                                                (    o)   |
    \                                                                 /
      ---------------------------------------------------------------
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemWithSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.GROUP_CARD_ITEM_BODY1)
){
    val haptic = LocalHapticFeedback.current

    ClickableBox(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCheckedChange(!checked)
        },
        modifier = Modifier.fillMaxWidth().height(60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = text,
                style = textStyle
            )

            Spacer(modifier = Modifier.weight(1f))

            MySwitch(
                checked = checked,
                onCheckedChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCheckedChange(it)
                }
            )
        }
    }
}

@Composable
private fun MySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
//            checkedThumbColor = MaterialTheme.colorScheme.onTertiaryContainer,
//            checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
//            checkedBorderColor = Color.Transparent,
//
//            uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
//            uncheckedTrackColor = Gray60,
//            uncheckedBorderColor = Color.Transparent
        )
    )
}