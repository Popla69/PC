package com.popla.perplexitydemo.domain.ai

import com.popla.perplexitydemo.data.model.AIResponse
import com.popla.perplexitydemo.data.model.SearchMode

interface AIProcessor {
    suspend fun processQuery(
        query: String,
        conversationHistory: List<String> = emptyList(),
        searchMode: SearchMode = SearchMode.GENERAL
    ): Result<AIResponse>
    
    suspend fun processStreamingQuery(
        query: String,
        conversationHistory: List<String> = emptyList(),
        searchMode: SearchMode = SearchMode.GENERAL,
        onChunk: (String) -> Unit
    ): Result<AIResponse>
}