package com.newpaper.somewhere.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.newpaper.somewhere.core.designsystem.component.button.AnimatedBottomSaveCancelButtons

@Composable
fun MyScaffold(
    modifier: Modifier = Modifier,

    bottomSaveCancelBarVisible: Boolean = false,
    onCancelClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    saveEnabled: Boolean = true,
    use2PanesAndSpotScreen: Boolean = false,

    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: androidx.compose.material3.FabPosition = androidx.compose.material3.FabPosition.End,
    containerColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = WindowInsets(bottom = 0),
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier, topBar, bottomBar, snackbarHost, floatingActionButton,
        floatingActionButtonPosition, containerColor, contentColor, contentWindowInsets,

    ) { paddingValues ->
        Box(contentAlignment = Alignment.BottomCenter) {

            content(paddingValues)

            //bottom save cancel bar
            AnimatedBottomSaveCancelButtons(
                visible = bottomSaveCancelBarVisible,
                onCancelClick = onCancelClick,
                onSaveClick = onSaveClick,
                saveEnabled = saveEnabled,
                use2PanesAndSpotScreen = use2PanesAndSpotScreen
            )
        }
    }
}