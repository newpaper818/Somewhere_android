package com.newpaper.somewhere

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.newpaper.somewhere.enumUtils.AppTheme
import com.newpaper.somewhere.ui.navigation.SomewhereApp
import com.newpaper.somewhere.viewModel.SomewhereViewModelProvider
import com.newpaper.somewhere.ui.theme.SomewhereTheme
import com.newpaper.somewhere.viewModel.AppViewModel
import com.google.android.gms.location.LocationServices
import com.newpaper.somewhere.utils.calculateWindowSizeClass

class MainActivity : ComponentActivity() {

    //appViewModel
    private val appViewModel: AppViewModel by viewModels(
        factoryProducer = { SomewhereViewModelProvider.Factory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //expand screen to status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //splash screen
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !appViewModel.isReady.value
            }
        }

        setContent {
            //get app theme
            val appUiState = appViewModel.appUiState.collectAsState()
            val isDarkAppTheme = when (appUiState.value.theme.appTheme){
                AppTheme.LIGHT -> false
                AppTheme.DARK  -> true
                AppTheme.AUTO  -> isSystemInDarkTheme()
            }

            SomewhereTheme(darkTheme = isDarkAppTheme) {

                //user location fused client
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                //window size
                val configuration = LocalConfiguration.current
                val windowSize = DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)
                val windowSizeClass = calculateWindowSizeClass(windowSize = windowSize)
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SomewhereApp(
                        isDarkAppTheme = isDarkAppTheme,
                        windowSizeClass = windowSizeClass,
                        fusedLocationClient = fusedLocationClient,
                        appViewModel = appViewModel
                    )
                }
            }
        }
    }
}
