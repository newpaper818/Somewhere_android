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

private const val GEMINI_MODEL_NAME = "gemini-2.0-flash"

class GeminiAiApi @Inject constructor(

): AiRemoteDataSource{
    override suspend fun getRecommendSpots(
        city: String,
        tripDays: Int,
        tripWith: String,
        tripType: String
    ): Set<String>? {
        Log.d(GEMINI_AI_TAG, "getRecommendSpots start")

        val generativeModel =
            GenerativeModel(
                // Specify a Gemini model appropriate for your use case
                modelName = GEMINI_MODEL_NAME,
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.GEMINI_AI_API_KEY,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                }
            )

        val prompt = """
            You are a travel assistant.
            
            Return only a valid JSON array of strings, with no extra text.
            - Format: ["N Seoul Tower", "Empire State Building", "Central Park"]
            - Do NOT wrap the array in another array or object.
            - Do NOT include any explanations, comments, or sentences.
            - Each element must be just a place name (String), not a sentence.
            - The array must contain at least ${tripDays * 5} place names.
            
            <Trip Info>
            - City or country: $city
            - Trip with (solo, partner, friend, family): $tripWith
            - Trip type: $tripType
        """.trimIndent()

//        Log.d("gemini-ai", "prompt: $prompt")

        try {
            val response = generativeModel.generateContent(prompt)

            if (response.text != null) {
                val placeSet = Json.decodeFromString<Set<String>>(response.text!!)

                Log.d(GEMINI_AI_TAG, placeSet.toString())

                return placeSet
            }
            else
                return null
        }
        catch (e: Exception){
            Log.e(GEMINI_AI_TAG, "getRecommendSpots error - $e")
            return null
        }
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
                modelName = GEMINI_MODEL_NAME,
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = BuildConfig.GEMINI_AI_API_KEY,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                }
            )

        val prompt = """
            You are a travel assistant. Make a detailed trip plan.
            When you make trip plan, use <places>.
            
            <trip info>
            city(or country): $city
            trip date: $tripDate
            trip with(solo, partner, friend, family): $tripWith
            trip type: $tripType
            
            <format>
            language: $language
            time format: HH:MM (24h, zero-padded, no AM/PM, 00:00 to 23:59)
            date format: yyyy-mm-dd (zero-padded, no extra text)
            
            <places>
            ${placesToString(places)}
            
            <other request>
            - Consider transportation time.
            - spotList must be sorted by time.
            - googleMapsPlacesId must exactly match the given <places>'s id.
            - spot's titleText must equal the place's name.
            - You don't have to follow <places> order.
            - From <places>'s placeTypes, map to <spot type list> instead of copying.
            - Include breakfast, lunch, dinner.
            - Lodging should be close to <places>.
            
            <spot type list>
            ${enumValues<SpotType>().map { it }}
            
            <return>
            Return only a valid JSON object that strictly follows this schema:
            ${jsonTypeString()}
        """.trimIndent()

//        Log.d("gemini-ai", "prompt: $prompt")


        try {
            val response = generativeModel.generateContent(prompt)

            Log.d(GEMINI_AI_TAG, response.text.toString())
            return response.text
        }
        catch (e: Exception){
            Log.e(GEMINI_AI_TAG, "getTripPlan error - $e")
            return null
        }
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