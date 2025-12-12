package com.popla.perplexitydemo.webagent.domain

import com.popla.perplexitydemo.webagent.data.model.*

/**
 * Interface for processing natural language commands into structured task intents
 */
interface NaturalLanguageProcessor {
    
    /**
     * Parse a natural language command into a structured task intent
     */
    suspend fun parseCommand(input: String): TaskIntent
    
    /**
     * Extract entities from natural language input
     */
    suspend fun extractEntities(input: String): List<Entity>
    
    /**
     * Generate clarification questions for ambiguous intents
     */
    suspend fun clarifyAmbiguity(
        intent: TaskIntent, 
        context: UserContext
    ): List<ClarificationQuestion>
    
    /**
     * Validate if a command is safe and appropriate for execution
     */
    suspend fun validateCommand(input: String): CommandValidation
}

/**
 * User context for command processing
 */
data class UserContext(
    val currentUrl: String?,
    val recentCommands: List<String>,
    val userPreferences: UserPreferences,
    val personalData: PersonalData?
)

/**
 * Clarification question for ambiguous user input
 */
data class ClarificationQuestion(
    val question: String,
    val options: List<String>?,
    val field: String,
    val required: Boolean = true
)

/**
 * Result of command validation
 */
data class CommandValidation(
    val isValid: Boolean,
    val isSafe: Boolean,
    val warnings: List<String>,
    val suggestedAlternatives: List<String>
)