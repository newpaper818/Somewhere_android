package com.newpaper.somewhere.feature.more.about

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.card.AppIconWithAppNameCard
import com.newpaper.somewhere.core.ui.card.ShareAppCard
import com.newpaper.somewhere.core.ui.card.VersionCard
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.ui.item.ItemWithText
import com.newpaper.somewhere.core.ui.item.ListGroupCard
import com.newpaper.somewhere.core.utils.BUG_REPORT_URL
import com.newpaper.somewhere.core.utils.FEEDBACK_URL
import com.newpaper.somewhere.core.utils.GITHUB_URL
import com.newpaper.somewhere.core.utils.SOMEWHERE_PLAY_STORE_URL
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.core.utils.onClickPrivacyPolicy
import com.newpaper.somewhere.feature.more.BuildConfig
import com.newpaper.somewhere.feature.more.R
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay

@Composable
fun AboutRoute(
    use2Panes: Boolean,
    spacerValue: Dp,
    useBlurEffect: Boolean,

    internetEnabled: Boolean,

    currentAppVersionCode: Int,
    currentAppVersionName: String,
    isDebugMode: Boolean,

    navigateToOpenSourceLicense: () -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,

    aboutViewModel: AboutViewModel = hiltViewModel()
){
    LaunchedEffect(internetEnabled) {
        if (internetEnabled) {
            delay(500)
            aboutViewModel.updateIsLatestAppVersion(currentAppVersionCode)
        }
    }


    val aboutUiState by aboutViewModel.aboutUiState.collectAsStateWithLifecycle()

    val uriHandler = LocalUriHandler.current

    val context = LocalContext.current

    val email = stringResource(id = R.string.contact_email)



    AboutScreen(
        use2Panes = use2Panes,
        startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        endSpacerValue = spacerValue,
        useBlurEffect = useBlurEffect,

        currentAppVersionName = currentAppVersionName,
        isLatestAppVersion = aboutUiState.isLatestAppVersion,
        isDebugMode = isDebugMode,
        navigateToOpenSourceLicense = navigateToOpenSourceLicense,
        navigateUp = navigateUp,
        onClickUpdate = { uriHandler.openUri(SOMEWHERE_PLAY_STORE_URL) },
        onClickDeveloper = { uriHandler.openUri(GITHUB_URL) },
        onClickContact = {
            val uri = Uri.parse("mailto:$email")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            context.startActivity(intent)
        },
        onClickReviewTheApp = {
            uriHandler.openUri(SOMEWHERE_PLAY_STORE_URL)
//            showAppReviewDialog(context) {
//                uriHandler.openUri(SOMEWHERE_PLAY_STORE_URL)
//            }
        },
        onClickSendFeedback = { uriHandler.openUri(FEEDBACK_URL) },
        onClickReportBugs = { uriHandler.openUri(BUG_REPORT_URL) },
        onClickPrivacyPolicy = { onClickPrivacyPolicy(uriHandler) },
        onClickShareApp = {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, SOMEWHERE_PLAY_STORE_URL)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)

            context.startActivity(shareIntent)
        },
        modifier = modifier
    )
}





private fun showAppReviewDialog(
    context: Context,
    openPlayStoreUrl: () -> Unit
) {
    val reviewManager = if (BuildConfig.DEBUG) { FakeReviewManager(context) }
                        else { ReviewManagerFactory.create(context) }


    val activity = context as? Activity ?: return

    Log.d("review", "start review")
    reviewManager.requestReviewFlow().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("review", "get review info success")

            val reviewInfo = task.result

            val flow = reviewManager.launchReviewFlow(activity, reviewInfo)

            flow.addOnCompleteListener {
                Log.d("review", "review complete")
            }
        } else {
            Log.d("review", "request review fail")

            openPlayStoreUrl()
        }
    }
}





@Composable
private fun AboutScreen(
    modifier: Modifier = Modifier,

    use2Panes: Boolean = false,
    startSpacerValue: Dp = 16.dp,
    endSpacerValue: Dp = 16.dp,
    useBlurEffect: Boolean = true,

    currentAppVersionName: String = "99.99.999",
    isLatestAppVersion: Boolean? = true,
    isDebugMode: Boolean = false,

    navigateToOpenSourceLicense: () -> Unit = {},
    navigateUp: () -> Unit = {},

    onClickUpdate: () -> Unit = {},
    onClickDeveloper: () -> Unit = {},
    onClickContact: () -> Unit = {},
    onClickReviewTheApp: () -> Unit = {},
    onClickSendFeedback: () -> Unit = {},
    onClickReportBugs: () -> Unit = {},
    onClickPrivacyPolicy: () -> Unit = {},
    onClickShareApp: () -> Unit = {},
){
    val topAppBarHazeState = if(useBlurEffect) rememberHazeState() else null

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.about),
                navigationIcon = if (!use2Panes) TopAppBarIcon.back else null,
                onClickNavigationIcon = { navigateUp() },
                hazeState = topAppBarHazeState
            )
        }
    ){ paddingValues ->

        val itemModifier = Modifier.widthIn(max = itemMaxWidthSmall)

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp + paddingValues.calculateTopPadding(), endSpacerValue, 200.dp),
            modifier = if (topAppBarHazeState != null) Modifier.fillMaxSize()
                            .hazeSource(state = topAppBarHazeState).background(MaterialTheme.colorScheme.background)
                        else Modifier.fillMaxSize()
        ) {

            //app icon image
            item{
                MySpacerColumn(height = 16.dp)
                AppIconWithAppNameCard(
                    modifier = itemModifier
                )
                MySpacerColumn(height = 32.dp)

                ItemDivider(modifier = itemModifier)
            }

            //app version
            item{
                VersionCard(
                    currentAppVersionName = currentAppVersionName,
                    isLatestAppVersion = isLatestAppVersion,
                    onClickUpdate = onClickUpdate,
                    modifier = itemModifier
                )

                MySpacerColumn(16.dp)
            }

            //developer
            item{
                ListGroupCard(
                    modifier = itemModifier
                ) {
                    ItemWithText(
                        titleText = stringResource(id = R.string.developer),
                        text = stringResource(id = R.string.developer_info),
                        onItemClick = onClickDeveloper,
                        iconRight = MyIcons.openInNew
                    )
                }
            }

            //contact
            item{
                val email = stringResource(id = R.string.contact_email)

                ListGroupCard(
                    modifier = itemModifier
                ) {
                    ItemWithText(
                        titleText = stringResource(id = R.string.contact),
                        text = email,
                        onItemClick = onClickContact,
                        iconRight = MyIcons.sendEmail
                    )
                }
            }

            //feedback and bug report
            item {
                ListGroupCard(
                    modifier = itemModifier
                ) {
                    //google play app review
                    ItemWithText(
                        text = stringResource(id = R.string.app_review),
                        onItemClick = onClickReviewTheApp,
                        iconRight = MyIcons.openInNew
                    )

                    ItemDivider()

                    //send feedback - open web browser to google form
                    ItemWithText(
                        text = stringResource(id = R.string.send_feedback),
                        onItemClick = onClickSendFeedback,
                        iconRight = MyIcons.openInNew
                    )

                    ItemDivider()

                    //bug report - open web browser to google form
                    ItemWithText(
                        text = stringResource(id = R.string.bug_report),
                        onItemClick = onClickReportBugs,
                        iconRight = MyIcons.openInNew
                    )
                }
            }

            item{
                ListGroupCard(
                    modifier = itemModifier
                ) {
                    //privacy policy
                    ItemWithText(
                        text = stringResource(id = R.string.privacy_policy),
                        onItemClick = onClickPrivacyPolicy,
                        iconRight = MyIcons.openInNew
                    )

                    ItemDivider()

                    //open source license
                    ItemWithText(
                        text = stringResource(id = R.string.open_source_license),
                        onItemClick = navigateToOpenSourceLicense
                    )
                }
            }

            //share app / qr code
            item {
                MySpacerColumn(height = 64.dp)

                ShareAppCard(
                    modifier = itemModifier.fillMaxWidth(),
                    onClickShareApp = onClickShareApp,
                )
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
private fun AboutScreenPreview(){
    SomewhereTheme {
        AboutScreen(
            isLatestAppVersion = true
        )
    }
}

@PreviewLightDark
@Composable
private fun AboutScreenPreviewNotLatest(){
    SomewhereTheme {
        AboutScreen(
            isLatestAppVersion = false
        )
    }
}
