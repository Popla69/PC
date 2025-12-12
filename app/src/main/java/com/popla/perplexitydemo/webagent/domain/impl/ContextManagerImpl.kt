package com.popla.perplexitydemo.webagent.domain.impl

import com.popla.perplexitydemo.webagent.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ContextManager for managing web context and user preferences
 */
@Singleton
class ContextManagerImpl @Inject constructor(
    private val webEngine: WebEngine,
    private val formDetector: FormDetector
) : ContextManager {

    private val _currentContext = MutableStateFlow(
        WebContext(
            currentUrl = "",
            pageTitle = null,
            sessionId = UUID.randomUUID().toString()
        )
    )

    private val _userPreferences = MutableStateFlow(UserPreferences())
    private val taskHistory = mutableListOf<TaskHistoryEntry>()

    override suspend fun getCurrentContext(): WebContext {
        return _currentContext.value
    }

    override suspend fun updateContext(context: WebContext) {
        _currentContext.value = context
    }

    override suspend fun getUserPreferences(): UserPreferences {
        return _userPreferences.value
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences) {
        _userPreferences.value = preferences
    }

    override suspend fun getTaskHistory(limit: Int): List<TaskHistoryEntry> {
        return taskHistory.takeLast(limit)
    }

    override suspend fun addTaskToHistory(entry: TaskHistoryEntry) {
        taskHistory.add(entry)
        // Keep only last 100 entries
        if (taskHistory.size > 100) {
            taskHistory.removeAt(0)
        }
    }

    override suspend fun predictNextAction(): List<ActionSuggestion> {
        val currentUrl = webEngine.getCurrentUrl()
        val suggestions = mutableListOf<ActionSuggestion>()

        // Analyze current page for suggestions
        val forms = formDetector.detectForms()
        if (forms.isNotEmpty()) {
            suggestions.add(ActionSuggestion(
                id = UUID.randomUUID().toString(),
                type = "fill_form",
                description = "Fill detected form with your information",
                confidence = 0.8,
                estimatedTime = 30000,
                requiredData = listOf("personalInfo", "contactInfo")
            ))
        }

        // Check for common actions based on URL patterns
        when {
            currentUrl.contains("checkout") || currentUrl.contains("cart") -> {
                suggestions.add(ActionSuggestion(
                    id = UUID.randomUUID().toString(),
                    type = "complete_checkout",
                    description = "Complete checkout process",
                    confidence = 0.9,
                    estimatedTime = 120000,
                    requiredData = listOf("paymentInfo", "addressInfo")
                ))
            }
            currentUrl.contains("login") || currentUrl.contains("signin") -> {
                suggestions.add(ActionSuggestion(
                    id = UUID.randomUUID().toString(),
                    type = "auto_login",
                    description = "Fill login credentials",
                    confidence = 0.7,
                    estimatedTime = 10000,
                    requiredData = listOf("credentials")
                ))
            }
        }

        return suggestions
    }
}