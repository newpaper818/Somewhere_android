package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape


@Composable
fun ClickableBox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = { },
    containerColor: Color = Color.Transparent,
    shape: Shape = MaterialTheme.shapes.medium,  //smooth rounded corner shape 16.dp
    contentAlignment: Alignment = Alignment.TopStart,

    content: @Composable() () -> Unit,
) {
    val boxModifier = if (!enabled) modifier.clip(shape).background(containerColor)
                        else        modifier.clip(shape).background(containerColor)
                                        .clickable(onClick = onClick)

    Box(
        modifier = boxModifier,
        contentAlignment = contentAlignment
    ) {
        content()
    }
}