package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.SelectSwitchIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.utils.itemMaxWidth

@Composable
fun MySegmentedButtons(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable RowScope.() -> Unit
){
    MyCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(modifier = Modifier.padding(6.dp)) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySegmentedButtonItem(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    icon: MyIcon? = null
){

    val noRippleConfiguration = RippleConfiguration(
        color = MaterialTheme.colorScheme.surface
    )

    val containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceContainer
        else Color.Transparent

//    val contentColor = if (isSelected) MaterialTheme.colorScheme.contentColorFor(containerColor)
//        else MaterialTheme.colorScheme.onSurfaceVariant

    val contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor)


    val haptic = LocalHapticFeedback.current

    CompositionLocalProvider(LocalRippleConfiguration provides noRippleConfiguration) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
        ) {
            MyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
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
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}





















@Composable
@PreviewLightDark
private fun NavigationDrawerPreview(){
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            MySegmentedButtons(
                modifier = Modifier.padding(16.dp)
            ) {
                MySegmentedButtonItem(
                    modifier = Modifier.weight(1f),
                    isSelected = true,
                    icon = SelectSwitchIcon.allowEdit,
                    text = "Allow edit",
                    onClick = { }
                )

                MySegmentedButtonItem(
                    modifier = Modifier.weight(1f),
                    isSelected = false,
                    icon = SelectSwitchIcon.viewOnly,
                    text = "View only",
                    onClick = { }
                )
            }
        }
    }
}

