package com.newpaper.somewhere

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SomewhereApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}