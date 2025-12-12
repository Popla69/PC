package com.popla.perplexitydemo.domain

import com.popla.perplexitydemo.data.model.AIResponse

/**
 * Interface for processing queries and generating responses
 */
interface QueryProcessor {
    /**
     * Process a query and return response
     */
    suspend fun processQuery(query: String, mode: SearchMode = SearchMode.GENERAL): AIResponse
    
    /**
     * Process query with context
     */
    suspend fun processQueryWithContext(query: String, context: String, mode: SearchMode = SearchMode.GENERAL): AIResponse
    
    /**
     * Validate query format
     */
    fun validateQuery(query: String): Boolean
    
    /**
     * Get supported search modes
     */
    fun getSupportedModes(): List<SearchMode>
}