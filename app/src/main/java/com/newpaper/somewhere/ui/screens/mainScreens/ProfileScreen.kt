package com.newpaper.somewhere.ui.screens.mainScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereNavigationBottomBar
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.navigation.NavigationDestination

// USE LATER? --------------------------------------------------------------------------------------
object ProfileDestination : NavigationDestination {
    override val route = "profile"
    override var title = "Profile"
}

@Composable
fun ProfileScreen(
    navigateTo: (NavigationDestination) -> Unit,
    navigateToMain: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold (
        modifier = Modifier.displayCutoutPadding().statusBarsPadding().navigationBarsPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        //top app bar
        topBar = {
            SomewhereTopAppBar(
                title = ProfileDestination.title,
            )
        },
        bottomBar = {
            SomewhereNavigationBottomBar(
                currentDestination = ProfileDestination,
                navigateTo = navigateToMain,
                scrollToTop = { }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { 
                Text(text = "profileeeeeeeeee")
            }
        }
    }
}