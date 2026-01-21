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
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.trip.TripRoute
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trip"

fun NavController.navigateToTrip(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.TRIP.route, navOptions)

fun NavGraphBuilder.tripScreen(
    isDarkAppTheme: Boolean,
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    navigateTo: (ScreenDestination) -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    navigateUp: () -> Unit,
) {
    composable(
        route = ScreenDestination.TRIP.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.TRIP)
        }

        val appUiState by appViewModel.appUiState.collectAsStateWithLifecycle()

        if (appUiState.appUserData != null) {
            TripRoute(
                isDarkAppTheme = isDarkAppTheme,
                use2Panes = externalState.windowSizeClass.use2Panes,
                spacerValue = externalState.windowSizeClass.spacerValue,
                useBlurEffect = appUiState.appPreferences.theme.useBlurEffect,
                appUserData = appUiState.appUserData!!,
                dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                internetEnabled = externalState.internetEnabled,
                commonTripViewModel = commonTripViewModel,

                navigateUp = navigateUp,
                navigateToShareTrip = { imageList, initialImageIndex ->
                    commonTripViewModel.setImageListAndInitialImageIndex(imageList, initialImageIndex)
                    navigateTo(ScreenDestination.SHARE_TRIP)
                },
                navigateUpAndDeleteNewTrip = { deleteTrip ->
                    navigateUp()
                    commonTripViewModel.deleteTempTrip(deleteTrip)
                },
                navigateToInviteFriend = {
                    navigateTo(ScreenDestination.INVITE_FRIEND)
                },
                navigateToInvitedFriends = {
                    navigateTo(ScreenDestination.INVITED_FRIENDS)
                },
                navigateToImage = { imageList, initialImageIndex ->
                    commonTripViewModel.setImageListAndInitialImageIndex(imageList, initialImageIndex)
                    navigateTo(ScreenDestination.IMAGE)
                },
                navigateToSpot = { dateIndex, spotIndex ->
                    navigateToSpot(dateIndex, spotIndex)
                },
                navigateToTripMap = {
                    navigateTo(ScreenDestination.TRIP_MAP)
                },
                setIsShowingDialog = {isShowingDialog ->
                    commonTripViewModel.setIsShowingDialog(isShowingDialog)
                }
            )
        }
        else{
            ErrorScreen()
            navigateUp()
        }
    }
}