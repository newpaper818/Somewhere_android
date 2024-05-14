package com.newpaper.somewhere.feature.signin.signIn

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.PrivacyPolicyButton
import com.newpaper.somewhere.core.designsystem.component.button.SignInWithButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.suite
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId
import com.newpaper.somewhere.core.model.enums.getProviderIdFromString
import com.newpaper.somewhere.core.ui.InternetUnavailableIconWithText
import com.newpaper.somewhere.core.ui.card.AppIconWithAppNameCard
import com.newpaper.somewhere.core.utils.PRIVACY_POLICY_URL
import com.newpaper.somewhere.feature.signin.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun SignInRoute(
    isDarkAppTheme: Boolean,
    internetEnabled: Boolean,
    appVersionName: String,

    signInViewModel: SignInViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val signInUiState by signInViewModel.signInUiState.collectAsState()
    val isSigningIn = signInUiState.isSigningIn
    val signInButtonEnabled = signInUiState.signInButtonEnabled

    //snackbar
    val snackBarHostState = remember { SnackbarHostState() }
    val signInErrorText = stringResource(id = R.string.sign_in_error)

    val signInErrorSnackbar = {
        coroutineScope.launch {
            snackBarHostState.showSnackbar(
                message = signInErrorText
            )
        }
    }

    //set signInButtonEnabled
    LaunchedEffect(internetEnabled, isSigningIn) {
        if (!isSigningIn && !signInButtonEnabled){
            delay(1200)
            signInViewModel.setSignInButtonEnabled(internetEnabled)
        }
        else {
            signInViewModel.setSignInButtonEnabled(internetEnabled && !isSigningIn)
        }
    }






    //Google sign in launcher
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            coroutineScope.launch {
                signInViewModel.signInWithGoogleResult(
                    result = result,
                    onDone = {}, //FIXME===========================================
                    showErrorSnackbar = { signInErrorSnackbar() }
                )
            }
        }
    )







    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    SignInScreen(
        isDarkAppTheme = isDarkAppTheme,
        internetEnabled = internetEnabled,
        appVersionName = appVersionName,

        isSigningIn = isSigningIn,
        signInButtonEnabled = signInButtonEnabled,

        onSignInClick = {providerId ->
            when (providerId){
                ProviderId.GOOGLE -> {
                    coroutineScope.launch {
                        //launch google one tap sign in launcher
                        signInViewModel.signInLaunchGoogleLauncher(
                            launcher = googleLauncher,
                            showErrorSnackbar = { signInErrorSnackbar() }
                        )
                    }
                }
                ProviderId.APPLE -> {
                    coroutineScope.launch {
                        val firebaseUserData = signInViewModel.signInWithApple(
                            activity = context as Activity,
                            showErrorSnackbar = { signInErrorSnackbar() }
                        )

                        val userData = if (firebaseUserData == null) null
                                        else {
                                            UserData(
                                                userId = firebaseUserData.uid,
                                                userName = firebaseUserData.displayName,
                                                email = firebaseUserData.email,
                                                profileImagePath = firebaseUserData.photoUrl?.toString(),
                                                providerIds = firebaseUserData.providerData.mapNotNull {
                                                                    getProviderIdFromString(it.providerId)
                                                                }
                                            )
                                        }

                        signInViewModel.updateUserDataFromRemote(
                            userData = userData,
                            onDone = {}, //FIXME===========================================
                            showErrorSnackbar = { signInErrorSnackbar() }
                        )
                    }
                }
            }
        },
        onClickPrivacyPolicy = {
            uriHandler.openUri(PRIVACY_POLICY_URL)
        },
        setSignInButtonEnabled = signInViewModel::setSignInButtonEnabled,
        snackBarHostState = snackBarHostState
    )
}


@Composable
private fun SignInScreen(
    isDarkAppTheme: Boolean,
    internetEnabled: Boolean,
    appVersionName: String,

    isSigningIn: Boolean,
    signInButtonEnabled: Boolean,

    onSignInClick: (providerId: ProviderId) -> Unit,
    onClickPrivacyPolicy: () -> Unit,
    setSignInButtonEnabled: (signInButtonEnabled: Boolean) -> Unit,
    snackBarHostState: SnackbarHostState,

    modifier: Modifier = Modifier
) {
    MyScaffold(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding()
            .displayCutoutPadding(),

        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.width(500.dp)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            MySpacerColumn(height = 50.dp)

            //app icon with text
            AppIconWithAppNameCard()
            MySpacerColumn(height = 16.dp)


            //login
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {


                item {
                    Column(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //signing in...
                        if (isSigningIn){
                            SigningIn()
                        }
                        //internet unavailable
                        else if (!internetEnabled) {
                            InternetUnavailableIconWithText()
                        }
                        //welcome message
                        else{
                            WelcomeText()
                        }
                    }

                    MySpacerColumn(height = 32.dp)
                }

                //sign in buttons
                item {
                    val providerIds = ProviderId.entries.toTypedArray()

                    providerIds.forEach { providerId ->
                        SignInWithButton(
                            providerId = providerId,
                            onClick = {
                                if (signInButtonEnabled) {
                                    setSignInButtonEnabled(false)
                                    onSignInClick(providerId)
                                }
                            },
                            buttonEnabled = signInButtonEnabled,
                            isDarkAppTheme = isDarkAppTheme
                        )

                        MySpacerColumn(height = 16.dp)
                    }

                    MySpacerColumn(height = 48.dp)
                }

                item{
                    AppVersionTextWithPrivacyPolicy(
                        appVersionName = appVersionName,
                        onClickPrivacyPolicy = onClickPrivacyPolicy
                    )

                    MySpacerColumn(height = 16.dp)
                }
            }
        }
    }
}













@Composable
private fun WelcomeText(){
    Text(
        text = stringResource(id = R.string.welcome_message),
        fontSize = 18.sp,
        fontFamily = suite,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        lineHeight = 18.sp * 1.3,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SigningIn(){
    DisplayIcon(icon = MyIcons.signIn)
    MySpacerColumn(height = 6.dp)
    Text(
        text = stringResource(id = R.string.signing_in),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(start = 10.dp)
    )
}

@Composable
private fun AppVersionTextWithPrivacyPolicy(
    appVersionName: String,
    onClickPrivacyPolicy: () -> Unit
){
    PrivacyPolicyButton(onClick = onClickPrivacyPolicy)

    Text(
        text = "v $appVersionName",
        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    )
}

































@PreviewLightDark
@Composable
private fun SignInScreenPreview_Default(){
    SomewhereTheme {
        SignInScreen(
            isDarkAppTheme = isSystemInDarkTheme(),
            internetEnabled = true,
            appVersionName = "1.6.2",
            isSigningIn = false,
            signInButtonEnabled = true,
            onSignInClick = {},
            onClickPrivacyPolicy = {},
            setSignInButtonEnabled = {},
            snackBarHostState = SnackbarHostState()
        )
    }
}

@PreviewLightDark
@Composable
private fun SignInScreenPreview_NoInternet(){
    SomewhereTheme {
        SignInScreen(
            isDarkAppTheme = isSystemInDarkTheme(),
            internetEnabled = false,
            appVersionName = "1.6.2",
            isSigningIn = false,
            signInButtonEnabled = true,
            onSignInClick = {},
            onClickPrivacyPolicy = {},
            setSignInButtonEnabled = {},
            snackBarHostState = SnackbarHostState()
        )
    }
}

@PreviewLightDark
@Composable
private fun SignInScreenPreview_SigningIn(){
    SomewhereTheme {
        SignInScreen(
            isDarkAppTheme = isSystemInDarkTheme(),
            internetEnabled = true,
            appVersionName = "1.6.2",
            isSigningIn = true,
            signInButtonEnabled = false,
            onSignInClick = {},
            onClickPrivacyPolicy = {},
            setSignInButtonEnabled = {},
            snackBarHostState = SnackbarHostState()
        )
    }
}