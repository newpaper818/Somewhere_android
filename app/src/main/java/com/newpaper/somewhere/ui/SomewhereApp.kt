package com.newpaper.somewhere.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.navigation.mainScreen
import com.newpaper.somewhere.navigation.more.setDateTimeFormatScreen
import com.newpaper.somewhere.navigation.more.setThemeScreen
import com.newpaper.somewhere.navigation.navigationToMain
import com.newpaper.somewhere.navigation.signIn.signInScreen

@Composable
fun SomewhereApp(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    isDarkAppTheme: Boolean,

    modifier: Modifier = Modifier,
) {
    val navController = externalState.navController

    val appUiState by appViewModel.appUiState.collectAsState()

    val isDarkMapTheme = appUiState.appPreferences.theme.mapTheme == MapTheme.DARK
            || appUiState.appPreferences.theme.mapTheme == MapTheme.AUTO && isDarkAppTheme

    //system ui controller for status bar icon color
    val systemUiController = rememberSystemUiController()

    //set status bar color
    when (appUiState.screenDestination.currentScreenDestination) {
        //image
        ScreenDestination.IMAGE_ROUTE -> {
            systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
        }

        //trip map
        ScreenDestination.TRIP_MAP_ROUTE -> {
            if (isDarkMapTheme)
                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
            else
                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)
        }
        else -> {
            if (isDarkAppTheme)
                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
            else
                systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)
        }
    }


    //set navigation bar color
    when (appUiState.screenDestination.currentScreenDestination) {
        ScreenDestination.IMAGE_ROUTE -> systemUiController.setNavigationBarColor(color = Color.Transparent)

        ScreenDestination.TRIPS_ROUTE, ScreenDestination.PROFILE_ROUTE,
        ScreenDestination.MORE_ROUTE, ScreenDestination.TRIP_ROUTE,
        ScreenDestination.DATE_ROUTE, ScreenDestination.SPOT_ROUTE,
        ScreenDestination.TRIP_MAP_ROUTE -> systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.surfaceDim)

        else -> systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.surface)
    }



    val navigateUp = {
        if (navController.previousBackStackEntry != null) {
            navController.navigateUp()
        }
    }


    val startDestination = appUiState.screenDestination.startScreenDestination?.route

    if (startDestination != null){
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {

            //signIn ===============================================================================
            signInScreen(
                appViewModel = appViewModel,
                isDarkAppTheme = isDarkAppTheme,
                internetEnabled = externalState.internetEnabled,
                updateUserData = {usrData ->
                     appViewModel.updateUserData(userData = usrData)
                },
                navigateToMain = {
                    navController.navigationToMain(
                        navOptions = navOptions{
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    )
                }
            )

            //main: trips, profile, more ===========================================================
            mainScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                navigateToTrip = {_,_ -> },
                navigateToGlanceSpot = {},
                navigateToAccount = {},
                navigateTo = {
                    navController.navigate(it.route)
                }
            )

            //more =================================================================================
            setDateTimeFormatScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                navigateUp = navigateUp
            )

            setThemeScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                navigateUp = navigateUp
            )








            //trip =================================================================================

        }
    }
}