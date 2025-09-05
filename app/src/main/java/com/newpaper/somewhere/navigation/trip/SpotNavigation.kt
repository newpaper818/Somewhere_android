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
import com.google.android.gms.location.FusedLocationProviderClient
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.spot.SpotRoute
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import com.newpaper.somewhere.util.WindowWidthSizeClass

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/spot"

fun NavController.navigateToSpot(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.SPOT.route, navOptions)

fun NavGraphBuilder.spotScreen(
    isDarkAppTheme: Boolean,
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    isDarkMapTheme: Boolean,

    //
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    //
    navigateTo: (ScreenDestination) -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.SPOT.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SPOT)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        if (appUiState.appUserData != null) {
            SpotRoute(
                isDarkAppTheme = isDarkAppTheme,
                use2Panes = externalState.windowSizeClass.use2Panes,
                spacerValue = externalState.windowSizeClass.spacerValue,
                appUserId = appUiState.appUserData!!.userId,
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                internetEnabled = externalState.internetEnabled,
                commonTripViewModel = commonTripViewModel,
                isCompactWidth = externalState.windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
                isDarkMapTheme = isDarkMapTheme,
                fusedLocationClient = fusedLocationClient,
                userLocationEnabled = userLocationEnabled,
                navigateUp = navigateUp,
                navigateToImage = { imageList, initialImageIndex ->
                    commonTripViewModel.setImageListAndInitialImageIndex(imageList, initialImageIndex)
                    navigateTo(ScreenDestination.IMAGE)
                },
                setUserLocationEnabled = setUserLocationEnabled,
                modifier = modifier
            )
        }
        else{
            ErrorScreen()
            navigateUp()
        }
    }
}