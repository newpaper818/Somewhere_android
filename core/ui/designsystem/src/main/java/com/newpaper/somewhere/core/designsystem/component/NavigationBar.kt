package com.newpaper.somewhere.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.NavigationBarIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

val NAVIGATION_BOTTOM_BAR_WIDTH = 0.dp
val NAVIGATION_RAIL_BAR_WIDTH = 80.dp
val NAVIGATION_DRAWER_BAR_WIDTH = 180.dp



//compact
@OptIn(ExperimentalHazeApi::class)
@Composable
fun SomewhereNavigationBottomBar(
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val containerColor = if (hazeState == null) MaterialTheme.colorScheme.surfaceDim
                            else Color.Transparent

    val topAppBarColor = MaterialTheme.colorScheme.surfaceDim

    val navBarModifier = if (hazeState == null) modifier.clip(SmoothRoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
                            else modifier
                                    .clip(SmoothRoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
                                    .hazeEffect(state = hazeState) {
                                        blurRadius = 16.dp
                                        tints = listOf(
                                            HazeTint(topAppBarColor.copy(alpha = 0.9f))
                                        )
                                        inputScale = HazeInputScale.Fixed(0.5f)
                                    }

    NavigationBar(
        modifier = navBarModifier,
        containerColor = containerColor,
        content = content
    )
}

@Composable
fun RowScope.SomewhereNavigationBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: MyIcon,
    unSelectedIcon: MyIcon,
    labelText: String
){
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected)   DisplayIcon(icon = selectedIcon)
            else            DisplayIcon(icon = unSelectedIcon)
        },
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        colors = NavigationBarItemColors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,

            unselectedIconColor = MaterialTheme.colorScheme.inverseSurface,
            unselectedTextColor = MaterialTheme.colorScheme.inverseSurface,

            disabledIconColor = Color.Unspecified,
            disabledTextColor = Color.Unspecified
        )
    )
}



//medium
@Composable
fun SomewhereNavigationRailBar(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
){
    NavigationRail(
//        modifier = modifier.width(NAVIGATION_RAIL_BAR_WIDTH),
        modifier = Modifier.clip(SmoothRoundedCornerShape(0.dp, 24.dp, 24.dp, 0.dp)),
        header = {
            //FAB
        },
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) {
        Column(
            modifier = modifier.width(NAVIGATION_RAIL_BAR_WIDTH)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            content()

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SomewhereNavigationRailBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: MyIcon,
    unSelectedIcon: MyIcon,
    labelText: String
){
    NavigationRailItem(
        modifier = Modifier.padding(vertical = 10.dp),
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected)   DisplayIcon(icon = selectedIcon)
            else            DisplayIcon(icon = unSelectedIcon)
        },
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        colors = NavigationRailItemColors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,

            unselectedIconColor = MaterialTheme.colorScheme.inverseSurface,
            unselectedTextColor = MaterialTheme.colorScheme.inverseSurface,

            disabledIconColor = Color.Unspecified,
            disabledTextColor = Color.Unspecified
        )
    )
}

//expanded
@Composable
fun SomewhereNavigationDrawer(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
){
    Box(
        modifier = modifier
            .clip(SmoothRoundedCornerShape(0.dp, 24.dp, 24.dp, 0.dp))
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
                    content()
                }
            }
        ) {

        }
    }
}

@Composable
fun SomewhereNavigationDrawerItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: MyIcon,
    unSelectedIcon: MyIcon,
    labelText: String
){
    NavigationDrawerItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected)   DisplayIcon(icon = selectedIcon)
            else            DisplayIcon(icon = unSelectedIcon)
        },
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,

            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.inverseSurface,
            unselectedTextColor = MaterialTheme.colorScheme.inverseSurface,
        ),
    )
}




























@Composable
@PreviewLightDark
private fun NavigationBottomBarPreview(){
    val labelTexts = listOf("Trips", "Profile", "More")
    val selectedIcons = listOf(
        NavigationBarIcon.tripsFilled,
        NavigationBarIcon.profileFilled,
        NavigationBarIcon.moreFilled
    )
    val unSelectedIcons = listOf(
        NavigationBarIcon.tripsOutlined,
        NavigationBarIcon.profileOutlined,
        NavigationBarIcon.moreOutlined
    )

    SomewhereTheme {
        SomewhereNavigationBottomBar {
            labelTexts.forEachIndexed { index, labelText ->
                SomewhereNavigationBottomBarItem(
                    selected = index == 0,
                    onClick = { },
                    selectedIcon = selectedIcons[index],
                    unSelectedIcon = unSelectedIcons[index],
                    labelText = labelText
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun NavigationRailBarPreview(){
    val labelTexts = listOf("Trips", "Profile", "More")
    val selectedIcons = listOf(
        NavigationBarIcon.tripsFilled,
        NavigationBarIcon.profileFilled,
        NavigationBarIcon.moreFilled
    )
    val unSelectedIcons = listOf(
        NavigationBarIcon.tripsOutlined,
        NavigationBarIcon.profileOutlined,
        NavigationBarIcon.moreOutlined
    )

    SomewhereTheme {
        SomewhereNavigationRailBar {
            labelTexts.forEachIndexed { index, labelText ->
                SomewhereNavigationRailBarItem(
                    selected = index == 0,
                    onClick = { },
                    selectedIcon = selectedIcons[index],
                    unSelectedIcon = unSelectedIcons[index],
                    labelText = labelText
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun NavigationDrawerPreview(){
    val labelTexts = listOf("Trips", "Profile", "More")
    val selectedIcons = listOf(
        NavigationBarIcon.tripsFilled,
        NavigationBarIcon.profileFilled,
        NavigationBarIcon.moreFilled
    )
    val unSelectedIcons = listOf(
        NavigationBarIcon.tripsOutlined,
        NavigationBarIcon.profileOutlined,
        NavigationBarIcon.moreOutlined
    )

    SomewhereTheme {
        SomewhereNavigationDrawer {
            labelTexts.forEachIndexed { index, labelText ->
                SomewhereNavigationDrawerItem(
                    selected = index == 0,
                    onClick = { },
                    selectedIcon = selectedIcons[index],
                    unSelectedIcon = unSelectedIcons[index],
                    labelText = labelText
                )
            }
        }
    }
}