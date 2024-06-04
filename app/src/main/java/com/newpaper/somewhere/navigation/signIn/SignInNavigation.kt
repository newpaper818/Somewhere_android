package com.newpaper.somewhere.navigation.signIn

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.signin.signIn.SignInRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/signIn"

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.SIGN_IN.route, navOptions)

fun NavGraphBuilder.signInScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    isDarkAppTheme: Boolean,

    navigateToMain: () -> Unit
){
    composable(
        route = ScreenDestination.SIGN_IN.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SIGN_IN)
            appViewModel.initAppUiState()
        }

        SignInRoute(
            isDarkAppTheme = isDarkAppTheme,
            internetEnabled = externalState.internetEnabled,
            appVersionName = BuildConfig.VERSION_NAME,
            updateUserData = {usrData ->
                appViewModel.updateUserData(userData = usrData)
            },
            navigateToMain = navigateToMain
        )
    }
}