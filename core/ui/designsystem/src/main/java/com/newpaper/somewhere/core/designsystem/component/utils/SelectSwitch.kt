package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.SelectSwitchIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.utils.itemMaxWidth

@Composable
fun SelectSwitch(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
){
    MyCard(
        modifier = modifier.widthIn(max = itemMaxWidth),
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Composable
fun SelectSwitchItem(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    icon: MyIcon? = null
){

    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary
    else Color.Transparent

    val contentColor = if (isSelected) MaterialTheme.colorScheme.contentColorFor(containerColor)
    else MaterialTheme.colorScheme.onSurfaceVariant

    val haptic = LocalHapticFeedback.current

    MyCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                DisplayIcon(icon = icon)

                MySpacerColumn(height = 4.dp)
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}





















@Composable
@PreviewLightDark
private fun NavigationDrawerPreview(){
    SomewhereTheme {
        SelectSwitch {
            SelectSwitchItem(
                modifier = Modifier.weight(1f),
                isSelected = true,
                icon = SelectSwitchIcon.allowEdit,
                text = "Allow edit",
                onClick = { }
            )

            SelectSwitchItem(
                modifier = Modifier.weight(1f),
                isSelected = false,
                icon = SelectSwitchIcon.viewOnly,
                text = "View only",
                onClick = { }
            )
        }
    }
}

