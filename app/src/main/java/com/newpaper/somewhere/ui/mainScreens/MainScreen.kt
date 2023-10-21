package com.newpaper.somewhere.ui.mainScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.newpaper.somewhere.model.Trip
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.viewModel.AppViewModel
import com.newpaper.somewhere.viewModel.DateTimeFormat

object MainDestination : NavigationDestination {
    override val route = "main"
    override var title = ""
}

@Composable
fun MainScreen(
    currentMainNavDestination: NavigationDestination,
    setCurrentMainNavDestination: (NavigationDestination) -> Unit,

    isEditMode: Boolean,
    dateTimeFormat: DateTimeFormat,
    changeEditMode: (editMode: Boolean?) -> Unit,

    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,

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
                addAddedImages = addAddedImages,
                addDeletedImages = addDeletedImages,
                organizeAddedDeletedImages = organizeAddedDeletedImages,
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