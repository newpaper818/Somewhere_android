package com.example.somewhere.ui.tripScreenUtils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.white

@Composable
fun EditAndMapFAB(
    visible: Boolean,
    onEditClick: () -> Unit,

    showMapFAB: Boolean,
    onMapClick: () -> Unit,
    isMapFABExpanded: Boolean,
    modifier: Modifier = Modifier
){
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(1000),
            initialOffsetX = { (it * 2) }
        ),
        exit = slideOutHorizontally(
            animationSpec = tween(1000),
            targetOffsetX = { (it * 2) }
        )
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = modifier
        ) {
            FloatingActionButton(onClick = onEditClick) {
                DisplayIcon(icon = MyIcons.edit)
            }

            if (showMapFAB) {
                MySpacerColumn(height = 16.dp)

                //trip map floating button
                SeeOnMapExtendedFAB(
                    onClick = onMapClick,
                    expanded = isMapFABExpanded
                )
            }
        }
    }
}

@Composable
fun IconFAB(
    onClick: () -> Unit,
    visible: Boolean,
    icon: MyIcon
){
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(700),
            initialOffsetX = { (it * 2) }
        ),
        exit = slideOutHorizontally(
            animationSpec = tween(700),
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
//    visible: Boolean,
    onClick: () -> Unit,
    expanded: Boolean
){
//    AnimatedVisibility(
//        visible = visible,
//        enter = slideInHorizontally(
//            animationSpec = tween(10000),
//            initialOffsetX = { (it * 2) }),
//        exit = slideOutHorizontally(
//            animationSpec = tween(2000),
//            targetOffsetX = { (it * 2) })
//    ) {
        SomewhereFloatingActionButton(
            text = stringResource(id = R.string.see_on_map),
            icon = MyIcons.map,
            onClick = onClick,
            expanded = expanded
        )
//    }
}

@Composable
private fun SomewhereFloatingActionButton(
    text: String,
    icon: MyIcon,
    onClick: () -> Unit,
    expanded: Boolean
) {
    ExtendedFloatingActionButton(
        text = { Text(text = text, style = getTextStyle(TextType.FAB)) },
        icon = { DisplayIcon(icon, color = white) },
        onClick = onClick,
        expanded = expanded
    )
}