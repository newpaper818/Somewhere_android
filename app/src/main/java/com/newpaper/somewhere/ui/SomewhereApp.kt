package com.newpaper.somewhere.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.navigation.mainScreen
import com.newpaper.somewhere.navigation.more.aboutScreen
import com.newpaper.somewhere.navigation.more.accountScreen
import com.newpaper.somewhere.navigation.more.setDateTimeFormatScreen
import com.newpaper.somewhere.navigation.more.setThemeScreen
import com.newpaper.somewhere.navigation.navigateToMain
import com.newpaper.somewhere.navigation.signIn.navigateToSignIn
import com.newpaper.somewhere.navigation.signIn.signInScreen
import com.newpaper.somewhere.navigationUi.TopLevelDestination

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
        ScreenDestination.IMAGE -> {
            systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
        }

        //trip map
        ScreenDestination.TRIP_MAP -> {
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
        ScreenDestination.IMAGE -> systemUiController.setNavigationBarColor(color = Color.Transparent)

        ScreenDestination.TRIPS, ScreenDestination.PROFILE,
        ScreenDestination.MORE, ScreenDestination.TRIP,
        ScreenDestination.DATE, ScreenDestination.SPOT,
        ScreenDestination.TRIP_MAP -> systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.surfaceDim)

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
                externalState = externalState,
                isDarkAppTheme = isDarkAppTheme,
                navigateToMain = {
                    navController.navigateToMain(
                        navOptions = navOptions{
                            popUpTo(ScreenDestination.SIGN_IN.route) { inclusive = true }
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

            accountScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                navigateToDeleteAccount = {
                    //FIXME like this?
//                    navController.navigateToMain()
                    navController.navigate(ScreenDestination.DELETE_ACCOUNT.route)
                },
                navigateToEditAccount = {
                    navController.navigate(ScreenDestination.EDIT_PROFILE.route)
                },
                navigateUp = navigateUp,
                onSignOutDone = {
                    navController.navigateToSignIn(
                        navOptions = navOptions{
                            popUpTo(ScreenDestination.MAIN.route) { inclusive = true }
                        }
                    )
                    appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.TRIPS)
                },
                modifier = modifier

            )

            aboutScreen(
                externalState = externalState,
                navigateToOpenSourceLicense = {
                    navController.navigate(ScreenDestination.OPEN_SOURCE_LICENSE.route)
                },
                navigateUp = navigateUp,
                modifier = modifier

            )




            //trip =================================================================================

        }
    }
}