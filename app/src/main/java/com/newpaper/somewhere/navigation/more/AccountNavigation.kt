package com.newpaper.somewhere.navigation.more

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
import com.newpaper.somewhere.feature.more.account.AccountRoute
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/account"

fun NavController.navigateToAccount(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.ACCOUNT.route, navOptions)

fun NavGraphBuilder.accountScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateToEditAccount: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToDeleteAccount: () -> Unit,
    navigateUp: () -> Unit,
    onSignOutDone: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.ACCOUNT.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.ACCOUNT)
            if (!externalState.windowSizeClass.use2Panes)
                appViewModel.updateMoreDetailCurrentScreenDestination(ScreenDestination.ACCOUNT)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        if (appUiState.appUserData != null) {
            AccountRoute(
                use2Panes = externalState.windowSizeClass.use2Panes,
                userData = appUiState.appUserData!!,
                internetEnabled = externalState.internetEnabled,
                spacerValue = externalState.windowSizeClass.spacerValue,
                navigateToEditAccount = navigateToEditAccount,
                navigateToSubscription = navigateToSubscription,
                navigateToDeleteAccount = navigateToDeleteAccount,
                navigateUp = navigateUp,
                onSignOutDone = onSignOutDone,
                modifier = modifier
            )
        }
        else {
            ErrorScreen()
            navigateUp()
        }
    }
}