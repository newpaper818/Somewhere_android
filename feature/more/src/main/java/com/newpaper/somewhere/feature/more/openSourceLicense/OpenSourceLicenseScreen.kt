package com.newpaper.somewhere.feature.more.openSourceLicense

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.feature.more.R

@Composable
fun OpenSourceLicenseRoute(
    startSpacerValue: Dp,
    navigateUp: () -> Unit
){
    BackHandler {
        navigateUp()
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
            .displayCutoutPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.open_source_license),
                navigationIcon = TopAppBarIcon.back,
                navigationIconOnClick = { navigateUp() }
            )
        },
    ) { paddingValues ->

        val lazyListState = rememberLazyListState()

        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .simpleVerticalScrollbar(lazyListState),
            lazyListState = lazyListState,
            showVersion = false,
            itemSpacing = 8.dp
        )
    }
}














//scroll bar
@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 6.dp
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500
    val delayMillis = if (state.isScrollInProgress) 0 else 1200

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

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

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