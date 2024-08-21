package com.newpaper.somewhere.navigation.more

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_DRAWER_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_RAIL_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.feature.more.more.MoreRoute
import com.newpaper.somewhere.navigation.TopEnterTransition
import com.newpaper.somewhere.navigation.TopExitTransition
import com.newpaper.somewhere.navigation.TopPopEnterTransition
import com.newpaper.somewhere.navigation.TopPopExitTransition
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.ExternalState
import com.newpaper.somewhere.util.WindowHeightSizeClass
import com.newpaper.somewhere.util.WindowWidthSizeClass
import java.util.UUID

private const val DEEP_LINK_URI_PATTERN =
    "https://www.somewhere.newpaper.com/more"

fun NavController.navigateToMore(navOptions: NavOptions? = null) =
    navigate(TopLevelDestination.MORE.route, navOptions)

fun NavGraphBuilder.moreScreen(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    moreNavController: NavHostController,
    moreNavKey: UUID,

    userDataIsNull: Boolean,
    lazyListState: LazyListState,
    navigateTo: (ScreenDestination) -> Unit,

    navigateToDeleteAccount: () -> Unit,
    navigateToEditAccount: () -> Unit,
    navigateToOpenSourceLicense: () -> Unit,
    onSignOutDone: () -> Unit,

    modifier: Modifier = Modifier,
) {
    composable(
        route = TopLevelDestination.MORE.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        ),
        enterTransition = { TopEnterTransition },
        exitTransition = { TopExitTransition },
        popEnterTransition = { TopPopEnterTransition },
        popExitTransition = { TopPopExitTransition }
    ) {
        LaunchedEffect(Unit) {
            appViewModel.updateCurrentScreenDestination(ScreenDestination.MORE)
            appViewModel.updateCurrentTopLevelDestination(TopLevelDestination.MORE)
        }

        val appUiState by appViewModel.appUiState.collectAsState()

        val widthSizeClass = externalState.windowSizeClass.widthSizeClass
        val heightSizeClass = externalState.windowSizeClass.heightSizeClass

        var currentScreen by rememberSaveable { mutableStateOf(
            appUiState.screenDestination.moreDetailStartScreenDestination
        ) }

//        val moreNavController = rememberNavController()


//        moreNavController.addOnDestinationChangedListener { controller, _, _ ->
//            val routes = controller
//                .currentBackStack.value
//                .map { it.destination.route }
//                .joinToString(", ")
//
//            Log.d("moreBackStackLog", "more BackStack: $routes")
//        }


        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .displayCutoutPadding()
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
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

                MoreRoute(
                    isDebugMode = BuildConfig.DEBUG,
                    userDataIsNull = userDataIsNull,
                    spacerValue = externalState.windowSizeClass.spacerValue,
                    lazyListState = lazyListState,
                    navigateTo = {
                        if (it != appUiState.screenDestination.currentScreenDestination) {
                            if (!externalState.windowSizeClass.use2Panes)
                                navigateTo(it)
                            else {
                                moreNavController.navigate(
                                    route = it.route,
                                    navOptions = navOptions {
                                        popUpTo(0) { inclusive = true }
                                    }
                                )
                                currentScreen = it
                            }
                        }
                    },
                    use2Panes = externalState.windowSizeClass.use2Panes,
                    currentScreenRoute =
                        if (externalState.windowSizeClass.use2Panes) moreNavController.currentDestination?.route
                        else null
                )
            }





            if (externalState.windowSizeClass.use2Panes){

                Box(modifier = Modifier.weight(1f)) {
                    key(moreNavKey) {
                        MoreDetailPane(
                            externalState = externalState,
                            appViewModel = appViewModel,
                            moreNavController = moreNavController,
                            startDestination = appUiState.screenDestination.moreDetailStartScreenDestination,
                            navigateToDeleteAccount = navigateToDeleteAccount,
                            navigateToEditAccount = navigateToEditAccount,
                            navigateToOpenSourceLicense = navigateToOpenSourceLicense,
                            onSignOutDone = onSignOutDone
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun MoreDetailPane(
    externalState: ExternalState,
    appViewModel: AppViewModel,
    moreNavController: NavHostController,
    startDestination: ScreenDestination,

    navigateToDeleteAccount: () -> Unit,
    navigateToEditAccount: () -> Unit,
    navigateToOpenSourceLicense: () -> Unit,
    onSignOutDone: () -> Unit,

    modifier: Modifier = Modifier,
){
    NavHost(
        navController = moreNavController,
        startDestination = startDestination.route
    ){
        setDateTimeFormatScreen(
            appViewModel = appViewModel,
            externalState = externalState,
            navigateUp = { }
        )

        setThemeScreen(
            appViewModel = appViewModel,
            externalState = externalState,
            navigateUp = { }
        )

        accountScreen(
            appViewModel = appViewModel,
            externalState = externalState,
            navigateToDeleteAccount = navigateToDeleteAccount,
            navigateToEditAccount = navigateToEditAccount,
            navigateUp = { },
            onSignOutDone = onSignOutDone,
            modifier = modifier

        )

        aboutScreen(
            externalState = externalState,
            appViewModel = appViewModel,
            navigateToOpenSourceLicense = navigateToOpenSourceLicense,
            navigateUp = { },
            modifier = modifier

        )
    }

}






