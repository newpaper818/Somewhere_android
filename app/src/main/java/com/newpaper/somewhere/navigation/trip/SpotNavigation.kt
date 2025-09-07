package com.newpaper.somewhere.navigation.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.newpaper.somewhere.feature.trip.trip.TripRoute
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

        var isErrorExitOnTripScreen by rememberSaveable{ mutableStateOf(false) }


        if (appUiState.appUserData != null) {
            Row {
                if (externalState.windowSizeClass.use2Panes) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
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
                                commonTripViewModel.setImageListAndInitialImageIndex(
                                    imageList,
                                    initialImageIndex
                                )
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
                                commonTripViewModel.setImageListAndInitialImageIndex(
                                    imageList,
                                    initialImageIndex
                                )
                                navigateTo(ScreenDestination.IMAGE)
                            },
                            navigateToSpot = { dateIndex, spotIndex ->
                                commonTripViewModel.setCurrentDateIndex(dateIndex)
                                commonTripViewModel.setCurrentSpotIndex(spotIndex)
                            },
                            navigateToTripMap = {
                                navigateTo(ScreenDestination.TRIP_MAP)
                            },
                            setIsShowingDialog = { isShowingDialog ->
                                commonTripViewModel.setIsShowingDialog(isShowingDialog)
                            },
                            setIsErrorExit = { isErrorExit ->
                                isErrorExitOnTripScreen = isErrorExit
                            }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    SpotRoute(
                        isDarkAppTheme = isDarkAppTheme,
                        use2Panes = externalState.windowSizeClass.use2Panes,
                        spacerValue = externalState.windowSizeClass.spacerValue,
                        appUserId = appUiState.appUserData!!.userId,
                        dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                        internetEnabled = externalState.internetEnabled,
                        isErrorExitOnTripScreen = isErrorExitOnTripScreen,
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
            }
        } else {
            ErrorScreen()
            navigateUp()
        }
    }
}