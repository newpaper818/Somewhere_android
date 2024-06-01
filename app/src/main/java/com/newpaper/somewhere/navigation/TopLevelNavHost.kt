package com.newpaper.somewhere.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_DRAWER_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_RAIL_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.trip.trips.TripsViewModel
import com.newpaper.somewhere.navigation.more.moreScreen
import com.newpaper.somewhere.navigation.more.navigateToAccount
import com.newpaper.somewhere.navigation.profile.profileScreen
import com.newpaper.somewhere.navigation.trip.tripsScreen
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import com.newpaper.somewhere.util.WindowHeightSizeClass
import com.newpaper.somewhere.util.WindowWidthSizeClass

//FIXME ====================================================================================
//TODO delete?
@Composable
fun TopLevelNavHost(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    tripsViewModel: TripsViewModel,

    topLevelNavController: NavHostController,
    navController: NavHostController,
    tripsLazyListState: LazyListState,
    profileLazyListState: LazyListState,
    moreLazyListState: LazyListState,

    modifier: Modifier = Modifier
){
    val appUiState by appViewModel.appUiState.collectAsState()
    val widthSizeClass = externalState.windowSizeClass.widthSizeClass
    val heightSizeClass = externalState.windowSizeClass.heightSizeClass

    Row {
        if (
            heightSizeClass == WindowHeightSizeClass.Compact
            || widthSizeClass == WindowWidthSizeClass.Medium
        ){
            MySpacerRow(width = NAVIGATION_RAIL_BAR_WIDTH)
        }
        else if (widthSizeClass == WindowWidthSizeClass.Expanded){
            MySpacerRow(width = NAVIGATION_DRAWER_BAR_WIDTH)
        }


//        NavHost(
//            navController = topLevelNavController,
//            startDestination = TopLevelDestination.TRIPS.route,
//            route = ScreenDestination.TOP_LEVEL.route
//        ) {
//            tripsScreen(
//                appViewModel = appViewModel,
//                tripsViewModel = tripsViewModel,
//                externalState = externalState,
//                lazyListState = tripsLazyListState,
//                navigateToTrip = { isNewTrip, trip ->
////                navController.navigateToTrip()
//                    //FIXME
//                },
//                navigateToGlanceSpot = {
//
//                }
//            )
//
//            profileScreen(
//                appViewModel = appViewModel,
//                externalState = externalState,
//                lazyListState = profileLazyListState,
//                navigateToAccount = {
//                    navController.navigateToAccount()
//                }
//            )
//
//            moreScreen(
//                externalState = externalState,
//                appViewModel = appViewModel,
//                userDataIsNull = appUiState.appUserData == null,
//                lazyListState = moreLazyListState,
//                navigateTo = {
//                    navController.navigate(it.route)
//                },
//                modifier = modifier,
//                currentScreen = appUiState.screenDestination.currentScreenDestination
//            )
//        }
    }
}

