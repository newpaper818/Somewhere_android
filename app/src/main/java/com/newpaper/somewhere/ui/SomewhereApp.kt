package com.newpaper.somewhere.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.navigation.mainScreen
import com.newpaper.somewhere.navigation.trip.tripsScreen

@Composable
fun SomewhereApp(
    externalState: ExternalState,
    appViewModel: AppViewModel,

    modifier: Modifier = Modifier,
) {
    val navController = externalState.navController

    NavHost(
        navController = navController,
        startDestination = ScreenDestination.TRIPS_ROUTE.route,
        modifier = modifier
    ) {

        //main: trips, profile, more
        mainScreen(
            appViewModel = appViewModel,
            externalState = externalState,
            navigateToTrip = {},
            navigateToGlanceSpot = {},
            navigateToAccount = {},
            navigateTo = {}

        )

//        tripsScreen()
//
//        spotScreen()

    }
}