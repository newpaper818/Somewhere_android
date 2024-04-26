package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    shape: Shape = CardDefaults.shape,
    colors: CardColors? = null,
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
){
    val cardColors = CardColors(
        containerColor = MaterialTheme.colorScheme.surfaceBright,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceBright,
        disabledContentColor = MaterialTheme.colorScheme.onSurface
    )

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors ?: cardColors,
            elevation = elevation,
            border = border
        ) {
            content()
        }
    }
    else{
        Card(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = colors ?: cardColors,
            elevation = elevation,
            border = border
        ) {
            content()
        }
    }
}