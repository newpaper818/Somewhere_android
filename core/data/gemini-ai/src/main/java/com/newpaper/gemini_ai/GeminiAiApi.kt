package com.newpaper.gemini_ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.newpaper.somewhere.core.data.gemini_ai.BuildConfig
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
            get trip spots (5 spots) using this JSON schema:
            return: Array<String>
        
            city: Seoul Korea
            trip with: friend
            trip type: activity
            total people: 4
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
}