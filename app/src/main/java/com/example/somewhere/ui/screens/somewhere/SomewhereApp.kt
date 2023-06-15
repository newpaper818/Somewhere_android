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

    Scaffold (
        //top app bar
        topBar = {
            AnimatedVisibility(
                visible = !(currentScreen == TripMapDestination ||
                        currentScreen == SpotMapDestination),
                enter = expandVertically(
                    animationSpec = tween(durationMillis = 300),
                    expandFrom = Alignment.Bottom
                ),
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 300),
                    shrinkTowards = Alignment.Bottom
                )
            ) {
                SomewhereTopAppBar(
                    title = topAppBarTitle,
                    contentType = contentType,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    isEditMode = somewhereUiState.isEditMode,
                    onBackButtonClicked = {
                        navController.navigateUp()
                    },
                    onEditButtonClicked = {
                        somewhereViewModel.changeEditMode()
                    },
                    currentScreen = currentScreen
                )
            }

        },

        //bottom floating button
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {

            //new trip floating button
//            AnimatedVisibility(
//                visible = somewhereUiState.isEditMode && currentScreen == MainDestination,
//                enter = slideInVertically(animationSpec = tween(500), initialOffsetY = {(it*1.5).toInt()}),
//                exit = slideOutVertically(animationSpec = tween(500), targetOffsetY = {(it*1.5).toInt()})
//            ) {
//                SomewhereFloatingButton(
//                    text = stringResource(id = R.string.new_trip),
//                    icon = MyIcons.new,
//                    onButtonClicked = {
//                        coroutineScope.launch {
//                            val nowTime = LocalDateTime.now()
//                            somewhereViewModel.addNewTrip(nowTime)
//
//                            val newTrip = somewhereViewModel.getTrip(nowTime)
//
//
//                            if (newTrip != null){
//                                navController.navigate("${TripDestination.route}/${newTrip.id}")
//                            }
//
//                            //TODO get id and navigate to trip???
//                        }
//                    }
//                )
//            }

            //trip map floating button
            AnimatedVisibility(
                visible = (currentScreen == TripDestination ||
                        currentScreen == DateDestination) &&
                        //uiState.currentTrip != null &&
                        //uiState.currentTrip?.getFirstLocation() != null &&
                        !somewhereUiState.isEditMode,
                enter = slideInVertically(animationSpec = tween(500), initialOffsetY = {(it*1.5).toInt()}),
                exit = slideOutVertically(animationSpec = tween(500), targetOffsetY = {(it*1.5).toInt()})
            ) {
                SomewhereFloatingButton(
                    text = stringResource(id = R.string.see_on_map),
                    icon = MyIcons.map,
                    onButtonClicked = { navController.navigate(AppShowingType.TRIP_MAP.name)}
                )
            }
        }
    ) {i ->
        val a = i

        //page animation
        val enterTransition = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it })
        val exitTransition = fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
        val popEnterTransition = fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
        val popExitTransition = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it })

        AnimatedNavHost(
            navController = navController,
            startDestination = MainDestination.route,
        ){

            // MAIN ================================================================================
            composable(
                route = MainDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ){
                //update current screen
                somewhereViewModel.updateCurrentScreen(MainDestination)

                //update top app bar title
                somewhereViewModel.updateTopAppBarTitle(
                    if (somewhereUiState.isEditMode) "Edit trips"
                    else stringResource(id = R.string.app_name)
                )

                if (!checkPermissionsIsGranted(userLocationPermissionState))
                    requestUserLocationPermission()

                MainScreen(
                    isEditMode = somewhereUiState.isEditMode,
                    onTripClicked = {
                        navController.navigate("${TripDestination.route}/${it.id}")
                    },
                    changeEditMode = {
                        somewhereViewModel.changeEditMode(it)
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
            ){
                //update current screen
                somewhereViewModel.updateCurrentScreen(TripMapDestination)

            }

            // TRIP ================================================================================
            composable(
                route = TripDestination.routeWithArgs,
                arguments = listOf(navArgument(TripDestination.tripIdArg){
                    type = NavType.IntType
                }),
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ){
                //update current screen
                somewhereViewModel.updateCurrentScreen(TripDestination)

                TripScreen(
                    isEditMode = somewhereUiState.isEditMode,
                    onDateClicked = {
                        navController.navigate("${DateDestination.route}/${it.id}")
                    },
                    changeEditMode = {
                        somewhereViewModel.changeEditMode()
                    },
                    updateTopAppBarTitle = {
                        somewhereViewModel.updateTopAppBarTitle(it)
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
            ){
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
            ){
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
            ){
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
            ){
                //update current screen
                somewhereViewModel.updateCurrentScreen(SpotMapDestination)


            }
        }



        // for tablet

        //for phone
    }
}

@Composable
private fun SomewhereTopAppBar(
    title: String,

    contentType: AppContentType,
    canNavigateBack: Boolean,
    isEditMode: Boolean,
    currentScreen: NavigationDestination,

    onBackButtonClicked: () -> Unit = {},
    onEditButtonClicked: () -> Unit = {}
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
                    style = MaterialTheme.typography.h1
                )
            },
            navigationIcon = {
                if (contentType == AppContentType.LIST_ONLY &&
                    canNavigateBack){

                    IconButton(onClick = onBackButtonClicked) {
                        DisplayIcon(icon = MyIcons.back)
                    }

                }
                else {
                    IconButton(onClick = { /*TODO*/}) {
                        DisplayIcon(icon = MyIcons.menu)
                    }

                }
            },
            actions = {
                if (!isEditMode)
                    IconButton(onClick = onEditButtonClicked) {
                        DisplayIcon(icon = MyIcons.edit)
                    }
                else if(currentScreen == MainDestination)
                    IconButton(onClick = onEditButtonClicked) {
                        DisplayIcon(icon = MyIcons.done)
                    }

                IconButton(onClick = { /*TODO*/ }) {
                    DisplayIcon(icon = MyIcons.more)
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
private fun SomewhereFloatingButton(
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