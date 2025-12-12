package com.popla.perplexitydemo.di

import com.popla.perplexitydemo.BuildConfig
import com.popla.perplexitydemo.data.network.AIApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val authInterceptor = Interceptor { chain ->
            val apiKey = BuildConfig.AGENT_API_KEY
            android.util.Log.d("NetworkModule", "Using API key (first 15 chars): ${apiKey.take(15)}...")
            android.util.Log.d("NetworkModule", "API Base URL: ${BuildConfig.AGENT_API_BASE}")
            
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "https://github.com/your-repo") // OpenRouter requires this
                .addHeader("X-Title", "Mobile Perplexity Comet") // Optional but recommended
                .build()
                
            android.util.Log.d("NetworkModule", "Request URL: ${request.url}")
            android.util.Log.d("NetworkModule", "Request headers: ${request.headers}")
            
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.AGENT_API_BASE)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAIApiService(retrofit: Retrofit): AIApiService {
        return retrofit.create(AIApiService::class.java)
    }
}