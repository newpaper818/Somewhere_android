package com.newpaper.somewhere.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.newpaper.somewhere.feature.trip.trips.TripsViewModel
import com.newpaper.somewhere.navigation.SomewhereNavHost

@Composable
fun SomewhereApp(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    tripsViewModel: TripsViewModel,
    isDarkAppTheme: Boolean,

    modifier: Modifier = Modifier,
) {

    val appUiState by appViewModel.appUiState.collectAsState()

//    val isDarkMapTheme = appUiState.appPreferences.theme.mapTheme == MapTheme.DARK
//            || appUiState.appPreferences.theme.mapTheme == MapTheme.AUTO && isDarkAppTheme

    //system ui controller for status bar icon color
//    val systemUiController = rememberSystemUiController()

    //set status bar color FIXME: use when dark map theme?
//    when (appUiState.screenDestination.currentScreenDestination) {
//        //image
//        ScreenDestination.IMAGE -> {
//            systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
//        }
//
//        //trip map
//        ScreenDestination.TRIP_MAP -> {
//            if (isDarkMapTheme)
//                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
//            else
//                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)
//        }
//
//        else -> {
//            if (isDarkAppTheme)
//                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
//            else
//                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)
//        }
//    }
//
//
//    //set navigation bar color
//    when (appUiState.screenDestination.currentScreenDestination) {
//        ScreenDestination.IMAGE -> systemUiController.setNavigationBarColor(color = Color.Transparent)
//
//        ScreenDestination.TRIPS, ScreenDestination.PROFILE,
//        ScreenDestination.MORE, ScreenDestination.TRIP,
//        ScreenDestination.DATE, ScreenDestination.SPOT,
//        ScreenDestination.TRIP_MAP -> systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.surfaceDim)
//
//        else -> systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.surface)
//    }



    val startDestination = appUiState.screenDestination.startScreenDestination?.route

    if (startDestination != null){
        SomewhereNavHost(
            externalState = externalState,
            appViewModel = appViewModel,
            tripsViewModel = tripsViewModel,
            isDarkAppTheme = isDarkAppTheme,
            startDestination = startDestination,
            modifier = modifier
        )
    }
}