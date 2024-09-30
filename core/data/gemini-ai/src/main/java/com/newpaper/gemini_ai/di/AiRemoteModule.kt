package com.newpaper.gemini_ai.di

import com.newpaper.gemini_ai.AiRemoteDataSource
import com.newpaper.gemini_ai.GeminiAiApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AiRemoteModule {
    @Binds
    internal abstract fun bindAiRemoteDataSource(
        geminiAiApi: GeminiAiApi
    ): AiRemoteDataSource
}