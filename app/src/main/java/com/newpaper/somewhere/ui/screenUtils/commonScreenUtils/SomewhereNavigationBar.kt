package com.newpaper.somewhere.ui.screenUtils.commonScreenUtils

import androidx.annotation.StringRes
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.navigation.MainNavigationDestination
import com.newpaper.somewhere.ui.navigation.MoreMainDestination
import com.newpaper.somewhere.ui.navigation.MyTripsMainDestination
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

val NAVIGATION_BOTTOM_BAR_WIDTH = 0.dp
val NAVIGATION_RAIL_BAR_WIDTH = 80.dp
val NAVIGATION_DRAWER_BAR_WIDTH = 180.dp

data class MainNavDestinationItem(
    @StringRes val titleTextId: Int,
    val selectedIcon: MyIcon,
    val unSelectedIcon: MyIcon,
    val mainNavDestination: MainNavigationDestination
)

private val mainNavDestinationItems = listOf(
    MainNavDestinationItem(
        titleTextId = R.string.my_trips,
        selectedIcon = MyIcons.myTripsFilled,
        unSelectedIcon = MyIcons.myTripsOutlined,
        mainNavDestination = MyTripsMainDestination
    ),
    MainNavDestinationItem(
        titleTextId = R.string.more,
        selectedIcon = MyIcons.moreHorizFilled,
        unSelectedIcon = MyIcons.moreHorizOutlined,
        mainNavDestination = MoreMainDestination
    ),
//    NavigationItem(
//        titleTextId = R.string.profile,
//        selectedIcon = MyIcons.profileFilled,
//        unSelectedIcon = MyIcons.profileOutlined,
//        navigateDestination = ProfileDestination
//    ),
)

//compact
@Composable
fun SomewhereNavigationBottomBar(
    currentMainNavDestination: MainNavigationDestination,
    onClickItem: (MainNavigationDestination) -> Unit,
    onClickItemAgain: () -> Unit,

    mainNavDestinationItems: List<MainNavDestinationItem> = com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.mainNavDestinationItems,
    unselectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_NOT_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_SELECTED)
) {
    NavigationBar(
//        containerColor = Color.Black.copy(alpha = 0.3f)
    ) {
        mainNavDestinationItems.forEach{ item ->
            val selected = currentMainNavDestination == item.mainNavDestination

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected)  onClickItem(item.mainNavDestination)
                    else            onClickItemAgain()
                },
                icon = {
                    if (selected)   DisplayIcon(icon = item.selectedIcon)
                    else            DisplayIcon(icon = item.unSelectedIcon)
                },
                label = {
                    Text(
                        text = stringResource(id = item.titleTextId),
                        style = if (selected)   selectedTextStyle
                        else            unselectedTextStyle
                    )
                }
            )
        }
    }
}

//medium
@Composable
fun SomewhereNavigationRailBar(
    currentMainNavDestination: MainNavigationDestination,
    onClickItem: (MainNavigationDestination) -> Unit,
    onClickItemAgain: () -> Unit,

    mainNavDestinationItems: List<MainNavDestinationItem> = com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.mainNavDestinationItems,
    unselectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_NOT_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_SELECTED)
){
    NavigationRail(
        modifier = Modifier.width(NAVIGATION_RAIL_BAR_WIDTH),
        header = {
            //FAB
        }
    ) {
        Spacer(modifier = Modifier.weight(1f))

        mainNavDestinationItems.forEach{ item ->
            val selected = currentMainNavDestination == item.mainNavDestination
            
            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (!selected)  onClickItem(item.mainNavDestination)
                    else            onClickItemAgain()
                },
                icon = {
                    if (selected)   DisplayIcon(icon = item.selectedIcon)
                    else            DisplayIcon(icon = item.unSelectedIcon)
                },
                label = {
                    Text(
                        text = stringResource(id = item.titleTextId),
                        style = if (selected)   selectedTextStyle
                                else            unselectedTextStyle
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
    currentMainNavDestination: MainNavigationDestination,
    onClickItem: (MainNavigationDestination) -> Unit,
    onClickItemAgain: () -> Unit,

    windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
    mainNavDestinationItems: List<MainNavDestinationItem> = com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.mainNavDestinationItems,
    unselectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_NOT_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_SELECTED)
){
    Box(
        modifier = Modifier
            .width(NAVIGATION_DRAWER_BAR_WIDTH)
            .background(MaterialTheme.colorScheme.surface)
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
                    
//                    //header
//                    Text(
//                        text = stringResource(id = R.string.app_name),
//                        style = getTextStyle(TextType.TOP_BAR__TITLE)
//                    )
//
//                    MySpacerColumn(height = 32.dp)
                    
                    
                    //nav items
                    mainNavDestinationItems.forEach { item ->

                        val selected = currentMainNavDestination == item.mainNavDestination

                        NavigationDrawerItem(
                            selected = selected,
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                            onClick = {
                                if (!selected)  onClickItem(item.mainNavDestination)
                                else            onClickItemAgain()
                            },
                            icon = {
                                if (selected)   DisplayIcon(icon = item.selectedIcon)
                                else            DisplayIcon(icon = item.unSelectedIcon)
                            },
                            label = {
                                Text(
                                    text = stringResource(id = item.titleTextId),
                                    style = if (selected) selectedTextStyle
                                    else unselectedTextStyle
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