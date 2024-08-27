package com.newpaper.somewhere.core.designsystem.component.utils

import android.util.Log
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlainTooltipBox(
    tooltipText: String,
    content: @Composable () -> Unit
){
    val tooltipState = rememberTooltipState()
    val tooltipPosition = TooltipDefaults.rememberPlainTooltipPositionProvider()

    TooltipBox(
        state = tooltipState,
        positionProvider = tooltipPosition,
        tooltip = {
            PlainTooltip(
                modifier = Modifier.padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = tooltipText,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(6.dp)
                )
            }
        },
    ) {
        content()
    }
}