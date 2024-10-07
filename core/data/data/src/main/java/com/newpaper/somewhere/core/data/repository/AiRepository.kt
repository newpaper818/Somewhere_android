package com.newpaper.somewhere.core.data.repository

import android.util.Log
import com.newpaper.gemini_ai.AiRemoteDataSource
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val aiRemoteDataSource: AiRemoteDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource
) {
    suspend fun getAiCreatedTrip(
        city: String,
        tripDate: String,
        tripWith: String,
        tripType: String,
        language: String
    ): String? {
        //get places from ai
        val list = aiRemoteDataSource.getRecommendSpots(
            city = city,
            tripWith = tripWith,
            tripType = tripType
        )
//        return list.toString()

        //get places info from Google places
        return if (list != null) {
            val places = placesRemoteDataSource.getPlacesInfo(list)

            //get trip plans from ai
            if (places != null){
                aiRemoteDataSource.getTripPlan(
                    places = places,
                    city = city,
                    tripDate = tripDate,
                    tripWith = tripWith,
                    tripType = tripType,
                    language = language
                )
            } else null

        } else null
    }
}