package com.newpaper.somewhere.core.designsystem.component.topAppBars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.IconTextButtonIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SomewhereTopAppBar(
    title: String,
    internetEnabled: Boolean = true,
    subtitle: String? = null,

    navigationIcon: MyIcon? = null,
    onClickNavigationIcon: () -> Unit = {},

    actionIcon1: MyIcon? = null,
    actionIcon1Onclick: () -> Unit = {},
    actionIcon1Visible: Boolean = true,

    actionIcon2: MyIcon? = null,
    actionIcon2Onclick: () -> Unit = {},
    actionIcon2Visible: Boolean = true,

    dropdownMenuContent: @Composable () -> Unit = {},

    useHorizontalLayoutTitles: Boolean = false,
    startPadding: Dp = 16.dp
) {

    //app bar
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
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
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subtitle != null) {
                            MySpacerColumn(height = 2.dp)
                            TopAppBarSubtitle(subtitle = subtitle)
                        }
                        else {
                            AnimatedVisibility(
                                visible = !internetEnabled,
                                enter = expandVertically(tween(500)) + fadeIn(tween(500, 200)),
                                exit = shrinkVertically(tween(500, 200)) + fadeOut(tween(500))
                            ) {
                                MySpacerColumn(height = 2.dp)
                                InternetUnavailable()
                            }
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
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subtitle != null) {
                            MySpacerRow(width = 24.dp)
                            TopAppBarSubtitle(subtitle = subtitle)
                        }
                        else {
                            AnimatedVisibility(
                                visible = !internetEnabled,
                                enter = expandVertically(tween(500)) + fadeIn(tween(500, 200)),
                                exit = shrinkVertically(tween(500, 200)) + fadeOut(tween(500))
                            ) {
                                Row {
                                    MySpacerRow(width = 24.dp)
                                    InternetUnavailable()
                                }
                            }
                        }
                    }
                }
            }

        },
        navigationIcon = {
            if (navigationIcon != null) {
                MyPlainTooltipBox(
                    tooltipText = stringResource(id = navigationIcon.descriptionTextId)
                ) {
                    IconButton(onClick = onClickNavigationIcon) {
                        DisplayIcon(icon = navigationIcon)
                    }
                }
            }
        },
        actions = {
            //action1
            AnimatedVisibility(
                visible = actionIcon1Visible,
                enter = fadeIn(tween(350)),
                exit = fadeOut(tween(350))
            ) {
                if (actionIcon1 != null) {
                    MyPlainTooltipBox(
                        tooltipText = stringResource(id = actionIcon1.descriptionTextId)
                    ) {
                        IconButton(onClick = actionIcon1Onclick) {
                            DisplayIcon(icon = actionIcon1)
                        }
                    }
                }
            }

            //action2
            AnimatedVisibility(
                visible = actionIcon2Visible,
                enter = fadeIn(tween(350)),
                exit = fadeOut(tween(350))
            ) {
                if (actionIcon2 != null) {
                    MyPlainTooltipBox(
                        tooltipText = stringResource(id = actionIcon2.descriptionTextId)
                    ) {
                        IconButton(onClick = actionIcon2Onclick) {
                            DisplayIcon(icon = actionIcon2)
                        }
                    }
                }
            }

            //dropdown menu
            dropdownMenuContent()
        }
    )
}

@Composable
private fun TopAppBarSubtitle(
    subtitle: String
){
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun InternetUnavailable(

){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(20.dp)
    ) {

        Text(
            text = stringResource(id = R.string.internet_unavailable),
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}










@Composable
@PreviewLightDark
private fun SomewhereTopAppBarPreview(){
    SomewhereTheme {
        SomewhereTopAppBar(
            title = "Top app bar title"
        )
    }
}

@Composable
@PreviewLightDark
private fun SomewhereTopAppBarPreview2(){
    SomewhereTheme {
        SomewhereTopAppBar(
            title = "Top app bar title",
            subtitle = "this is subtitle"
        )
    }
}

@Composable
@PreviewLightDark
private fun SomewhereTopAppBarPreview3(){
    SomewhereTheme {
        SomewhereTopAppBar(
            title = "Top app bar title",
            subtitle = "this is subtitle",
            navigationIcon = TopAppBarIcon.back,
            onClickNavigationIcon = { },
            actionIcon1 = TopAppBarIcon.edit,
            actionIcon2 = IconTextButtonIcon.add
        )
    }
}

@Composable
@PreviewLightDark
private fun SomewhereTopAppBarPreview4(){
    SomewhereTheme {
        SomewhereTopAppBar(
            internetEnabled = false,
            title = "Top app bar title",
            navigationIcon = TopAppBarIcon.back,
            onClickNavigationIcon = { },
            actionIcon1 = TopAppBarIcon.edit,
            actionIcon2 = IconTextButtonIcon.add
        )
    }
}

@Composable
@Preview(
    widthDp = 600
)
private fun SomewhereTopAppBarLarge_Dark(){
    SomewhereTheme(darkTheme = true) {
        SomewhereTopAppBar(
            title = "This is for large width",
            subtitle = "this is subtitle",
            navigationIcon = TopAppBarIcon.back,
            onClickNavigationIcon = { },
            actionIcon1 = TopAppBarIcon.edit,
            actionIcon2 = IconTextButtonIcon.add,
            useHorizontalLayoutTitles = true
        )
    }
}

@Composable
@Preview(
    widthDp = 600
)
private fun SomewhereTopAppBarLarge(){
    SomewhereTheme {
        SomewhereTopAppBar(
            title = "This is for large width",
            subtitle = "this is subtitle",
            navigationIcon = TopAppBarIcon.back,
            onClickNavigationIcon = { },
            actionIcon1 = TopAppBarIcon.edit,
            actionIcon2 = IconTextButtonIcon.add,
            useHorizontalLayoutTitles = true
        )
    }
}

@Composable
@Preview(
    widthDp = 600
)
private fun SomewhereTopAppBarLarge2_Dark(){
    SomewhereTheme(darkTheme = true) {
        SomewhereTopAppBar(
            internetEnabled = false,
            title = "This is for large width",
            navigationIcon = TopAppBarIcon.back,
            onClickNavigationIcon = { },
            actionIcon1 = TopAppBarIcon.edit,
            actionIcon2 = IconTextButtonIcon.add,
            useHorizontalLayoutTitles = true
        )
    }
}

@Composable
@Preview(
    widthDp = 600
)
private fun SomewhereTopAppBarLarge2(){
    SomewhereTheme {
        SomewhereTopAppBar(
            internetEnabled = false,
            title = "This is for large width",
            navigationIcon = TopAppBarIcon.back,
            onClickNavigationIcon = { },
            actionIcon1 = TopAppBarIcon.edit,
            actionIcon2 = IconTextButtonIcon.add,
            useHorizontalLayoutTitles = true
        )
    }
}