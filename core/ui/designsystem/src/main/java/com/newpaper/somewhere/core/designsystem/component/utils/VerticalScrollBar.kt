package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.verticalScrollbar(
    lazyListState: LazyListState,
    width: Dp = 6.dp
): Modifier {
    val targetAlpha = if (lazyListState.isScrollInProgress) 1f else 0f
    val duration = if (lazyListState.isScrollInProgress) 150 else 500
    val delayMillis = if (lazyListState.isScrollInProgress) 0 else 1200

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delayMillis
        ),
        label = ""
    )

    val color = MaterialTheme.colorScheme.surfaceVariant


    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = lazyListState.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / lazyListState.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = lazyListState.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRoundRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx() - 4.dp.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
                alpha = alpha
            )
        }
    }
}

@Composable
fun Modifier.verticalScrollbar(
    lazyGridState: LazyGridState,
    width: Dp = 6.dp
): Modifier {
    val targetAlpha = if (lazyGridState.isScrollInProgress) 1f else 0f
    val duration = if (lazyGridState.isScrollInProgress) 150 else 500
    val delayMillis = if (lazyGridState.isScrollInProgress) 0 else 1200

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delayMillis
        ),
        label = ""
    )

    val color = MaterialTheme.colorScheme.surfaceVariant


    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = lazyGridState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = lazyGridState.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / lazyGridState.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = lazyGridState.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRoundRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx() - 4.dp.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
                alpha = alpha
            )
        }
    }
}