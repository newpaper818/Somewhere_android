package com.newpaper.somewhere.core.data.repository

import com.newpaper.gemini_ai.AiRemoteDataSource
import com.newpaper.somewhere.core.data.moshi.SerializationDataSource
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
import com.newpaper.somewhere.core.model.tripData.Trip
import javax.inject.Inject

class SerializationRepository @Inject constructor(
    private val serializationDataSource: SerializationDataSource
) {
    fun jsonToTrip(
        json: String
    ): Trip?{
        return serializationDataSource.jsonToTrip(json)
    }
}
