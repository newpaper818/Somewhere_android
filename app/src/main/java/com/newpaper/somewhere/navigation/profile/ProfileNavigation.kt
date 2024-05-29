package com.newpaper.somewhere.navigation.profile

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.profile.profile.ProfileRoute
import com.newpaper.somewhere.navigation.TopEnterTransition
import com.newpaper.somewhere.navigation.TopExitTransition
import com.newpaper.somewhere.navigation.TopPopEnterTransition
import com.newpaper.somewhere.navigation.TopPopExitTransition
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/profile"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) =
    navigate(TopLevelDestination.PROFILE.route, navOptions)

fun NavGraphBuilder.profileScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    lazyListState: LazyListState,
    navigateToAccount: () -> Unit,
) {
    composable(
        route = TopLevelDestination.PROFILE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { TopEnterTransition },
        exitTransition = { TopExitTransition },
        popEnterTransition = { TopPopEnterTransition },
        popExitTransition = { TopPopExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.PROFILE)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        ProfileRoute(
            internetEnabled = externalState.internetEnabled,
            spacerValue = externalState.windowSizeClass.spacerValue,
            lazyListState = lazyListState,
            use2Panes = externalState.windowSizeClass.use2Panes,
            userData = appUiState.appUserData,
            navigateToAccount = navigateToAccount,
        )
    }
}
