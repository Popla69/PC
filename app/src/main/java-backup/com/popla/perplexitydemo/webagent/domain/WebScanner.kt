package com.popla.perplexitydemo.webagent.domain

import kotlinx.coroutines.flow.Flow
import java.time.Duration

/**
 * Interface for real-time web scanning and monitoring capabilities
 */
interface WebScanner {
    /**
     * Start monitoring a web page for changes
     */
    suspend fun startMonitoring(
        url: String,
        config: MonitoringConfig = MonitoringConfig()
    ): Flow<ChangeDetectionResult>
    
    /**
     * Stop monitoring a specific URL
     */
    suspend fun stopMonitoring(url: String)
    
    /**
     * Get current monitoring status
     */
    suspend fun getMonitoringStatus(): List<MonitoringSession>
    
    /**
     * Scan a page for specific content
     */
    suspend fun scanPage(url: String, config: ScanConfig): ScanResult
    
    /**
     * Extract structured data from a page
     */
    suspend fun extractStructuredData(url: String, selectors: List<String>): StructuredDataResult
}

/**
 * Configuration for monitoring sessions
 */
data class MonitoringConfig(
    val scanInterval: Duration = Duration.ofSeconds(30),
    val changeThreshold: Double = 0.1,
    val monitorSelectors: List<String> = emptyList(),
    val ignoreSelectors: List<String> = emptyList(),
    val alertOnChange: Boolean = true,
    val maxRetries: Int = 3,
    val timeout: Duration = Duration.ofSeconds(30)
)

/**
 * Configuration for page scanning
 */
data class ScanConfig(
    val selectors: List<String> = emptyList(),
    val contentPatterns: List<String> = emptyList(),
    val includeImages: Boolean = false,
    val includeScripts: Boolean = false,
    val maxDepth: Int = 1,
    val timeout: Duration = Duration.ofSeconds(30)
)

/**
 * Result of change detection
 */
data class ChangeDetectionResult(
    val url: String,
    val timestamp: Long,
    val changes: List<Change>,
    val alerts: List<Alert>,
    val confidence: Double
)

/**
 * Detected change in web content
 */
data class Change(
    val id: String,
    val type: ChangeType,
    val selector: String,
    val oldValue: String?,
    val newValue: String?,
    val timestamp: Long,
    val confidence: Double,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Types of changes that can be detected
 */
enum class ChangeType {
    CONTENT_CHANGED,
    ELEMENT_ADDED,
    ELEMENT_REMOVED,
    ATTRIBUTE_CHANGED,
    STYLE_CHANGED,
    STRUCTURE_CHANGED,
    PRICE_CHANGED,
    AVAILABILITY_CHANGED
}

/**
 * Alert generated from monitoring
 */
data class Alert(
    val id: String,
    val type: ScannerAlertType,
    val severity: AlertSeverity,
    val title: String,
    val description: String,
    val url: String,
    val timestamp: Long,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Types of alerts
 */
enum class ScannerAlertType {
    CONTENT_CHANGE,
    PRICE_DROP,
    AVAILABILITY_CHANGE,
    NEW_CONTENT,
    ERROR_DETECTED,
    THRESHOLD_EXCEEDED
}

/**
 * Alert severity levels
 */
enum class AlertSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Active monitoring session
 */
data class MonitoringSession(
    val id: String,
    val url: String,
    val config: MonitoringConfig,
    val startTime: Long,
    val lastScanTime: Long?,
    val isActive: Boolean,
    val totalScans: Int,
    val changesDetected: Int,
    val alertsGenerated: Int
)

/**
 * Result of page scanning
 */
data class ScanResult(
    val url: String,
    val timestamp: Long,
    val success: Boolean,
    val elements: List<ScannedElement>,
    val performance: ScanPerformance,
    val error: String? = null
)

/**
 * Information about a scanned element
 */
data class ScannedElement(
    val selector: String,
    val tagName: String,
    val text: String?,
    val attributes: Map<String, String>,
    val isVisible: Boolean,
    val boundingBox: ElementBounds?
)

/**
 * Element positioning information
 */
data class ElementBounds(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

/**
 * Performance metrics for scanning
 */
data class ScanPerformance(
    val scanDuration: Long,
    val elementsScanned: Int,
    val changesDetected: Int,
    val memoryUsage: Long
)

/**
 * Result of structured data extraction
 */
data class StructuredDataResult(
    val url: String,
    val timestamp: Long,
    val success: Boolean,
    val data: Map<String, Any>,
    val schema: DataSchema?,
    val confidence: Double
)

/**
 * Schema for structured data
 */
data class DataSchema(
    val type: String,
    val fields: List<SchemaField>,
    val version: String = "1.0"
)

/**
 * Field in data schema
 */
data class SchemaField(
    val name: String,
    val type: String,
    val selector: String,
    val required: Boolean = false,
    val defaultValue: Any? = null
)