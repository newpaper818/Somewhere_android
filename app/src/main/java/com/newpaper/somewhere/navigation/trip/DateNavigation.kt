package com.newpaper.somewhere.navigation.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.date.DateRoute
import com.newpaper.somewhere.feature.trip.trip.TripRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/date"

fun NavController.navigateToDate(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.DATE.route, navOptions)

fun NavGraphBuilder.dateScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    navigateTo: (ScreenDestination) -> Unit,
    navigateUp: () -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = ScreenDestination.DATE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.DATE)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        var isErrorExitOnTripScreen by rememberSaveable{ mutableStateOf(false) }

        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .displayCutoutPadding()
        ) {
            if (externalState.windowSizeClass.use2Panes) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (appUiState.appUserData != null) {
                        //trip screen - if use 2 panes
                        TripRoute(
                            use2Panes = externalState.windowSizeClass.use2Panes,
                            spacerValue = externalState.windowSizeClass.spacerValue,
                            appUserId = appUiState.appUserData!!.userId,
                            dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                            internetEnabled = externalState.internetEnabled,
                            commonTripViewModel = commonTripViewModel,

                            navigateUp = navigateUp,
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
                            navigateToDate = { dateIndex, ->
                                commonTripViewModel.setCurrentDateIndex(dateIndex)
                            },
                            navigateToTripMap = {
                                navigateTo(ScreenDestination.TRIP_MAP)
                            },
                            setIsShowingDialog = {isShowingDialog ->
                                commonTripViewModel.setIsShowingDialog(isShowingDialog)
                            },
                            setIsErrorExit = { isErrorExit ->
                                isErrorExitOnTripScreen = isErrorExit
                            }
                        )
                    } else {
                        ErrorScreen()
                        navigateUp()
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                //date screen
                DateRoute(
                    use2Panes = externalState.windowSizeClass.use2Panes,
                    spacerValue = externalState.windowSizeClass.spacerValue,
                    appUserId = appUiState.appUserData!!.userId,
                    dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                    internetEnabled = externalState.internetEnabled,
                    isErrorExitOnTripScreen = isErrorExitOnTripScreen,
                    showTripBottomSaveCancelBar = true,
                    commonTripViewModel = commonTripViewModel,

                    navigateUp = navigateUp,
                    navigateToSpot = navigateToSpot,
                    navigateToDateMap = {
                        navigateTo(ScreenDestination.TRIP_MAP)
                    },
                    setIsShowingDialog = {isShowingDialog ->
                        commonTripViewModel.setIsShowingDialog(isShowingDialog)
                    }
                )
            }
        }
    }
}