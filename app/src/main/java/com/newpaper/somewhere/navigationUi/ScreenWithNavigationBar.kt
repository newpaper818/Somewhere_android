package com.newpaper.somewhere.navigationUi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    onClickNavBarItem: (TopLevelDestination) -> Unit,
    onClickNavBarItemAgain: (TopLevelDestination) -> Unit,

    modifier: Modifier = Modifier,
    topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries,
    content: @Composable () -> Unit = {}
) {

    Box(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding()
            .displayCutoutPadding()
            .fillMaxSize()
    ) {

        //phone vertical ===========================================================================
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            Column {
                //content
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }

                //bottom navigation bar
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

        //phone horizontal (height compact) ========================================================
        //or foldable vertical or tablet vertical (width medium)
        else if (
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
            || windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
        ) {
            Row {
                //navigation rail bar
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

                //content
                content()
            }
        }

        //foldable horizontal or tablet horizontal =================================================
        else if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {

            Row {
                //navigation drawer bar
                SomewhereNavigationDrawer{
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

                //content
                content()
            }
        }
    }
}

