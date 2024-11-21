package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlainTooltipBox(
    tooltipText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
){
    val tooltipState = rememberTooltipState()
    val tooltipPosition = TooltipDefaults.rememberPlainTooltipPositionProvider()

    TooltipBox(
        modifier = modifier,
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