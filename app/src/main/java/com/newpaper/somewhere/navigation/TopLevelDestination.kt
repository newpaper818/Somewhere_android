package com.newpaper.somewhere.navigation

import androidx.annotation.StringRes
import com.newpaper.somewhere.R
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.NavigationBarIcon

enum class TopLevelDestination(
    val selectedIcon: MyIcon,
    val unselectedIcon: MyIcon,
    @StringRes val iconTextId: Int
) {
    TRIPS(
        selectedIcon = NavigationBarIcon.tripsFilled,
        unselectedIcon = NavigationBarIcon.tripsOutlined,
        iconTextId = R.string.trips
    ),
    PROFILE(
        selectedIcon = NavigationBarIcon.profileFilled,
        unselectedIcon = NavigationBarIcon.profileOutlined,
        iconTextId = R.string.profile
    ),
    MORE(
        selectedIcon = NavigationBarIcon.moreFilled,
        unselectedIcon = NavigationBarIcon.moreOutlined,
        iconTextId = R.string.more
    )
}