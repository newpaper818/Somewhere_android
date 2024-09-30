package com.newpaper.gemini_ai

interface AiRemoteDataSource {
    suspend fun getRecommendSpots(): List<String>?
}