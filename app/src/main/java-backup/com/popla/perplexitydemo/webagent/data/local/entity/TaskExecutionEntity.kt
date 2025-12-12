package com.popla.perplexitydemo.webagent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.popla.perplexitydemo.data.converter.Converters
import com.popla.perplexitydemo.webagent.data.model.ActionResult
import com.popla.perplexitydemo.webagent.data.model.TaskPlan
import java.time.Instant

/**
 * Room entity for storing task execution records
 */
@Entity(tableName = "task_executions")
@TypeConverters(Converters::class)
data class TaskExecutionEntity(
    @PrimaryKey
    val id: String,
    val commandId: String,
    val taskPlan: String, // JSON serialized TaskPlan
    val status: ExecutionStatus,
    val startTime: Instant,
    val endTime: Instant? = null,
    val results: String, // JSON serialized List<ActionResult>
    val errors: String, // JSON serialized List<ExecutionError>
    val userId: String? = null
)

enum class ExecutionStatus {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
}

/**
 * Room entity for storing user commands
 */
@Entity(tableName = "user_commands")
@TypeConverters(Converters::class)
data class UserCommandEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val timestamp: Instant,
    val userId: String,
    val context: String, // JSON serialized Map<String, Any>
    val intent: String? = null, // JSON serialized TaskIntent
    val processed: Boolean = false
)

/**
 * Room entity for storing monitoring sessions
 */
@Entity(tableName = "monitoring_sessions")
@TypeConverters(Converters::class)
data class MonitoringSessionEntity(
    @PrimaryKey
    val id: String,
    val url: String,
    val config: String, // JSON serialized MonitoringConfig
    val status: MonitoringStatus,
    val lastCheck: Instant? = null,
    val detectedChanges: String, // JSON serialized List<Change>
    val alerts: String, // JSON serialized List<Alert>
    val createdAt: Instant,
    val userId: String
)

enum class MonitoringStatus {
    ACTIVE, PAUSED, STOPPED, ERROR
}

/**
 * Room entity for storing user preferences
 */
@Entity(tableName = "user_preferences")
@TypeConverters(Converters::class)
data class UserPreferencesEntity(
    @PrimaryKey
    val userId: String,
    val autoFillForms: Boolean = true,
    val confirmBeforeActions: Boolean = true,
    val saveCredentials: Boolean = false,
    val defaultTimeout: Long = 30000,
    val preferredBrowser: String = "chromium",
    val enableScreenshots: Boolean = true,
    val maxConcurrentTasks: Int = 3,
    val personalData: String? = null, // JSON serialized PersonalData
    val updatedAt: Instant
)

/**
 * Room entity for storing web agent audit logs
 */
@Entity(tableName = "audit_logs")
@TypeConverters(Converters::class)
data class AuditLogEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val action: String, // JSON serialized WebAction
    val result: String, // JSON serialized ActionResult
    val timestamp: Instant,
    val riskLevel: RiskLevel,
    val metadata: String, // JSON serialized Map<String, Any>
    val userId: String
)

enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}