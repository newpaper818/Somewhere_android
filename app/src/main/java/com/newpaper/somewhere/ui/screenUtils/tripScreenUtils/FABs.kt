package com.newpaper.somewhere.ui.screenUtils.tripScreenUtils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcon
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.theme.white

@Composable
fun IconFAB(
    onClick: () -> Unit,
    visible: Boolean,
    icon: MyIcon
){
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(500),
            initialOffsetX = { (it * 2) }
        ),
        exit = slideOutHorizontally(
            animationSpec = tween(500),
            targetOffsetX = { (it * 2) }
        )
    ) {
        FloatingActionButton(onClick = onClick) {
            DisplayIcon(icon = icon)
        }
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
        SomewhereFloatingActionButton(
            text = stringResource(id = R.string.see_on_map),
            icon = MyIcons.map,
            onClick = onClick,
            expanded = expanded,
            modifier = Modifier
        )
    }
}

@Composable
private fun SomewhereFloatingActionButton(
    text: String,
    icon: MyIcon,
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        text = { Text(text = text, style = getTextStyle(TextType.FAB)) },
        icon = { DisplayIcon(icon, color = white) },
        onClick = onClick,
        expanded = expanded,
        modifier = modifier
    )
}