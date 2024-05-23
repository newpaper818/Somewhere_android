package com.newpaper.somewhere.navigation.signIn

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.core.data.repository.ImageRepository
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.signin.signIn.SignInRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/signIn"

fun NavController.navigationToSignIn(navOptions: NavOptions) = navigate(ScreenDestination.SIGN_IN_ROUTE.route, navOptions)

fun NavGraphBuilder.signInScreen(
    appViewModel: AppViewModel,

    isDarkAppTheme: Boolean,
    internetEnabled: Boolean,
    updateUserData: (userData: UserData) -> Unit,
    navigateToMain: () -> Unit
){
    composable(
        route = ScreenDestination.SIGN_IN_ROUTE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SIGN_IN_ROUTE)
        }

        SignInRoute(
            isDarkAppTheme = isDarkAppTheme,
            internetEnabled = internetEnabled,
            appVersionName = BuildConfig.VERSION_NAME,
            updateUserData = updateUserData,
            navigateToMain = navigateToMain
        )
    }
}