package com.example.somewhere.ui.mainScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.SomewhereNavigationBar
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.example.somewhere.ui.settingScreenUtils.ItemDivider
import com.example.somewhere.ui.settingScreenUtils.ItemWithText
import com.example.somewhere.ui.settingScreenUtils.ListGroupCard
import com.example.somewhere.ui.settingScreens.AboutDestination
import com.example.somewhere.ui.settingScreens.SetDateFormatDestination
import com.example.somewhere.ui.settingScreens.SetThemeScreenDestination
import kotlinx.coroutines.launch

object MoreDestination: NavigationDestination {
    override val route = "more"
    override var title = "More"
}

@OptIn(ExperimentalMaterial3Api::class)
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
                title = MoreDestination.title,
//                navigationIcon = MyIcons.back,
//                navigationIconOnClick = { navigateUp() }
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
            modifier = modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding())
        ) {

            //setting
            item {
                ListGroupCard(
                    title = "Settings"
                ) {
                    //date time format
                    ItemWithText(
                        body1Text = SetDateFormatDestination.title,
                        onItemClick = { navigateTo(SetDateFormatDestination) }
                    )

                    ItemDivider()

                    //app theme
                    ItemWithText(
                        body1Text = SetThemeScreenDestination.title,
                        onItemClick = { navigateTo(SetThemeScreenDestination) }
                    )
                }
            }

            //feedback and bug report
            item {
                val uriHandler = LocalUriHandler.current
                val feedbackUri = stringResource(id = R.string.feedback_uri)
                val bugReportUri = stringResource(id = R.string.bug_report_uri)

                ListGroupCard(
                    title = "Feedback"
                ) {
                    //send feedback - open web browser to google form
                    ItemWithText(
                        body1Text = stringResource(id = R.string.send_feedback),
                        onItemClick = { uriHandler.openUri(feedbackUri) },
                        isOpenInNew = true
                    )

                    ItemDivider()

                    //bug report - open web browser to google form
                    ItemWithText(
                        body1Text = stringResource(id = R.string.bug_report),
                        onItemClick = { uriHandler.openUri(bugReportUri) },
                        isOpenInNew = true
                    )
                }
            }

            //about
            item {
                ListGroupCard {
                    //send feedback
                    ItemWithText(
                        body1Text = AboutDestination.title,
                        onItemClick = { navigateTo(AboutDestination) }
                    )
                }
            }
        }
    }
}