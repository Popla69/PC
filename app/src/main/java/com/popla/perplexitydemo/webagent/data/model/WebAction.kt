package com.popla.perplexitydemo.webagent.data.model

import android.graphics.Point
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing all possible web actions
 */
sealed class WebAction : Parcelable {
    
    @Parcelize
    data class Click(
        val selector: String,
        val coordinates: Point? = null,
        val waitForElement: Boolean = true
    ) : WebAction()
    
    @Parcelize
    data class Type(
        val selector: String,
        val text: String,
        val clear: Boolean = true,
        val pressEnter: Boolean = false
    ) : WebAction()
    
    @Parcelize
    data class Navigate(
        val url: String,
        val waitForLoad: Boolean = true
    ) : WebAction()
    
    @Parcelize
    data class Scroll(
        val direction: ScrollDirection,
        val amount: Int,
        val selector: String? = null // Scroll within specific element
    ) : WebAction()
    
    @Parcelize
    data class Upload(
        val selector: String,
        val filePath: String,
        val fileType: String
    ) : WebAction()
    
    @Parcelize
    data class Select(
        val selector: String,
        val option: String,
        val byValue: Boolean = false // Select by value vs visible text
    ) : WebAction()
    
    @Parcelize
    data class Wait(
        val condition: Condition,
        val timeout: Long = 10000
    ) : WebAction()
    
    @Parcelize
    data class ExtractData(
        val selectors: Map<String, String>, // field name to CSS selector
        val outputFormat: DataFormat = DataFormat.JSON
    ) : WebAction()
    
    @Parcelize
    data class ExecuteScript(
        val script: String,
        val returnValue: Boolean = false
    ) : WebAction()
    
    @Parcelize
    data class TakeScreenshot(
        val fullPage: Boolean = false,
        val selector: String? = null // Screenshot specific element
    ) : WebAction()
}

enum class ScrollDirection {
    UP, DOWN, LEFT, RIGHT
}

enum class DataFormat {
    JSON, CSV, XML, PLAIN_TEXT
}

/**
 * Result of executing a web action
 */
data class ActionResult(
    val success: Boolean,
    val data: Any? = null,
    val error: String? = null,
    val executionTime: Long,
    val screenshot: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActionResult

        if (success != other.success) return false
        if (data != other.data) return false
        if (error != other.error) return false
        if (executionTime != other.executionTime) return false
        if (screenshot != null) {
            if (other.screenshot == null) return false
            if (!screenshot.contentEquals(other.screenshot)) return false
        } else if (other.screenshot != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + executionTime.hashCode()
        result = 31 * result + (screenshot?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * Result of executing a sequence of actions
 */
data class SequenceResult(
    val overallSuccess: Boolean,
    val completedActions: Int,
    val totalActions: Int,
    val results: List<ActionResult>,
    val totalExecutionTime: Long,
    val failedAt: Int? = null
)

/**
 * Represents a condition to wait for
 */
@Parcelize
data class Condition(
    val type: ConditionType,
    val selector: String? = null,
    val expectedValue: String? = null,
    val timeout: Long = 10000
) : Parcelable

/**
 * Types of conditions that can be waited for
 */
enum class ConditionType {
    ELEMENT_EXISTS,
    ELEMENT_VISIBLE,
    ELEMENT_CLICKABLE,
    PAGE_LOADED,
    URL_CONTAINS,
    TEXT_PRESENT,
    FORM_FIELD_FILLED
}

/**
 * Represents a form field
 */
data class FormField(
    val name: String,
    val type: String,
    val selector: String,
    val required: Boolean = false,
    val placeholder: String? = null,
    val value: String? = null
)