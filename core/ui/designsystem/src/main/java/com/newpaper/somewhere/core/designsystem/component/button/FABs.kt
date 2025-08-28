package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.FabIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R

@Composable
fun IconFAB(
    onClick: () -> Unit,
    visible: Boolean,
    icon: MyIcon
){
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { (it * 2.5f).toInt() }),
        exit = slideOutVertically(
            animationSpec = tween(500),
            targetOffsetY = { (it * 2.5f).toInt() })
    ) {
        FloatingActionButton(onClick = onClick) {
            DisplayIcon(icon = icon)
        }
    }
}

@Composable
fun NewTripExtendedFAB(
    visible: Boolean,
    onClick: () -> Unit,
    expanded: Boolean,
    useBottomNavBar: Boolean,
    glanceSpotShown: Boolean,
    use2Panes: Boolean
){
    val bottomNavBarPadding = if (useBottomNavBar) 80.dp else 0.dp
    val glanceSpotPadding = if (glanceSpotShown && useBottomNavBar) 70.dp else 0.dp
    val padding = if (glanceSpotShown && useBottomNavBar) 16.dp else 0.dp

    val modifier =
        if (glanceSpotShown && !useBottomNavBar) Modifier
            .navigationBarsPadding()
            .padding(bottom = bottomNavBarPadding + glanceSpotPadding + padding)
            .height(70.dp)
        else Modifier
            .navigationBarsPadding()
            .padding(bottom = bottomNavBarPadding + glanceSpotPadding + padding)

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { (it * 2.5f).toInt() }),
        exit = slideOutVertically(
            animationSpec = tween(500),
            targetOffsetY = { (it * 2.5f).toInt() })
    ) {
        SomewhereFAB(
            text = stringResource(id = R.string.new_trip),
            icon = FabIcon.add,
            onClick = onClick,
            expanded = expanded,
            modifier = modifier
        )
    }
}

@Composable
fun SeeOnMapExtendedFAB(
    visible: Boolean,
    onClick: () -> Unit,
    expanded: Boolean
){
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { (it * 2.5f).toInt() }),
        exit = slideOutVertically(
            animationSpec = tween(500),
            targetOffsetY = { (it * 2.5f).toInt() })
    ) {
        SomewhereFAB(
            text = stringResource(id = R.string.see_on_map),
            icon = FabIcon.map,
            onClick = onClick,
            expanded = expanded,
            modifier = Modifier.navigationBarsPadding()
        )
    }
}

@Composable
private fun SomewhereFAB(
    text: String,
    icon: MyIcon,
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        text = { Text(text = text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)) },
        icon = { DisplayIcon(icon) },
        onClick = onClick,
        expanded = expanded,
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp
        )
    )
}

























@Composable
@PreviewLightDark
private fun SeeOnMapFABPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(200.dp)
        ) {
            SeeOnMapExtendedFAB(
                visible = true,
                onClick = { },
                expanded = true
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SeeOnMapFABCollapsePreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(200.dp)
        ) {
            SeeOnMapExtendedFAB(
                visible = true,
                onClick = { },
                expanded = false
            )
        }
    }
}