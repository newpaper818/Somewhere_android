package com.example.somewhere.ui.screens

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.somewhere.typeUtils.AppContentType
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyIcon
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.utils.USER_LOCATION_PERMISSION_LIST
import com.example.somewhere.utils.checkPermissionUserLocation
import com.example.somewhere.viewModel.AppViewModel
import com.example.somewhere.viewModel.SomewhereViewModel
import com.example.somewhere.viewModel.SomewhereViewModelProvider
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun SomewhereApp(
    windowSize: WindowWidthSizeClass,
    fusedLocationClient: FusedLocationProviderClient,
    navController: NavHostController = rememberAnimatedNavController(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    appViewModel: AppViewModel,

    somewhereViewModel: SomewhereViewModel = viewModel(factory = SomewhereViewModelProvider.Factory)
) {
    val context = LocalContext.current

    //user location permission
    val userLocationPermissionState =
        rememberMultiplePermissionsState(permissions = USER_LOCATION_PERMISSION_LIST)

    //is user location permission enabled
    var userLocationEnabled by rememberSaveable {
        mutableStateOf(checkPermissionUserLocation(context))
    }


    //uiState
    val appUiState by appViewModel.appUiState.collectAsState()
    val somewhereUiState by somewhereViewModel.uiState.collectAsState()

    val currentScreen = somewhereUiState.currentScreen

    //Nav
    val backStackEntry by navController.currentBackStackEntryAsState()

    val navigateUp = {
        if (navController.previousBackStackEntry != null)
            navController.navigateUp()
        else
            Log.d("test", "Can't navigate back - empty stack")
    }

    //system ui controller for status bar icon color
    val systemUiController = rememberSystemUiController()

    // for tablet
    val contentType = when (windowSize) {
        WindowWidthSizeClass.Expanded -> {
            AppContentType.LIST_AND_DETAIL
        }

        else -> {
            AppContentType.LIST_ONLY
        }
    }

    val coroutineScope = rememberCoroutineScope()


    //page animation
    val enterTransition = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it })
    val exitTransition =
        fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
    val popEnterTransition =
        fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
    val popExitTransition = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it })

    var useImePadding by rememberSaveable { mutableStateOf(true) }

    val boxModifier = if(useImePadding) Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .imePadding()
                        else Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)

    Box(
        modifier = boxModifier
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = MainDestination.route,
        ) {

            // MAIN ================================================================================
            composable(
                route = MainDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                somewhereViewModel.updateCurrentScreen(MainDestination)

                MainScreen(
                    isEditMode = somewhereUiState.isEditMode,
//                    originalTripList = appUiState.tripList ?: listOf(),
//                    tempTripList = appUiState.tempTripList ?: listOf(),
                    changeEditMode = {
                        somewhereViewModel.toggleEditMode(it)
                    },
                    navigateToTrip = { isNewTrip, trip ->
                        coroutineScope.launch {
                            somewhereViewModel.changeTrip(tripId = trip.id)
                        }
                        somewhereViewModel.toggleIsNewTrip(isNewTrip)
                        navController.navigate(TripDestination.route)
                    },
                    appViewModel = appViewModel
                )
            }

            // TRIP MAP ============================================================================
            composable(
                route = TripMapDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                somewhereViewModel.updateCurrentScreen(TripMapDestination)

                if (somewhereUiState.trip != null) {
                    TripMapScreen(
                        currentTrip = somewhereUiState.trip!!,
                        navigateUp = { navigateUp() },
                        userLocationEnabled = userLocationEnabled,
                        setUserLocationEnabled = {
                            userLocationEnabled = it
                        },
                        fusedLocationClient = fusedLocationClient
                    )
                }
            }

            // TRIP ================================================================================
            composable(
                route = TripDestination.route,
//                arguments = listOf(navArgument(TripDestination.tripIdArg) {
//                    type = NavType.IntType
//                }),
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                somewhereViewModel.updateCurrentScreen(TripDestination)

                if (somewhereUiState.trip != null && somewhereUiState.tempTrip != null)
                    TripScreen(
                        isEditMode = somewhereUiState.isEditMode,

                        originalTrip = somewhereUiState.trip!!,
                        tempTrip = somewhereUiState.tempTrip!!,
                        isNewTrip = somewhereUiState.isNewTrip,

                        changeEditMode = {
                            somewhereViewModel.toggleEditMode(it)
                        },

                        navigateUp = {
                            navigateUp()
                            somewhereViewModel.toggleIsNewTrip(false)
                        },
                        navigateToDate = { dateId ->
                            somewhereViewModel.toggleIsNewTrip(false)
                            somewhereViewModel.updateId(dateId = dateId)
                            navController.navigate(DateDestination.route)
                        },
                        navigateToTripMap = {
                            navController.navigate(TripMapDestination.route)
                            somewhereViewModel.toggleIsNewTrip(false)
                        },
                        navigateUpAndDeleteTrip = { deleteTrip ->
                            navigateUp()
                            coroutineScope.launch {
                                somewhereViewModel.deleteTrip(deleteTrip)
                                appViewModel.updateAppUiStateFromRepository()
                            }
                        },

                        updateTripState = { toTempTrip, trip ->
                            somewhereViewModel.updateTripState(toTempTrip, trip)
                        },
                        updateTripDurationAndTripState = { toTempTrip, startDate, endDate ->
                            somewhereViewModel.updateTripDurationAndTripState(
                                toTempTrip,
                                startDate,
                                endDate
                            )
                        },
                        saveTrip = {
                            coroutineScope.launch {
                                somewhereViewModel.saveTrip{
                                    coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                }
                            }
                        },
                    )
            }

            // DATE ================================================================================
            composable(
                route = DateDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                somewhereViewModel.updateCurrentScreen(DateDestination)

                if (somewhereUiState.trip != null && somewhereUiState.tempTrip != null) {
                    DateScreen(
                        isEditMode = somewhereUiState.isEditMode,

                        originalTrip = somewhereUiState.trip!!,
                        tempTrip = somewhereUiState.tempTrip!!,
                        dateId = somewhereUiState.dateId ?: 0,

                        changeEditMode = {
                            somewhereViewModel.toggleEditMode(it)
                        },

                        updateTripState = { toTempTrip, trip ->
                            somewhereViewModel.updateTripState(toTempTrip, trip)
                        },

                        addNewSpot = { dateId ->
                            somewhereViewModel.addNewSpot(dateId)
                        },
                        deleteSpot = { dateId, spotId ->
                            somewhereViewModel.deleteSpot(dateId, spotId)
                        },
                        saveTrip = {
                            coroutineScope.launch {
                                somewhereViewModel.saveTrip{
                                    coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                }
                            }
                        },

                        navigateUp = {
                            navigateUp()
                        },
                        navigateToSpot = { dateId, spotId ->
                            somewhereViewModel.updateId(dateId = dateId, spotId = spotId)
                            navController.navigate(SpotDetailDestination.route)
                        },
                        navigateToDateMap = {
                            navController.navigate(TripMapDestination.route)
                            somewhereViewModel.toggleIsNewTrip(false)
                        }
                    )
                }
            }

            // SPOT DETAIL =========================================================================
            composable(
                route = SpotDetailDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                somewhereViewModel.updateCurrentScreen(SpotDetailDestination)

                if (somewhereUiState.trip != null && somewhereUiState.tempTrip != null) {
                    SpotDetailScreen(
                        isEditMode = somewhereUiState.isEditMode,
                        setUseImePadding = {
                            useImePadding = it
                        },

                        originalTrip = somewhereUiState.trip!!,
                        tempTrip = somewhereUiState.tempTrip!!,
                        dateId = somewhereUiState.dateId ?: 0,
                        spotId = somewhereUiState.spotId ?: 0,
                        changeEditMode = {
                            somewhereViewModel.toggleEditMode(it)
                        },
                        updateTripState = { toTempTrip, trip ->
                            somewhereViewModel.updateTripState(toTempTrip, trip)
                        },
                        addNewSpot = { dateId ->
                            somewhereViewModel.addNewSpot(dateId)
                        },
                        deleteSpot = { dateId, spotId ->
                            somewhereViewModel.deleteSpot(dateId, spotId)
                        },
                        saveTrip = {
                            coroutineScope.launch {
                                somewhereViewModel.saveTrip{
                                    coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                }
                            }
                        },

                        fusedLocationClient = fusedLocationClient,
                        onImageClicked = { /*TODO*/ },
                        userLocationEnabled = userLocationEnabled,
                        setUserLocationEnabled = {
                            userLocationEnabled = it
                        },
                        navigateUp = {
                            navigateUp()
                        }
                    )
                }

                // SPOT IMAGE ==========================================================================
                composable(
                    route = SpotImageDestination.route,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    //update current screen
                    somewhereViewModel.updateCurrentScreen(SpotImageDestination)


                }
            }
        }


        // for tablet

        //for phone

    }
}

@Composable
fun SomewhereTopAppBar(
    isEditMode: Boolean,
    title: String,
    subTitle: String? = null,

    navigationIcon: MyIcon? = null,
    navigationIconOnClick: () -> Unit = {},

    actionIcon1: MyIcon? = null,
    actionIcon1Onclick: () -> Unit = {},

    actionIcon2: MyIcon? = null,
    actionIcon2Onclick: () -> Unit = {}
) {
    Column {
        //status bar padding
        Box(modifier = Modifier.statusBarsPadding())

        //app bar
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
            title = {
                Column{
                    Text(
                        text = title,
                        style = getTextStyle(TextType.TOP_BAR__TITLE),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subTitle != null){
                        Text(
                            text = subTitle,
                            style = getTextStyle(TextType.TOP_BAR__SUBTITLE),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

            },
            navigationIcon = {
                if (navigationIcon != null) {
                    IconButton(onClick = navigationIconOnClick) {
                        DisplayIcon(icon = navigationIcon)
                    }
                }
            },
            actions = {
                if (actionIcon1 != null) {
                    IconButton(onClick = actionIcon1Onclick) {
                        DisplayIcon(icon = actionIcon1)
                    }
                }
                if (actionIcon2 != null) {
                    IconButton(onClick = actionIcon2Onclick) {
                        DisplayIcon(icon = actionIcon2)
                    }
                }
            }
        )


//        val transition = updateTransition(targetState = isEditMode, label = "Color transition")
//        val color = transition.animateColor(label = "Background Color Transition") {
//            if (it) MaterialTheme.colors.error
//            else MaterialTheme.colors.background
//        }
//
//        //if edit mode
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(4.dp)
//                .background(color = color.value)
//        )
    }
}
