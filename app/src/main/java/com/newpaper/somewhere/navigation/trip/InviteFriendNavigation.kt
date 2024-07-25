package com.newpaper.somewhere.navigation.trip

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
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.inviteFriend.InviteFriendRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trip/inviteFriend"

fun NavController.navigateToInviteFriend(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.INVITE_FRIEND.route, navOptions)

fun NavGraphBuilder.inviteFriendScreen(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    commonTripViewModel: CommonTripViewModel,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.INVITE_FRIEND.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.INVITE_FRIEND)
        }

        val appUiState by appViewModel.appUiState.collectAsState()
        val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()

        val appUserData = appUiState.appUserData
        val currentTrip = commonTripUiState.tripInfo.trip

        if (appUserData != null && currentTrip != null) {
            InviteFriendRoute(
                spacerValue = externalState.windowSizeClass.spacerValue,
                appUserData = appUserData,
                internetEnabled = externalState.internetEnabled,
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                trip = currentTrip,
                navigateUp = navigateUp,
                updateTripState = commonTripViewModel::updateTripState,
                modifier = modifier
            )
        }
        else {
            Text(text = "No user or trip")
        }
    }
}