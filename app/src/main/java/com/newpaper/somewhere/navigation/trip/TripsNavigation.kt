package com.newpaper.somewhere.navigation.trip

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
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
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
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
import kotlinx.coroutines.delay

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trips"

fun NavController.navigateToTrips(navOptions: NavOptions? = null) =
    navigate(TopLevelDestination.TRIPS.route, navOptions)

fun NavGraphBuilder.tripsScreen(
    appViewModel: AppViewModel,
    commonTripViewModel: CommonTripViewModel,
    tripsViewModel: TripsViewModel,
    externalState: ExternalState,

    lazyListState: LazyListState,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,
    navigateToTripAi: () -> Unit,
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
            appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.TRIPS)
            delay(100)
            appViewModel.updateCurrentScreenDestination(ScreenDestination.TRIPS)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        val widthSizeClass = externalState.windowSizeClass.widthSizeClass
        val heightSizeClass = externalState.windowSizeClass.heightSizeClass

        Row {
            if (widthSizeClass == WindowWidthSizeClass.Compact){
                MySpacerRow(width = 0.dp)
            }
            else if (
                heightSizeClass == WindowHeightSizeClass.Compact
                || widthSizeClass == WindowWidthSizeClass.Medium
            ) {
                MySpacerRow(width = NAVIGATION_RAIL_BAR_WIDTH)
            } else if (widthSizeClass == WindowWidthSizeClass.Expanded) {
                MySpacerRow(width = NAVIGATION_DRAWER_BAR_WIDTH)
            }

            TripsRoute(
                commonTripViewModel = commonTripViewModel,
                tripsViewModel = tripsViewModel,

                use2Panes = externalState.windowSizeClass.use2Panes,
                spacerValue = externalState.windowSizeClass.spacerValue,
                appUserId = appUiState.appUserData?.userId ?: "",
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                internetEnabled = externalState.internetEnabled,

                useBottomNavBar = externalState.windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
                firstLaunch = appUiState.firstLaunch,
                firstLaunchToFalse = appViewModel::firstLaunchToFalse,

                lazyListState = lazyListState,

                navigateToTrip = navigateToTrip,
                navigateToTripAi = navigateToTripAi,
                navigateToGlanceSpot = navigateToGlanceSpot
            )
        }
    }
}