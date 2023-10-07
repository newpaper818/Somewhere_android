package com.example.somewhere.ui.mainScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.viewModel.AppViewModel
import com.example.somewhere.viewModel.DateTimeFormat

object MainDestination : NavigationDestination {
    override val route = "main"
    override var title = "Somewhere"
}

@Composable
fun MainScreen(
    currentMainNavDestination: NavigationDestination,
    setCurrentMainNavDestination: (NavigationDestination) -> Unit,

    isEditMode: Boolean,
    dateTimeFormat: DateTimeFormat,
    changeEditMode: (editMode: Boolean?) -> Unit,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,
    navigateTo: (NavigationDestination) -> Unit,

    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {

    when (currentMainNavDestination){
        MyTripsDestination -> {
            MyTripsScreen(
                isEditMode = isEditMode,
                dateTimeFormat = dateTimeFormat,
                changeEditMode = changeEditMode,
                navigateToTrip = navigateToTrip,
                navigateTo = navigateTo,
                navigateToMain = {
                    setCurrentMainNavDestination(it)
                },
                appViewModel = appViewModel,
                modifier = modifier
            )
        }
        MoreDestination -> {
            MoreScreen(
                navigateTo = navigateTo,
                navigateToMain = {
                    setCurrentMainNavDestination(it)
                }
            )
        }

//        ProfileDestination -> {
//            ProfileScreen(
//                navigateTo = navigateTo,
//                navigateToMain = {
//                    showingNavigationDestination = it
//                },
//                modifier = modifier
//            )
//        }
        else -> { }
    }

}