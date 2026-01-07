package com.newpaper.somewhere

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.play.core.review.ReviewManagerFactory
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.feature.trip.trips.TripsViewModel
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.SomewhereApp
import com.newpaper.somewhere.ui.rememberExternalState
import com.newpaper.somewhere.util.ConnectivityObserver
import com.newpaper.somewhere.util.NetworkConnectivityObserver
import com.newpaper.somewhere.util.calculateWindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val MAIN_ACTIVITY_TAG = "MainActivity1"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()
    private val tripsViewModel: TripsViewModel by viewModels()

    private lateinit var connectivityObserver: ConnectivityObserver


    override fun onCreate(savedInstanceState: Bundle?) {

        //splash screen
        Log.d(MAIN_ACTIVITY_TAG, "--- on create")
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            appViewModel.appUiState.value.screenDestination.startScreenDestination == null
        }

        //get signed user and update start destination
        appViewModel.viewModelScope.launch {
            //get signed user and update start destination
            //this function will get user and set {appViewModel.appUiState.value.screenDestination.startScreenDestination}
            appViewModel.intiUserAndUpdateStartDestination()
        }



        enableEdgeToEdge()



        //in app review
        showFeedbackDialog()


//        Log.d(MAIN_ACTIVITY_TAG, "set connectivity observer")
        connectivityObserver = NetworkConnectivityObserver(applicationContext)


        setContent {
            val appUiState by appViewModel.appUiState.collectAsState()

//            LaunchedEffect(appUiState.screenDestination.startScreenDestination) {
//                if (appUiState.screenDestination.startScreenDestination != null)
//                    Log.d(MAIN_ACTIVITY_TAG, "--- splash done")
//            }

//            Log.d(MAIN_ACTIVITY_TAG, "create externalState, appUiState")
            val externalState = rememberExternalState(
                context = applicationContext,
                windowSizeClass = calculateWindowSizeClass(),
                connectivityObserver = connectivityObserver
            )

            //get app theme
//            Log.d(MAIN_ACTIVITY_TAG, "get app theme")
            val isDarkAppTheme = when (appUiState.appPreferences.theme.appTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.AUTO -> isSystemInDarkTheme()
            }


            SomewhereTheme(darkTheme = isDarkAppTheme) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Log.d(MAIN_ACTIVITY_TAG, "launch somewhere app")
                    SomewhereApp(
                        externalState = externalState,
                        appViewModel = appViewModel,
                        tripsViewModel = tripsViewModel,
                        isDarkAppTheme = isDarkAppTheme,
                        fusedLocationClient = fusedLocationClient
                    )
                }
            }
        }
    }
















    private fun showFeedbackDialog() {
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        reviewManager.requestReviewFlow().addOnCompleteListener {
            if (it.isSuccessful) {
                reviewManager.launchReviewFlow(this, it.result)
            }
        }
    }
}
