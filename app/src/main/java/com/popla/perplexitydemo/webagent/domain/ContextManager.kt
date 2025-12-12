package com.popla.perplexitydemo.webagent.domain

/**
 * Interface for managing web context and user preferences
 */
interface ContextManager {
    /**
     * Get current web context
     */
    suspend fun getCurrentContext(): WebContext
    
    /**
     * Update current context
     */
    suspend fun updateContext(context: WebContext)
    
    /**
     * Get user preferences
     */
    suspend fun getUserPreferences(): UserPreferences
    
    /**
     * Update user preferences
     */
    suspend fun updateUserPreferences(preferences: UserPreferences)
    
    /**
     * Get task history
     */
    suspend fun getTaskHistory(limit: Int = 50): List<TaskHistoryEntry>
    
    /**
     * Add task to history
     */
    suspend fun addTaskToHistory(entry: TaskHistoryEntry)
    
    /**
     * Predict next action based on context and history
     */
    suspend fun predictNextAction(): List<ActionSuggestion>
}

/**
 * Current web context information
 */
data class WebContext(
    val currentUrl: String,
    val pageTitle: String?,
    val detectedForms: List<DetectedForm> = emptyList(),
    val availableActions: List<String> = emptyList(),
    val pageMetadata: Map<String, Any> = emptyMap(),
    val userLocation: String? = null,
    val sessionId: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * User preferences and settings
 */
data class UserPreferences(
    val personalData: PersonalData = PersonalData(),
    val automationSettings: AutomationSettings = AutomationSettings(),
    val privacySettings: PrivacySettings = PrivacySettings(),
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val customMappings: Map<String, String> = emptyMap()
)

/**
 * Personal data for form filling
 */
data class PersonalData(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: Address? = null,
    val dateOfBirth: String? = null,
    val company: String? = null,
    val jobTitle: String? = null
)

/**
 * Address information
 */
data class Address(
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null
)

/**
 * Automation settings
 */
data class AutomationSettings(
    val autoFillForms: Boolean = false,
    val confirmBeforeSubmit: Boolean = true,
    val enableMonitoring: Boolean = false,
    val maxConcurrentTasks: Int = 3,
    val defaultTimeout: Long = 30000,
    val retryAttempts: Int = 3
)

/**
 * Privacy settings
 */
data class PrivacySettings(
    val allowDataCollection: Boolean = false,
    val shareUsageStats: Boolean = false,
    val enableEncryption: Boolean = true,
    val clearDataOnExit: Boolean = false,
    val trustedDomains: List<String> = emptyList()
)

/**
 * Notification settings
 */
data class NotificationSettings(
    val enableAlerts: Boolean = true,
    val alertTypes: List<String> = listOf("PRICE_DROP", "AVAILABILITY_CHANGE"),
    val quietHours: TimeRange? = null,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

/**
 * Time range for quiet hours
 */
data class TimeRange(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
)

/**
 * Task history entry
 */
data class TaskHistoryEntry(
    val id: String,
    val taskType: String,
    val url: String,
    val description: String,
    val timestamp: Long,
    val success: Boolean,
    val duration: Long,
    val context: Map<String, Any> = emptyMap()
)

/**
 * Action suggestion based on context
 */
data class ActionSuggestion(
    val id: String,
    val type: String,
    val description: String,
    val confidence: Double,
    val estimatedTime: Long,
    val requiredData: List<String> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
)