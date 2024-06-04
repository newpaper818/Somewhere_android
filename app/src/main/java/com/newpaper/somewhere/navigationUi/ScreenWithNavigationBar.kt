package com.newpaper.somewhere.navigationUi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.newpaper.somewhere.core.designsystem.component.SomewhereNavigationBottomBar
import com.newpaper.somewhere.core.designsystem.component.SomewhereNavigationBottomBarItem
import com.newpaper.somewhere.core.designsystem.component.SomewhereNavigationDrawer
import com.newpaper.somewhere.core.designsystem.component.SomewhereNavigationDrawerItem
import com.newpaper.somewhere.core.designsystem.component.SomewhereNavigationRailBar
import com.newpaper.somewhere.core.designsystem.component.SomewhereNavigationRailBarItem
import com.newpaper.somewhere.util.WindowHeightSizeClass
import com.newpaper.somewhere.util.WindowSizeClass
import com.newpaper.somewhere.util.WindowWidthSizeClass

@Composable
fun ScreenWithNavigationBar(
    windowSizeClass: WindowSizeClass,
    currentTopLevelDestination: TopLevelDestination,
    showNavigationBar: Boolean,

    onClickNavBarItem: (TopLevelDestination) -> Unit,
    onClickNavBarItemAgain: (TopLevelDestination) -> Unit,

    modifier: Modifier = Modifier,
    topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries,
    content: @Composable () -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        //phone vertical ===========================================================================
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize()
            ) {
                //content
                content()

                //bottom navigation bar
                AnimatedVisibility(
                    visible = showNavigationBar,
                    enter = slideInVertically(tween(300), initialOffsetY = { it }),
                    exit = slideOutVertically(tween(300), targetOffsetY = { it })
                ) {
                    SomewhereNavigationBottomBar{
                        topLevelDestinations.forEach {
                            SomewhereNavigationBottomBarItem(
                                selected = it == currentTopLevelDestination,
                                onClick = {
                                    if (it != currentTopLevelDestination)
                                        onClickNavBarItem(it)
                                    else
                                        onClickNavBarItemAgain(it)
                                },
                                selectedIcon = it.selectedIcon,
                                unSelectedIcon = it.unselectedIcon,
                                labelText = stringResource(id = it.labelTextId)
                            )
                        }
                    }
                }
            }
        }

        //phone horizontal (height compact) ========================================================
        //or foldable vertical or tablet vertical (width medium)
        else if (
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
            || windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
        ) {
            Box(
                modifier = Modifier.displayCutoutPadding().fillMaxSize()
            ) {
                //content
                content()

                //navigation rail bar
                AnimatedVisibility(
                    visible = showNavigationBar,
                    enter = slideInHorizontally(tween(300), initialOffsetX = { -it }),
                    exit = slideOutHorizontally(tween(300), targetOffsetX = { -it })
                ) {
                    SomewhereNavigationRailBar {
                        topLevelDestinations.forEach {
                            SomewhereNavigationRailBarItem(
                                selected = it == currentTopLevelDestination,
                                onClick = {
                                    if (it != currentTopLevelDestination)
                                        onClickNavBarItem(it)
                                    else
                                        onClickNavBarItemAgain(it)
                                },
                                selectedIcon = it.selectedIcon,
                                unSelectedIcon = it.unselectedIcon,
                                labelText = stringResource(id = it.labelTextId)
                            )
                        }
                    }
                }
            }
        }

        //foldable horizontal or tablet horizontal =================================================
        else if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                //content
                content()

                //navigation drawer bar
                AnimatedVisibility(
                    visible = showNavigationBar,
                    enter = slideInHorizontally(tween(300), initialOffsetX = { -it }),
                    exit = slideOutHorizontally(tween(300), targetOffsetX = { -it })
                ) {
                    SomewhereNavigationDrawer {
                        topLevelDestinations.forEach {
                            SomewhereNavigationDrawerItem(
                                selected = it == currentTopLevelDestination,
                                onClick = {
                                    if (it != currentTopLevelDestination)
                                        onClickNavBarItem(it)
                                    else
                                        onClickNavBarItemAgain(it)
                                },
                                selectedIcon = it.selectedIcon,
                                unSelectedIcon = it.unselectedIcon,
                                labelText = stringResource(id = it.labelTextId)
                            )
                        }
                    }
                }
            }
        }
    }
}

