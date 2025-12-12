package com.popla.perplexitydemo.webagent.domain

import com.popla.perplexitydemo.webagent.data.model.*

/**
 * Interface for low-level web browser interactions
 */
interface WebEngine {
    
    /**
     * Create a new browser session
     */
    suspend fun createSession(): BrowserSession
    
    /**
     * Navigate to a specific URL
     */
    suspend fun navigateTo(url: String): NavigationResult
    
    /**
     * Find a single element on the page
     */
    suspend fun findElement(selector: String): WebElement?
    
    /**
     * Find multiple elements on the page
     */
    suspend fun findElements(selector: String): List<WebElement>
    
    /**
     * Execute JavaScript code on the page
     */
    suspend fun executeScript(script: String): Any?
    
    /**
     * Capture a screenshot of the current page
     */
    suspend fun captureScreenshot(): ByteArray
    
    /**
     * Get the current page content as HTML
     */
    suspend fun getPageContent(): String
    
    /**
     * Get the current page URL
     */
    suspend fun getCurrentUrl(): String
    
    /**
     * Get the current page title
     */
    suspend fun getPageTitle(): String
    
    /**
     * Close the browser session
     */
    suspend fun closeSession()
}

/**
 * Represents a browser session
 */
data class BrowserSession(
    val id: String,
    val browserType: BrowserType,
    val userAgent: String,
    val cookies: MutableList<Cookie> = mutableListOf(),
    val localStorage: MutableMap<String, String> = mutableMapOf(),
    val sessionStorage: MutableMap<String, String> = mutableMapOf()
)

enum class BrowserType {
    CHROMIUM, FIREFOX, WEBKIT, ANDROID_WEBVIEW
}

/**
 * Represents a browser cookie
 */
data class Cookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String = "/",
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val expiryTime: Long? = null
)

/**
 * Result of a navigation operation
 */
data class NavigationResult(
    val success: Boolean,
    val finalUrl: String,
    val statusCode: Int?,
    val loadTime: Long,
    val error: String? = null
)

/**
 * Interface for interacting with web elements
 */
interface WebElement {
    suspend fun click()
    suspend fun type(text: String)
    suspend fun clear()
    suspend fun getText(): String
    suspend fun getAttribute(name: String): String?
    suspend fun isVisible(): Boolean
    suspend fun isEnabled(): Boolean
    suspend fun isSelected(): Boolean
    suspend fun getTagName(): String
    suspend fun getCssValue(property: String): String
    suspend fun getRect(): ElementRect
    suspend fun takeScreenshot(): ByteArray
}

/**
 * Represents the position and size of an element
 */
data class ElementRect(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)