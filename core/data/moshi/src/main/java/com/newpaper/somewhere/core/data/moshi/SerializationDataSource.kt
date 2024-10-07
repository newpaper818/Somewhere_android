package com.newpaper.somewhere.core.data.moshi

import com.newpaper.somewhere.core.model.tripData.Trip

interface SerializationDataSource {
    fun jsonToTrip(
        json: String
    ): Trip?
}