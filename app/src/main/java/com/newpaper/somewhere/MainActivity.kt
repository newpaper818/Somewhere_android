package com.newpaper.somewhere

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.AppTheme
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

    private lateinit var connectivityObserver: ConnectivityObserver


    override fun onCreate(savedInstanceState: Bundle?) {

        //splash screen
        Log.d(MAIN_ACTIVITY_TAG, "on splash screen")
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        //get signed user and update start destination
        lifecycleScope.launch {
            Log.d(MAIN_ACTIVITY_TAG, "init user and update start destination")
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                appViewModel.intiUserAndUpdateStartDestination()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            appViewModel.appUiState.value.screenDestination.startScreenDestination == null
        }





        //expand screen to status bar
        Log.d(MAIN_ACTIVITY_TAG, "expand screen to status bar")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //firebase init app check
        initFirebase()

        //register admob test device and initialize google admob
        initializeAdmob()

        //in app review
//        showFeedbackDialog()

        Log.d(MAIN_ACTIVITY_TAG, "set connectivity observer")
        connectivityObserver = NetworkConnectivityObserver(applicationContext)



        setContent {
            //DO NOT REMOVE: if remove and add new spot, app will crash!!!
            //val tempSpot = Spot(id = 0, date = LocalDate.of(2023, 12, 13))

            Log.d(MAIN_ACTIVITY_TAG, "create externalState, appUiState")
            val externalState = rememberExternalState(
                context = applicationContext,
                windowSizeClass = calculateWindowSizeClass(),
                connectivityObserver = connectivityObserver
            )

            val appUiState by appViewModel.appUiState.collectAsState()

            //get app theme
            Log.d(MAIN_ACTIVITY_TAG, "get app theme")
            val isDarkAppTheme = when (appUiState.appPreferences.theme.appTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.AUTO -> isSystemInDarkTheme()
            }


            SomewhereTheme(darkTheme = isDarkAppTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Column(modifier = Modifier.fillMaxSize()) {
//                        Text(text = "width: ${externalState.windowSizeClass.widthSizeClass}")
//                        Text(text = "internet: ${externalState.internetEnabled}")
//                    }
                    Log.d(MAIN_ACTIVITY_TAG, "launch somewhere app")
                    SomewhereApp(
                        externalState = externalState,
                        appViewModel = appViewModel,
                        isDarkAppTheme = isDarkAppTheme
                    )
                }
            }
        }
    }













    private fun initializeAdmob(){
        //register admob test device
        Log.d(MAIN_ACTIVITY_TAG, "register admob test device")
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    //SM-N976N as admob test device
                    .setTestDeviceIds(listOf("CB0B799A397825135B612701F65D8B09"))
                    .build()
            )
        }

        //initialize google admob
        Log.d(MAIN_ACTIVITY_TAG, "initialize google admob")
        MobileAds.initialize(applicationContext)
    }

    private fun initFirebase(){
        Log.d(MAIN_ACTIVITY_TAG, "init firebase app")
        FirebaseApp.initializeApp(applicationContext)

        //firebase app check
        Log.d(MAIN_ACTIVITY_TAG, "init firebase app check")
        Firebase.initialize(context = applicationContext)

        Firebase.appCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG || "a" in BuildConfig.VERSION_NAME)
                DebugAppCheckProviderFactory.getInstance()
            else
                PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
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
