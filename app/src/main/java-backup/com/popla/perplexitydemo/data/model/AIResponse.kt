package com.popla.perplexitydemo.data.model

data class AIResponse(
    val content: String,
    val citations: List<Citation> = emptyList(),
    val searchResults: List<SearchResult> = emptyList(),
    val processingTime: Long = 0,
    val model: String = "",
    val usage: TokenUsage? = null
)

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String,
    val domain: String,
    val relevanceScore: Double = 0.0,
    val publishedDate: String? = null
)

data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)