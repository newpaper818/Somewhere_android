package com.newpaper.somewhere.navigation.trip

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
import com.newpaper.somewhere.feature.trip.date.DateRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/date"

fun NavController.navigateToDate(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.DATE.route, navOptions)

fun NavGraphBuilder.dateScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    navigateTo: () -> Unit,
    navigateUp: () -> Unit,

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
        val coroutineScope = rememberCoroutineScope()

        DateRoute(
            use2Panes = externalState.windowSizeClass.use2Panes,
            spacerValue = externalState.windowSizeClass.spacerValue,
            dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
            internetEnabled = externalState.internetEnabled,
            showTripBottomSaveCancelBar = true,
            commonTripViewModel = commonTripViewModel,
            navigateUp = navigateUp,
            navigateToSpot = {_,_ ->},
            navigateToDateMap = { },
            saveTrip = {
                coroutineScope.launch {
                    //save tripUiState trip
                    commonTripViewModel.saveTrip(appUserId = appUiState.appUserData!!.userId)

                    //save to firestore
                    commonTripViewModel.saveTripAndAllDates(
                        trip = commonTripViewModel.commonTripUiState.value.tripInfo.tempTrip!!,
                    )
                }
            }
        )
    }
}