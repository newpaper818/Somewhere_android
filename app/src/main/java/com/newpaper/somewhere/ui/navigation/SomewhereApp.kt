package com.newpaper.somewhere.ui.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.newpaper.somewhere.enumUtils.AppContentType
import com.newpaper.somewhere.enumUtils.MapTheme
import com.newpaper.somewhere.ui.screens.mainScreens.MainDestination
import com.newpaper.somewhere.ui.screens.mainScreens.MainScreen
import com.newpaper.somewhere.ui.screens.mainScreens.MyTripsDestination
import com.newpaper.somewhere.ui.screens.settingScreens.AboutDestination
import com.newpaper.somewhere.ui.screens.settingScreens.AboutScreen
import com.newpaper.somewhere.ui.screens.settingScreens.SetDateFormatDestination
import com.newpaper.somewhere.ui.screens.settingScreens.SetDateTimeFormatScreen
import com.newpaper.somewhere.ui.screens.settingScreens.SetThemeScreen
import com.newpaper.somewhere.ui.screens.settingScreens.SetThemeScreenDestination
import com.newpaper.somewhere.ui.screens.tripScreens.DateDestination
import com.newpaper.somewhere.ui.screens.tripScreens.DateScreen
import com.newpaper.somewhere.ui.screens.tripScreens.SpotDetailDestination
import com.newpaper.somewhere.ui.screens.tripScreens.SpotScreen
import com.newpaper.somewhere.ui.screens.tripScreens.TripDestination
import com.newpaper.somewhere.ui.screens.tripScreens.TripMapDestination
import com.newpaper.somewhere.ui.screens.tripScreens.TripMapScreen
import com.newpaper.somewhere.ui.screens.tripScreens.TripScreen
import com.newpaper.somewhere.utils.USER_LOCATION_PERMISSION_LIST
import com.newpaper.somewhere.utils.checkPermissionUserLocation
import com.newpaper.somewhere.viewModel.AppViewModel
import com.newpaper.somewhere.viewModel.SomewhereViewModel
import com.newpaper.somewhere.viewModel.SomewhereViewModelProvider
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import com.newpaper.somewhere.ui.screens.tripScreens.ImageDestination
import com.newpaper.somewhere.ui.screens.tripScreens.ImageScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun SomewhereApp(
    isDarkAppTheme: Boolean,
    windowSize: WindowWidthSizeClass,
    fusedLocationClient: FusedLocationProviderClient,
    navController: NavHostController = rememberAnimatedNavController(),
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

    //map theme
    val isDarkMapTheme = appUiState.theme.mapTheme == MapTheme.DARK || appUiState.theme.mapTheme == MapTheme.AUTO && isDarkAppTheme

    //system ui controller for status bar icon color
    val systemUiController = rememberSystemUiController()

    //navigation bar color
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.surface)

    //set status bar color
    //image
    if (somewhereUiState.currentScreen == ImageDestination) {
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
        systemUiController.setNavigationBarColor(color = Color.Transparent)
    }

    //trip map
    else if (somewhereUiState.currentScreen == TripMapDestination && isDarkMapTheme)
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
    else if (somewhereUiState.currentScreen == TripMapDestination && !isDarkMapTheme)
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)

    //else
    else if (isDarkAppTheme)
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
    else
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)



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

    //page animation for image
    val imageEnterTransition = fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
    val imageExitTransition =
        fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
    val imagePopEnterTransition =
        fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
    val imagePopExitTransition = fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)


    var useImePadding by rememberSaveable { mutableStateOf(true) }

//    val boxModifier = if(useImePadding) Modifier//.imePadding()
//                      else              Modifier

//    val boxModifier =
//        if (somewhereUiState.currentScreen == TripMapDestination)
//            Modifier.navigationBarsPadding()
//        else if (somewhereUiState.currentScreen == ImageDestination)
//            Modifier
//        else
//            Modifier
//                .navigationBarsPadding()
//                .statusBarsPadding()
//                .displayCutoutPadding()

//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = MyTripsDestination.route
        ) {

            // MAIN ================================================================================
            composable(
                route = MyTripsDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ) {
                somewhereViewModel.updateCurrentScreen(MainDestination)

                MainScreen(
                    currentMainNavDestination = somewhereUiState.currentMainNavigationDestination,
                    setCurrentMainNavDestination = {
                        somewhereViewModel.updateCurrentMainNavDestination(it)
                    },
                    isEditMode = somewhereUiState.isEditMode,
                    dateTimeFormat = appUiState.dateTimeFormat,
                    addAddedImages = { newImages ->
                        somewhereViewModel.addAddedImages(newImages)
                    },
                    addDeletedImages = { newImages ->
                        somewhereViewModel.addDeletedImages(newImages)
                    },
                    organizeAddedDeletedImages = { isClickSave ->
                        somewhereViewModel.organizeAddedDeletedImages(context, isClickSave)
                    },

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
                    navigateTo = { navController.navigate(it.route) },
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

                        dateTimeFormat = appUiState.dateTimeFormat,

                        changeEditMode = {
                            somewhereViewModel.toggleEditMode(it)
                        },

                        navigateUp = {
                            navigateUp()
                            somewhereViewModel.toggleIsNewTrip(false)
                        },
                        navigateToImage = { imageList, initialImageIndex ->
                            somewhereViewModel.updateImageListAndInitialImageIndex(imageList, initialImageIndex)
                            navController.navigate(ImageDestination.route)
                        },
                        navigateToDate = { dateIndex ->
                            somewhereViewModel.toggleIsNewTrip(false)
                            somewhereViewModel.updateId(dateIndex = dateIndex)
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

                        addAddedImages = { newImages ->
                            somewhereViewModel.addAddedImages(newImages)
                        },
                        addDeletedImages = { newImages ->
                            somewhereViewModel.addDeletedImages(newImages)
                        },
                        organizeAddedDeletedImages = { isClickSave ->
                            somewhereViewModel.organizeAddedDeletedImages(context, isClickSave)
                        },
                        reorderTripImageList = { currentIndex, destinationIndex ->
                            somewhereViewModel.reorderTripImageList(currentIndex, destinationIndex)
                        },
                        reorderDateList = { currentIndex, destinationIndex ->
                            somewhereViewModel.reorderDateList(currentIndex, destinationIndex)
                        },

                        saveTrip = {
                            coroutineScope.launch {
                                somewhereViewModel.saveTrip(
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
                        dateIndex = somewhereUiState.dateIndex ?: 0,

                        dateTimeFormat = appUiState.dateTimeFormat,
                        changeEditMode = {
                            somewhereViewModel.toggleEditMode(it)
                        },

                        addAddedImages = { newImages ->
                            somewhereViewModel.addAddedImages(newImages)
                        },
                        addDeletedImages = { newImages ->
                            somewhereViewModel.addDeletedImages(newImages)
                        },
                        organizeAddedDeletedImages = { isClickSave ->
                            somewhereViewModel.organizeAddedDeletedImages(context, isClickSave)
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
                        reorderSpotList = { dateId, currentIndex, destinationIndex ->
                            somewhereViewModel.reorderSpotList(dateId, currentIndex, destinationIndex)
                        },
                        saveTrip = {
                            coroutineScope.launch {
                                somewhereViewModel.saveTrip({
                                    coroutineScope.launch { appViewModel.updateAppUiStateFromRepository() }
                                })
                            }
                        },

                        navigateUp = {
                            navigateUp()
                        },
                        navigateToSpot = { dateId, spotId ->
                            somewhereViewModel.updateId(dateIndex = dateId, spotIndex = spotId)
                            navController.navigate(SpotDetailDestination.route)
                        },
                        navigateToDateMap = {
                            navController.navigate(TripMapDestination.route)
                            somewhereViewModel.toggleIsNewTrip(false)
                        }
                    )
                }
            }

            // SPOT ================================================================================
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
                    SpotScreen(
                        isEditMode = somewhereUiState.isEditMode,
                        setUseImePadding = {
                            useImePadding = it
                        },

                        originalTrip = somewhereUiState.trip!!,
                        tempTrip = somewhereUiState.tempTrip!!,
                        dateIndex = somewhereUiState.dateIndex ?: 0,
                        spotIndex = somewhereUiState.spotIndex ?: 0,
                        dateTimeFormat = appUiState.dateTimeFormat,
                        changeEditMode = {
                            somewhereViewModel.toggleEditMode(it)
                        },
                        addAddedImages = { newImages ->
                            somewhereViewModel.addAddedImages(newImages)
                        },
                        addDeletedImages = { newImages ->
                            somewhereViewModel.addDeletedImages(newImages)
                        },
                        organizeAddedDeletedImages = { isClickSave ->
                            somewhereViewModel.organizeAddedDeletedImages(context, isClickSave)
                        },
                        reorderSpotImageList = { dateIndex, spotIndex, currentIndex, destinationIndex ->
                            somewhereViewModel.reorderSpotImageList(dateIndex, spotIndex, currentIndex, destinationIndex)
                        },

                        navigateToImage = { imageList, initialImageIndex ->
                            somewhereViewModel.updateImageListAndInitialImageIndex(imageList, initialImageIndex)
                            navController.navigate(ImageDestination.route)
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
                                somewhereViewModel.saveTrip({
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
                route = ImageDestination.route,
                enterTransition = { imageEnterTransition },
                exitTransition = { imageExitTransition },
                popEnterTransition = { imagePopEnterTransition },
                popExitTransition = { imagePopExitTransition }
            ){
                somewhereViewModel.updateCurrentScreen(ImageDestination)

                ImageScreen(
                    imagePathList = somewhereUiState.imageList ?: listOf(),
                    initialImageIndex = somewhereUiState.initialImageIndex,

                    navigateUp = { navigateUp() }
                )
            }



            // THEME SETTING =======================================================================
            composable(
                route = SetThemeScreenDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ){
                somewhereViewModel.updateCurrentScreen(SetThemeScreenDestination)

                SetThemeScreen(
                    theme = appUiState.theme,
                    saveUserPreferences = { appTheme_, mapTheme_ ->
                        appViewModel.saveThemeUserPreferences(appTheme_, mapTheme_)
                    },
                    navigateUp = { navigateUp() }
                )
            }


            // SET DATE FORMAT =====================================================================
            composable(
                route = SetDateFormatDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ){
                somewhereViewModel.updateCurrentScreen(SetDateFormatDestination)

                SetDateTimeFormatScreen(
                    dateTimeFormat = appUiState.dateTimeFormat,
                    saveUserPreferences = {dateFormat_, useMonthName_, includeDayOfWeek_, timeFormat_ ->
                        appViewModel.saveDateTimeFormatUserPreferences(dateFormat_, useMonthName_, includeDayOfWeek_, timeFormat_)
                    },
                    navigateUp = { navigateUp() }
                )
            }

            // ABOUT ===============================================================================
            composable(
                route = AboutDestination.route,
                enterTransition = { enterTransition },
                exitTransition = { exitTransition },
                popEnterTransition = { popEnterTransition },
                popExitTransition = { popExitTransition }
            ){
                somewhereViewModel.updateCurrentScreen(AboutDestination)

                AboutScreen(navigateUp = { navigateUp() })
            }
        }


        // for tablet

        //for phone

//    }
}
