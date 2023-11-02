package com.newpaper.somewhere.ui.screenUtils.commonScreenUtils

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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

@Composable
fun SomewhereNavigationBar(
    currentDestination: NavigationDestination,
    navigateTo: (NavigationDestination) -> Unit,
    scrollToTop: () -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_NOT_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.NAVIGATION_BAR_SELECTED)
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                if (currentDestination == MyTripsDestination)   DisplayIcon(icon = MyIcons.myTripsFilled)
                else                                            DisplayIcon(icon = MyIcons.myTripsOutlined)
            },
            label = {
                Text(
                    text = stringResource(id = R.string.my_trips),
                    style = if (currentDestination == MyTripsDestination)   selectedTextStyle
                            else                                            textStyle
                )
            },
            selected = currentDestination == MyTripsDestination,
            onClick = {
                if (currentDestination != MyTripsDestination)
                    navigateTo(MyTripsDestination)
                else
                    scrollToTop()
            }
        )

        NavigationBarItem(
            icon = {
                if (currentDestination == MyTripsDestination)   DisplayIcon(icon = MyIcons.moreHorizFilled)
                else                                            DisplayIcon(icon = MyIcons.moreHorizOutlined)
            },
            label = {
                Text(
                    text = stringResource(id = R.string.more),
                    style = if (currentDestination == MoreDestination)   selectedTextStyle
                            else                                            textStyle
                )
            },
            selected = currentDestination == MoreDestination,
            onClick = {
                if (currentDestination != MoreDestination)
                    navigateTo(MoreDestination)
                else
                    scrollToTop()
            }
        )

        // USE LATER? ------------------------------------------------------------------------------
//        NavigationBarItem(
//            icon = {
//                if (currentDestination == ProfileDestination)
//                    DisplayIcon(icon = MyIcons.profileSelected)
//                else
//                    DisplayIcon(icon = MyIcons.profileNotSelected)
//            },
//            label = { Text(text = "Profile") },
//            selected = currentDestination == ProfileDestination,
//            onClick = {
//                if (currentDestination != ProfileDestination)
//                    navigateTo(ProfileDestination)
//            }
//        )
    }
}