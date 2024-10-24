package com.newpaper.somewhere.feature.more.about

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.ui.card.AppIconWithAppNameCard
import com.newpaper.somewhere.core.ui.card.DeveloperCard
import com.newpaper.somewhere.core.ui.card.ShareAppCard
import com.newpaper.somewhere.core.ui.card.VersionCard
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.ui.item.ItemWithText
import com.newpaper.somewhere.core.ui.item.ListGroupCard
import com.newpaper.somewhere.core.utils.PLAY_STORE_URL
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.core.utils.onClickPrivacyPolicy
import com.newpaper.somewhere.feature.more.R

@Composable
fun AboutRoute(
    use2Panes: Boolean,
    spacerValue: Dp,

    currentAppVersionCode: Int,
    currentAppVersionName: String,
    isDebugMode: Boolean,

    navigateToOpenSourceLicense: () -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,

    aboutViewModel: AboutViewModel = hiltViewModel()
){
    LaunchedEffect(Unit) {
        aboutViewModel.updateIsLatestAppVersion(currentAppVersionCode)
    }

    val aboutUiState by aboutViewModel.aboutUiState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, PLAY_STORE_URL)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    AboutScreen(
        use2Panes = use2Panes,
        startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        endSpacerValue = spacerValue,
        currentAppVersionName = currentAppVersionName,
        isLatestAppVersion = aboutUiState.isLatestAppVersion,
        isDebugMode = isDebugMode,
        navigateToOpenSourceLicense = navigateToOpenSourceLicense,
        navigateUp = navigateUp,
        onClickShareApp = {
            context.startActivity(shareIntent)
        },
        snackBarHostState = snackBarHostState,
        modifier = modifier
    )
}

@Composable
private fun AboutScreen(
    use2Panes: Boolean,
    startSpacerValue: Dp,
    endSpacerValue: Dp,

    currentAppVersionName: String,
    isLatestAppVersion: Boolean?,
    isDebugMode: Boolean,

    navigateToOpenSourceLicense: () -> Unit,
    navigateUp: () -> Unit,

    onClickShareApp: () -> Unit,

    snackBarHostState: SnackbarHostState,

    modifier: Modifier = Modifier
){
    val uriHandler = LocalUriHandler.current

    val scaffoldModifier = if (use2Panes) modifier
                    else modifier.navigationBarsPadding()

    Scaffold(
        modifier = scaffoldModifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.about),
                navigationIcon = if (!use2Panes) TopAppBarIcon.back else null,
                onClickNavigationIcon = { navigateUp() }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.width(500.dp),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        }
    ){ paddingValues ->

        val itemModifier = Modifier.widthIn(max = itemMaxWidthSmall)

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item{
                //app icon image
                MySpacerColumn(height = 16.dp)
                AppIconWithAppNameCard(
                    modifier = itemModifier
                )
                MySpacerColumn(height = 24.dp)
            }

            item{
                //app version
                VersionCard(
                    currentAppVersionName = currentAppVersionName,
                    isLatestAppVersion = isLatestAppVersion,
                    onClickUpdate = { uriHandler.openUri(PLAY_STORE_URL) },
                    modifier = itemModifier
                )
            }

            item{
                //developer info
                DeveloperCard(
                    modifier = itemModifier
                )
            }

            item{
                ListGroupCard(
                    modifier = itemModifier
                ) {
                    //privacy policy
                    ItemWithText(
                        text = stringResource(id = R.string.privacy_policy),
                        isOpenInNew = true,
                        onItemClick = { onClickPrivacyPolicy(uriHandler)}
                    )

                    ItemDivider()

                    //open source license
                    ItemWithText(
                        text = stringResource(id = R.string.open_source_license),
                        onItemClick = {
                            navigateToOpenSourceLicense()
                        }
                    )
                }
            }

            item {
                MySpacerColumn(height = 64.dp)

                //copy link / qr code
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