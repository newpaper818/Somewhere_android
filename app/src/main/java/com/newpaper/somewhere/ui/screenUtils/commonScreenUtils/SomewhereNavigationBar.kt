package com.newpaper.somewhere.ui.screenUtils.commonScreenUtils

import androidx.annotation.StringRes
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.screens.mainScreens.MoreDestination
import com.newpaper.somewhere.ui.screens.mainScreens.MyTripsDestination
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

data class NavigationItem(
    @StringRes val titleTextId: Int,
    val selectedIcon: MyIcon,
    val unSelectedIcon: MyIcon,
    val navigateDestination: NavigationDestination
)

val navigationItems = listOf(
    NavigationItem(
        titleTextId = R.string.my_trips,
        selectedIcon = MyIcons.myTripsFilled,
        unSelectedIcon = MyIcons.myTripsOutlined,
        navigateDestination = MyTripsDestination
    ),
    NavigationItem(
        titleTextId = R.string.more,
        selectedIcon = MyIcons.moreHorizFilled,
        unSelectedIcon = MyIcons.moreHorizOutlined,
        navigateDestination = MoreDestination
    ),
//    NavigationItem(
//        titleTextId = R.string.profile,
//        selectedIcon = MyIcons.profileFilled,
//        unSelectedIcon = MyIcons.profileOutlined,
//        navigateDestination = ProfileDestination
//    ),
)


@Composable
fun SomewhereNavigationRailBar(
    currentDestination: NavigationDestination,
    navigateTo: (NavigationDestination) -> Unit,
    scrollToTop: () -> Unit,

    navigationItems: List<NavigationItem> = com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.navigationItems,
    unselectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_NOT_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_SELECTED)
){
    NavigationRail(
        header = {
            //FAB
        }
    ) {
        navigationItems.forEach{ item ->
            val selected = currentDestination == item.navigateDestination
            
            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (!selected)  navigateTo(item.navigateDestination)
                    else            scrollToTop()
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

@Composable
fun SomewhereNavigationBottomBar(
    currentDestination: NavigationDestination,
    navigateTo: (NavigationDestination) -> Unit,
    scrollToTop: () -> Unit,

    navigationItems: List<NavigationItem> = com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.navigationItems,
    unselectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_NOT_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_SELECTED)
) {
    NavigationBar {

        navigationItems.forEach{ item ->
            val selected = currentDestination == item.navigateDestination

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected)  navigateTo(item.navigateDestination)
                    else            scrollToTop()
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