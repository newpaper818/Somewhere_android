package com.newpaper.somewhere.navigation.more

import androidx.compose.material3.Text
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
import com.newpaper.somewhere.feature.more.account.AccountRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/account"

fun NavController.navigateToAccount(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.ACCOUNT.route, navOptions)

fun NavGraphBuilder.accountScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateToDeleteAccount: () -> Unit,
    navigateToEditAccount: () -> Unit,
    navigateUp: () -> Unit,
    onSignOutDone: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.ACCOUNT.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.ACCOUNT)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        if (appUiState.appUserData != null) {
            AccountRoute(
                userData = appUiState.appUserData!!,
                internetEnabled = externalState.internetEnabled,
                spacerValue = externalState.windowSizeClass.spacerValue,
                navigateToDeleteAccount = navigateToDeleteAccount,
                navigateToEditAccount = navigateToEditAccount,
                navigateUp = navigateUp,
                onSignOutDone = onSignOutDone,
                modifier = modifier
            )
        }
        else {
            Text("no user")
        }
    }
}