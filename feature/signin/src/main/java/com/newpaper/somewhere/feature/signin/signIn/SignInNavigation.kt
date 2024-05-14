package com.newpaper.somewhere.feature.signin.signIn

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.NavDestination

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/signIn"

fun NavController.navigationToSignIn(navOptions: NavOptions) = navigate(NavDestination.SIGN_IN_ROUTE.route, navOptions)

fun NavGraphBuilder.signIn(
    isDarkAppTheme: Boolean,
    internetEnabled: Boolean,
    appVersionName: String
){
    composable(
        route = NavDestination.SIGN_IN_ROUTE.route,
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