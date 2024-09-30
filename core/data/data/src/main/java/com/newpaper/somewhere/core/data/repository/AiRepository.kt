package com.newpaper.somewhere.core.data.repository

import com.newpaper.gemini_ai.AiRemoteDataSource
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val aiRemoteDataSource: AiRemoteDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource
) {
    suspend fun getAiCreatedTrip(

    ): String{
        val list =  aiRemoteDataSource.getRecommendSpots()
        return list.toString()


        //get places from ai

        //get places info from Google places

        //get trip plans from ai
    }
}