package com.newpaper.somewhere.navigation

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.utils.checkUserLocationPermission
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.trips.TripsViewModel
import com.newpaper.somewhere.navigation.more.aboutScreen
import com.newpaper.somewhere.navigation.more.accountScreen
import com.newpaper.somewhere.navigation.more.moreScreen
import com.newpaper.somewhere.navigation.more.navigateToOpenSourceLicense
import com.newpaper.somewhere.navigation.more.openSourceLicenseScreen
import com.newpaper.somewhere.navigation.more.setDateTimeFormatScreen
import com.newpaper.somewhere.navigation.more.setThemeScreen
import com.newpaper.somewhere.navigation.profile.deleteAccountScreen
import com.newpaper.somewhere.navigation.profile.editProfileScreen
import com.newpaper.somewhere.navigation.profile.navigateToDeleteAccount
import com.newpaper.somewhere.navigation.profile.navigateToEditProfile
import com.newpaper.somewhere.navigation.profile.navigateToSubscription
import com.newpaper.somewhere.navigation.profile.profileScreen
import com.newpaper.somewhere.navigation.profile.subscriptionScreen
import com.newpaper.somewhere.navigation.signIn.navigateToSignIn
import com.newpaper.somewhere.navigation.signIn.signInScreen
import com.newpaper.somewhere.navigation.trip.dateScreen
import com.newpaper.somewhere.navigation.trip.imageScreen
import com.newpaper.somewhere.navigation.trip.inviteFriendScreen
import com.newpaper.somewhere.navigation.trip.invitedFriendsScreen
import com.newpaper.somewhere.navigation.trip.navigateToDate
import com.newpaper.somewhere.navigation.trip.navigateToInviteFriend
import com.newpaper.somewhere.navigation.trip.navigateToSpot
import com.newpaper.somewhere.navigation.trip.navigateToTrip
import com.newpaper.somewhere.navigation.trip.navigateToTripAi
import com.newpaper.somewhere.navigation.trip.navigateToTrips
import com.newpaper.somewhere.navigation.trip.spotScreen
import com.newpaper.somewhere.navigation.trip.tripAiScreen
import com.newpaper.somewhere.navigation.trip.tripMapScreen
import com.newpaper.somewhere.navigation.trip.tripScreen
import com.newpaper.somewhere.navigation.trip.tripsScreen
import com.newpaper.somewhere.navigationUi.ScreenWithNavigationBar
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppUiState
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SomewhereNavHost(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    tripsViewModel: TripsViewModel,
    isDarkAppTheme: Boolean,
    startDestination: String,
    fusedLocationClient: FusedLocationProviderClient,

    modifier: Modifier = Modifier,
    commonTripViewModel: CommonTripViewModel = hiltViewModel()
) {
    val mainNavController = rememberNavController()
//    val moreNavController = rememberNavController()

    var moreNavKey by rememberSaveable(
        stateSaver = Saver({ it.toString() }, UUID::fromString),
    ) {
        mutableStateOf(UUID.randomUUID())
    }
    val moreNavController = key(moreNavKey) {
        rememberNavController()
    }

    val appUiState by appViewModel.appUiState.collectAsState()

    val isDarkMapTheme = appUiState.appPreferences.theme.mapTheme == MapTheme.DARK
            || appUiState.appPreferences.theme.mapTheme == MapTheme.AUTO && isDarkAppTheme

    val context = LocalContext.current

    var userLocationEnabled by rememberSaveable {
        mutableStateOf(checkUserLocationPermission(context))
    }


    val navigateUp = {
        if (mainNavController.previousBackStackEntry != null) {
            mainNavController.navigateUp()
        }
    }

    val isOnTopLevel = appUiState.screenDestination.currentScreenDestination == ScreenDestination.TRIPS
            || appUiState.screenDestination.currentScreenDestination == ScreenDestination.PROFILE
            || appUiState.screenDestination.currentScreenDestination == ScreenDestination.MORE

    val isOnMoreList = appUiState.screenDestination.currentScreenDestination == ScreenDestination.MORE

    val isOnMoreDetail = appUiState.screenDestination.currentScreenDestination == ScreenDestination.SET_DATE_TIME_FORMAT
            || appUiState.screenDestination.currentScreenDestination == ScreenDestination.SET_THEME
            || appUiState.screenDestination.currentScreenDestination == ScreenDestination.ACCOUNT
            || appUiState.screenDestination.currentScreenDestination == ScreenDestination.ABOUT

    val isOnMore3rd = appUiState.screenDestination.currentScreenDestination == ScreenDestination.EDIT_PROFILE
            || appUiState.screenDestination.currentScreenDestination == ScreenDestination.DELETE_ACCOUNT
            || appUiState.screenDestination.currentScreenDestination == ScreenDestination.OPEN_SOURCE_LICENSE


    val showNavigationBar =
        if (!externalState.windowSizeClass.use2Panes) isOnTopLevel
        else isOnTopLevel || isOnMoreDetail

    LaunchedEffect(appUiState.screenDestination.currentTopLevelDestination) {
//        if (appUiState.screenDestination.currentTopLevelDestination != TopLevelDestination.MORE){
//            //remove all more nav stack
//            moreNavController.popBackStack(
//                route = moreNavController.,
//                inclusive = false
//            )
//        }
    }

    var beforeUse2Panes by rememberSaveable {
        mutableStateOf(externalState.windowSizeClass.use2Panes)
    }

    //nav stack
    LaunchedEffect(externalState.windowSizeClass.use2Panes) {
        organizeNavStack(
            use2Panes = externalState.windowSizeClass.use2Panes,
            beforeUse2Panes = beforeUse2Panes,
            appUiState = appUiState,
            mainNavController = mainNavController,
            moreNavController = moreNavController,
            setMoreNavKey = { moreNavKey = UUID.randomUUID() }
        )
        beforeUse2Panes = externalState.windowSizeClass.use2Panes
    }


    val tripsLazyListState = rememberLazyListState()
    val profileLazyListState = rememberLazyListState()
    val moreLazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()


    val onSignOutDone = {
        mainNavController.navigateToSignIn(
            navOptions = navOptions {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        )
        appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.TRIPS)
    }

    mainNavController.addOnDestinationChangedListener { controller, _, _ ->
        val routes = controller
            .currentBackStack.value
            .map { it.destination.route }
            .joinToString(", ")

        Log.d("mainBackStackLog", "main BackStack: $routes")
    }


    ScreenWithNavigationBar(
        windowSizeClass = externalState.windowSizeClass,
        currentTopLevelDestination = appUiState.screenDestination.currentTopLevelDestination,
        showNavigationBar = showNavigationBar,
        onClickNavBarItem = {
            val prevTopLevelDestination = appUiState.screenDestination.currentTopLevelDestination
            val currentMoreDetailScreenDestination = appUiState.screenDestination.currentScreenDestination

            moreNavKey = UUID.randomUUID()
            mainNavController.navigate(
                route = it.route,
                navOptions = navOptions{
                    popUpTo(TopLevelDestination.TRIPS.route) { inclusive = it == TopLevelDestination.TRIPS }
                    launchSingleTop = true
                }
            )

            if (it != TopLevelDestination.TRIPS)
                tripsViewModel.setLoadingTrips(true)

            if (
                externalState.windowSizeClass.use2Panes
                && prevTopLevelDestination == TopLevelDestination.MORE
                && it != TopLevelDestination.MORE
            ){
                appViewModel.updateMoreDetailCurrentScreenDestination(currentMoreDetailScreenDestination)
            }
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
            navController = mainNavController,
            startDestination = startDestination,
            modifier = modifier
        ) {



            //signIn ===============================================================================
            signInScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                isDarkAppTheme = isDarkAppTheme,
                navigateToMain = {
                    mainNavController.navigate(
                        route = ScreenDestination.TRIPS.route,
                        navOptions = navOptions {
                            popUpTo(ScreenDestination.SIGN_IN.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    )
                }
            )


            //top level screen =====================================================================
            tripsScreen(
                appViewModel = appViewModel,
                commonTripViewModel = commonTripViewModel,
                tripsViewModel = tripsViewModel,
                externalState = externalState,
                lazyListState = tripsLazyListState,
                navigateToTrip = { isNewTrip, trip ->
                    commonTripViewModel.setTrip(trip)
                    commonTripViewModel.setIsEditMode(isNewTrip)
                    commonTripViewModel.setIsNewTrip(isNewTrip)

                    if (externalState.windowSizeClass.use2Panes)
                        mainNavController.navigateToDate(
                            navOptions = navOptions { launchSingleTop = true }
                        )
                    else
                        mainNavController.navigateToTrip(
                            navOptions = navOptions { launchSingleTop = true }
                        )
                },
                navigateToTripAi = {
                    mainNavController.navigateToTripAi(
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateToGlanceSpot = { glance ->
                    if (glance.trip != null)
                        commonTripViewModel.setTrip(glance.trip)
                    commonTripViewModel.setCurrentDateIndex(glance.date?.index)
                    commonTripViewModel.setCurrentSpotIndex(glance.spot?.index)

                    if (!externalState.windowSizeClass.use2Panes)
                        mainNavController.navigateToTrip(
                            navOptions = navOptions { launchSingleTop = true }
                        )

                    mainNavController.navigateToDate(
                        navOptions = navOptions { launchSingleTop = true }
                    )
                    mainNavController.navigateToSpot(
                        navOptions = navOptions { launchSingleTop = true }
                    )
                }
            )

            profileScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                lazyListState = profileLazyListState,
                navigateToAccount = {
                    if (externalState.windowSizeClass.use2Panes)
                        appViewModel.updateMoreDetailCurrentScreenDestination(ScreenDestination.ACCOUNT)

                    mainNavController.navigate(
                        route = ScreenDestination.MORE.route,
                        navOptions = navOptions {
                            popUpTo(ScreenDestination.PROFILE.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    )

                    if (!externalState.windowSizeClass.use2Panes)
                        mainNavController.navigate(
                            route = ScreenDestination.ACCOUNT.route,
                            navOptions = navOptions { launchSingleTop = true }
                        )
                },
                navigateUp = navigateUp
            )

            moreScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                moreNavController = moreNavController,
                moreNavKey = moreNavKey,
                userDataIsNull = appUiState.appUserData == null,
                lazyListState = moreLazyListState,
                navigateTo = {
                    mainNavController.navigate(
                        route = it.route,
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateToEditAccount = { mainNavController.navigateToEditProfile(
                    navOptions = navOptions { launchSingleTop = true }
                ) },
                navigateToSubscription = { mainNavController.navigateToSubscription(
                        navOptions = navOptions { launchSingleTop = true }
                ) },
                navigateToDeleteAccount = { mainNavController.navigateToDeleteAccount(
                    navOptions = navOptions { launchSingleTop = true }
                ) },
                navigateToOpenSourceLicense = { mainNavController.navigateToOpenSourceLicense(
                    navOptions = navOptions { launchSingleTop = true }
                ) },
                onSignOutDone = onSignOutDone,
                modifier = modifier
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
                navigateToEditAccount = { mainNavController.navigateToEditProfile(
                        navOptions = navOptions { launchSingleTop = true }
                    ) },
                navigateToSubscription = { mainNavController.navigateToSubscription(
                        navOptions = navOptions { launchSingleTop = true }
                    ) },
                navigateToDeleteAccount = { mainNavController.navigateToDeleteAccount(
                        navOptions = navOptions { launchSingleTop = true }
                    ) },

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

            subscriptionScreen(
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
                    mainNavController.navigateToSignIn(
                        navOptions = navOptions {
                            popUpTo(mainNavController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
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
                    mainNavController.navigateToOpenSourceLicense(
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateUp = navigateUp,
                modifier = modifier

            )

            openSourceLicenseScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                navigateUp = navigateUp
            )


            //trip =================================================================================
            tripAiScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                commonTripViewModel = commonTripViewModel,
                navigateToAiCreatedTrip = { aiCreatedTrip ->
                    commonTripViewModel.setTrip(aiCreatedTrip)
                    commonTripViewModel.setIsEditMode(false)
                    commonTripViewModel.setIsNewTrip(false)

                    mainNavController.popBackStack()

                    if (externalState.windowSizeClass.use2Panes)
                        mainNavController.navigateToDate(
                            navOptions = navOptions { launchSingleTop = true }
                        )
                    else
                        mainNavController.navigateToTrip(
                            navOptions = navOptions { launchSingleTop = true }
                        )
                },
                navigateUp = navigateUp
            )

            tripScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                commonTripViewModel = commonTripViewModel,
                navigateTo = { screenDestination ->
                    mainNavController.navigate(
                        route = screenDestination.route,
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateToDate = { dateIndex ->
                    commonTripViewModel.setIsNewTrip(false)
                    commonTripViewModel.setCurrentDateIndex(dateIndex)
                    mainNavController.navigateToDate(
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateUp = navigateUp
            )

            dateScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                commonTripViewModel = commonTripViewModel,
                navigateTo = { screenDestination ->
                    mainNavController.navigate(
                        route = screenDestination.route,
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateUp = navigateUp,
                navigateToSpot = { dateIndex, spotIndex ->
                    commonTripViewModel.setCurrentDateIndex(dateIndex)
                    commonTripViewModel.setCurrentSpotIndex(spotIndex)
                    mainNavController.navigateToSpot(
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                modifier = modifier
            )

            spotScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                commonTripViewModel = commonTripViewModel,
                isDarkMapTheme = isDarkMapTheme,
                fusedLocationClient = fusedLocationClient,
                userLocationEnabled = userLocationEnabled,
                setUserLocationEnabled = {
                    userLocationEnabled = it
                },
                navigateTo = { screenDestination ->
                    mainNavController.navigate(
                        route = screenDestination.route,
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateUp = navigateUp,
                modifier = modifier
            )

            tripMapScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                commonTripViewModel = commonTripViewModel,
                isDarkMapTheme = isDarkMapTheme,
                fusedLocationClient = fusedLocationClient,
                userLocationEnabled = userLocationEnabled,
                setUserLocationEnabled = {
                    userLocationEnabled = it
                },
                navigateUp = navigateUp,
                modifier = modifier

            )

            invitedFriendsScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                commonTripViewModel = commonTripViewModel,
                tripsViewModel = tripsViewModel,
                navigateUp = navigateUp,
                navigateToInviteFriend = {
                    mainNavController.navigateToInviteFriend(
                        navOptions = navOptions { launchSingleTop = true }
                    )
                },
                navigateToMyTrips = {
                    mainNavController.navigateToTrips(
                        navOptions = navOptions {
                            popUpTo(ScreenDestination.TRIPS.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    )
                },
                modifier = modifier
            )

            inviteFriendScreen(
                externalState = externalState,
                appViewModel = appViewModel,
                commonTripViewModel = commonTripViewModel,
                navigateUp = navigateUp,
                modifier = modifier
            )

            //image ================================================================================
            imageScreen(
                appViewModel = appViewModel,
                externalState = externalState,
                commonTripViewModel = commonTripViewModel,
                navigateUp = navigateUp
            )
        }
    }
}










private fun organizeNavStack(
    use2Panes: Boolean,
    beforeUse2Panes: Boolean,
    appUiState: AppUiState,
    mainNavController: NavHostController,
    moreNavController: NavHostController,
    setMoreNavKey: () -> Unit
){
    val currentScreenDestination = appUiState.screenDestination.currentScreenDestination

    val isOnTrip = currentScreenDestination == ScreenDestination.TRIP
    val isOnDate = currentScreenDestination == ScreenDestination.DATE
    val isOnSpot = currentScreenDestination == ScreenDestination.SPOT

    val isOnMoreDetail = currentScreenDestination == ScreenDestination.SET_DATE_TIME_FORMAT
            || currentScreenDestination == ScreenDestination.SET_THEME
            || currentScreenDestination == ScreenDestination.ACCOUNT
            || currentScreenDestination == ScreenDestination.ABOUT

    val isOnMore3rd = currentScreenDestination == ScreenDestination.EDIT_PROFILE
            || currentScreenDestination == ScreenDestination.DELETE_ACCOUNT
            || currentScreenDestination == ScreenDestination.OPEN_SOURCE_LICENSE

    //delete duplicate nav stack
    //???

    //set nav stack

    // 1pane -> 2panes
    if (!beforeUse2Panes && use2Panes){
        if (isOnTrip){
            //delete Trip, add Date
            //    Trips - Trip
            // -> Trips - Date
            mainNavController.navigate(
                route = ScreenDestination.DATE.route,
                navOptions = navOptions {
                    popUpTo(ScreenDestination.TRIPS.route){ inclusive = false }
                    launchSingleTop = true
                }
            )
        }
        else if (isOnDate){
            //delete Trip
            //    Trips - Trip - Date
            // -> Trips -        Date
            mainNavController.navigate(
                route = appUiState.screenDestination.currentScreenDestination.route,
                navOptions = navOptions {
                    popUpTo(ScreenDestination.TRIPS.route){ inclusive = false }
                    launchSingleTop = true
                }
            )
        }
        else if (isOnSpot){
            //delete Trip
            //     Trips - Trip - Date - Spot
            // ->  Trips -        Date - spot
            mainNavController.navigate(
                route = ScreenDestination.DATE.route,
                navOptions = navOptions {
                    popUpTo(ScreenDestination.TRIPS.route){ inclusive = false }
                    launchSingleTop = true
                }
            )
            mainNavController.navigate(
                route = ScreenDestination.SPOT.route,
                navOptions = navOptions { launchSingleTop = true }
            )
        }
        else if (isOnMore3rd) {
            //    More - About - OSL (Open source license)
            // -> More -         OSL
            mainNavController.navigate(
                route = appUiState.screenDestination.currentScreenDestination.route,
                navOptions = navOptions {
                    popUpTo(ScreenDestination.MORE.route) {inclusive = false}
                    launchSingleTop = true
                }
            )
        }
        else if (isOnMoreDetail) {
            mainNavController.popBackStack()
        }
    }
    // 2panes -> 1pane
    else if (beforeUse2Panes && !use2Panes) {
        if (isOnDate){
            //add Trip
            //    Trips -        Date
            // -> Trips - Trip - Date
            mainNavController.popBackStack()
            mainNavController.navigate(
                route = ScreenDestination.TRIP.route,
                navOptions = navOptions { launchSingleTop = true }
            )
            mainNavController.navigate(
                route = ScreenDestination.DATE.route,
                navOptions = navOptions { launchSingleTop = true }
            )
        }
        else if (isOnSpot){
            //add Trip
            //    Trips -        Date - spot
            // -> Trips - Trip - Date - Spot
            mainNavController.navigate(
                route = ScreenDestination.TRIP.route,
                navOptions = navOptions {
                    popUpTo(ScreenDestination.TRIPS.route){ inclusive = false }
                    launchSingleTop = true
                }
            )
            mainNavController.navigate(
                route = ScreenDestination.DATE.route,
                navOptions = navOptions { launchSingleTop = true }
            )
            mainNavController.navigate(
                route = ScreenDestination.SPOT.route,
                navOptions = navOptions { launchSingleTop = true }
            )
        }
        else if (isOnMore3rd) {
            setMoreNavKey()
            val moreDetailRoute = when (currentScreenDestination){
                ScreenDestination.EDIT_PROFILE -> ScreenDestination.ACCOUNT
                ScreenDestination.DELETE_ACCOUNT -> ScreenDestination.ACCOUNT
                ScreenDestination.OPEN_SOURCE_LICENSE -> ScreenDestination.ABOUT
                else -> null
            }

            mainNavController.popBackStack()

            if (moreDetailRoute != null) {
                mainNavController.navigate(
                    route = moreDetailRoute.route,
                    navOptions = navOptions { launchSingleTop = true }
                )
            }

            mainNavController.navigate(
                route = currentScreenDestination.route,
                navOptions = navOptions { launchSingleTop = true }
            )
            moreNavController.popBackStack()
        }
        else if (isOnMoreDetail) {
            setMoreNavKey()
            mainNavController.navigate(
                route = currentScreenDestination.route,
                navOptions = navOptions { launchSingleTop = true }
            )
            moreNavController.popBackStack()
        }
    }
}

