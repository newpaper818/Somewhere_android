package com.newpaper.somewhere.navigation.profile

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.more.deleteAccount.DeleteAccountRoute
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/account/deleteAccount"

fun NavController.navigateToDeleteAccount(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.DELETE_ACCOUNT.route, navOptions)

fun NavGraphBuilder.deleteAccountScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    isDarkAppTheme: Boolean,

    navigateToSignIn: () -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.DELETE_ACCOUNT.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.DELETE_ACCOUNT)
        }

        val appUiState by appViewModel.appUiState.collectAsStateWithLifecycle()

        if (appUiState.appUserData != null) {
            DeleteAccountRoute(
                isDarkAppTheme = isDarkAppTheme,
                userData = appUiState.appUserData!!,
                internetEnabled = externalState.internetEnabled,
                spacerValue = externalState.windowSizeClass.spacerValue,
                useBlurEffect = appUiState.appPreferences.theme.useBlurEffect,
                navigateUp = navigateUp,
                onDeleteAccountDone = navigateToSignIn,
                modifier = modifier,
            )
        }
        else {
            ErrorScreen()
            navigateUp()
        }
    }
}