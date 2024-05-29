package com.newpaper.somewhere.navigation.trip

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
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
import com.newpaper.somewhere.navigation.TopEnterTransition
import com.newpaper.somewhere.navigation.TopExitTransition
import com.newpaper.somewhere.navigation.TopPopEnterTransition
import com.newpaper.somewhere.navigation.TopPopExitTransition
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trips"

fun NavController.navigateToTrips(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.TRIPS.route, navOptions)

fun NavGraphBuilder.tripsScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    lazyListState: LazyListState,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,
) {
    composable(
        route = ScreenDestination.TRIPS.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { TopEnterTransition },
        exitTransition = { TopExitTransition },
        popEnterTransition = { TopPopEnterTransition },
        popExitTransition = { TopPopExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.TRIPS)
        }

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