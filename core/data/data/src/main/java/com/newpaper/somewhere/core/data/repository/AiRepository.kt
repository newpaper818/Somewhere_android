package com.newpaper.somewhere.core.data.repository

import com.newpaper.gemini_ai.AiRemoteDataSource
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val aiRemoteDataSource: AiRemoteDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource
) {
    suspend fun getAiCreatedTrip(
        city: String,
        startDate: LocalDate,
        endDate: LocalDate,
        tripWith: String,
        tripType: String,
        language: String
    ): String? {
        //get places from ai
        val list = aiRemoteDataSource.getRecommendSpots(
            city = city,
            tripDays = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays().toInt() + 1,
            tripWith = tripWith,
            tripType = tripType
        )

        //get places info from Google places
        return if (list != null) {
            val places = placesRemoteDataSource.getPlacesInfo(list)

            //get trip plans from ai
            if (places != null){
                aiRemoteDataSource.getTripPlan(
                    places = places,
                    city = city,
                    tripDate = "$startDate ~ $endDate",
                    tripWith = tripWith,
                    tripType = tripType,
                    language = language
                )
            } else null

        } else null
    }
}