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
import com.google.android.gms.location.FusedLocationProviderClient
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.tripMap.TripMapRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import com.newpaper.somewhere.util.WindowWidthSizeClass

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/tripMap"

fun NavController.navigateToTripMap(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.TRIP_MAP.route, navOptions)

fun NavGraphBuilder.tripMapScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    isDarkMapTheme: Boolean,

    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (Boolean) -> Unit,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.TRIP_MAP.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.TRIP_MAP)
        }

        val appUiState by appViewModel.appUiState.collectAsState()
        val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()

        val currentTrip = commonTripUiState.tripInfo.trip

        if (currentTrip != null) {
            TripMapRoute(
                currentTrip = currentTrip,
                useVerticalLayout = externalState.windowSizeClass.isVertical
                        || externalState.windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                navigateUp = navigateUp,
                isDarkMapTheme = isDarkMapTheme,
                fusedLocationClient = fusedLocationClient,
                userLocationEnabled = userLocationEnabled,
                setUserLocationEnabled = setUserLocationEnabled
            )
        }
        else {
            ErrorScreen()
            navigateUp()
        }
    }
}