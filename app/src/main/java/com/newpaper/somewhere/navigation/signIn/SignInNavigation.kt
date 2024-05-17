package com.newpaper.somewhere.navigation.signIn

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.signin.signIn.SignInRoute

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/signIn"

fun NavController.navigationToSignIn(navOptions: NavOptions) = navigate(ScreenDestination.SIGN_IN_ROUTE.route, navOptions)

fun NavGraphBuilder.signInScreen(
    isDarkAppTheme: Boolean,
    internetEnabled: Boolean,
    appVersionName: String
){
    composable(
        route = ScreenDestination.SIGN_IN_ROUTE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        )
    ) {
        SignInRoute(
            isDarkAppTheme = isDarkAppTheme,
            internetEnabled = internetEnabled,
            appVersionName = appVersionName
        )
    }
}