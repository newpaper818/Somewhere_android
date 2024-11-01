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
import com.newpaper.somewhere.feature.more.deleteAccount.DeleteAccountRoute
import com.newpaper.somewhere.feature.more.subscription.SubscriptionRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/account/subscription"

fun NavController.navigateToSubscription(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.SUBSCRIPTION.route, navOptions)

fun NavGraphBuilder.subscriptionScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.SUBSCRIPTION.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SUBSCRIPTION)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        if (appUiState.appUserData != null) {
            SubscriptionRoute(
                use2Panes = externalState.windowSizeClass.use2Panes,
                spacerValue = externalState.windowSizeClass.spacerValue,
                internetEnabled = externalState.internetEnabled,
                updateIsUsingSomewherePro = { isUsingSomewherePro ->
                    val newUserData = appUiState.appUserData!!.copy(
                        isUsingSomewherePro = isUsingSomewherePro
                    )
                    appViewModel.updateUserData(newUserData)
                },
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