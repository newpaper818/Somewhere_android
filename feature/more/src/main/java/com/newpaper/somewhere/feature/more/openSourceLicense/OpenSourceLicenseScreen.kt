package com.newpaper.somewhere.feature.more.openSourceLicense

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.verticalScrollbar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.feature.more.R
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun OpenSourceLicenseRoute(
    startSpacerValue: Dp,
    useBlurEffect: Boolean,
    navigateUp: () -> Unit
){
    BackHandler {
        navigateUp()
    }

    val topAppBarHazeState = if(useBlurEffect) rememberHazeState() else null

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.open_source_license),
                navigationIcon = TopAppBarIcon.back,
                onClickNavigationIcon = { navigateUp() },
                hazeState = topAppBarHazeState
            )
        },
    ) { paddingValues ->

        val lazyListState = rememberLazyListState()

        Box {
            LibrariesContainer(
                modifier = if (topAppBarHazeState != null) Modifier.fillMaxSize()
                                .hazeSource(state = topAppBarHazeState).background(MaterialTheme.colorScheme.background)
                            else Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = paddingValues.calculateTopPadding(), bottom = 200.dp),
                lazyListState = lazyListState,
                showVersion = false,
                itemSpacing = 8.dp
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp)
                    .verticalScrollbar(lazyListState = lazyListState)
            )
        }
    }
}






