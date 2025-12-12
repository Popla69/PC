package com.popla.perplexitydemo.webagent.domain.impl

import com.popla.perplexitydemo.webagent.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WebScanner for real-time web monitoring
 */
@Singleton
class WebScannerImpl @Inject constructor(
    private val webEngine: WebEngine
) : WebScanner {

    private val monitoringSessions = ConcurrentHashMap<String, MonitoringJob>()
    private val scanCache = ConcurrentHashMap<String, ScanResult>()
    private val changeHistory = ConcurrentHashMap<String, MutableList<Change>>()

    override suspend fun startMonitoring(
        url: String,
        config: MonitoringConfig
    ): Flow<ChangeDetectionResult> = flow {
        val sessionId = UUID.randomUUID().toString()
        val session = MonitoringSession(
            id = sessionId,
            url = url,
            config = config,
            startTime = System.currentTimeMillis(),
            lastScanTime = null,
            isActive = true,
            totalScans = 0,
            changesDetected = 0,
            alertsGenerated = 0
        )

        val job = MonitoringJob(session, currentCoroutineContext())
        monitoringSessions[url] = job

        // Initial scan
        val initialScan = performScan(url, config.toScanConfig())
        scanCache[url] = initialScan
        changeHistory[url] = mutableListOf()

        emit(ChangeDetectionResult(
            url = url,
            timestamp = System.currentTimeMillis(),
            changes = emptyList(),
            alerts = emptyList(),
            confidence = 1.0
        ))

        // Periodic scanning
        while (currentCoroutineContext().isActive) {
            delay(config.scanInterval.toMillis())
            
            try {
                val currentScan = performScan(url, config.toScanConfig())
                val previousScan = scanCache[url]
                
                if (previousScan != null) {
                    val changes = detectChanges(previousScan, currentScan, config)
                    val alerts = generateAlerts(changes, config)
                    
                    if (changes.isNotEmpty()) {
                        changeHistory[url]?.addAll(changes)
                        
                        emit(ChangeDetectionResult(
                            url = url,
                            timestamp = System.currentTimeMillis(),
                            changes = changes,
                            alerts = alerts,
                            confidence = calculateConfidence(changes)
                        ))
                        
                        job.session = job.session.copy(
                            changesDetected = job.session.changesDetected + changes.size,
                            alertsGenerated = job.session.alertsGenerated + alerts.size
                        )
                    }
                }
                
                scanCache[url] = currentScan
                job.session = job.session.copy(
                    lastScanTime = System.currentTimeMillis(),
                    totalScans = job.session.totalScans + 1
                )
                
            } catch (e: Exception) {
                // Log error and continue monitoring
                emit(ChangeDetectionResult(
                    url = url,
                    timestamp = System.currentTimeMillis(),
                    changes = emptyList(),
                    alerts = listOf(Alert(
                        id = UUID.randomUUID().toString(),
                        type = ScannerAlertType.ERROR_DETECTED,
                        severity = AlertSeverity.MEDIUM,
                        title = "Monitoring Error",
                        description = "Error during monitoring: ${e.message}",
                        url = url,
                        timestamp = System.currentTimeMillis()
                    )),
                    confidence = 0.0
                ))
            }
        }
    }

    override suspend fun stopMonitoring(url: String) {
        monitoringSessions[url]?.let { job ->
            job.cancel()
            monitoringSessions.remove(url)
            scanCache.remove(url)
            changeHistory.remove(url)
        }
    }

    override suspend fun getMonitoringStatus(): List<MonitoringSession> {
        return monitoringSessions.values.map { it.session }
    }

    override suspend fun scanPage(url: String, config: ScanConfig): ScanResult {
        return performScan(url, config)
    }

    override suspend fun extractStructuredData(
        url: String, 
        selectors: List<String>
    ): StructuredDataResult {
        return try {
            val navigationResult = webEngine.navigateTo(url)
            if (!navigationResult.success) {
                return StructuredDataResult(
                    url = url,
                    timestamp = System.currentTimeMillis(),
                    success = false,
                    data = emptyMap(),
                    schema = null,
                    confidence = 0.0
                )
            }

            val extractedData = mutableMapOf<String, Any>()
            val schemaFields = mutableListOf<SchemaField>()

            selectors.forEach { selector ->
                val elements = webEngine.findElements(selector)
                if (elements.isNotEmpty()) {
                    val values = elements.mapNotNull { it.getText() }.filter { it.isNotBlank() }
                    if (values.isNotEmpty()) {
                        extractedData[selector] = if (values.size == 1) values.first() else values
                        schemaFields.add(SchemaField(
                            name = selector.replace("[^a-zA-Z0-9]".toRegex(), "_"),
                            type = if (values.size == 1) "string" else "array",
                            selector = selector,
                            required = false
                        ))
                    }
                }
            }

            val schema = if (schemaFields.isNotEmpty()) {
                DataSchema(
                    type = "extracted_data",
                    fields = schemaFields
                )
            } else null

            StructuredDataResult(
                url = url,
                timestamp = System.currentTimeMillis(),
                success = true,
                data = extractedData,
                schema = schema,
                confidence = if (extractedData.isNotEmpty()) 0.8 else 0.0
            )

        } catch (e: Exception) {
            StructuredDataResult(
                url = url,
                timestamp = System.currentTimeMillis(),
                success = false,
                data = emptyMap(),
                schema = null,
                confidence = 0.0
            )
        }
    }

    private suspend fun performScan(url: String, config: ScanConfig): ScanResult {
        val startTime = System.currentTimeMillis()
        
        return try {
            val navigationResult = webEngine.navigateTo(url)
            if (!navigationResult.success) {
                return ScanResult(
                    url = url,
                    timestamp = System.currentTimeMillis(),
                    success = false,
                    elements = emptyList(),
                    performance = ScanPerformance(
                        scanDuration = System.currentTimeMillis() - startTime,
                        elementsScanned = 0,
                        changesDetected = 0,
                        memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                    ),
                    error = navigationResult.error
                )
            }

            val elements = mutableListOf<ScannedElement>()
            
            // Scan by specific selectors or use defaults
            val selectorsToScan = if (config.selectors.isNotEmpty()) {
                config.selectors
            } else {
                listOf("h1", "h2", "h3", "p", "a", "button", "input", "form", ".price", ".availability")
            }

            selectorsToScan.forEach { selector ->
                val foundElements = webEngine.findElements(selector)
                foundElements.forEach { element ->
                    elements.add(ScannedElement(
                        selector = selector,
                        tagName = element.getAttribute("tagName") ?: "div",
                        text = element.getText(),
                        attributes = mapOf("class" to (element.getAttribute("class") ?: "")),
                        isVisible = element.isVisible(),
                        boundingBox = ElementBounds(0, 0, 0, 0) // Simplified for demo
                    ))
                }
            }

            ScanResult(
                url = url,
                timestamp = System.currentTimeMillis(),
                success = true,
                elements = elements,
                performance = ScanPerformance(
                    scanDuration = System.currentTimeMillis() - startTime,
                    elementsScanned = elements.size,
                    changesDetected = 0,
                    memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                )
            )

        } catch (e: Exception) {
            ScanResult(
                url = url,
                timestamp = System.currentTimeMillis(),
                success = false,
                elements = emptyList(),
                performance = ScanPerformance(
                    scanDuration = System.currentTimeMillis() - startTime,
                    elementsScanned = 0,
                    changesDetected = 0,
                    memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                ),
                error = e.message
            )
        }
    }

    private fun detectChanges(
        previous: ScanResult,
        current: ScanResult,
        config: MonitoringConfig
    ): List<Change> {
        val changes = mutableListOf<Change>()
        
        // Group elements by selector for comparison
        val previousElements = previous.elements.groupBy { it.selector }
        val currentElements = current.elements.groupBy { it.selector }

        // Detect content changes
        currentElements.forEach { (selector, currentList) ->
            val previousList = previousElements[selector] ?: emptyList()
            
            // Skip if selector is in ignore list
            if (config.ignoreSelectors.contains(selector)) return@forEach
            
            // Compare element counts
            if (currentList.size != previousList.size) {
                changes.add(Change(
                    id = UUID.randomUUID().toString(),
                    type = if (currentList.size > previousList.size) ChangeType.ELEMENT_ADDED else ChangeType.ELEMENT_REMOVED,
                    selector = selector,
                    oldValue = previousList.size.toString(),
                    newValue = currentList.size.toString(),
                    timestamp = System.currentTimeMillis(),
                    confidence = 0.9
                ))
            }
            
            // Compare content
            currentList.forEachIndexed { index, currentElement ->
                val previousElement = previousList.getOrNull(index)
                if (previousElement != null && currentElement.text != previousElement.text) {
                    val changeType = when {
                        selector.contains("price", ignoreCase = true) -> ChangeType.PRICE_CHANGED
                        selector.contains("availability", ignoreCase = true) || 
                        selector.contains("stock", ignoreCase = true) -> ChangeType.AVAILABILITY_CHANGED
                        else -> ChangeType.CONTENT_CHANGED
                    }
                    
                    changes.add(Change(
                        id = UUID.randomUUID().toString(),
                        type = changeType,
                        selector = selector,
                        oldValue = previousElement.text,
                        newValue = currentElement.text,
                        timestamp = System.currentTimeMillis(),
                        confidence = calculateChangeConfidence(previousElement.text, currentElement.text)
                    ))
                }
            }
        }

        return changes
    }

    private fun generateAlerts(changes: List<Change>, config: MonitoringConfig): List<Alert> {
        val alerts = mutableListOf<Alert>()
        
        changes.forEach { change ->
            val alert = when (change.type) {
                ChangeType.PRICE_CHANGED -> {
                    val severity = if (isPriceDecrease(change.oldValue, change.newValue)) {
                        AlertSeverity.HIGH
                    } else {
                        AlertSeverity.MEDIUM
                    }
                    
                    Alert(
                        id = UUID.randomUUID().toString(),
                        type = ScannerAlertType.PRICE_DROP,
                        severity = severity,
                        title = "Price Change Detected",
                        description = "Price changed from ${change.oldValue} to ${change.newValue}",
                        url = change.selector,
                        timestamp = change.timestamp
                    )
                }
                
                ChangeType.AVAILABILITY_CHANGED -> Alert(
                    id = UUID.randomUUID().toString(),
                    type = ScannerAlertType.AVAILABILITY_CHANGE,
                    severity = AlertSeverity.HIGH,
                    title = "Availability Change",
                    description = "Availability changed from ${change.oldValue} to ${change.newValue}",
                    url = change.selector,
                    timestamp = change.timestamp
                )
                
                ChangeType.CONTENT_CHANGED -> Alert(
                    id = UUID.randomUUID().toString(),
                    type = ScannerAlertType.CONTENT_CHANGE,
                    severity = AlertSeverity.LOW,
                    title = "Content Updated",
                    description = "Content changed in ${change.selector}",
                    url = change.selector,
                    timestamp = change.timestamp
                )
                
                else -> null
            }
            
            if (alert != null && config.alertOnChange) {
                alerts.add(alert)
            }
        }
        
        return alerts
    }

    private fun calculateConfidence(changes: List<Change>): Double {
        if (changes.isEmpty()) return 1.0
        return changes.map { it.confidence }.average()
    }

    private fun calculateChangeConfidence(oldValue: String?, newValue: String?): Double {
        if (oldValue == null || newValue == null) return 0.5
        
        val similarity = calculateSimilarity(oldValue, newValue)
        return 1.0 - similarity // Higher confidence for more different values
    }

    private fun calculateSimilarity(str1: String, str2: String): Double {
        val longer = if (str1.length > str2.length) str1 else str2
        val shorter = if (str1.length > str2.length) str2 else str1
        
        if (longer.isEmpty()) return 1.0
        
        val editDistance = levenshteinDistance(longer, shorter)
        return (longer.length - editDistance) / longer.length.toDouble()
    }

    private fun levenshteinDistance(str1: String, str2: String): Int {
        val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }
        
        for (i in 0..str1.length) dp[i][0] = i
        for (j in 0..str2.length) dp[0][j] = j
        
        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return dp[str1.length][str2.length]
    }

    private fun isPriceDecrease(oldPrice: String?, newPrice: String?): Boolean {
        if (oldPrice == null || newPrice == null) return false
        
        val oldValue = extractNumericValue(oldPrice)
        val newValue = extractNumericValue(newPrice)
        
        return oldValue != null && newValue != null && newValue < oldValue
    }

    private fun extractNumericValue(priceString: String): Double? {
        val regex = Regex("""[\d,]+\.?\d*""")
        val match = regex.find(priceString.replace(",", ""))
        return match?.value?.toDoubleOrNull()
    }

    private fun MonitoringConfig.toScanConfig(): ScanConfig {
        return ScanConfig(
            selectors = this.monitorSelectors,
            timeout = this.timeout
        )
    }

    private data class MonitoringJob(
        var session: MonitoringSession,
        private val context: kotlin.coroutines.CoroutineContext
    ) {
        fun cancel() {
            context.cancel()
        }
    }
}