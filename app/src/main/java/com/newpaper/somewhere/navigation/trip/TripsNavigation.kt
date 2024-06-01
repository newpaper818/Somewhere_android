package com.newpaper.somewhere.navigation.trip

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_DRAWER_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_RAIL_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.feature.trip.trips.Glance
import com.newpaper.somewhere.feature.trip.trips.TripsRoute
import com.newpaper.somewhere.feature.trip.trips.TripsViewModel
import com.newpaper.somewhere.navigation.TopEnterTransition
import com.newpaper.somewhere.navigation.TopExitTransition
import com.newpaper.somewhere.navigation.TopPopEnterTransition
import com.newpaper.somewhere.navigation.TopPopExitTransition
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import com.newpaper.somewhere.util.WindowHeightSizeClass
import com.newpaper.somewhere.util.WindowWidthSizeClass

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trips"

fun NavController.navigateToTrips(navOptions: NavOptions? = null) =
    navigate(TopLevelDestination.TRIPS.route, navOptions)

fun NavGraphBuilder.tripsScreen(
    appViewModel: AppViewModel,
    tripsViewModel: TripsViewModel,
    externalState: ExternalState,

    lazyListState: LazyListState,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,
) {
    composable(
        route = TopLevelDestination.TRIPS.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { TopEnterTransition },
        exitTransition = { TopExitTransition },
        popEnterTransition = { TopPopEnterTransition },
        popExitTransition = { TopPopExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.initCurrentScreenDestination(ScreenDestination.TRIPS)
            appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.TRIPS)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        val widthSizeClass = externalState.windowSizeClass.widthSizeClass
        val heightSizeClass = externalState.windowSizeClass.heightSizeClass

        Row {
            if (
                heightSizeClass == WindowHeightSizeClass.Compact
                || widthSizeClass == WindowWidthSizeClass.Medium
            ) {
                MySpacerRow(width = NAVIGATION_RAIL_BAR_WIDTH)
            } else if (widthSizeClass == WindowWidthSizeClass.Expanded) {
                MySpacerRow(width = NAVIGATION_DRAWER_BAR_WIDTH)
            }

            TripsRoute(
                tripsViewModel = tripsViewModel,
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
}