package com.popla.perplexitydemo.domain

/**
 * Interface for parsing citations from AI responses
 */
interface CitationParser {
    /**
     * Parse citations from text
     */
    fun parseCitations(text: String): List<Citation>
    
    /**
     * Extract citation text
     */
    fun extractCitationText(text: String): String
    
    /**
     * Validate citation format
     */
    fun validateCitation(citation: Citation): Boolean
}

/**
 * Represents a citation
 */
data class Citation(
    val id: String,
    val url: String,
    val title: String?,
    val snippet: String?,
    val position: Int
)