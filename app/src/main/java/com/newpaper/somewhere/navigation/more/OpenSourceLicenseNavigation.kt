package com.newpaper.somewhere.navigation.more

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.more.openSourceLicense.OpenSourceLicenseRoute
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/openSourceLicense"

fun NavController.navigateToOpenSourceLicense(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.OPEN_SOURCE_LICENSE.route, navOptions)

fun NavGraphBuilder.openSourceLicenseScreen(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    navigateUp: () -> Unit
) {
    composable(
        route = ScreenDestination.OPEN_SOURCE_LICENSE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.OPEN_SOURCE_LICENSE)
        }

        OpenSourceLicenseRoute(
            startSpacerValue = externalState.windowSizeClass.spacerValue,
            navigateUp = navigateUp
        )
    }
}