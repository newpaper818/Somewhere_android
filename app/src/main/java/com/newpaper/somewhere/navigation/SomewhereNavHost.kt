package com.newpaper.somewhere.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.trip.trips.TripsViewModel
import com.newpaper.somewhere.navigation.more.aboutScreen
import com.newpaper.somewhere.navigation.more.accountScreen
import com.newpaper.somewhere.navigation.more.deleteAccountScreen
import com.newpaper.somewhere.navigation.more.editProfileScreen
import com.newpaper.somewhere.navigation.more.moreScreen
import com.newpaper.somewhere.navigation.more.navigateToAccount
import com.newpaper.somewhere.navigation.more.navigateToDeleteAccount
import com.newpaper.somewhere.navigation.more.navigateToEditProfile
import com.newpaper.somewhere.navigation.more.navigateToOpenSourceLicense
import com.newpaper.somewhere.navigation.more.openSourceLicenseScreen
import com.newpaper.somewhere.navigation.more.setDateTimeFormatScreen
import com.newpaper.somewhere.navigation.more.setThemeScreen
import com.newpaper.somewhere.navigation.profile.profileScreen
import com.newpaper.somewhere.navigation.signIn.navigateToSignIn
import com.newpaper.somewhere.navigation.signIn.signInScreen
import com.newpaper.somewhere.navigation.trip.tripsScreen
import com.newpaper.somewhere.navigationUi.ScreenWithNavigationBar
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
@Composable
fun SomewhereNavHost(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    tripsViewModel: TripsViewModel,
    isDarkAppTheme: Boolean,
    startDestination: String,

    modifier: Modifier = Modifier
) {
    val navController = externalState.navController

    val appUiState by appViewModel.appUiState.collectAsState()

    val navigateUp = {
        if (navController.previousBackStackEntry != null) {
            navController.navigateUp()
        }
    }

    val showNavigationBar =
        appUiState.screenDestination.currentScreenDestination == ScreenDestination.TRIPS
        || appUiState.screenDestination.currentScreenDestination == ScreenDestination.PROFILE
        || appUiState.screenDestination.currentScreenDestination == ScreenDestination.MORE


    val tripsLazyListState = rememberLazyListState()
    val profileLazyListState = rememberLazyListState()
    val moreLazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()


    val onSignOutDone = {
        navController.navigateToSignIn(
            navOptions = navOptions {
                popUpTo(0) {
                    inclusive = true
                }
            }
        )
        appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.TRIPS)
    }

    navController.addOnDestinationChangedListener { controller, _, _ ->
        val routes = controller
            .currentBackStack.value
            .map { it.destination.route }
            .joinToString(", ")

        Log.d("BackStackLog", "BackStack: $routes")
    }


    ScreenWithNavigationBar(
        windowSizeClass = externalState.windowSizeClass,
        currentTopLevelDestination = appUiState.screenDestination.currentTopLevelDestination,
        showNavigationBar = showNavigationBar,
        onClickNavBarItem = {
            navController.navigate(
                route = it.route,
                navOptions = navOptions{
                    popUpTo(appUiState.screenDestination.currentTopLevelDestination.route) { inclusive = true }
                }
            )

            if (it != TopLevelDestination.TRIPS)
                tripsViewModel.setLoadingTrips(true)
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
    ) {

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {



            //signIn ===================================================================================
            signInScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                isDarkAppTheme = isDarkAppTheme,
                navigateToMain = {
                    navController.navigate(
                        route = ScreenDestination.TRIPS.route,
                        navOptions = navOptions {
                            popUpTo(ScreenDestination.SIGN_IN.route) { inclusive = true }
                        }
                    )
                }
            )


            //top level screen =========================================================================
            tripsScreen(
                appViewModel = appViewModel,
                tripsViewModel = tripsViewModel,
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
                    navController.navigateToAccount()
                }
            )

            moreScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                userDataIsNull = appUiState.appUserData == null,
                lazyListState = moreLazyListState,
                navigateTo = {
                    navController.navigate(it.route)
                },
                navigateToDeleteAccount = { navController.navigateToDeleteAccount() },
                navigateToEditAccount = { navController.navigateToEditProfile() },
                navigateToOpenSourceLicense = { navController.navigateToOpenSourceLicense() },
                onSignOutDone = onSignOutDone,
                modifier = modifier
            )














            //more =====================================================================================
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
                    navController.navigateToDeleteAccount()
                },
                navigateToEditAccount = {
                    navController.navigateToEditProfile()
                },
                navigateUp = navigateUp,
                onSignOutDone = onSignOutDone,
                modifier = modifier

            )

            editProfileScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                navigateUp = navigateUp,
                modifier = modifier
            )

            deleteAccountScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                isDarkAppTheme = isDarkAppTheme,
                navigateToSignIn = {
                    navController.navigateToSignIn(
                        navOptions = navOptions {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    )
                },
                navigateUp = navigateUp,
                modifier = modifier
            )

            aboutScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                navigateToOpenSourceLicense = {
                    navController.navigateToOpenSourceLicense()
                },
                navigateUp = navigateUp,
                modifier = modifier

            )

            openSourceLicenseScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                navigateUp = navigateUp
            )


            //trip =====================================================================================

        }
    }
}