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
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.location.LocationServices
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val MAIN_ACTIVITY_TAG = "MainActivity1"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()
    private val tripsViewModel: TripsViewModel by viewModels()

    private lateinit var connectivityObserver: ConnectivityObserver


    override fun onCreate(savedInstanceState: Bundle?) {

        //splash screen
        Log.d(MAIN_ACTIVITY_TAG, "--- splash screen")
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)



        splashScreen.setKeepOnScreenCondition {
            appViewModel.appUiState.value.screenDestination.startScreenDestination == null
        }

        val backgroundScope = CoroutineScope(Dispatchers.IO)

        backgroundScope.launch {
            //init firebase
            initializeFirebase()
        }

        //get signed user and update start destination
        appViewModel.viewModelScope.launch {
            //get signed user and update start destination
            Log.d(MAIN_ACTIVITY_TAG, "- init user and update start destination start")

            //this function will get user and set {appViewModel.appUiState.value.screenDestination.startScreenDestination}
            appViewModel.intiUserAndUpdateStartDestination()
        }

        backgroundScope.launch {
            //register admob test device and initialize google admob
            initializeAdmob()
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













    private fun initializeAdmob(){
        Log.d(MAIN_ACTIVITY_TAG, "*init google admob start - register test device, init")
        //register admob test device
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    //SM-N976N as admob test device
                    .setTestDeviceIds(listOf("CB0B799A397825135B612701F65D8B09"))
                    .build()
            )
        }

        //initialize google admob
        MobileAds.initialize(applicationContext)
        Log.d(MAIN_ACTIVITY_TAG, "                              *init google admob done")
    }

    private fun initializeFirebase(){
        //firebase
        Log.d(MAIN_ACTIVITY_TAG, "+init firebase start")

        Firebase.initialize(applicationContext)
//        Log.d(MAIN_ACTIVITY_TAG, "                              init firebase done")

        //firebase app check
//        Log.d(MAIN_ACTIVITY_TAG, "init firebase app check start")
        Firebase.appCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG || "a" in BuildConfig.VERSION_NAME)
                DebugAppCheckProviderFactory.getInstance()
            else
                PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        FirebaseApp.initializeApp(applicationContext)

        Log.d(MAIN_ACTIVITY_TAG, "                              +init firebase app check done")
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
