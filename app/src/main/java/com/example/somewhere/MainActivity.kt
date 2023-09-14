package com.example.somewhere

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat

import com.example.somewhere.viewModel.SomewhereViewModelProvider
import com.example.somewhere.ui.screens.SomewhereApp
import com.example.somewhere.ui.theme.SomewhereTheme
import com.example.somewhere.viewModel.AppViewModel
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    //appViewModel
    private val appViewModel: AppViewModel by viewModels(
        factoryProducer = { SomewhereViewModelProvider.Factory }
    )

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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
            SomewhereTheme {

                //user location fused client
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                //window size for phone or tablet
                val windowSize = calculateWindowSizeClass(activity = this)

                SomewhereApp(
                    windowSize = windowSize.widthSizeClass,
                    fusedLocationClient = fusedLocationClient,
                    appViewModel = appViewModel
                )
            }
        }
    }
}
