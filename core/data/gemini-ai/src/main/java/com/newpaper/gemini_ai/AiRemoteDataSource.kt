package com.newpaper.gemini_ai

import com.google.android.libraries.places.api.model.Place
import com.newpaper.somewhere.core.model.tripData.Trip

interface AiRemoteDataSource {
    suspend fun getRecommendSpots(): List<String>?

    suspend fun  getTripPlan(
        places: List<Place>
    ): Trip?
}