package com.newpaper.gemini_ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.android.libraries.places.api.model.Place
import com.newpaper.somewhere.core.data.gemini_ai.BuildConfig
import com.newpaper.somewhere.core.model.enums.SpotType
import kotlinx.serialization.json.Json
import javax.inject.Inject


private const val GEMINI_AI_TAG = "Gemini-Ai"

class GeminiAiApi @Inject constructor(

): AiRemoteDataSource{
    override suspend fun getRecommendSpots(
        city: String,
        tripDays: Int,
        tripWith: String,
        tripType: String
    ): Set<String>? {
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
            Get trip places (${tripDays * 5} places) using this JSON schema:
            Return: Array<String> (Each String should not be sentence. Like [N Seoul tower, Empire state building, Central park])
        
            [trip info]
            city(or country): $city
            trip with(solo, partner, friend, family): $tripWith
            trip type: $tripType
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)

        if (response.text != null) {
            val placeSet = Json.decodeFromString<Set<String>>(response.text!!)

            placeSet.forEach {
                Log.d(GEMINI_AI_TAG, it)
            }

            return placeSet
        }
        else
            return null
    }

    override suspend fun getTripPlan(
        places: Set<Place>,
        city: String,
        tripDate: String,
        tripWith: String,
        tripType: String,
        language: String
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
            city(or country): $city
            trip date: $tripDate
            trip with(solo, partner, friend, family): $tripWith
            trip type: $tripType
            
            [format] - follow this rule
            language: $language
            time format: HH:MM (24h, do not add additional data)
            date format: yyyy-mm-dd (do not add additional data)
            
            [places]
            ${placesToString(places)}
            
            [other request]
            Consider transportation time.
            spotList's item is order by time.
            googleMapsPlacesId should be same with given [places]'s id.
            spot's titleText is place's name.
            You don't have to follow [places] order.
            From [places]'s placeTypes, set spotType from [spot type list] (Do not use [places]'s placeTypes.
            Only use [spot type list]'s item for spotType.
            Include breakfast, lunch, dinner.
            Lodging should be close to [places].
            
            [spot type list] - select spotType from below
            ${enumValues<SpotType>().map { it }}
            
            [return: JSON type]
            ${jsonTypeString()}
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)

        Log.d(GEMINI_AI_TAG, response.text.toString())
        return response.text
    }

    private fun placesToString(
        places: Set<Place>
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
                      "date": {
                        "type": "string"
                      },
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