package com.popla.perplexitydemo.webagent.domain

import com.popla.perplexitydemo.webagent.data.model.*

/**
 * Interface for interpreting task intents into executable action plans
 */
interface TaskInterpreter {
    
    /**
     * Convert a task intent into an executable task plan
     */
    suspend fun interpretTask(intent: TaskIntent, context: WebContext): TaskPlan
    
    /**
     * Validate a task plan for feasibility and safety
     */
    suspend fun validatePlan(plan: TaskPlan): ValidationResult
    
    /**
     * Adapt an existing plan to new context or conditions
     */
    suspend fun adaptPlan(plan: TaskPlan, newContext: WebContext): TaskPlan
    
    /**
     * Estimate the success probability of a task plan
     */
    suspend fun estimateSuccess(plan: TaskPlan, context: WebContext): Float
    
    /**
     * Generate alternative plans for the same intent
     */
    suspend fun generateAlternatives(intent: TaskIntent, context: WebContext): List<TaskPlan>
}

/**
 * Context update for plan adaptation
 */
data class ContextUpdate(
    val type: UpdateType,
    val data: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis()
)

enum class UpdateType {
    PAGE_CHANGED,
    ELEMENT_APPEARED,
    ELEMENT_DISAPPEARED,
    FORM_DETECTED,
    ERROR_OCCURRED,
    USER_INTERVENTION_REQUIRED
}