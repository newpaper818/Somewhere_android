package com.newpaper.somewhere.ui.mainScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.commonScreenUtils.SomewhereNavigationBar
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.settingScreenUtils.ItemDivider
import com.newpaper.somewhere.ui.settingScreenUtils.ItemWithText
import com.newpaper.somewhere.ui.settingScreenUtils.ListGroupCard
import com.newpaper.somewhere.ui.settingScreens.AboutDestination
import com.newpaper.somewhere.ui.settingScreens.SetDateFormatDestination
import com.newpaper.somewhere.ui.settingScreens.SetThemeScreenDestination
import kotlinx.coroutines.launch

private const val feedbackUrl = "https://forms.gle/2UqNgmLqPdECiSb17"
private const val bugReportUrl = "https://forms.gle/5XZSxD6xPuLAeXah7"

object MoreDestination: NavigationDestination {
    override val route = "more"
    override var title = ""
}

@Composable
fun MoreScreen(
    navigateTo: (NavigationDestination) -> Unit,
    navigateToMain: (NavigationDestination) -> Unit,

//    navigateUp: () -> Unit,

    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    Scaffold(
        topBar = {
            SomewhereTopAppBar(
                title = stringResource(id = R.string.more)
            )
        },
        bottomBar = {
            SomewhereNavigationBar(
                currentDestination = MoreDestination,
                navigateTo = navigateToMain,
                scrollToTop = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                    }
                }
            )
        }
    ){ paddingValues ->

        LazyColumn(
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {

            //setting
            item {
                ListGroupCard(
                    title = stringResource(id = R.string.settings)
                ) {
                    //date time format
                    ItemWithText(
                        body1Text = stringResource(id = R.string.date_time_format),
                        onItemClick = { navigateTo(SetDateFormatDestination) }
                    )

                    ItemDivider()

                    //app theme
                    ItemWithText(
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
                        onItemClick = { uriHandler.openUri(feedbackUrl) },
                        isOpenInNew = true
                    )

                    ItemDivider()

                    //bug report - open web browser to google form
                    ItemWithText(
                        body1Text = stringResource(id = R.string.bug_report),
                        onItemClick = { uriHandler.openUri(bugReportUrl) },
                        isOpenInNew = true
                    )
                }
            }

            //about
            //about
            item {
                ListGroupCard {
                    //send feedback
                    ItemWithText(
                        body1Text = stringResource(id = R.string.about),
                        onItemClick = { navigateTo(AboutDestination) }
                    )
                }
            }
        }
    }
}