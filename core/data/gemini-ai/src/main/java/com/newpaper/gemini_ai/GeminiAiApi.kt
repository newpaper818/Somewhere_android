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
            get trip places (20 places) using this JSON schema:
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
    ): String? {
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
            trip date: 2024-10-01 ~ 2024-10-03
            trip with(solo, partner, friend, family): friend
            trip type: activity
            
            [format - follow this rule]
            language: Korean
            time format: HH:MM (24h, do not add additional data)
            date format: yyyy-mm-dd (do not add additional data)
            
            [places]
            ${placesToString(places)}
            
            [other request]
            Consider transportation time.
            spotList's item is order by time.
            googleMapsPlacesId should be same with given [places]'s id.
            spot's titleText is place's name (only allow translation).
            You don't have to follow [places] order.
            
            [spot type list- select spotType from below]
            ${enumValues<SpotType>().map { it }}
            
            [return: JSON type]
            ${jsonTypeString()}
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)

        Log.d(GEMINI_AI_TAG, response.text.toString())
        return response.text

//        if (response.text != null) {
//            val trip = Json.decodeFromString<Trip>(response.text!!)
//
//            Log.d(GEMINI_AI_TAG, trip.toString())
//
//            return trip
//        }
//        else
//            return null
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
            "id: " + place.id,
//            "location(lat/lng): (" + place.location?.latitude + "," + place.location?.latitude + ")",
            "placeTypes: " + place.placeTypes
        )

        return stringList.joinToString(", ", "{", "}")
    }

    private fun jsonTypeString(

    ): String{
        return """
            {
              "type": "object",
              "properties": {
                "titleText": {
                  "type": "string"
                },
                "startDate": {
                  "type": "string"
                },
                "endDate": {
                  "type": "string"
                },
                "memoText": {
                  "type": "string"
                },
                "dateList": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "titleText": {
                        "type": "string"
                      },
                      "memoText": {
                        "type": "string"
                      },
                      "spotList": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "titleText": {
                              "type": "string"
                            },
                            "date": {
                              "type": "string"
                            },
                            "startTime": {
                              "type": "string"
                            },
                            "endTime": {
                              "type": "string"
                            },
                            "memoText": {
                              "type": "string"
                            },
                            "spotType": {
                              "type": "string"
                            },
                            "googleMapsPlacesId": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "titleText",
                            "date",
                            "startTime",
                            "endTime",
                            "googleMapsPlacesId"
                          ]
                        }
                      }
                    },
                    "required": [
                      "titleText",
                      "spotList"
                    ]
                  }
                }
              },
              "required": [
                "titleText",
                "startDate",
                "endDate",
                "dateList"
              ]
            }
        """.trimIndent()
    }
}