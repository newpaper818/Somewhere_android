package com.newpaper.somewhere.ui.screenUtils.commonScreenUtils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import com.newpaper.somewhere.ui.navigation.MainNavigationDestination
import com.newpaper.somewhere.utils.WindowHeightSizeClass
import com.newpaper.somewhere.utils.WindowSizeClass
import com.newpaper.somewhere.utils.WindowWidthSizeClass

@Composable
fun ScreenLayout(
    showNavigationBar: Boolean,
    windowSizeClass: WindowSizeClass,

    currentMainNavDestination: MainNavigationDestination,
    onClickNavBarItem: (MainNavigationDestination) -> Unit,
    onClickNavBarItemAgain: () -> Unit,

    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        //phone vertical
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            Column {
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }

                AnimatedVisibility(
                    visible = showNavigationBar,
                    enter = expandVertically(tween(300)),
                    exit = shrinkVertically(tween(300)),
                ) {
                    SomewhereNavigationBottomBar(
                        currentMainNavDestination = currentMainNavDestination,
                        onClickItem = onClickNavBarItem,
                        onClickItemAgain = onClickNavBarItemAgain
                    )
                }
            }
        }

        //phone horizontal (height compact)
        //or foldable vertical or tablet vertical (width medium)
        else if (
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
            || windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
        ) {
            Row {
                AnimatedVisibility(
                    visible = showNavigationBar,
                    enter = expandHorizontally(tween(300)),
                    exit = shrinkHorizontally(tween(300)),
                ) {
                    SomewhereNavigationRailBar(
                        currentMainNavDestination = currentMainNavDestination,
                        onClickItem = onClickNavBarItem,
                        onClickItemAgain = onClickNavBarItemAgain
                    )
                }

                content()
            }
        }

        //foldable horizontal or tablet horizontal
        else if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
            Row {
                AnimatedVisibility(
                    visible = showNavigationBar,
                    enter = expandHorizontally(tween(300)),
                    exit = shrinkHorizontally(tween(300)),
                ) {
                    SomewhereNavigationDrawer(
                        currentMainNavDestination = currentMainNavDestination,
                        onClickItem = onClickNavBarItem,
                        onClickItemAgain = onClickNavBarItemAgain
                    )
                }

                content()
            }
        }
    }
}