package com.newpaper.somewhere.db

import android.content.Context

interface AppContainer {
    val tripsRepository: TripRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val tripsRepository: TripRepository by lazy {
        OfflineTripRepository(TripDatabase.getDatabase(context).tripDao())
    }
}