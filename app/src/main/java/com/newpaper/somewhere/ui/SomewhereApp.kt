package com.newpaper.somewhere.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.navigation.SomewhereNavHost
import com.newpaper.somewhere.navigationUi.ScreenWithNavigationBar
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import kotlinx.coroutines.launch

@Composable
fun SomewhereApp(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    isDarkAppTheme: Boolean,

    modifier: Modifier = Modifier,
) {

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



    val tripsLazyListState = rememberLazyListState()
    val profileLazyListState = rememberLazyListState()
    val moreLazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()


    val startDestination = appUiState.screenDestination.startScreenDestination?.route

    val navController = externalState.navController










    if (startDestination != null){

        //navigation bar with content
        ScreenWithNavigationBar(
            windowSizeClass = externalState.windowSizeClass,
            currentTopLevelDestination = appUiState.screenDestination.currentTopLevelDestination,
            showNavigationBar = appUiState.screenDestination.currentScreenDestination == ScreenDestination.TRIPS ||
                    appUiState.screenDestination.currentScreenDestination == ScreenDestination.PROFILE ||
                    appUiState.screenDestination.currentScreenDestination == ScreenDestination.MORE,
            onClickNavBarItem = {
                val prevScreenRoute = appUiState.screenDestination.currentTopLevelDestination.route
                appViewModel.updateCurrentTopLevelDestination(it)

                navController.navigate(
                    route = it.route,
                    navOptions = navOptions{
                        popUpTo(prevScreenRoute) { inclusive = true }
                    }
                )
            },
            onClickNavBarItemAgain = {
                coroutineScope.launch {
                    when (it) {
                        TopLevelDestination.TRIPS -> tripsLazyListState.animateScrollToItem(0)
                        TopLevelDestination.PROFILE -> profileLazyListState.animateScrollToItem(0)
                        TopLevelDestination.MORE -> moreLazyListState.animateScrollToItem(0)
                    }
                }
            }
        ){
            SomewhereNavHost(
                externalState = externalState,
                appViewModel = appViewModel,
                isDarkAppTheme = isDarkAppTheme,
                startDestination = startDestination,

                tripsLazyListState = tripsLazyListState,
                profileLazyListState = profileLazyListState,
                moreLazyListState = moreLazyListState,

                modifier = modifier
            )
        }
    }
}