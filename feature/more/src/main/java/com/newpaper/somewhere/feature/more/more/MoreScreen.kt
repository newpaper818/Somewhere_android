package com.newpaper.somewhere.feature.more.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.GoogleMediumRectangleAd
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.ui.item.ItemWithText
import com.newpaper.somewhere.core.ui.item.ListGroupCard
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID_TEST
import com.newpaper.somewhere.core.utils.convert.getContainAds
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.more.R
import com.newpaper.somewhere.feature.trip.BuildConfig

@Composable
fun MoreRoute(
    isDebugMode: Boolean,
    appUserData: UserData?,
    isUsingSomewherePro: Boolean,

    use2Panes: Boolean,
    spacerValue: Dp,
    lazyListState: LazyListState,
    navigateTo: (ScreenDestination) -> Unit,

    modifier: Modifier = Modifier,
    currentScreenRoute: String? = null
) {
    val context = LocalContext.current

    val adView = AdView(context).apply {
        setAdSize(AdSize.MEDIUM_RECTANGLE)
        adUnitId = if (BuildConfig.DEBUG) BANNER_AD_UNIT_ID_TEST
            else BANNER_AD_UNIT_ID

        loadAd(AdRequest.Builder().build())
    }

    MoreScreen(
        isDebugMode = isDebugMode,
        appUserData = appUserData,
        isUsingSomewherePro = isUsingSomewherePro,

        startSpacerValue = spacerValue,
        endSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        lazyListState = lazyListState,
        navigateTo = navigateTo,
        adView = adView,
        modifier = modifier,
        currentScreenRoute = currentScreenRoute
    )
}

@Composable
private fun MoreScreen(
    isDebugMode: Boolean,
    appUserData: UserData?,
    isUsingSomewherePro: Boolean,

    startSpacerValue: Dp,
    endSpacerValue: Dp,
    lazyListState: LazyListState,
    navigateTo: (ScreenDestination) -> Unit,
    adView: AdView,

    modifier: Modifier = Modifier,
    currentScreenRoute: String? = null
) {

    val itemModifier = Modifier.widthIn(max = itemMaxWidthSmall)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
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
        ) {
            //somewhere pro
            if (appUserData != null && !appUserData.isUsingSomewherePro) {
                item {
                    ListGroupCard(
                        modifier = itemModifier
                    ) {
                        ItemWithText(
                            text = stringResource(id = R.string.somewhere_pro),
                            onItemClick = { navigateTo(ScreenDestination.SUBSCRIPTION) }
                        )
                    }
                }
            }

            //setting
            item {
                ListGroupCard(
                    title = stringResource(id = R.string.settings),
                    modifier = itemModifier
                ) {
                    //date time format
                    ItemWithText(
                        isSelected = currentScreenRoute == ScreenDestination.SET_DATE_TIME_FORMAT.route,
                        text = stringResource(id = R.string.date_time_format),
                        onItemClick = { navigateTo(ScreenDestination.SET_DATE_TIME_FORMAT) }
                    )

                    ItemDivider()

                    //app theme
                    ItemWithText(
                        isSelected = currentScreenRoute == ScreenDestination.SET_THEME.route,
                        text = stringResource(id = R.string.theme),
                        onItemClick = { navigateTo(ScreenDestination.SET_THEME) }
                    )

                    ItemDivider()

                    //account
                    ItemWithText(
                        isSelected = currentScreenRoute == ScreenDestination.ACCOUNT.route
                                || currentScreenRoute == ScreenDestination.SIGN_IN.route,
                        text = stringResource(id = R.string.account),
                        onItemClick = {
                            if (appUserData != null)
                                navigateTo(ScreenDestination.ACCOUNT)
                            else
                                navigateTo(ScreenDestination.SIGN_IN)
                        }
                    )
                }
            }

            //about
            item {
                ListGroupCard(
                    modifier = itemModifier
                ) {
                    ItemWithText(
                        isSelected = currentScreenRoute == ScreenDestination.ABOUT.route,
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

            if (getContainAds(isUsingSomewherePro)) {
                item {
                    MySpacerColumn(height = 32.dp)

                    GoogleMediumRectangleAd(
                        adView = adView,
                        onClickRemoveAds = {
                            navigateTo(ScreenDestination.SUBSCRIPTION)
                        }
                    )
                }
            }
        }
    }
}
































@PreviewLightDark
@Composable
private fun MoreScreenPreview(){
    SomewhereTheme {
        val context = LocalContext.current
        MoreScreen(
            isDebugMode = false,
            appUserData = UserData("", "", "", "", listOf()),
            isUsingSomewherePro = false,
            startSpacerValue = 16.dp,
            endSpacerValue = 16.dp,
            lazyListState = LazyListState(),
            navigateTo = {},
            adView = AdView(context).apply {}
        )
    }
}

@PreviewLightDark
@Composable
private fun MoreScreenPreview_Debug(){
    SomewhereTheme {
        val context = LocalContext.current
        MoreScreen(
            isDebugMode = true,
            appUserData = UserData("", "", "", "", listOf()),
            isUsingSomewherePro = false,
            startSpacerValue = 16.dp,
            endSpacerValue = 16.dp,
            lazyListState = LazyListState(),
            navigateTo = {},
            adView = AdView(context).apply {}
        )
    }
}