package com.newpaper.somewhere.navigation.trip

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.navigation.enterTransitionVertical
import com.newpaper.somewhere.navigation.exitTransitionVertical
import com.newpaper.somewhere.navigation.popEnterTransitionVertical
import com.newpaper.somewhere.navigation.popExitTransitionVertical
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/spot/setLocation"

fun NavController.navigateToSetLocation(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.SET_LOCATION.route, navOptions)

fun NavGraphBuilder.setLocationScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateTo: () -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.SET_LOCATION.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionVertical },
        exitTransition = { exitTransitionVertical },
        popEnterTransition = { popEnterTransitionVertical },
        popExitTransition = { popExitTransitionVertical }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SET_LOCATION)
        }

    }
}