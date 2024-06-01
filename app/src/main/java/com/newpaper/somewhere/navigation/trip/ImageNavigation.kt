package com.newpaper.somewhere.navigation.trip

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/image"

fun NavController.navigateToImage(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.IMAGE.route, navOptions)

fun NavGraphBuilder.imageScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateTo: () -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.IMAGE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.initCurrentScreenDestination(ScreenDestination.IMAGE)
        }

    }
}