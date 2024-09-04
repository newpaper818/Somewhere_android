package com.newpaper.somewhere.navigation.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_DRAWER_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_RAIL_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.core.ui.ErrorScreen
import com.newpaper.somewhere.feature.profile.profile.ProfileRoute
import com.newpaper.somewhere.navigation.TopEnterTransition
import com.newpaper.somewhere.navigation.TopExitTransition
import com.newpaper.somewhere.navigation.TopPopEnterTransition
import com.newpaper.somewhere.navigation.TopPopExitTransition
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import com.newpaper.somewhere.util.WindowHeightSizeClass
import com.newpaper.somewhere.util.WindowWidthSizeClass
import kotlinx.coroutines.delay

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/profile"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) =
    navigate(TopLevelDestination.PROFILE.route, navOptions)

fun NavGraphBuilder.profileScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    lazyListState: LazyListState,
    navigateToAccount: () -> Unit,
    navigateUp: () -> Unit
) {
    composable(
        route = TopLevelDestination.PROFILE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { TopEnterTransition },
        exitTransition = { TopExitTransition },
        popEnterTransition = { TopPopEnterTransition },
        popExitTransition = { TopPopExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.PROFILE)
            delay(100)
            appViewModel.updateCurrentScreenDestination(ScreenDestination.PROFILE)
        }


        val appUiState by appViewModel.appUiState.collectAsState()
        val widthSizeClass = externalState.windowSizeClass.widthSizeClass
        val heightSizeClass = externalState.windowSizeClass.heightSizeClass

        if (appUiState.appUserData != null){
            Row {
                if (widthSizeClass == WindowWidthSizeClass.Compact){
                    MySpacerRow(width = 0.dp)
                }
                else if (
                    heightSizeClass == WindowHeightSizeClass.Compact
                    || widthSizeClass == WindowWidthSizeClass.Medium
                ) {
                    MySpacerRow(width = NAVIGATION_RAIL_BAR_WIDTH)
                } else if (widthSizeClass == WindowWidthSizeClass.Expanded) {
                    MySpacerRow(width = NAVIGATION_DRAWER_BAR_WIDTH)
                }

                ProfileRoute(
                    internetEnabled = externalState.internetEnabled,
                    spacerValue = externalState.windowSizeClass.spacerValue,
                    lazyListState = lazyListState,
                    use2Panes = externalState.windowSizeClass.use2Panes,
                    userData = appUiState.appUserData!!,
                    navigateToAccount = navigateToAccount,
                )
            }
        }
        else {
            ErrorScreen()
            navigateUp()
        }
    }
}
