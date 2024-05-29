package com.newpaper.somewhere.navigation

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.newpaper.somewhere.navigation.more.moreScreen
import com.newpaper.somewhere.navigation.more.navigateToAccount
import com.newpaper.somewhere.navigation.profile.profileScreen
import com.newpaper.somewhere.navigation.trip.tripsScreen
import com.newpaper.somewhere.navigationUi.ScreenWithNavigationBar
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch

@Composable
fun TopLevelNavHost(
    externalState: ExternalState,
    appViewModel: AppViewModel,

    modifier: Modifier = Modifier
){
    val topLevelNavController = rememberNavController()

    val appUiState by appViewModel.appUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val tripsLazyListState = rememberLazyListState()
    val profileLazyListState = rememberLazyListState()
    val moreLazyListState = rememberLazyListState()

    ScreenWithNavigationBar(
        windowSizeClass = externalState.windowSizeClass,
        currentTopLevelDestination = appUiState.screenDestination.currentTopLevelDestination,
        showNavigationBar = true,
        onClickNavBarItem = {
            topLevelNavController.navigate(
                route = it.route,
                navOptions = navOptions{
                    popUpTo(appUiState.screenDestination.currentTopLevelDestination.route) { inclusive = true }
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
        NavHost(
            navController = topLevelNavController,
            startDestination = TopLevelDestination.TRIPS.route
        ){
            tripsScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                lazyListState = tripsLazyListState,
                navigateToTrip = { isNewTrip, trip ->
//                navController.navigateToTrip()
                    //FIXME
                },
                navigateToGlanceSpot = {

                }
            )

            profileScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                lazyListState = profileLazyListState,
                navigateToAccount = {
                    externalState.navController.navigateToAccount()
                }
            )

            moreScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                userDataIsNull = appUiState.appUserData == null,
                lazyListState = moreLazyListState,
                navigateTo = {
                    externalState.navController.navigate(it.route)
                },
                modifier = modifier,
                currentScreen = appUiState.screenDestination.currentScreenDestination
            )
        }
    }

}

