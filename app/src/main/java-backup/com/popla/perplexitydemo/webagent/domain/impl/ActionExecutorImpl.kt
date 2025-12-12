package com.popla.perplexitydemo.webagent.domain.impl

import com.popla.perplexitydemo.webagent.data.model.*
import com.popla.perplexitydemo.webagent.domain.*
import kotlinx.coroutines.delay
import java.time.Duration
// import javax.inject.Inject
// import javax.inject.Singleton

/**
 * Implementation of ActionExecutor for executing web actions and managing automation workflows
 */
// @Singleton
class ActionExecutorImpl /* @Inject */ constructor(
    private val webEngine: WebEngine
) : ActionExecutor {
    
    override suspend fun executeAction(action: WebAction): ActionResult {
        val startTime = System.currentTimeMillis()
        
        return try {
            when (action) {
                is WebAction.Click -> executeClickAction(action, startTime)
                is WebAction.Type -> executeTypeAction(action, startTime)
                is WebAction.Navigate -> executeNavigateAction(action, startTime)
                is WebAction.Scroll -> executeScrollAction(action, startTime)
                is WebAction.Upload -> executeUploadAction(action, startTime)
                is WebAction.Select -> executeSelectAction(action, startTime)
                is WebAction.Wait -> executeWaitAction(action, startTime)
                is WebAction.ExtractData -> executeExtractDataAction(action, startTime)
                is WebAction.ExecuteScript -> executeScriptAction(action, startTime)
                is WebAction.TakeScreenshot -> executeScreenshotAction(action, startTime)
            }
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Action execution failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    override suspend fun executeSequence(actions: List<WebAction>): SequenceResult {
        val startTime = System.currentTimeMillis()
        val results = mutableListOf<ActionResult>()
        var failedAt: Int? = null
        
        actions.forEachIndexed { index, action ->
            val result = executeAction(action)
            results.add(result)
            
            if (!result.success && failedAt == null) {
                failedAt = index
                return@forEachIndexed
            }
            
            delay(100)
        }
        
        return SequenceResult(
            overallSuccess = failedAt == null,
            completedActions = results.count { it.success },
            totalActions = actions.size,
            results = results,
            totalExecutionTime = System.currentTimeMillis() - startTime,
            failedAt = failedAt
        )
    }
    
    override suspend fun waitForCondition(condition: WebCondition, timeout: Duration): Boolean {
        val startTime = System.currentTimeMillis()
        val timeoutMs = timeout.toMillis()
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (checkCondition(condition)) {
                return true
            }
            delay(500)
        }
        
        return false
    }
    
    override suspend fun captureState(): WebState {
        val url = webEngine.getCurrentUrl()
        val title = webEngine.getPageTitle()
        
        return WebState(
            url = url,
            title = title,
            readyState = "complete",
            forms = emptyList(),
            links = emptyList(),
            buttons = emptyList(),
            inputs = emptyList()
        )
    }

    override suspend fun pause(duration: Duration) {
        delay(duration.toMillis())
    }
    
    override suspend fun retryAction(action: WebAction, maxAttempts: Int): ActionResult {
        var lastResult: ActionResult? = null
        
        repeat(maxAttempts) { attempt ->
            lastResult = executeAction(action)
            
            if (lastResult!!.success) {
                return lastResult!!
            }
            
            if (attempt < maxAttempts - 1) {
                val waitTime = (1000 * (attempt + 1)).toLong()
                delay(waitTime)
            }
        }
        
        return lastResult ?: ActionResult(
            success = false,
            error = "All retry attempts failed",
            executionTime = 0
        )
    }
    
    // Private helper methods
    
    private suspend fun executeClickAction(action: WebAction.Click, startTime: Long): ActionResult {
        return try {
            val element = webEngine.findElement(action.selector)
            if (element == null) {
                return ActionResult(
                    success = false,
                    error = "Element not found: ${action.selector}",
                    executionTime = System.currentTimeMillis() - startTime
                )
            }
            
            element.click()
            
            ActionResult(
                success = true,
                data = "Element clicked successfully",
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Click failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeTypeAction(action: WebAction.Type, startTime: Long): ActionResult {
        return try {
            val element = webEngine.findElement(action.selector)
            if (element == null) {
                return ActionResult(
                    success = false,
                    error = "Element not found: ${action.selector}",
                    executionTime = System.currentTimeMillis() - startTime
                )
            }
            
            if (action.clear) {
                element.clear()
            }
            
            element.type(action.text)
            
            ActionResult(
                success = true,
                data = action.text,
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Type failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeNavigateAction(action: WebAction.Navigate, startTime: Long): ActionResult {
        return try {
            val result = webEngine.navigateTo(action.url)
            
            ActionResult(
                success = result.success,
                data = result.finalUrl,
                error = result.error,
                executionTime = result.loadTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Navigation failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeScrollAction(action: WebAction.Scroll, startTime: Long): ActionResult {
        return try {
            val scrollScript = "window.scrollBy(0, ${action.amount});"
            webEngine.executeScript(scrollScript)
            
            ActionResult(
                success = true,
                data = "Scrolled ${action.direction} by ${action.amount}",
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Scroll failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeUploadAction(action: WebAction.Upload, startTime: Long): ActionResult {
        return try {
            val element = webEngine.findElement(action.selector)
            if (element == null) {
                return ActionResult(
                    success = false,
                    error = "Upload element not found: ${action.selector}",
                    executionTime = System.currentTimeMillis() - startTime
                )
            }
            
            // For file upload, we would typically use element.sendKeys(filePath) in Selenium
            // This is a placeholder implementation
            val script = """
                var element = document.querySelector('${action.selector}');
                if (element && element.type === 'file') {
                    // File upload simulation - in real implementation would handle file selection
                    element.dispatchEvent(new Event('change', { bubbles: true }));
                }
            """.trimIndent()
            
            webEngine.executeScript(script)
            
            ActionResult(
                success = true,
                data = "File upload initiated: ${action.filePath}",
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Upload failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeSelectAction(action: WebAction.Select, startTime: Long): ActionResult {
        return try {
            val script = """
                var element = document.querySelector('${action.selector}');
                if (element && element.tagName === 'SELECT') {
                    element.value = '${action.option}';
                    element.dispatchEvent(new Event('change', { bubbles: true }));
                }
            """.trimIndent()
            
            webEngine.executeScript(script)
            
            ActionResult(
                success = true,
                data = "Selected option: ${action.option}",
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Select failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeWaitAction(action: WebAction.Wait, startTime: Long): ActionResult {
        return try {
            val webCondition = convertToWebCondition(action.condition)
            val success = waitForCondition(webCondition, Duration.ofMillis(action.timeout))
            
            ActionResult(
                success = success,
                data = if (success) "Condition met" else "Condition timeout",
                error = if (!success) "Wait condition not met within timeout" else null,
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Wait failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeExtractDataAction(action: WebAction.ExtractData, startTime: Long): ActionResult {
        return try {
            val extractedData = mutableMapOf<String, String>()
            
            action.selectors.forEach { (key, selector) ->
                val element = webEngine.findElement(selector)
                extractedData[key] = element?.getText() ?: ""
            }
            
            ActionResult(
                success = true,
                data = extractedData,
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Data extraction failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeScriptAction(action: WebAction.ExecuteScript, startTime: Long): ActionResult {
        return try {
            val result = webEngine.executeScript(action.script)
            
            ActionResult(
                success = true,
                data = if (action.returnValue) result else "Script executed",
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Script execution failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun executeScreenshotAction(action: WebAction.TakeScreenshot, startTime: Long): ActionResult {
        return try {
            val screenshot = webEngine.captureScreenshot()
            
            ActionResult(
                success = true,
                data = "Screenshot captured",
                screenshot = screenshot,
                executionTime = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            ActionResult(
                success = false,
                error = "Screenshot failed: ${e.message}",
                executionTime = System.currentTimeMillis() - startTime
            )
        }
    }
    
    private suspend fun checkCondition(condition: WebCondition): Boolean {
        return try {
            when (condition) {
                is WebCondition.ElementExists -> {
                    webEngine.findElement(condition.selector) != null
                }
                is WebCondition.ElementVisible -> {
                    val element = webEngine.findElement(condition.selector)
                    element?.isVisible() == true
                }
                is WebCondition.ElementClickable -> {
                    val element = webEngine.findElement(condition.selector)
                    element?.isVisible() == true && element.isEnabled()
                }
                is WebCondition.TextPresent -> {
                    val pageContent = webEngine.getPageContent()
                    pageContent.contains(condition.text, ignoreCase = true)
                }
                is WebCondition.UrlContains -> {
                    val currentUrl = webEngine.getCurrentUrl()
                    currentUrl.contains(condition.urlPart, ignoreCase = true)
                }
                is WebCondition.PageLoaded -> {
                    val script = "document.readyState === 'complete'"
                    webEngine.executeScript(script) == "true"
                }
                is WebCondition.CustomScript -> {
                    val result = webEngine.executeScript(condition.script)
                    result == condition.expectedResult
                }
            }
        } catch (e: Exception) {
            false
        }
    }
    
    private fun convertToWebCondition(condition: Condition): WebCondition {
        return when (condition.type) {
            ConditionType.ELEMENT_EXISTS -> WebCondition.ElementExists(condition.selector ?: "body")
            ConditionType.ELEMENT_VISIBLE -> WebCondition.ElementVisible(condition.selector ?: "body")
            ConditionType.ELEMENT_CLICKABLE -> WebCondition.ElementClickable(condition.selector ?: "body")
            ConditionType.PAGE_LOADED -> WebCondition.PageLoaded()
            ConditionType.URL_CONTAINS -> WebCondition.UrlContains(condition.expectedValue ?: "")
            ConditionType.TEXT_PRESENT -> WebCondition.TextPresent(condition.expectedValue ?: "")
            ConditionType.FORM_FIELD_FILLED -> WebCondition.CustomScript(
                "document.querySelector('${condition.selector}').value !== ''",
                true
            )
        }
    }
}