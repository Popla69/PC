package com.popla.perplexitydemo.webagent.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.Instant

/**
 * Represents a user's intent to perform a web automation task
 */
@Parcelize
data class TaskIntent(
    val id: String,
    val description: String,
    val targetUrl: String? = null,
    val formData: Map<String, String> = emptyMap(),
    val expectedOutcome: String? = null,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val timeout: Long = 30000, // 30 seconds default
    val createdAt: @RawValue Instant = Instant.now()
) : Parcelable

/**
 * Priority levels for task execution
 */
enum class TaskPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

/**
 * Types of web automation actions
 */
enum class ActionType {
    CLICK,
    TYPE,
    NAVIGATE,
    SCROLL,
    WAIT,
    EXTRACT,
    UPLOAD,
    SELECT,
    SUBMIT
}