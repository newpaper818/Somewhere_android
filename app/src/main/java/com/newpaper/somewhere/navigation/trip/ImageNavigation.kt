package com.newpaper.somewhere.navigation.trip

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.image.ImageRoute
import com.newpaper.somewhere.navigation.imageEnterTransition
import com.newpaper.somewhere.navigation.imageExitTransition
import com.newpaper.somewhere.navigation.imagePopEnterTransition
import com.newpaper.somewhere.navigation.imagePopExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/main/image"

fun NavController.navigateToImage(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.IMAGE.route, navOptions)

fun NavGraphBuilder.imageScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,
    commonTripViewModel: CommonTripViewModel,

    navigateUp: () -> Unit,
) {
    composable(
        route = ScreenDestination.IMAGE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { imageEnterTransition },
        exitTransition = { imageExitTransition },
        popEnterTransition = { imagePopEnterTransition },
        popExitTransition = { imagePopExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.IMAGE)
        }

        val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsStateWithLifecycle()

        val tripManagerId = commonTripUiState.tripInfo.trip?.managerId

        if (tripManagerId != null) {
            ImageRoute(
                imageUerId = tripManagerId,
                internetEnabled = externalState.internetEnabled,
                imagePathList = commonTripUiState.imageList,
                initialImageIndex = commonTripUiState.initialImageIndex,
                navigateUp = navigateUp,
                downloadImage = commonTripViewModel::getImage,
                saveImageToExternalStorage = commonTripViewModel::saveImageToExternalStorage
            )
        }
    }
}