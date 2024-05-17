package com.newpaper.somewhere

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.ui.AppViewModel
import com.newpaper.somewhere.ui.rememberExternalState
import com.newpaper.somewhere.util.NetworkConnectivityObserver
import com.newpaper.somewhere.util.calculateWindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        //splash screen
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        //get signed user and update start destination
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                Log.d("mainActivity", "initttttt")
                appViewModel.intiUserAndUpdateStartDestination()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            appViewModel.appUiState.value.screenDestination.startScreenDestination == null
        }





        //expand screen to status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //firebase init app check
        firebaseInitAppCheck()

        //register admob test device and initialize google admob
        initializeAdmob()

        //in app review
//        showFeedbackDialog()




        setContent {

            val externalState = rememberExternalState(
                context = applicationContext,
                windowSizeClass = calculateWindowSizeClass(),
                connectivityObserver = NetworkConnectivityObserver(applicationContext)
            )

            //DO NOT REMOVE: if remove and add new spot, app will crash!!!
//            val tempSpot = Spot(id = 0, date = LocalDate.of(2023, 12, 13))

            val appUiState by appViewModel.appUiState.collectAsState()

            //get app theme
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
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "width: ${externalState.windowSizeClass.widthSizeClass}")
                        Text(text = "internet: ${externalState.internetEnabled}")
                    }



                }
            }
        }
    }













    private fun initializeAdmob(){
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
    }

    private fun firebaseInitAppCheck(){
        //firebase app check
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
