package com.example.somewhere

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import com.example.somewhere.ui.screens.somewhere.SomewhereApp
import com.example.somewhere.ui.theme.SomewhereTheme
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        //expand screen to status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)

        setContent {
            SomewhereTheme {

                //my location
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                //window size for phone or tablet
                val windowSize = calculateWindowSizeClass(activity = this)

                SomewhereApp(
                    windowSize = windowSize.widthSizeClass,
                    fusedLocationClient = fusedLocationClient
                )
            }
        }
    }
}
