package com.newpaper.somewhere.navigation.more

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.more.setDateTimeFormat.SetDateTimeFormatRoute
import com.newpaper.somewhere.navigation.enterTransitionHorizontal
import com.newpaper.somewhere.navigation.exitTransitionHorizontal
import com.newpaper.somewhere.navigation.popEnterTransitionHorizontal
import com.newpaper.somewhere.navigation.popExitTransitionHorizontal
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/setDateTimeFormat"

fun NavController.navigateToSetDateTimeFormat(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.SET_DATE_TIME_FORMAT.route, navOptions)

fun NavGraphBuilder.setDateTimeFormatScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateUp: () -> Unit
){
    composable(
        route = ScreenDestination.SET_DATE_TIME_FORMAT.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransitionHorizontal },
        exitTransition = { exitTransitionHorizontal },
        popEnterTransition = { popEnterTransitionHorizontal },
        popExitTransition = { popExitTransitionHorizontal }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SET_DATE_TIME_FORMAT)
            if (!externalState.windowSizeClass.use2Panes)
                appViewModel.updateMoreDetailCurrentScreenDestination(ScreenDestination.SET_DATE_TIME_FORMAT)
        }

        val coroutineScope = rememberCoroutineScope()

        val appUiState by appViewModel.appUiState.collectAsState()

        SetDateTimeFormatRoute(
            use2Panes = externalState.windowSizeClass.use2Panes,
            spacerValue = externalState.windowSizeClass.spacerValue,
            useBlurEffect = appUiState.appPreferences.theme.useBlurEffect,
            dateTimeFormat = appUiState.appPreferences.dateTimeFormat,
            updatePreferencesValue = {
                coroutineScope.launch{
                    appViewModel.getAppPreferencesValue()
                }
            },
            navigateUp = navigateUp,
        )
    }
}