package com.newpaper.gemini_ai

import com.google.android.libraries.places.api.model.Place

interface AiRemoteDataSource {
    suspend fun getRecommendSpots(
        city: String,
        tripDays: Int,
        tripWith: String,
        tripType: String
    ): Set<String>?

    suspend fun  getTripPlan(
        placesWithPlaces: Set<Pair<String, Place>>,
        city: String,
        tripDate: String,
        tripWith: String,
        tripType: String,
        language: String
    ): String?
}