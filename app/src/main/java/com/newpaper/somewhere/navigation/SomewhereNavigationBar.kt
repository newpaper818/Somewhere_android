package com.newpaper.somewhere.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon

val NAVIGATION_BOTTOM_BAR_WIDTH = 0.dp
val NAVIGATION_RAIL_BAR_WIDTH = 80.dp
val NAVIGATION_DRAWER_BAR_WIDTH = 180.dp


@Composable
fun SomewhereNavigationBottomBar(
    currentTopLevelDestination: TopLevelDestination,
    onClickItem: (TopLevelDestination) -> Unit,
    onClickItemAgain: (TopLevelDestination) -> Unit,

    topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
) {
    NavigationBar(
//        modifier = Modifier.height(NAVIGATION_BOTTOM_BAR_HEIGHT),
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) {
        topLevelDestinations.forEach{ item ->
            val selected = currentTopLevelDestination == item

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected)  onClickItem(item)
                    else            onClickItemAgain(item)
                },
                icon = {
                    if (selected)   DisplayIcon(icon = item.selectedIcon)
                    else            DisplayIcon(icon = item.unselectedIcon)
                },
                label = {
                    Text(
                        text = stringResource(id = item.iconTextId),
                        style = if (selected)   MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        else            MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

//medium
@Composable
fun SomewhereNavigationRailBar(
    currentTopLevelDestination: TopLevelDestination,
    onClickItem: (TopLevelDestination) -> Unit,
    onClickItemAgain: (TopLevelDestination) -> Unit,

    topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
){
    NavigationRail(
        modifier = Modifier.width(NAVIGATION_RAIL_BAR_WIDTH),
        header = {
            //FAB
        },
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) {
        Spacer(modifier = Modifier.weight(1f))

        topLevelDestinations.forEach{ item ->
            val selected = currentTopLevelDestination == item

            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (!selected)  onClickItem(item)
                    else            onClickItemAgain(item)
                },
                icon = {
                    if (selected)   DisplayIcon(icon = item.selectedIcon)
                    else            DisplayIcon(icon = item.unselectedIcon)
                },
                label = {
                    Text(
                        text = stringResource(id = item.iconTextId),
                        style = if (selected)   MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        else            MaterialTheme.typography.labelMedium
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

//expanded
@Composable
fun SomewhereNavigationDrawer(
    currentTopLevelDestination: TopLevelDestination,
    onClickItem: (TopLevelDestination) -> Unit,
    onClickItemAgain: (TopLevelDestination) -> Unit,

    windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
    topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
){
    Box(
        modifier = Modifier
            .width(NAVIGATION_DRAWER_BAR_WIDTH)
            .background(MaterialTheme.colorScheme.surfaceDim)
    ) {
        PermanentNavigationDrawer(
            drawerContent = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 10.dp)
                        .windowInsetsPadding(windowInsets)
                ) {

                    //nav items
                    topLevelDestinations.forEach { item ->

                        val selected = currentTopLevelDestination == item

                        NavigationDrawerItem(
                            selected = selected,
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                            onClick = {
                                if (!selected)  onClickItem(item)
                                else            onClickItemAgain(item)
                            },
                            icon = {
                                if (selected)   DisplayIcon(icon = item.selectedIcon)
                                else            DisplayIcon(icon = item.unselectedIcon)
                            },
                            label = {
                                Text(
                                    text = stringResource(id = item.iconTextId),
                                    style = if (selected)   MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    else            MaterialTheme.typography.labelMedium
                                )
                            }
                        )

                        MySpacerColumn(height = 4.dp)
                    }
                }
            }
        ) {

        }
    }
}