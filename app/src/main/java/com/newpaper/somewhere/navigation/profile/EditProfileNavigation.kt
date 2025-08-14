package com.newpaper.somewhere.navigation.profile

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.more.editProfile.EditProfileRoute
import com.newpaper.somewhere.navigation.enterTransitionVertical
import com.newpaper.somewhere.navigation.exitTransitionVertical
import com.newpaper.somewhere.navigation.popEnterTransitionVertical
import com.newpaper.somewhere.navigation.popExitTransitionVertical
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/account/editProfile"

fun NavController.navigateToEditProfile(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.EDIT_PROFILE.route, navOptions)

fun NavGraphBuilder.editProfileScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.EDIT_PROFILE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionVertical },
        exitTransition = { exitTransitionVertical },
        popEnterTransition = { popEnterTransitionVertical },
        popExitTransition = { popExitTransitionVertical }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.EDIT_PROFILE)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        if (appUiState.appUserData != null) {
            EditProfileRoute(
                userData = appUiState.appUserData!!,
                internetEnabled = externalState.internetEnabled,
                spacerValue = externalState.windowSizeClass.spacerValue,
                updateUserState = appViewModel::updateUserData,
                navigateUp = navigateUp,
                modifier = modifier
            )
        }
        else {
            ErrorScreen()
            navigateUp()
        }
    }
}