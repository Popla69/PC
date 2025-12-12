package com.popla.perplexitydemo.data.network

import com.popla.perplexitydemo.data.model.AIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AIApiService {
    
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>
}

data class ChatRequest(
    val model: String = "openai/gpt-4o-mini",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 2000,
    val stream: Boolean = false
)

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatResponse(
    val id: String,
    val choices: List<ChatChoice>,
    val usage: Usage? = null
)

data class ChatChoice(
    val message: ChatMessage,
    val finish_reason: String? = null
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)