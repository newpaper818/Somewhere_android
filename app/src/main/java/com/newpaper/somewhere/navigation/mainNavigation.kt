package com.newpaper.somewhere.navigation

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.feature.more.more.MoreRoute
import com.newpaper.somewhere.feature.profile.profile.ProfileRoute
import com.newpaper.somewhere.feature.trip.trips.Glance
import com.newpaper.somewhere.feature.trip.trips.TripsRoute
import com.newpaper.somewhere.navigationUi.ScreenWithNavigationBar
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main"

fun NavController.navigationToMain(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.MAIN_ROUTE.route, navOptions)

fun NavGraphBuilder.mainScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,
    navigateToAccount: () -> Unit,
    navigateTo: (screenDestination: ScreenDestination) -> Unit
) {
    composable(
        route = ScreenDestination.MAIN_ROUTE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
//        arguments = listOf(
//            navArgument()
//        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {


        val appUiState by appViewModel.appUiState.collectAsState()
        val destinationState = appUiState.screenDestination

        LaunchedEffect(Unit) {
            appViewModel.updateCurrentTopLevelDestination(destinationState.currentTopLevelDestination)

            when (destinationState.currentTopLevelDestination) {
                TopLevelDestination.TRIPS -> appViewModel.updateCurrentScreenDestination(
                    ScreenDestination.TRIPS_ROUTE
                )

                TopLevelDestination.PROFILE -> appViewModel.updateCurrentScreenDestination(
                    ScreenDestination.PROFILE_ROUTE
                )

                TopLevelDestination.MORE -> appViewModel.updateCurrentScreenDestination(
                    ScreenDestination.MORE_ROUTE
                )
            }
        }

        val myTripsLazyListState = rememberLazyListState()
        val profileLazyListState = rememberLazyListState()
        val moreLazyListState = rememberLazyListState()

        val coroutineScope = rememberCoroutineScope()




        ScreenWithNavigationBar(
            windowSizeClass = externalState.windowSizeClass,
            currentTopLevelDestination = destinationState.currentTopLevelDestination,
            onClickNavBarItem = {
                appViewModel.updateCurrentTopLevelDestination(it)

                when (it) {
                    TopLevelDestination.TRIPS -> appViewModel.updateCurrentScreenDestination(
                        ScreenDestination.TRIPS_ROUTE
                    )

                    TopLevelDestination.PROFILE -> appViewModel.updateCurrentScreenDestination(
                        ScreenDestination.PROFILE_ROUTE
                    )

                    TopLevelDestination.MORE -> {
                        if (!externalState.windowSizeClass.use2Panes) {
                            appViewModel.updateCurrentScreenDestination(
                                ScreenDestination.MORE_ROUTE
                            )
                        } else {
                            appViewModel.updateCurrentScreenDestination(
                                ScreenDestination.SET_DATE_TIME_FORMAT_ROUTE
                            )
                        }
                    }
                }
            },
            onClickNavBarItemAgain = {
                coroutineScope.launch {
                    when (it) {
                        TopLevelDestination.TRIPS -> myTripsLazyListState.animateScrollToItem(0)
                        TopLevelDestination.PROFILE -> profileLazyListState.animateScrollToItem(0)
                        TopLevelDestination.MORE -> moreLazyListState.animateScrollToItem(0)
                    }
                }
            }
        ){
            var currentScreenDestination by remember { mutableStateOf(destinationState.currentScreenDestination) }

            //content
            LaunchedEffect(destinationState.currentScreenDestination) {
                val tempDestination = destinationState.currentScreenDestination
                if (tempDestination == ScreenDestination.TRIPS_ROUTE
                    || tempDestination == ScreenDestination.PROFILE_ROUTE
                    || tempDestination == ScreenDestination.MORE_ROUTE

                    || externalState.windowSizeClass.use2Panes && (
                        tempDestination == ScreenDestination.SET_DATE_TIME_FORMAT_ROUTE
                        || tempDestination == ScreenDestination.SET_THEME_ROUTE
                        || tempDestination == ScreenDestination.ABOUT_ROUTE)
                ) {
                    currentScreenDestination = tempDestination
                }
            }

            //myTrips screen -------------------------------------------------------------------
            if (currentScreenDestination == ScreenDestination.TRIPS_ROUTE){
                TripsRoute(
                    appUserId = appUiState.appUserData?.userId ?: "",
                    internetEnabled = externalState.internetEnabled,
                    firstLaunch = appUiState.firstLaunch,
                    firstLaunchToFalse = appViewModel::firstLaunchToFalse,
                    isEditMode = appUiState.isEditMode,
                    setEditMode = { appViewModel.setIsEditMode(it) },
                    spacerValue = externalState.windowSizeClass.spacerValue,
                    lazyListState = myTripsLazyListState,
                    dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
                    navigateToTrip = navigateToTrip,
                    navigateToGlanceSpot = navigateToGlanceSpot
                )
            }
            //profile screen -------------------------------------------------------------------
            else if (currentScreenDestination == ScreenDestination.PROFILE_ROUTE){
                val snackBarHostState = remember { SnackbarHostState() }

                ProfileRoute(
                    internetEnabled = externalState.internetEnabled,
                    spacerValue = externalState.windowSizeClass.spacerValue,
                    lazyListState = profileLazyListState,
                    use2Panes = externalState.windowSizeClass.use2Panes,
                    userData = appUiState.appUserData,
                    navigateToAccount = navigateToAccount,
                    snackBarHostState = snackBarHostState
                )
            }
            //more screen ----------------------------------------------------------------------
            else if (currentScreenDestination == ScreenDestination.MORE_ROUTE
                || currentScreenDestination == ScreenDestination.SET_DATE_TIME_FORMAT_ROUTE
                || currentScreenDestination == ScreenDestination.SET_THEME_ROUTE
                || currentScreenDestination == ScreenDestination.ACCOUNT_ROUTE
                || currentScreenDestination == ScreenDestination.ABOUT_ROUTE
            ){
                //single pane
                if (!externalState.windowSizeClass.use2Panes) {
                    MoreRoute(
                        isDebugMode = BuildConfig.DEBUG,
                        userDataIsNull = appUiState.appUserData == null,
                        startSpacerValue = externalState.windowSizeClass.spacerValue,
                        endSpacerValue = externalState.windowSizeClass.spacerValue,
                        lazyListState = moreLazyListState,
                        navigateTo = navigateTo
                    )
                }
                //use 2 panes
                else {
                    //TODO
                }
            }



        }







    }
}