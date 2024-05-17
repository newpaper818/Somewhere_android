package com.newpaper.somewhere.navigationUi

import androidx.annotation.StringRes
import com.newpaper.somewhere.R
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.NavigationBarIcon

enum class TopLevelDestination(
    val selectedIcon: MyIcon,
    val unselectedIcon: MyIcon,
    @StringRes val labelTextId: Int
) {
    TRIPS(
        selectedIcon = NavigationBarIcon.tripsFilled,
        unselectedIcon = NavigationBarIcon.tripsOutlined,
        labelTextId = R.string.trips
    ),
    PROFILE(
        selectedIcon = NavigationBarIcon.profileFilled,
        unselectedIcon = NavigationBarIcon.profileOutlined,
        labelTextId = R.string.profile
    ),
    MORE(
        selectedIcon = NavigationBarIcon.moreFilled,
        unselectedIcon = NavigationBarIcon.moreOutlined,
        labelTextId = R.string.more
    )
}