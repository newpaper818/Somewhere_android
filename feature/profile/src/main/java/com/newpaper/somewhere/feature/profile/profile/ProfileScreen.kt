package com.newpaper.somewhere.feature.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lightspark.composeqr.DotShape
import com.lightspark.composeqr.QrCodeView
import com.newpaper.somewhere.core.designsystem.component.ImageFromDrawable
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.core.ui.card.UserProfileCard
import com.newpaper.somewhere.feature.profile.R
import com.newpaper.somewhere.feature.trip.image.ImageViewModel

@Composable
fun ProfileRoute(
    internetEnabled: Boolean,
    spacerValue: Dp,
    lazyListState: LazyListState,
    use2Panes: Boolean,
    userData: UserData?,
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
    userData: UserData?,
    onProfileClick: () -> Unit,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,

    modifier: Modifier = Modifier
) {
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
            if (userData == null) {
                item {
                    ErrorScreen()
                }
            }
            else {
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
            }



            //user QR code
            if (userData != null) {
                item{
                    UserQrCode(userId = userData.userId)
                }
            }
        }
    }
}




@Composable
fun UserQrCode(
    userId: String
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
    ) {
        QrCodeView(
            data = "somewhere_$userId",
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            dotShape = DotShape.Circle
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                ImageFromDrawable(
                    imageDrawable = com.newpaper.somewhere.core.ui.ui.R.drawable.app_icon_fit,
                    contentDescription = stringResource(id = com.newpaper.somewhere.core.ui.ui.R.string.somewhere_app_icon),
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxSize()
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