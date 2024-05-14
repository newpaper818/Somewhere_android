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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.NavigationBarIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

val NAVIGATION_BOTTOM_BAR_WIDTH = 0.dp
val NAVIGATION_RAIL_BAR_WIDTH = 80.dp
val NAVIGATION_DRAWER_BAR_WIDTH = 180.dp

//compact
@Composable
fun SomewhereNavigationBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceDim,
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
                style = if (selected)   MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        else            MaterialTheme.typography.labelMedium
            )
        }
    )
}



//medium
@Composable
fun SomewhereNavigationRailBar(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
){
    NavigationRail(
        modifier = modifier.width(NAVIGATION_RAIL_BAR_WIDTH),
        header = {
            //FAB
        },
        containerColor = MaterialTheme.colorScheme.surfaceDim
    ) {
        Spacer(modifier = Modifier.weight(1f))

        content()

        Spacer(modifier = Modifier.weight(1f))
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
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected)   DisplayIcon(icon = selectedIcon)
            else            DisplayIcon(icon = unSelectedIcon)
        },
        label = {
            Text(
                text = labelText,
                style = if (selected)   MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        else            MaterialTheme.typography.labelMedium
            )
        }
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
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
        onClick = onClick,
        icon = {
            if (selected)   DisplayIcon(icon = selectedIcon)
            else            DisplayIcon(icon = unSelectedIcon)
        },
        label = {
            Text(
                text = labelText,
                style = if (selected)   MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        else            MaterialTheme.typography.labelMedium
            )
        }
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