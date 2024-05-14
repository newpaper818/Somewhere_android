package com.newpaper.somewhere.feature.more.more

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.enums.NavDestination

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more"

fun NavController.navigationToMore(navOptions: NavOptions) = navigate(NavDestination.MORE_ROUTE.route, navOptions)

fun NavGraphBuilder.more(
    isDebugMode: Boolean,
    userDataIsNull: Boolean,

    startSpacerValue: Dp,
    endSpacerValue: Dp,
    lazyListState: LazyListState,
    navigateTo: (NavDestination) -> Unit,

    modifier: Modifier = Modifier,
    currentScreen: NavDestination? = null
) {
    composable(
        route = NavDestination.MORE_ROUTE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
//        arguments = listOf(
//            navArgument()
//        )
    ) {
        MoreRoute(
            isDebugMode = isDebugMode,
            userDataIsNull = userDataIsNull,
            startSpacerValue = startSpacerValue,
            endSpacerValue = endSpacerValue,
            lazyListState = lazyListState,
            navigateTo = navigateTo,
            modifier = modifier,
            currentScreen = currentScreen
        )
    }
}