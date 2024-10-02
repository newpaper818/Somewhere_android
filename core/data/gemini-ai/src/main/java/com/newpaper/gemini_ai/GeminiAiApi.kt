package com.newpaper.gemini_ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.android.libraries.places.api.model.Place
import com.newpaper.somewhere.core.data.gemini_ai.BuildConfig
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.tripData.Trip
import kotlinx.serialization.json.Json
import javax.inject.Inject


private const val GEMINI_AI_TAG = "Gemini-Ai"

class GeminiAiApi @Inject constructor(

): AiRemoteDataSource{
    override suspend fun getRecommendSpots(

    ): List<String>? {
        val generativeModel =
            GenerativeModel(
                // Specify a Gemini model appropriate for your use case
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.GEMINI_AI_API_KEY,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                }
            )

        val prompt = """
            get trip places (5 places) using this JSON schema:
            return: Array<String> (each String should not be sentence. like [N Seoul tower, Empire state building, Central park])
        
            [trip info]
            city: Seoul, Korea
            trip with(solo, partner, friend, family): friend
            trip type: activity
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)

        if (response.text != null) {
            val list = Json.decodeFromString<List<String>>(response.text!!)

            list.forEach {
                Log.d(GEMINI_AI_TAG, it)
            }

            return list
        }
        else
            return null
    }

    override suspend fun getTripPlan(
        places: List<Place>
    ): Trip? {
        val generativeModel =
            GenerativeModel(
                // Specify a Gemini model appropriate for your use case
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.GEMINI_AI_API_KEY,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                }
            )

        val prompt = """
            Make trip plan.
            When you make trip plan, use [places].
            
            [trip info] 
            city: Seoul, Korea
            trip with(solo, partner, friend, family): friend 
            trip type: activity
            trip date: 2024-09-30 ~ 2024-10-05
            
            [places]
            ${placesToString(places)}
            
            [spot type list]
            ${enumValues<SpotType>().map { it }}
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)

        if (response.text != null) {
            val trip = Json.decodeFromString<Trip>(response.text!!)

            Log.d(GEMINI_AI_TAG, trip.toString())

            return trip
        }
        else
            return null
    }

    private fun placesToString(
        places: List<Place>
    ): String{
        val placeList = places.map { place ->
            placeToString(place)
        }

        return placeList.joinToString(", ", "[", "]")
    }

    private fun placeToString(
        place: Place
    ): String {
        val stringList = listOf(
            "name: " + place.displayName,
            "location(lat/lng): (" + place.location?.latitude + "," + place.location?.latitude + ")",
            "place types: " + place.placeTypes
        )

        return stringList.joinToString(", ", "{", "}")
    }
}