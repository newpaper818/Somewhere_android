package com.newpaper.somewhere.feature.profile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.MyQrCodeButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.GoogleMediumRectangleAd
import com.newpaper.somewhere.core.ui.card.UserProfileCard
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID_TEST
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.dialog.myQrCode.MyQrCodeDialog
import com.newpaper.somewhere.feature.profile.R
import com.newpaper.somewhere.feature.trip.BuildConfig
import com.newpaper.somewhere.feature.trip.image.ImageViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun ProfileRoute(
    internetEnabled: Boolean,
    spacerValue: Dp,
    lazyListState: LazyListState,
    use2Panes: Boolean,
    userData: UserData,
    navigateToAccount: () -> Unit,
    navigateToSubscription: () -> Unit,
    hazeState: HazeState?,

    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val adView =
        if (!userData.isUsingSomewherePro) {
            AdView(context).apply {
                setAdSize(AdSize.MEDIUM_RECTANGLE)
                adUnitId = if (BuildConfig.DEBUG) BANNER_AD_UNIT_ID_TEST
                            else BANNER_AD_UNIT_ID

                loadAd(AdRequest.Builder().build())
            }
        }
        else null

    ProfileScreen(
        internetEnabled = internetEnabled,
        spacerValue = spacerValue,
        lazyListState = lazyListState,
        userData = userData,
        onProfileClick = navigateToAccount,
        onClickRemoveAds = navigateToSubscription,
        downloadImage = imageViewModel::getImage,
        adView = adView,
        hazeState = hazeState,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    internetEnabled: Boolean,
    spacerValue: Dp,
    lazyListState: LazyListState,
    userData: UserData,
    onProfileClick: () -> Unit,
    onClickRemoveAds: () -> Unit,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,
    adView: AdView?,
    hazeState: HazeState?,

    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showMyQrCodeDialog by rememberSaveable { mutableStateOf(false) }


    if (showMyQrCodeDialog) {
        MyQrCodeDialog(
            userData = userData,
            internetEnabled = internetEnabled,
            downloadImage = downloadImage,
            onDismissRequest = {
                showMyQrCodeDialog = false
            }
        )
    }
    MyScaffold(
        modifier = modifier,
        topBar = {
            SomewhereTopAppBar(
                startPadding = spacerValue,
                title = stringResource(id = R.string.profile),
                hazeState = hazeState
            )
        }
    ) {paddingValues ->
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(spacerValue, 8.dp + paddingValues.calculateTopPadding(), spacerValue, 200.dp),
            modifier = if(hazeState != null) Modifier
                            .fillMaxSize()
                            .hazeSource(state = hazeState)
                        else Modifier.fillMaxSize()
        ){

            item {
                //user image and name
                UserProfileCard(
                    userData = userData,
                    internetEnabled = internetEnabled,
                    downloadImage = downloadImage,
                    onProfileClick = onProfileClick,
                    modifier = Modifier.widthIn(max = itemMaxWidth)
                )
            }

            //user QR code
            item{
                //see my qr code
                MyQrCodeButton(
                    onClick = { showMyQrCodeDialog = true }
                )
            }

            if (adView != null) {
                item {
                    MySpacerColumn(height = 100.dp)

                    GoogleMediumRectangleAd(
                        adView = adView,
                        onClickRemoveAds = onClickRemoveAds
                    )
                }
            }
        }
    }
}





























@Composable
@PreviewLightDark
private fun ProfileScreenPreview(){
    SomewhereTheme {
        val context = LocalContext.current
        MyScaffold {
            ProfileScreen(
                internetEnabled = true,
                spacerValue = 16.dp,
                lazyListState = LazyListState(),
                userData = UserData(
                    userId = "dfsd",
                    userName = "user name",
                    email = "somewhere@gmail.com",
                    profileImagePath = null,
                    providerIds = listOf(),
                ),
                onProfileClick = { },
                onClickRemoveAds = { },
                downloadImage = { _,_,_ ->},
                adView = AdView(context).apply {},
                hazeState = rememberHazeState()
            )
        }
    }
}