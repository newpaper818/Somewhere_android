package com.newpaper.somewhere.navigation.profile

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.profile.profile.ProfileRoute
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/profile"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.PROFILE.route, navOptions)

fun NavGraphBuilder.profileScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    lazyListState: LazyListState,
    navigateToAccount: () -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    composable(
        route = ScreenDestination.PROFILE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        )
    ) {
        val appUiState by appViewModel.appUiState.collectAsState()

        ProfileRoute(
            internetEnabled = externalState.internetEnabled,
            spacerValue = externalState.windowSizeClass.spacerValue,
            lazyListState = lazyListState,
            use2Panes = externalState.windowSizeClass.use2Panes,
            userData = appUiState.appUserData,
            navigateToAccount = navigateToAccount,
            snackBarHostState = snackBarHostState
        )
    }
}
