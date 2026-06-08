package com.newpaper.somewhere.navigation.trip

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.shareTrip.ShareTripRoute
import com.newpaper.somewhere.navigation.enterTransitionVertical
import com.newpaper.somewhere.navigation.exitTransitionVertical
import com.newpaper.somewhere.navigation.popEnterTransitionVertical
import com.newpaper.somewhere.navigation.popExitTransitionVertical
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trip/shareTrip"

fun NavController.navigateToShareTrip(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.SHARE_TRIP.route, navOptions)


fun NavGraphBuilder.shareTripScreen(
    externalState: ExternalState,
    isDarkAppTheme: Boolean,
    appViewModel: AppViewModel,
    commonTripViewModel: CommonTripViewModel,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.SHARE_TRIP.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionVertical },
        exitTransition = { exitTransitionVertical },
        popEnterTransition = { popEnterTransitionVertical },
        popExitTransition = { popExitTransitionVertical }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SHARE_TRIP)
        }

        val appUiState by appViewModel.appUiState.collectAsStateWithLifecycle()
        val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsStateWithLifecycle()

        val currentTrip = commonTripUiState.tripInfo.trip

        if (currentTrip != null) {
            ShareTripRoute(
                isDarkAppTheme = isDarkAppTheme,
                spacerValue = externalState.windowSizeClass.spacerValue,
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                trip = currentTrip,
                imageIndex = commonTripUiState.initialImageIndex,
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