package com.newpaper.somewhere.navigation.trip

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
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.invitedFriend.InvitedFriendsRoute
import com.newpaper.somewhere.feature.trip.trips.TripsViewModel
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trip/invitedFriend"

fun NavController.navigateToInvitedFriends(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.INVITED_FRIENDS.route, navOptions)

fun NavGraphBuilder.invitedFriendsScreen(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    commonTripViewModel: CommonTripViewModel,
    tripsViewModel: TripsViewModel,

    navigateUp: () -> Unit,
    navigateToInviteFriend: () -> Unit,
    navigateToMyTrips: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.INVITED_FRIENDS.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.INVITED_FRIENDS)
        }

        val appUiState by appViewModel.appUiState.collectAsState()
        val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()

        val appUserData = appUiState.appUserData
        val currentTrip = commonTripUiState.tripInfo.trip

        if (appUserData != null && currentTrip != null) {
            InvitedFriendsRoute(
                spacerValue = externalState.windowSizeClass.spacerValue,
                useBlurEffect = appUiState.appPreferences.theme.useBlurEffect,
                appUserData = appUserData,
                internetEnabled = externalState.internetEnabled,
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,

                trip = currentTrip,
                onDeleteSharedTrip = { sharedTrip ->
                    //delete shared trip from tempSharedTrip
                    tripsViewModel.deleteTripFromTempSharedTrip(sharedTrip)

                    //update trips to temp trips
                    tripsViewModel.updateTripListAndShardTripList()
                    tripsViewModel.initDeletedTripsIdListAndDeletedSharedTripList()


                    //navigate to myTrips screen
                    navigateToMyTrips()
                },

                navigateUp = navigateUp,
                navigateToInviteFriend = navigateToInviteFriend,
                updateTripState = commonTripViewModel::updateTripState,
                modifier = modifier
            )
        }
        else {
            ErrorScreen()
            navigateUp()
        }
    }
}