package com.newpaper.somewhere.feature.more.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.ui.item.ItemWithText
import com.newpaper.somewhere.core.ui.item.ListGroupCard
import com.newpaper.somewhere.core.utils.BUG_REPORT_URL
import com.newpaper.somewhere.core.utils.FEEDBACK_URL
import com.newpaper.somewhere.feature.more.R

@Composable
fun MoreRoute(
    isDebugMode: Boolean,
    userDataIsNull: Boolean,

    spacerValue: Dp,
    lazyListState: LazyListState,
    navigateTo: (ScreenDestination) -> Unit,

    modifier: Modifier = Modifier,
    use2Panes: Boolean = false,
    currentScreen: ScreenDestination? = null
) {
    MoreScreen(
        isDebugMode = isDebugMode,
        userDataIsNull = userDataIsNull,
        startSpacerValue = spacerValue,
        endSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        lazyListState = lazyListState,
        navigateTo = navigateTo,
        modifier = modifier,
        currentScreen = currentScreen
    )
}

@Composable
private fun MoreScreen(
    isDebugMode: Boolean,
    userDataIsNull: Boolean,

    startSpacerValue: Dp,
    endSpacerValue: Dp,
    lazyListState: LazyListState,
    navigateTo: (ScreenDestination) -> Unit,

    modifier: Modifier = Modifier,
    currentScreen: ScreenDestination? = null
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.more)
            )
        },
    ){ paddingValues ->

        LazyColumn(
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {
            //setting
            item {
                ListGroupCard(
                    title = stringResource(id = R.string.settings)
                ) {
                    //date time format
                    ItemWithText(
                        isSelected = currentScreen == ScreenDestination.SET_DATE_TIME_FORMAT,
                        text = stringResource(id = R.string.date_time_format),
                        onItemClick = { navigateTo(ScreenDestination.SET_DATE_TIME_FORMAT) }
                    )

                    ItemDivider()

                    //app theme
                    ItemWithText(
                        isSelected = currentScreen == ScreenDestination.SET_THEME,
                        text = stringResource(id = R.string.theme),
                        onItemClick = { navigateTo(ScreenDestination.SET_THEME) }
                    )

                    ItemDivider()

                    //account
                    ItemWithText(
                        isSelected = currentScreen == ScreenDestination.ACCOUNT
                                || currentScreen == ScreenDestination.SIGN_IN,
                        text = stringResource(id = R.string.account),
                        onItemClick = {
                            if (!userDataIsNull)
                                navigateTo(ScreenDestination.ACCOUNT)
                            else
                                navigateTo(ScreenDestination.SIGN_IN)
                        }
                    )
                }
            }

            //feedback and bug report
            item {
                val uriHandler = LocalUriHandler.current

                ListGroupCard(
                    title = stringResource(id = R.string.feedback)
                ) {
                    //send feedback - open web browser to google form
                    ItemWithText(
                        text = stringResource(id = R.string.send_feedback),
                        onItemClick = { uriHandler.openUri(FEEDBACK_URL) },
                        isOpenInNew = true
                    )

                    ItemDivider()

                    //bug report - open web browser to google form
                    ItemWithText(
                        text = stringResource(id = R.string.bug_report),
                        onItemClick = { uriHandler.openUri(BUG_REPORT_URL) },
                        isOpenInNew = true
                    )
                }
            }

            //about
            item {
                ListGroupCard {
                    ItemWithText(
                        isSelected = currentScreen == ScreenDestination.ABOUT,
                        text = stringResource(id = R.string.about),
                        onItemClick = { navigateTo(ScreenDestination.ABOUT) }
                    )
                }
            }

            if (isDebugMode)
                item{
                    Text(
                        text = "Debug Mode",
                        style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
        }
    }
}
































@PreviewLightDark
@Composable
private fun MoreScreenPreview(){
    SomewhereTheme {
        MoreScreen(
            isDebugMode = false,
            userDataIsNull = false,
            startSpacerValue = 16.dp,
            endSpacerValue = 16.dp,
            lazyListState = LazyListState(),
            navigateTo = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun MoreScreenPreview_Debug(){
    SomewhereTheme {
        MoreScreen(
            isDebugMode = true,
            userDataIsNull = false,
            startSpacerValue = 16.dp,
            endSpacerValue = 16.dp,
            lazyListState = LazyListState(),
            navigateTo = {}
        )
    }
}