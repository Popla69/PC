package com.popla.perplexitydemo.webagent.domain

import com.popla.perplexitydemo.webagent.data.model.*
import java.time.Duration

/**
 * Interface for executing web actions and managing automation workflows
 */
interface ActionExecutor {
    
    /**
     * Execute a single web action
     */
    suspend fun executeAction(action: WebAction): ActionResult
    
    /**
     * Execute a sequence of web actions
     */
    suspend fun executeSequence(actions: List<WebAction>): SequenceResult
    
    /**
     * Wait for a specific condition to be met
     */
    suspend fun waitForCondition(condition: WebCondition, timeout: Duration): Boolean
    
    /**
     * Capture the current state of the web page
     */
    suspend fun captureState(): WebState
    
    /**
     * Pause execution for a specified duration
     */
    suspend fun pause(duration: Duration)
    
    /**
     * Retry a failed action with different strategies
     */
    suspend fun retryAction(action: WebAction, maxAttempts: Int = 3): ActionResult
}

/**
 * Represents a condition to wait for during web automation
 */
sealed class WebCondition {
    data class ElementExists(val selector: String) : WebCondition()
    data class ElementVisible(val selector: String) : WebCondition()
    data class ElementClickable(val selector: String) : WebCondition()
    data class TextPresent(val text: String) : WebCondition()
    data class UrlContains(val urlPart: String) : WebCondition()
    data class PageLoaded(val timeout: Duration = Duration.ofSeconds(30)) : WebCondition()
    data class CustomScript(val script: String, val expectedResult: Any) : WebCondition()
}

/**
 * Represents the current state of a web page
 */
data class WebState(
    val url: String,
    val title: String,
    val readyState: String,
    val forms: List<DetectedForm>,
    val links: List<DetectedLink>,
    val buttons: List<DetectedButton>,
    val inputs: List<DetectedInput>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Represents a detected form on the page
 */
data class DetectedForm(
    val selector: String,
    val action: String?,
    val method: String?,
    val fields: List<FormField>
)

/**
 * Represents a detected link on the page
 */
data class DetectedLink(
    val selector: String,
    val href: String?,
    val text: String?,
    val isExternal: Boolean
)

/**
 * Represents a detected button on the page
 */
data class DetectedButton(
    val selector: String,
    val text: String?,
    val type: String?,
    val isEnabled: Boolean
)

/**
 * Represents a detected input field on the page
 */
data class DetectedInput(
    val selector: String,
    val type: String,
    val name: String?,
    val placeholder: String?,
    val value: String?,
    val required: Boolean
)