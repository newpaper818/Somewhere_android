package com.newpaper.somewhere.feature.profile.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
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
    snackBarHostState: SnackbarHostState,

    modifier: Modifier = Modifier,
    imageViewModel: ImageViewModel = hiltViewModel()
) {

    ProfileScreen(
        internetEnabled = internetEnabled,
        spacerValue = spacerValue,
        lazyListState = lazyListState,
        use2Panes = use2Panes,
        userData = userData,
        onProfileClick = navigateToAccount,
        snackBarHostState = snackBarHostState,
        downloadImage = imageViewModel::getImage,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    internetEnabled: Boolean,
    spacerValue: Dp,
    lazyListState: LazyListState,
    use2Panes: Boolean,
    userData: UserData?,
    onProfileClick: () -> Unit,
    snackBarHostState: SnackbarHostState,
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
        },
        //FIXME can I remove this? if don't use snackbar
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.width(500.dp)
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
        ){
            if (userData == null) {
                item {
                    Text(text = "no user")
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
                use2Panes = false,
                userData = UserData(
                    userId = "dfsd",
                    userName = "user name",
                    email = "somewhere@gmail.com",
                    profileImagePath = null,
                    providerIds = listOf()
                ),
                onProfileClick = { },
                snackBarHostState = SnackbarHostState(),
                downloadImage = { _,_,_ ->}
            )
        }
    }
}