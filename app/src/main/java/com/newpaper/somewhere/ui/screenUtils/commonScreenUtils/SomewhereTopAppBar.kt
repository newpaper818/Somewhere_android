package com.newpaper.somewhere.ui.screenUtils.commonScreenUtils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.theme.n100
import com.newpaper.somewhere.ui.theme.n50
import com.newpaper.somewhere.ui.theme.n90

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SomewhereTopAppBar(
    title: String,
    subTitle: String? = null,

    navigationIcon: MyIcon? = null,
    navigationIconOnClick: () -> Unit = {},

    actionIcon1: MyIcon? = null,
    actionIcon1Onclick: () -> Unit = {},

    actionIcon2: MyIcon? = null,
    actionIcon2Onclick: () -> Unit = {},

    useHorizontalLayoutTitles: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.background,
    startPadding: Dp = SPACER_SMALL
) {

    //app bar
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
        title = {
            Row {
                if (navigationIcon == null)
                    MySpacerRow(width = startPadding)

                if (!useHorizontalLayoutTitles) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = getTextStyle(TextType.TOP_BAR__TITLE),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subTitle != null) {
                            MySpacerColumn(height = 2.dp)
                            
                            Text(
                                text = subTitle,
                                style = getTextStyle(TextType.TOP_BAR__SUBTITLE),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                else {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = title,
                            style = getTextStyle(TextType.TOP_BAR__TITLE),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subTitle != null) {
                            MySpacerRow(width = SPACER_BIG)
                            
                            Text(
                                text = subTitle,
                                style = getTextStyle(TextType.TOP_BAR__SUBTITLE),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

        },
        navigationIcon = {
            Row {
                if (navigationIcon != null) {
                    IconButton(onClick = navigationIconOnClick) {
                        DisplayIcon(icon = navigationIcon)
                    }
                }
            }
        },
        actions = {
            if (actionIcon1 != null) {
                IconButton(onClick = actionIcon1Onclick) {
                    DisplayIcon(icon = actionIcon1)
                }
            }
            if (actionIcon2 != null) {
                IconButton(onClick = actionIcon2Onclick) {
                    DisplayIcon(icon = actionIcon2)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageTopAppBar(
    visible: Boolean,
    title: String,

    navigationIcon: MyIcon? = MyIcons.imageScreenClose,
    navigationIconOnClick: () -> Unit = {},

    actionIcon1: MyIcon? = null,
    actionIcon1Onclick: () -> Unit = {},

    containerColor: Color = getColor(ColorType.IMAGE_FOREGROUND)
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500))
    ) {


        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
            title = {
                Text(
                    text = title,
                    style = getTextStyle(TextType.TOP_BAR__SUBTITLE).copy(color = n100),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (navigationIcon != null) {
                    IconButton(onClick = navigationIconOnClick) {
                        DisplayIcon(icon = navigationIcon)
                    }
                }
            },
            actions = {
                if (actionIcon1 != null) {
                    IconButton(onClick = actionIcon1Onclick) {
                        DisplayIcon(icon = actionIcon1, color = n100)
                    }
                }
            }
        )
    }
}