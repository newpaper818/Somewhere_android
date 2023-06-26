package com.example.somewhere.ui.screens.somewhere

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.somewhere.R
import com.example.somewhere.typeUtils.AppContentType
import com.example.somewhere.typeUtils.AppShowingType
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyIcon
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screens.SomewhereViewModelProvider
import com.example.somewhere.ui.screens.date.DateDestination
import com.example.somewhere.ui.screens.main.MainDestination
import com.example.somewhere.ui.screens.main.MainScreen
import com.example.somewhere.ui.screens.spotDetail.SpotDetailDestination
import com.example.somewhere.ui.screens.spotImage.SpotImageDestination
import com.example.somewhere.ui.screens.spotMap.SpotMapDestination
import com.example.somewhere.ui.screens.trip.TripDestination
import com.example.somewhere.ui.screens.trip.TripScreen
import com.example.somewhere.ui.screens.tripMap.TripMapDestination
import com.example.somewhere.utils.USER_LOCATION_PERMISSION_LIST
import com.example.somewhere.utils.checkPermissionsIsGranted
import com.example.somewhere.utils.requestPermissions
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

    somewhereViewModel: SomewhereViewModel = viewModel(factory = SomewhereViewModelProvider.Factory)
) {

    //user location permission
    val userLocationPermissionState = rememberMultiplePermissionsState(permissions = USER_LOCATION_PERMISSION_LIST)

    var userLocationEnabled by rememberSaveable{ mutableStateOf(
        checkPermissionsIsGranted(userLocationPermissionState))
    }

    val requestUserLocationPermission = {
        requestPermissions(userLocationPermissionState)
        userLocationEnabled = checkPermissionsIsGranted(userLocationPermissionState)
    }

    //uiState
    val somewhereUiState by somewhereViewModel.uiState.collectAsState()

    val currentScreen = somewhereUiState.currentScreen
    val topAppBarTitle = somewhereUiState.topAppBarTitle

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
    val exitTransition = fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
    val popEnterTransition = fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
    val popExitTransition = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
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

                if (!checkPermissionsIsGranted(userLocationPermissionState))
                    requestUserLocationPermission()

                MainScreen(
                    isEditMode = somewhereUiState.isEditMode,
                    changeEditMode = {
                        somewhereViewModel.changeEditMode(it)
                    },
                    onTripClicked = {
                        somewhereViewModel.updateId(tripId = it.id)
                        somewhereViewModel.changeIsNewTrip(false)
                        navController.navigate("${TripDestination.route}/${it.id}")
                    },
                    navigateToNewTrip = {
                        somewhereViewModel.updateId(tripId = it.id)
                        somewhereViewModel.changeIsNewTrip(true)
                        navController.navigate("${TripDestination.route}/${it.id}")
                    }
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

            }

            // TRIP ================================================================================
            composable(
                route = TripDestination.routeWithArgs,
                arguments = listOf(navArgument(TripDestination.tripIdArg) {
                    type = NavType.IntType
                }),
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                somewhereViewModel.updateCurrentScreen(TripDestination)

                TripScreen(
                    isNewTrip = somewhereUiState.isNewTrip,
                    isEditMode = somewhereUiState.isEditMode,
                    onDateClicked = {
                        navController.navigate("${DateDestination.route}/${it.id}")
                        somewhereViewModel.changeIsNewTrip(false)
                    },
                    changeEditMode = {
                        somewhereViewModel.changeEditMode(it)
                    },
                    changeIsNewTrip = {
                        somewhereViewModel.changeIsNewTrip(it)
                    },
                    navigateUp = {
                        navigateUp()
                        somewhereViewModel.changeIsNewTrip(false)
                    },
                    navigateToTripMap = {
                        navController.navigate(TripMapDestination.route)
                        somewhereViewModel.changeIsNewTrip(false)
                    },
                    navigateUpAndDeleteTrip = {deleteTrip ->
                        navigateUp()
                        coroutineScope.launch {
                            somewhereViewModel.deleteTrip(deleteTrip)
                        }
                    }
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

            // SPOT MAP ============================================================================
            composable(
                route = SpotMapDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                somewhereViewModel.updateCurrentScreen(SpotMapDestination)


            }
        }
    }


    // for tablet

    //for phone

}

@Composable
fun SomewhereTopAppBar(
    isEditMode: Boolean,
    title: String,

    navigationIcon: MyIcon? = null,
    navigationIconOnClick: () -> Unit = {},

    actionIcon1: MyIcon? = null,
    actionIcon1Onclick: () -> Unit = {},

    actionIcon2: MyIcon? = null,
    actionIcon2Onclick: () -> Unit = {}
){
    Column {
        //status bar padding
        Box(modifier = Modifier.statusBarsPadding())

        //app bar
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (navigationIcon != null){
                    IconButton(onClick = navigationIconOnClick) {
                        DisplayIcon(icon = navigationIcon)
                    }
                }
            },
            actions = {
                if (actionIcon1 != null){
                    IconButton(onClick = actionIcon1Onclick) {
                        DisplayIcon(icon = actionIcon1)
                    }
                }
                if(actionIcon2 != null){
                    IconButton(onClick = actionIcon2Onclick) {
                        DisplayIcon(icon = actionIcon2)
                    }
                }
            }
        )


        val transition = updateTransition(targetState = isEditMode, label = "Color transition")
        val color = transition.animateColor(label = "Background Color Transition") {
            if (it) MaterialTheme.colors.error
            else MaterialTheme.colors.background
        }

        //if edit mode
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(color = color.value)
        )
    }
}

@Composable
fun SomewhereFloatingButton(
    text: String,
    icon: MyIcon,
    onButtonClicked: () -> Unit
){
    ExtendedFloatingActionButton(
        text = { Text(text = text) },
        icon = { DisplayIcon(icon) },
        onClick = onButtonClicked,
        backgroundColor = MaterialTheme.colors.secondaryVariant
    )
}