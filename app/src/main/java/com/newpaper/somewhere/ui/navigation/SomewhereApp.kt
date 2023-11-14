package com.newpaper.somewhere.ui.navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.newpaper.somewhere.enumUtils.MapTheme
import com.newpaper.somewhere.ui.screens.moreScreens.AboutScreen
import com.newpaper.somewhere.ui.screens.moreScreens.SetDateTimeFormatScreen
import com.newpaper.somewhere.ui.screens.moreScreens.SetThemeScreen
import com.newpaper.somewhere.ui.screens.myTripsScreens.DateScreen
import com.newpaper.somewhere.ui.screens.myTripsScreens.SpotScreen
import com.newpaper.somewhere.ui.screens.myTripsScreens.TripMapScreen
import com.newpaper.somewhere.ui.screens.myTripsScreens.TripScreen
import com.newpaper.somewhere.utils.USER_LOCATION_PERMISSION_LIST
import com.newpaper.somewhere.utils.checkPermissionUserLocation
import com.newpaper.somewhere.viewModel.AppViewModel
import com.newpaper.somewhere.viewModel.TripViewModel
import com.newpaper.somewhere.viewModel.SomewhereViewModelProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.ScreenLayout
import com.newpaper.somewhere.ui.screens.moreScreens.MoreScreen
import com.newpaper.somewhere.ui.screens.myTripsScreens.MyTripsScreen
import com.newpaper.somewhere.ui.screens.myTripsScreens.ImageScreen
import com.newpaper.somewhere.utils.WindowSizeClass
import com.newpaper.somewhere.utils.WindowWidthSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SomewhereApp(
    isDarkAppTheme: Boolean,
    windowSizeClass: WindowSizeClass,
    fusedLocationClient: FusedLocationProviderClient,
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel,

    tripViewModel: TripViewModel = viewModel(factory = SomewhereViewModelProvider.Factory)
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
    val tripUiState by tripViewModel.uiState.collectAsState()


    //Nav
    val navigateUp = {
        if (navController.previousBackStackEntry != null) {
            navController.navigateUp()
//            appViewModel.updateCurrentMainNavDestination(navController.currentBackStack.value.first())
        }
        else
            Log.d("test", "Can't navigate back - empty stack")
    }

    //map theme
    val isDarkMapTheme = appUiState.theme.mapTheme == MapTheme.DARK || appUiState.theme.mapTheme == MapTheme.AUTO && isDarkAppTheme

    //system ui controller for status bar icon color
    val systemUiController = rememberSystemUiController()

    //set status bar color
    //image
    if (appUiState.currentScreen == ImageScreenDestination)
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)

    //trip map
    else if (appUiState.currentScreen == TripMapScreenDestination && isDarkMapTheme)
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
    else if (appUiState.currentScreen == TripMapScreenDestination && !isDarkMapTheme)
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)

    //else
    else if (isDarkAppTheme)
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
    else
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)


    //set navigation bar color
    if (appUiState.currentScreen == ImageScreenDestination)
        systemUiController.setNavigationBarColor(color = Color.Transparent)
    else
        systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.surface)


    val coroutineScope = rememberCoroutineScope()


    //page animation
    val enterTransition = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it })
    val exitTransition =
        fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
    val popEnterTransition =
        fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
    val popExitTransition = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it })

    //page animation for image
    val imageEnterTransition = fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
    val imageExitTransition =
        fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
    val imagePopEnterTransition =
        fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
    val imagePopExitTransition = fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)

    //no animation
    val noEnterTransition = fadeIn(tween(0))
    val noExitTransition = fadeOut(tween(0))

    var useImePadding by rememberSaveable { mutableStateOf(true) }

    val screenLayoutModifier =
        if (appUiState.currentScreen == TripMapScreenDestination)
            Modifier.navigationBarsPadding()

        else if (appUiState.currentScreen == ImageScreenDestination)
            Modifier

        else
            Modifier
                .navigationBarsPadding()
                .displayCutoutPadding()

    val showNavigationBar =
        if (!windowSizeClass.use2Panes){
            appUiState.currentScreen == MyTripsScreenDestination ||
            appUiState.currentScreen == MoreScreenDestination
        }
        else{
            appUiState.currentScreen == MyTripsScreenDestination ||
            appUiState.currentScreen == MoreScreenDestination ||
            appUiState.currentScreen == SetDateTimeFormatScreenDestination ||
            appUiState.currentScreen == SetThemeScreenDestination ||
            appUiState.currentScreen == AboutScreenDestination
        }

//    LaunchedEffect(windowSizeClass.use2Panes){
//        if (appUiState.currentScreen == DateScreenDestination)
//            appViewModel.updateCurrentScreen(TripScreenDestination)
//    }




















    ScreenLayout(
        showNavigationBar = showNavigationBar,
        windowSizeClass = windowSizeClass,
        currentMainNavDestination = appUiState.currentMainNavigationDestination,
        onClickNavBarItem = {
            appViewModel.updateCurrentMainNavDestination(it)
            navController.popBackStack()

            if (it == MyTripsMainDestination)
                navController.navigate(MyTripsScreenDestination.route)

            else if (it == MoreMainDestination)
                navController.navigate(MoreScreenDestination.route)
        },
        onClickNavBarItemAgain = {

        },
        modifier = screenLayoutModifier
    ) {

        NavHost(
            navController = navController,
            startDestination = MyTripsScreenDestination.route
        ) {


            //use at any screen size
            // MY TRIPS  ===========================================================================
            composable(
                route = MyTripsScreenDestination.route,
                enterTransition = { noEnterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                LaunchedEffect(Unit) {
                    appViewModel.updateCurrentScreen(MyTripsScreenDestination)
                    tripViewModel.updateDateIndex(null)
                }

                val myTripsScrollState = rememberLazyListState()

                MyTripsScreen(
                    spacerValue = windowSizeClass.spacerValue,
                    isEditMode = tripUiState.isEditMode,
                    scrollState = myTripsScrollState,
                    dateTimeFormat = appUiState.dateTimeFormat,
                    changeEditMode = { tripViewModel.toggleEditMode(it) },
                    addDeletedImages = { newImages ->
                        tripViewModel.addDeletedImages(newImages)
                    },
                    organizeAddedDeletedImages = { isClickSave ->
                        tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                    },
                    navigateToTrip = { isNewTrip, trip ->
                        coroutineScope.launch {
                            tripViewModel.changeTrip(tripId = trip.id)
                        }
                        tripViewModel.toggleIsNewTrip(isNewTrip)
                        navController.navigate(TripScreenDestination.route)
                    },
                    appViewModel = appViewModel
                )
            }

            // TRIP MAP ============================================================================
            composable(
                route = TripMapScreenDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                LaunchedEffect(Unit) {
                    appViewModel.updateCurrentScreen(TripMapScreenDestination)
                }

                if (tripUiState.trip != null) {
                    TripMapScreen(
                        currentTrip = tripUiState.trip!!,
                        dateTimeFormat = appUiState.dateTimeFormat,
                        navigateUp = { navigateUp() },
                        isDarkMapTheme = isDarkMapTheme,
                        userLocationEnabled = userLocationEnabled,
                        setUserLocationEnabled = {
                            userLocationEnabled = it
                        },
                        fusedLocationClient = fusedLocationClient
                    )
                }
            }

            // SPOT ================================================================================
            composable(
                route = SpotScreenDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                //update current screen
                LaunchedEffect(Unit) {
                    appViewModel.updateCurrentScreen(SpotScreenDestination)
                }

                if (tripUiState.trip != null && tripUiState.tempTrip != null) {
                    SpotScreen(
                        use2Panes = windowSizeClass.use2Panes,
                        isCompactWindow = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
                        spacerValue = windowSizeClass.spacerValue,
                        isEditMode = tripUiState.isEditMode,
                        setUseImePadding = {
                            useImePadding = it
                        },

                        originalTrip = tripUiState.trip!!,
                        tempTrip = tripUiState.tempTrip!!,
                        dateIndex = tripUiState.dateIndex ?: 0,
                        spotIndex = tripUiState.spotIndex ?: 0,
                        dateTimeFormat = appUiState.dateTimeFormat,
                        changeEditMode = {
                            tripViewModel.toggleEditMode(it)
                        },
                        addAddedImages = { newImages ->
                            tripViewModel.addAddedImages(newImages)
                        },
                        addDeletedImages = { newImages ->
                            tripViewModel.addDeletedImages(newImages)
                        },
                        organizeAddedDeletedImages = { isClickSave ->
                            tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                        },
                        reorderSpotImageList = { dateIndex, spotIndex, currentIndex, destinationIndex ->
                            tripViewModel.reorderSpotImageList(
                                dateIndex,
                                spotIndex,
                                currentIndex,
                                destinationIndex
                            )
                        },

                        navigateToImage = { imageList, initialImageIndex ->
                            tripViewModel.updateImageListAndInitialImageIndex(
                                imageList,
                                initialImageIndex
                            )
                            navController.navigate(ImageScreenDestination.route)
                        },
                        updateTripState = { toTempTrip, trip ->
                            tripViewModel.updateTripState(toTempTrip, trip)
                        },
                        updateDateIndex = {dateIndex ->
                            tripViewModel.updateDateIndex(dateIndex)
                        },
                        addNewSpot = { dateId ->
                            tripViewModel.addNewSpot(dateId)
                        },
                        deleteSpot = { dateId, spotId ->
                            tripViewModel.deleteSpot(dateId, spotId)
                        },
                        saveTrip = {
                            coroutineScope.launch {
                                tripViewModel.saveTrip({
                                    coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                })
                            }
                        },

                        isDarkMapTheme = isDarkMapTheme,
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
            }

            // IMAGE ===============================================================================
            composable(
                route = ImageScreenDestination.route,
                enterTransition = { imageEnterTransition },
                exitTransition = { imageExitTransition },
                popEnterTransition = { imagePopEnterTransition },
                popExitTransition = { imagePopExitTransition }
            ) {
                LaunchedEffect(Unit) {
                    appViewModel.updateCurrentScreen(ImageScreenDestination)
                }

                ImageScreen(
                    imagePathList = tripUiState.imageList ?: listOf(),
                    initialImageIndex = tripUiState.initialImageIndex,

                    navigateUp = { navigateUp() }
                )
            }



            //single pane (phone)
            if (!windowSizeClass.use2Panes){


                // MORE ================================================================================
                composable(
                    route = MoreScreenDestination.route,
                    enterTransition = { noEnterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(MoreScreenDestination)
                    }

                    MoreScreen(
                        startSpacerValue = windowSizeClass.spacerValue,
                        endSpacerValue = windowSizeClass.spacerValue,
                        navigateTo = { navController.navigate(it.route) },
//                    navigateToMain = {
//                        appViewModel.updateCurrentMainNavDestination(it)
//                    },
                    )

                }



                // TRIP ================================================================================
                composable(
                    route = TripScreenDestination.route,
//                arguments = listOf(navArgument(TripDestination.tripIdArg) {
//                    type = NavType.IntType
//                }),
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    //update current screen
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(TripScreenDestination)
                    }

                    if (tripUiState.trip != null && tripUiState.tempTrip != null)
                        TripScreen(
                            startSpacerValue = windowSizeClass.spacerValue,
                            endSpacerValue = windowSizeClass.spacerValue,
                            isEditMode = tripUiState.isEditMode,

                            originalTrip = tripUiState.trip!!,
                            tempTrip = tripUiState.tempTrip!!,
                            isNewTrip = tripUiState.isNewTrip,

                            dateTimeFormat = appUiState.dateTimeFormat,

                            changeEditMode = {
                                tripViewModel.toggleEditMode(it)
                            },

                            navigateUp = {
                                navigateUp()
                                tripViewModel.toggleIsNewTrip(false)
                            },
                            navigateToImage = { imageList, initialImageIndex ->
                                tripViewModel.updateImageListAndInitialImageIndex(
                                    imageList,
                                    initialImageIndex
                                )
                                navController.navigate(ImageScreenDestination.route)
                            },
                            navigateToDate = { dateIndex ->
                                tripViewModel.toggleIsNewTrip(false)
                                tripViewModel.updateDateIndex(dateIndex)
                                navController.navigate(DateScreenDestination.route)
                            },
                            navigateToTripMap = {
                                navController.navigate(TripMapScreenDestination.route)
                                tripViewModel.toggleIsNewTrip(false)
                            },
                            navigateUpAndDeleteTrip = { deleteTrip ->
                                navigateUp()
                                coroutineScope.launch {
                                    tripViewModel.deleteTrip(deleteTrip)
                                    appViewModel.updateAppUiStateFromRepository()
                                }
                            },

                            updateTripState = { toTempTrip, trip ->
                                tripViewModel.updateTripState(toTempTrip, trip)
                            },
                            updateTripDurationAndTripState = { toTempTrip, startDate, endDate ->
                                tripViewModel.updateTripDurationAndTripState(
                                    toTempTrip,
                                    startDate,
                                    endDate
                                )
                            },

                            addAddedImages = { newImages ->
                                tripViewModel.addAddedImages(newImages)
                            },
                            addDeletedImages = { newImages ->
                                tripViewModel.addDeletedImages(newImages)
                            },
                            organizeAddedDeletedImages = { isClickSave ->
                                tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                            },
                            reorderTripImageList = { currentIndex, destinationIndex ->
                                tripViewModel.reorderTripImageList(currentIndex, destinationIndex)
                            },
                            reorderDateList = { currentIndex, destinationIndex ->
                                tripViewModel.reorderDateList(currentIndex, destinationIndex)
                            },

                            saveTrip = {
                                coroutineScope.launch {
                                    tripViewModel.saveTrip(
                                        updateAppUiState = {
                                            coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                        },
                                        deleteNotEnabledDate = true
                                    )
                                }
                            },
                        )
                }

                // DATE ================================================================================
                composable(
                    route = DateScreenDestination.route,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    //update current screen
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(DateScreenDestination)
                    }

                    if (tripUiState.trip != null && tripUiState.tempTrip != null) {
                        DateScreen(
                            startSpacerValue = windowSizeClass.spacerValue,
                            endSpacerValue = windowSizeClass.spacerValue,
                            isEditMode = tripUiState.isEditMode,

                            originalTrip = tripUiState.trip!!,
                            tempTrip = tripUiState.tempTrip!!,
                            dateIndex = tripUiState.dateIndex,

                            dateTimeFormat = appUiState.dateTimeFormat,
                            changeEditMode = {
                                tripViewModel.toggleEditMode(it)
                            },

                            addAddedImages = { newImages ->
                                tripViewModel.addAddedImages(newImages)
                            },
                            addDeletedImages = { newImages ->
                                tripViewModel.addDeletedImages(newImages)
                            },
                            organizeAddedDeletedImages = { isClickSave ->
                                tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                            },

                            updateTripState = { toTempTrip, trip ->
                                tripViewModel.updateTripState(toTempTrip, trip)
                            },

                            addNewSpot = { dateId ->
                                tripViewModel.addNewSpot(dateId)
                            },
                            deleteSpot = { dateId, spotId ->
                                tripViewModel.deleteSpot(dateId, spotId)
                            },
                            reorderSpotList = { dateId, currentIndex, destinationIndex ->
                                tripViewModel.reorderSpotListAndUpdateTravelDistance(
                                    dateId,
                                    currentIndex,
                                    destinationIndex
                                )
                            },
                            saveTrip = {
                                coroutineScope.launch {
                                    tripViewModel.saveTrip({
                                        coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                    })
                                }
                            },

                            navigateUp = {
                                navigateUp()
                            },
                            navigateToSpot = { dateIndex, spotIndex ->
                                tripViewModel.updateDateIndex(dateIndex)
                                tripViewModel.updateSpotIndex(spotIndex)
                                navController.navigate(SpotScreenDestination.route)
                            },
                            navigateToDateMap = {
                                navController.navigate(TripMapScreenDestination.route)
                                tripViewModel.toggleIsNewTrip(false)
                            }
                        )
                    }
                }




                // THEME SETTING =======================================================================
                composable(
                    route = SetThemeScreenDestination.route,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(SetThemeScreenDestination)
                    }

                    SetThemeScreen(
                        startSpacerValue = windowSizeClass.spacerValue,
                        endSpacerValue = windowSizeClass.spacerValue,
                        theme = appUiState.theme,
                        saveUserPreferences = { appTheme, mapTheme ->
                            appViewModel.saveThemeUserPreferences(appTheme, mapTheme)
                        },
                        navigateUp = { navigateUp() }
                    )
                }


                // SET DATE FORMAT =====================================================================
                composable(
                    route = SetDateTimeFormatScreenDestination.route,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(SetDateTimeFormatScreenDestination)
                    }

                    SetDateTimeFormatScreen(
                        startSpacerValue = windowSizeClass.spacerValue,
                        endSpacerValue = windowSizeClass.spacerValue,
                        dateTimeFormat = appUiState.dateTimeFormat,
                        saveUserPreferences = { dateFormat, useMonthName, includeDayOfWeek, timeFormat ->
                            appViewModel.saveDateTimeFormatUserPreferences(
                                dateFormat,
                                useMonthName,
                                includeDayOfWeek,
                                timeFormat
                            )
                        },
                        navigateUp = { navigateUp() }
                    )
                }

                // ABOUT ===============================================================================
                composable(
                    route = AboutScreenDestination.route,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(AboutScreenDestination)
                    }

                    AboutScreen(
                        startSpacerValue = windowSizeClass.spacerValue,
                        endSpacerValue = windowSizeClass.spacerValue,
                        navigateUp = { navigateUp() }
                    )
                }
            }











            //====================================================================================================================
            //====================================================================================================================







            //2 panes (large screen)
            else{

                // MORE ================================================================================
                composable(
                    route = MoreScreenDestination.route,
                    enterTransition = { noEnterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    LaunchedEffect(Unit){
                        appViewModel.updateCurrentScreen(SetDateTimeFormatScreenDestination)
                    }

                    Row {
                        MoreScreen(
                            currentScreen = appUiState.currentScreen,
                            startSpacerValue = windowSizeClass.spacerValue,
                            endSpacerValue = windowSizeClass.spacerValue / 2,
                            navigateTo = {
                                /*ignore not to navigate*/
                                appViewModel.updateCurrentScreen(it)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        when (appUiState.currentScreen){
                            SetDateTimeFormatScreenDestination -> {
                                SetDateTimeFormatScreen(
                                    use2Panes = true,
                                    startSpacerValue = windowSizeClass.spacerValue / 2,
                                    endSpacerValue = windowSizeClass.spacerValue,
                                    dateTimeFormat = appUiState.dateTimeFormat,
                                    saveUserPreferences = { dateFormat, useMonthName, includeDayOfWeek, timeFormat ->
                                        appViewModel.saveDateTimeFormatUserPreferences(
                                            dateFormat, useMonthName, includeDayOfWeek, timeFormat
                                        )
                                    },
                                    navigateUp = { navigateUp() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            SetThemeScreenDestination -> {
                                SetThemeScreen(
                                    use2Panes = true,
                                    startSpacerValue = windowSizeClass.spacerValue / 2,
                                    endSpacerValue = windowSizeClass.spacerValue,
                                    theme = appUiState.theme,
                                    saveUserPreferences = { appTheme, mapTheme ->
                                        appViewModel.saveThemeUserPreferences(appTheme, mapTheme)
                                    },
                                    navigateUp = { navigateUp() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            AboutScreenDestination -> {
                                AboutScreen(
                                    use2Panes = true,
                                    startSpacerValue = windowSizeClass.spacerValue / 2,
                                    endSpacerValue = windowSizeClass.spacerValue,
                                    navigateUp = { navigateUp() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            else -> {

                            }
                        }

                    }
                }

                // TRIP / DATE - trip screen =======================================================
                composable(
                    route = TripScreenDestination.route,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    //update current screen
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(TripScreenDestination)
                    }

                    var showTripBottomSaveCancelBar by rememberSaveable{ mutableStateOf(true) }
                    var tripCurrentDateIndex: Int? by rememberSaveable { mutableStateOf(null) }
                    var dateCurrentDateIndex: Int? by rememberSaveable { mutableStateOf(null) }

                    LaunchedEffect(Unit) {
                        tripCurrentDateIndex = tripUiState.dateIndex
                        dateCurrentDateIndex = tripUiState.dateIndex
                    }

                    if (tripUiState.trip != null && tripUiState.tempTrip != null) {
                        Row {
                            TripScreen(
                                setShowTripBottomSaveCancelBar = { showTripBottomSaveCancelBar = it},
                                use2Panes = true,
                                startSpacerValue = windowSizeClass.spacerValue,
                                endSpacerValue = windowSizeClass.spacerValue / 2,
                                isEditMode = tripUiState.isEditMode,

                                originalTrip = tripUiState.trip!!,
                                tempTrip = tripUiState.tempTrip!!,
                                isNewTrip = tripUiState.isNewTrip,
                                currentDateIndex = tripCurrentDateIndex,

                                dateTimeFormat = appUiState.dateTimeFormat,

                                changeEditMode = {
                                    tripViewModel.toggleEditMode(it)
                                },

                                navigateUp = {
                                    navigateUp()
                                    tripViewModel.toggleIsNewTrip(false)
                                },
                                navigateToImage = { imageList, initialImageIndex ->
                                    tripViewModel.updateImageListAndInitialImageIndex(
                                        imageList,
                                        initialImageIndex
                                    )
                                    navController.navigate(ImageScreenDestination.route)
                                },
                                navigateToDate = { dateIndex ->
                                    tripViewModel.toggleIsNewTrip(false)
                                    tripViewModel.updateDateIndex(dateIndex)
                                    tripCurrentDateIndex = dateIndex
                                    dateCurrentDateIndex = dateIndex
                                },
                                navigateToTripMap = {
                                    navController.navigate(TripMapScreenDestination.route)
                                    tripViewModel.toggleIsNewTrip(false)
                                },
                                navigateUpAndDeleteTrip = { deleteTrip ->
                                    navigateUp()
                                    coroutineScope.launch {
                                        tripViewModel.deleteTrip(deleteTrip)
                                        appViewModel.updateAppUiStateFromRepository()
                                    }
                                },

                                updateTripState = { toTempTrip, trip ->
                                    tripViewModel.updateTripState(toTempTrip, trip)
                                },
                                updateTripDurationAndTripState = { toTempTrip, startDate, endDate ->
                                    tripViewModel.updateTripDurationAndTripState(
                                        toTempTrip,
                                        startDate,
                                        endDate
                                    )
                                },

                                addAddedImages = { newImages ->
                                    tripViewModel.addAddedImages(newImages)
                                },
                                addDeletedImages = { newImages ->
                                    tripViewModel.addDeletedImages(newImages)
                                },
                                organizeAddedDeletedImages = { isClickSave ->
                                    tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                                },
                                reorderTripImageList = { currentIndex, destinationIndex ->
                                    tripViewModel.reorderTripImageList(
                                        currentIndex,
                                        destinationIndex
                                    )
                                },
                                reorderDateList = { currentIndex, destinationIndex ->
                                    tripViewModel.reorderDateList(currentIndex, destinationIndex)
                                },

                                saveTrip = {
                                    coroutineScope.launch {
                                        tripViewModel.saveTrip(
                                            updateAppUiState = {
                                                coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                            },
                                            deleteNotEnabledDate = true
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            DateScreen(
                                showTripBottomSaveCancelBar = showTripBottomSaveCancelBar,
                                use2Panes = true,
                                startSpacerValue = windowSizeClass.spacerValue / 2,
                                endSpacerValue = windowSizeClass.spacerValue,
                                isEditMode = tripUiState.isEditMode,

                                originalTrip = tripUiState.trip!!,
                                tempTrip = tripUiState.tempTrip!!,
                                dateIndex = dateCurrentDateIndex,

                                dateTimeFormat = appUiState.dateTimeFormat,
                                changeEditMode = {
                                    tripViewModel.toggleEditMode(it)
                                },

                                addAddedImages = { newImages ->
                                    tripViewModel.addAddedImages(newImages)
                                },
                                addDeletedImages = { newImages ->
                                    tripViewModel.addDeletedImages(newImages)
                                },
                                organizeAddedDeletedImages = { isClickSave ->
                                    tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                                },

                                updateTripState = { toTempTrip, trip ->
                                    tripViewModel.updateTripState(toTempTrip, trip)
                                },
                                updateDateIndex = { newDateIndex ->
                                    tripCurrentDateIndex = newDateIndex
                                },

                                addNewSpot = { dateId ->
                                    tripViewModel.addNewSpot(dateId)
                                },
                                deleteSpot = { dateId, spotId ->
                                    tripViewModel.deleteSpot(dateId, spotId)
                                },
                                reorderSpotList = { dateId, currentIndex, destinationIndex ->
                                    tripViewModel.reorderSpotListAndUpdateTravelDistance(
                                        dateId,
                                        currentIndex,
                                        destinationIndex
                                    )
                                },
                                saveTrip = {
                                    coroutineScope.launch {
                                        tripViewModel.saveTrip(
                                            {
                                                coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                            },
                                            deleteNotEnabledDate = true
                                        )
                                    }
                                },

                                navigateUp = {
                                    navigateUp()
                                },
                                navigateToSpot = { dateIndex, spotIndex ->
                                    tripViewModel.updateDateIndex(dateIndex)
                                    tripViewModel.updateSpotIndex(spotIndex)
                                    navController.navigate(SpotScreenDestination.route)
                                },
                                navigateToDateMap = {
                                    navController.navigate(TripMapScreenDestination.route)
                                    tripViewModel.toggleIsNewTrip(false)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // TRIP / DATE - date screen =======================================================
                composable(
                    route = DateScreenDestination.route,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition }
                ) {
                    //update current screen
                    LaunchedEffect(Unit) {
                        appViewModel.updateCurrentScreen(TripScreenDestination)
                    }


                    var showTripBottomSaveCancelBar by rememberSaveable{ mutableStateOf(true) }
                    var tripCurrentDateIndex by rememberSaveable { mutableStateOf(tripUiState.dateIndex) }
                    var dateCurrentDateIndex by rememberSaveable { mutableStateOf(tripUiState.dateIndex) }

                    if (tripUiState.trip != null && tripUiState.tempTrip != null) {
                        Row {
                            TripScreen(
                                setShowTripBottomSaveCancelBar = { showTripBottomSaveCancelBar = it},
                                use2Panes = true,
                                startSpacerValue = windowSizeClass.spacerValue,
                                endSpacerValue = windowSizeClass.spacerValue / 2,
                                isEditMode = tripUiState.isEditMode,

                                originalTrip = tripUiState.trip!!,
                                tempTrip = tripUiState.tempTrip!!,
                                isNewTrip = tripUiState.isNewTrip,
                                currentDateIndex = tripCurrentDateIndex,

                                dateTimeFormat = appUiState.dateTimeFormat,

                                changeEditMode = {
                                    tripViewModel.toggleEditMode(it)
                                },

                                navigateUp = {
                                    navigateUp()
                                    tripViewModel.toggleIsNewTrip(false)
                                },
                                navigateToImage = { imageList, initialImageIndex ->
                                    tripViewModel.updateImageListAndInitialImageIndex(
                                        imageList,
                                        initialImageIndex
                                    )
                                    navController.navigate(ImageScreenDestination.route)
                                },
                                navigateToDate = { dateIndex ->
                                    tripViewModel.toggleIsNewTrip(false)
                                    tripViewModel.updateDateIndex(dateIndex)
                                    tripCurrentDateIndex = dateIndex
                                    dateCurrentDateIndex = dateIndex
                                },
                                navigateToTripMap = {
                                    navController.navigate(TripMapScreenDestination.route)
                                    tripViewModel.toggleIsNewTrip(false)
                                },
                                navigateUpAndDeleteTrip = { deleteTrip ->
                                    navigateUp()
                                    coroutineScope.launch {
                                        tripViewModel.deleteTrip(deleteTrip)
                                        appViewModel.updateAppUiStateFromRepository()
                                    }
                                },

                                updateTripState = { toTempTrip, trip ->
                                    tripViewModel.updateTripState(toTempTrip, trip)
                                },
                                updateTripDurationAndTripState = { toTempTrip, startDate, endDate ->
                                    tripViewModel.updateTripDurationAndTripState(
                                        toTempTrip,
                                        startDate,
                                        endDate
                                    )
                                },

                                addAddedImages = { newImages ->
                                    tripViewModel.addAddedImages(newImages)
                                },
                                addDeletedImages = { newImages ->
                                    tripViewModel.addDeletedImages(newImages)
                                },
                                organizeAddedDeletedImages = { isClickSave ->
                                    tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                                },
                                reorderTripImageList = { currentIndex, destinationIndex ->
                                    tripViewModel.reorderTripImageList(
                                        currentIndex,
                                        destinationIndex
                                    )
                                },
                                reorderDateList = { currentIndex, destinationIndex ->
                                    tripViewModel.reorderDateList(currentIndex, destinationIndex)
                                },

                                saveTrip = {
                                    coroutineScope.launch {
                                        tripViewModel.saveTrip(
                                            updateAppUiState = {
                                                coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                            },
                                            deleteNotEnabledDate = true
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            DateScreen(
                                showTripBottomSaveCancelBar = showTripBottomSaveCancelBar,
                                use2Panes = true,
                                startSpacerValue = windowSizeClass.spacerValue / 2,
                                endSpacerValue = windowSizeClass.spacerValue,
                                isEditMode = tripUiState.isEditMode,

                                originalTrip = tripUiState.trip!!,
                                tempTrip = tripUiState.tempTrip!!,
                                dateIndex = dateCurrentDateIndex,

                                dateTimeFormat = appUiState.dateTimeFormat,
                                changeEditMode = {
                                    tripViewModel.toggleEditMode(it)
                                },

                                addAddedImages = { newImages ->
                                    tripViewModel.addAddedImages(newImages)
                                },
                                addDeletedImages = { newImages ->
                                    tripViewModel.addDeletedImages(newImages)
                                },
                                organizeAddedDeletedImages = { isClickSave ->
                                    tripViewModel.organizeAddedDeletedImages(context, isClickSave)
                                },

                                updateTripState = { toTempTrip, trip ->
                                    tripViewModel.updateTripState(toTempTrip, trip)
                                },
                                updateDateIndex = { newDateIndex ->
                                    tripCurrentDateIndex = newDateIndex
                                },

                                addNewSpot = { dateId ->
                                    tripViewModel.addNewSpot(dateId)
                                },
                                deleteSpot = { dateId, spotId ->
                                    tripViewModel.deleteSpot(dateId, spotId)
                                },
                                reorderSpotList = { dateId, currentIndex, destinationIndex ->
                                    tripViewModel.reorderSpotListAndUpdateTravelDistance(
                                        dateId,
                                        currentIndex,
                                        destinationIndex
                                    )
                                },
                                saveTrip = {
                                    coroutineScope.launch {
                                        tripViewModel.saveTrip(
                                            {
                                                coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                            },
                                                deleteNotEnabledDate = true
                                        )
                                    }
                                },

                                navigateUp = {
                                    navigateUp()
                                },
                                navigateToSpot = { dateIndex, spotIndex ->
                                    tripViewModel.updateDateIndex(dateIndex)
                                    tripViewModel.updateSpotIndex(spotIndex)
                                    navController.navigate(SpotScreenDestination.route)
                                },
                                navigateToDateMap = {
                                    navController.navigate(TripMapScreenDestination.route)
                                    tripViewModel.toggleIsNewTrip(false)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }




            }
        }
    }
}
