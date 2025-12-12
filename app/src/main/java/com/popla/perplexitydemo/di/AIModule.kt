package com.popla.perplexitydemo.di

import com.popla.perplexitydemo.domain.ai.AIProcessor
import com.popla.perplexitydemo.domain.ai.AIProcessorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AIModule {

    @Binds
    @Singleton
    abstract fun bindAIProcessor(
        aiProcessorImpl: AIProcessorImpl
    ): AIProcessor
}