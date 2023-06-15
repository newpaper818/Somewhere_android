package com.example.somewhere

import android.app.Application
import com.example.somewhere.db.AppContainer
import com.example.somewhere.db.AppDataContainer

class SomewhereApplication : Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}