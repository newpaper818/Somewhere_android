package com.newpaper.gemini_ai

import com.google.android.libraries.places.api.model.Place

interface AiRemoteDataSource {
    suspend fun getRecommendSpots(
        city: String,
        tripDays: Int,
        tripWith: String,
        tripType: String
    ): List<String>?

    suspend fun  getTripPlan(
        places: List<Place>,
        city: String,
        tripDate: String,
        tripWith: String,
        tripType: String,
        language: String
    ): String?
}