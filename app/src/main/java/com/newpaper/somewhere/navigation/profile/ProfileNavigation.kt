package com.newpaper.somewhere.navigation.profile

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.profile.profile.ProfileRoute

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/profile"

fun NavController.navigationToProfile(navOptions: NavOptions) = navigate(ScreenDestination.PROFILE_ROUTE.route, navOptions)

fun NavGraphBuilder.profileScreen(
    internetEnabled: Boolean,
    spacerValue: Dp,
    lazyListState: LazyListState,
    use2Panes: Boolean,
    userData: UserData?,
    navigateToAccount: () -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    composable(
        route = ScreenDestination.PROFILE_ROUTE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
//        arguments = listOf(
//            navArgument()
//        )
    ) {
        ProfileRoute(
            internetEnabled = internetEnabled,
            spacerValue = spacerValue,
            lazyListState = lazyListState,
            use2Panes = use2Panes,
            userData = userData,
            navigateToAccount = navigateToAccount,
            snackBarHostState = snackBarHostState
        )
    }
}
