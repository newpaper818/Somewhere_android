package com.example.somewhere.ui.commonScreenUtils

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.example.somewhere.ui.mainScreens.MoreDestination
import com.example.somewhere.ui.mainScreens.MyTripsDestination
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle

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
            icon = { DisplayIcon(icon = MyIcons.myTrips) },
            label = {
                Text(
                    text = "My trips",
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
            icon = { DisplayIcon(icon = MyIcons.moreHoriz) },
            label = {
                Text(
                    text = "More",
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