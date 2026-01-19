package com.newpaper.somewhere

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


private const val MAIN_ACTIVITY_TAG = "MainActivity1"

@HiltAndroidApp
class SomewhereApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {

        super.onCreate()

        applicationScope.launch {
            initializeFirebase()
        }

        applicationScope.launch {
            initializeAdmob()
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
            if (BuildConfig.DEBUG || "alpha" in BuildConfig.VERSION_NAME)
                DebugAppCheckProviderFactory.getInstance()
            else
                PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        FirebaseApp.initializeApp(applicationContext)

        Log.d(MAIN_ACTIVITY_TAG, "                              +init firebase done")
    }
}

