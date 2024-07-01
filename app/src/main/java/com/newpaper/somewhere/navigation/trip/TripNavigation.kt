package com.newpaper.somewhere.navigation.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.trip.TripRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/trip"

fun NavController.navigateToTrip(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.TRIP.route, navOptions)

fun NavGraphBuilder.tripScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    navigateTo: () -> Unit,
    navigateToDate: (dateIndex: Int) -> Unit,
    navigateUp: () -> Unit,
) {
    composable(
        route = ScreenDestination.TRIP.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.TRIP)
        }

        val appUiState by appViewModel.appUiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()

        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .displayCutoutPadding()
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                if (appUiState.appUserData != null) {
                    TripRoute(
                        use2Panes = externalState.windowSizeClass.use2Panes,
                        spacerValue = externalState.windowSizeClass.spacerValue,
                        appUserId = appUiState.appUserData!!.userId,
                        dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                        internetEnabled = externalState.internetEnabled,
                        commonTripViewModel = commonTripViewModel,
                        isNewTrip = commonTripViewModel.commonTripUiState.value.isNewTrip,
                        navigateUp = navigateUp,
                        navigateToInviteFriend = { /*TODO*/ },
                        navigateToInvitedFriends = { /*TODO*/ },
                        navigateToImage = { _,_, -> /*TODO*/ },
                        navigateToDate = { dateIndex, ->
                            navigateToDate(dateIndex)
                        },
                        navigateToTripMap = { /*TODO*/ },
                        navigateUpAndDeleteNewTrip = { deleteTrip ->
                            navigateUp()
                            commonTripViewModel.deleteTempTrip(deleteTrip)
                        },
                        updateTripState = { toTempTrip, trip ->
                            commonTripViewModel.updateTripState(toTempTrip, trip)
                        },
                        addAddedImages = { newImages ->
                            commonTripViewModel.addAddedImages(newImages)
                        },
                        addDeletedImages = {newImages ->
                            commonTripViewModel.addDeletedImages(newImages)
                        },
                        organizeAddedDeletedImages = { isClickSave ->
                            commonTripViewModel.organizeAddedDeletedImages(
                                tripManagerId = commonTripViewModel.commonTripUiState.value.tripInfo.tempTrip?.managerId ?: "",
                                isClickSave = isClickSave,
                                isInTripsScreen = true
                            )
                        },
                        saveTrip = {
                            coroutineScope.launch {
                                //save tripUiState tripList
                                val beforeTempTripDateListLastIndex =
                                    commonTripViewModel.saveTrip(
                                        appUserId = appUiState.appUserData!!.userId,
                                        deleteNotEnabledDate = true
                                    )

                                commonTripViewModel.setIsEditMode(false)
                                commonTripViewModel.setIsNewTrip(false)

                                //save to firestore
                                commonTripViewModel.saveTripAndAllDates(
                                    trip = commonTripViewModel.commonTripUiState.value.tripInfo.tempTrip!!,
                                    tempTripDateListLastIndex = beforeTempTripDateListLastIndex
                                )
                            }
                        }
                    )
                }
                else{
                    Text(text = "no user")
                }
            }

            if (externalState.windowSizeClass.use2Panes){
                Box(modifier = Modifier.weight(1f)) {
                    //date screen
                    //TODO
                }
            }
        }
    }
}