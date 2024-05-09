package com.newpaper.somewhere.feature.trip.trips

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

const val TRIPS_ROUTE = "trips"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/trips"

fun NavController.navigationToTrips(navOptions: NavOptions) = navigate(TRIPS_ROUTE, navOptions)

fun NavGraphBuilder.trips(
    
) {
    composable(
        route = TRIPS_ROUTE,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        arguments = listOf(
            navArgument()
        )
    ) {
        TripsRoute(
            appUserId = ,
            internetEnabled = ,
            firstLaunch = ,
            firstLaunchToFalse = { /*TODO*/ },
            isEditMode = ,
            setEditMode = ,
            spacerValue = ,
            lazyListState = ,
            dateTimeFormat = ,
            addDeletedImages = ,
            organizeAddedDeletedImages = ,
            navigateToTrip = ,
            navigateToGlanceSpot = 
        )
    }
}