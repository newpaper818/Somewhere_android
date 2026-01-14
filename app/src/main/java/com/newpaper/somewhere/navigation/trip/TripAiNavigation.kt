package com.newpaper.somewhere.navigation.trip

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.tripAi.TripAiRoute
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/tripAi"

fun NavController.navigateToTripAi(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.TRIP_AI.route, navOptions)

fun NavGraphBuilder.tripAiScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    navigateToAiCreatedTrip: (Trip) -> Unit,
    navigateUp: () -> Unit,
){
    composable(
        route = ScreenDestination.TRIP_AI.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ){
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.TRIP_AI)
        }

        val appUiState by appViewModel.appUiState.collectAsStateWithLifecycle()

        if (appUiState.appUserData != null) {
            TripAiRoute(
                appUserData = appUiState.appUserData!!,
                internetEnabled = externalState.internetEnabled,
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                commonTripViewModel = commonTripViewModel,
                navigateUp = navigateUp,
                navigateToAiCreatedTrip = navigateToAiCreatedTrip
            )
        }
        else{
            ErrorScreen()
            navigateUp()
        }
    }
}