package com.popla.perplexitydemo.webagent.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.Duration
import java.time.Instant

/**
 * Represents a complete execution plan for a web automation task
 */
@Parcelize
data class TaskPlan(
    val id: String,
    val steps: List<ActionStep>,
    val conditions: List<Condition>,
    val fallbacks: List<FallbackAction>,
    val estimatedDuration: Long, // Duration in milliseconds
    val createdAt: @RawValue Instant = Instant.now()
) : Parcelable

/**
 * Represents a single step in a task execution plan
 */
@Parcelize
data class ActionStep(
    val id: String,
    val action: WebAction,
    val order: Int,
    val dependencies: List<String>, // IDs of steps that must complete first
    val timeout: Long, // Timeout in milliseconds
    val retryCount: Int = 3
) : Parcelable

// Condition and ConditionType are defined in WebAction.kt

/**
 * Represents a fallback action when primary actions fail
 */
@Parcelize
data class FallbackAction(
    val triggerCondition: String,
    val alternativeAction: WebAction,
    val maxAttempts: Int = 2
) : Parcelable

/**
 * Validation result for a task plan
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val warnings: List<String>,
    val estimatedSuccessRate: Float
)