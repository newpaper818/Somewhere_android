package com.newpaper.somewhere.ui.screens.moreScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.navigation.AboutScreenDestination
import com.newpaper.somewhere.ui.navigation.ScreenDestination
import com.newpaper.somewhere.ui.navigation.SetDateTimeFormatScreenDestination
import com.newpaper.somewhere.ui.navigation.SetThemeScreenDestination
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ItemDivider
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ItemWithText
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ListGroupCard

private const val FEEDBACK_URL = "https://forms.gle/2UqNgmLqPdECiSb17"
private const val BUG_REPORT_URL = "https://forms.gle/5XZSxD6xPuLAeXah7"

@Composable
fun MoreScreen(
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    navigateTo: (ScreenDestination) -> Unit,

    modifier: Modifier = Modifier,
    currentScreen: ScreenDestination? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

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
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            //setting
            item {
                ListGroupCard(
                    title = stringResource(id = R.string.settings)
                ) {
                    //date time format
                    ItemWithText(
                        isSelected = currentScreen == SetDateTimeFormatScreenDestination,
                        body1Text = stringResource(id = R.string.date_time_format),
                        onItemClick = { navigateTo(SetDateTimeFormatScreenDestination) }
                    )

                    ItemDivider()

                    //app theme
                    ItemWithText(
                        isSelected = currentScreen == SetThemeScreenDestination,
                        body1Text = stringResource(id = R.string.theme),
                        onItemClick = { navigateTo(SetThemeScreenDestination) }
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
                        body1Text = stringResource(id = R.string.send_feedback),
                        onItemClick = { uriHandler.openUri(FEEDBACK_URL) },
                        isOpenInNew = true
                    )

                    ItemDivider()

                    //bug report - open web browser to google form
                    ItemWithText(
                        body1Text = stringResource(id = R.string.bug_report),
                        onItemClick = { uriHandler.openUri(BUG_REPORT_URL) },
                        isOpenInNew = true
                    )
                }
            }

            //about
            item {
                ListGroupCard {
                    ItemWithText(
                        isSelected = currentScreen == AboutScreenDestination,
                        body1Text = stringResource(id = R.string.about),
                        onItemClick = { navigateTo(AboutScreenDestination) }
                    )
                }
            }
        }
    }
}