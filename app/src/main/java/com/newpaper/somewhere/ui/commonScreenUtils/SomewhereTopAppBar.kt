package com.newpaper.somewhere.ui.commonScreenUtils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

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
    actionIcon2Onclick: () -> Unit = {}
) {

    //app bar
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        title = {
            Row {
                if (navigationIcon == null)
                    MySpacerRow(width = 16.dp)
                
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = getTextStyle(TextType.TOP_BAR__TITLE),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subTitle != null) {
                        Text(
                            text = subTitle,
                            style = getTextStyle(TextType.TOP_BAR__SUBTITLE),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

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