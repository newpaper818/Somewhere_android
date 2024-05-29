package com.newpaper.somewhere.navigation.more

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.more.more.MoreRoute
import com.newpaper.somewhere.navigation.TopEnterTransition
import com.newpaper.somewhere.navigation.TopExitTransition
import com.newpaper.somewhere.navigation.TopPopEnterTransition
import com.newpaper.somewhere.navigation.TopPopExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more"

fun NavController.navigateToMore(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.MORE.route, navOptions)

fun NavGraphBuilder.moreScreen(
    externalState: ExternalState,
    appViewModel: AppViewModel,

    userDataIsNull: Boolean,
    lazyListState: LazyListState,
    navigateTo: (ScreenDestination) -> Unit,

    modifier: Modifier = Modifier,
    currentScreen: ScreenDestination? = null
) {
    composable(
        route = ScreenDestination.MORE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { TopEnterTransition },
        exitTransition = { TopExitTransition },
        popEnterTransition = { TopPopEnterTransition },
        popExitTransition = { TopPopExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.MORE)
        }

        MoreRoute(
            isDebugMode = BuildConfig.DEBUG,
            userDataIsNull = userDataIsNull,
            spacerValue = externalState.windowSizeClass.spacerValue,
            lazyListState = lazyListState,
            navigateTo = navigateTo,
            modifier = modifier,
            currentScreen = currentScreen
        )
    }
}