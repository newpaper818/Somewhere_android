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
import com.newpaper.somewhere.feature.more.setTheme.SetThemeRoute
import com.newpaper.somewhere.navigation.enterTransition
import com.newpaper.somewhere.navigation.exitTransition
import com.newpaper.somewhere.navigation.popEnterTransition
import com.newpaper.somewhere.navigation.popExitTransition
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import kotlinx.coroutines.launch

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more/setTheme"

fun NavController.navigateToSetTheme(navOptions: NavOptions? = null) =
    navigate(ScreenDestination.SET_THEME.route, navOptions)

fun NavGraphBuilder.setThemeScreen(
    appViewModel: AppViewModel,
    externalState: ExternalState,

    navigateUp: () -> Unit
){
    composable(
        route = ScreenDestination.SET_THEME.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.SET_THEME)
        }

        val coroutineScope = rememberCoroutineScope()

        val appUiState by appViewModel.appUiState.collectAsState()

        SetThemeRoute(
            startSpacerValue = externalState.windowSizeClass.spacerValue,
            endSpacerValue = externalState.windowSizeClass.spacerValue,
            theme = appUiState.appPreferences.theme,
            updatePreferencesValue = {
                coroutineScope.launch{
                    appViewModel.getAppPreferencesValue()
                }
            },
            navigateUp = navigateUp
        )
    }
}