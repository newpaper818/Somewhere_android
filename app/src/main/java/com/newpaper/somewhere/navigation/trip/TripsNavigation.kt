package com.newpaper.somewhere.navigation.trip

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.feature.trip.trips.Glance
import com.newpaper.somewhere.feature.trip.trips.TripsRoute
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/trips"

fun NavController.navigationToTrips(navOptions: NavOptions) = navigate(ScreenDestination.TRIPS_ROUTE.route, navOptions)

fun NavGraphBuilder.tripsScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    lazyListState: LazyListState,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,
) {
    composable(
        route = ScreenDestination.TRIPS_ROUTE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
//        arguments = listOf(
//            navArgument()
//        )
    ) {

        val appUiState by appViewModel.appUiState.collectAsState()

        TripsRoute(
            appUserId = appUiState.appUserData?.userId ?: "",
            internetEnabled = externalState.internetEnabled,
            firstLaunch = appUiState.firstLaunch,
            firstLaunchToFalse = appViewModel::firstLaunchToFalse,
            isEditMode = appUiState.isEditMode,
            setEditMode = { appViewModel.setIsEditMode(it) },
            spacerValue = externalState.windowSizeClass.spacerValue,
            lazyListState = lazyListState,
            dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
            navigateToTrip = navigateToTrip,
            navigateToGlanceSpot = navigateToGlanceSpot
        )
    }
}