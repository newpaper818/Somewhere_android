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

    ): String {
        //get places from ai
        val list = aiRemoteDataSource.getRecommendSpots()
//        return list.toString()

        //get places info from Google places
        if (list != null) {
            val places = placesRemoteDataSource.getPlacesInfo(list)

            //get trip plans from ai
            if (places != null){
                aiRemoteDataSource.getTripPlan(places)
                return "null"

            }
            else return "null"

        }
        else return "null"
    }
}