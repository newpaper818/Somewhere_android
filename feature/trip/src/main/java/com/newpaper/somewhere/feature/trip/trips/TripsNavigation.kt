package com.newpaper.somewhere.feature.trip.trips

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip

const val TRIPS_ROUTE = "trips"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/trips"

fun NavController.navigationToTrips(navOptions: NavOptions) = navigate(TRIPS_ROUTE, navOptions)

fun NavGraphBuilder.trips(
    appUserId: String,
    internetEnabled: Boolean,
    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,
    isEditMode: Boolean,
    setEditMode: (editMode: Boolean?) -> Unit,

    spacerValue: Dp,

    lazyListState: LazyListState,

    dateTimeFormat: DateTimeFormat,

    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,
    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,
) {
    composable(
        route = TRIPS_ROUTE,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
//        arguments = listOf(
//            navArgument()
//        )
    ) {
        TripsRoute(
            appUserId = appUserId,
            internetEnabled = internetEnabled,
            firstLaunch = firstLaunch,
            firstLaunchToFalse = firstLaunchToFalse,
            isEditMode = isEditMode,
            setEditMode = setEditMode,
            spacerValue = spacerValue,
            lazyListState = lazyListState,
            dateTimeFormat = dateTimeFormat,
            addDeletedImages = addDeletedImages,
            organizeAddedDeletedImages = organizeAddedDeletedImages,
            navigateToTrip = navigateToTrip,
            navigateToGlanceSpot = navigateToGlanceSpot
        )
    }
}