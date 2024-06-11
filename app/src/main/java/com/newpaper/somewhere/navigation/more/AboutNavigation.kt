package com.newpaper.somewhere.navigation.more

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.more.about.AboutRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/about"

fun NavController.navigateToAbout(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.ABOUT.route, navOptions)

fun NavGraphBuilder.aboutScreen(
    externalState: ExternalState,
    appViewModel: AppViewModel,

    navigateToOpenSourceLicense: () -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.ABOUT.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.ABOUT)
            if (!externalState.windowSizeClass.use2Panes)
                appViewModel.updateMoreDetailCurrentScreenDestination(ScreenDestination.ABOUT)
        }

        AboutRoute(
            use2Panes = externalState.windowSizeClass.use2Panes,
            spacerValue = externalState.windowSizeClass.spacerValue,
            currentAppVersionCode = BuildConfig.VERSION_CODE,
            currentAppVersionName = BuildConfig.VERSION_NAME,
            isDebugMode = BuildConfig.DEBUG,
            navigateToOpenSourceLicense = navigateToOpenSourceLicense,
            navigateUp = navigateUp,
            modifier = modifier
        )
    }
}