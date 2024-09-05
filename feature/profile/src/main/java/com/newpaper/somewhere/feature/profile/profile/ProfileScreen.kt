package com.newpaper.somewhere.feature.profile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.MyQrCodeButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.card.UserProfileCard
import com.newpaper.somewhere.feature.dialog.myQrCode.MyQrCodeDialog
import com.newpaper.somewhere.feature.profile.R
import com.newpaper.somewhere.feature.trip.image.ImageViewModel

@Composable
fun ProfileRoute(
    internetEnabled: Boolean,
    spacerValue: Dp,
    lazyListState: LazyListState,
    use2Panes: Boolean,
    userData: UserData,
    navigateToAccount: () -> Unit,

    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel = hiltViewModel()
) {

    ProfileScreen(
        internetEnabled = internetEnabled,
        spacerValue = spacerValue,
        lazyListState = lazyListState,
        userData = userData,
        onProfileClick = navigateToAccount,
        downloadImage = imageViewModel::getImage,
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
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,

    modifier: Modifier = Modifier
) {
    
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
                title = stringResource(id = R.string.profile)
            )
        }
    ) {paddingValues ->
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(spacerValue, 8.dp, spacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
        ){

            item {
                //user image and name
                UserProfileCard(
                    userData = userData,
                    internetEnabled = internetEnabled,
                    downloadImage = downloadImage,
                    onProfileClick = onProfileClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            //user QR code
            item{
                //see my qr code
                MyQrCodeButton(
                    onClick = { showMyQrCodeDialog = true }
                )
            }
        }
    }
}





























@Composable
@PreviewLightDark
private fun ProfileScreenPreview(){
    SomewhereTheme {
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
                    providerIds = listOf()
                ),
                onProfileClick = { },
                downloadImage = { _,_,_ ->}
            )
        }
    }
}