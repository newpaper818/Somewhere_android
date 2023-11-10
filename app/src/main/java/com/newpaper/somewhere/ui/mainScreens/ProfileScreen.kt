package com.newpaper.somewhere.ui.mainScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.commonScreenUtils.SomewhereNavigationBar
import com.newpaper.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
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
        //top app bar
        topBar = {
            SomewhereTopAppBar(
                title = ProfileDestination.title,
            )
        },
        bottomBar = {
            SomewhereNavigationBar(
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