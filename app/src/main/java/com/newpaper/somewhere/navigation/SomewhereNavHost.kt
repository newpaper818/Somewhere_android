package com.newpaper.somewhere.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.newpaper.somewhere.core.model.enums.ScreenDestination
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
import com.newpaper.somewhere.navigation.trip.navigateToTrip
import com.newpaper.somewhere.navigation.trip.navigateToTrips
import com.newpaper.somewhere.navigation.trip.tripsScreen
import com.newpaper.somewhere.navigationUi.ScreenWithNavigationBar
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

@Composable
fun SomewhereNavHost(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    isDarkAppTheme: Boolean,
    startDestination: String,

    tripsLazyListState: LazyListState,
    profileLazyListState: LazyListState,
    moreLazyListState: LazyListState,

    modifier: Modifier = Modifier
) {
    val navController = externalState.navController
    val appUiState by appViewModel.appUiState.collectAsState()

    val navigateUp = {
        if (navController.previousBackStackEntry != null) {
            navController.navigateUp()
        }
    }




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
                navController.navigateToTrips(
                    navOptions = navOptions{
                        popUpTo(ScreenDestination.SIGN_IN.route) { inclusive = true }
                    }
                )
            }
        )





        //top level screen =========================================================================
        composable(
            route = ScreenDestination.TOP_LEVEL.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            TopLevelNavHost(
                externalState = externalState,
                appViewModel = appViewModel
            )
        }





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
            onSignOutDone = {
                navController.navigateToSignIn(
                    navOptions = navOptions{
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                )
                appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.TRIPS)
            },
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
                    navOptions = navOptions{
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
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