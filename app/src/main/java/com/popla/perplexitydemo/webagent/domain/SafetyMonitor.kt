package com.popla.perplexitydemo.webagent.domain

/**
 * Interface for safety monitoring and security compliance
 */
interface SafetyMonitor {
    /**
     * Check if a URL is safe to visit
     */
    suspend fun checkUrlSafety(url: String): SafetyResult
    
    /**
     * Monitor for malicious content on current page
     */
    suspend fun scanForMaliciousContent(): SecurityScanResult
    
    /**
     * Validate form security before filling
     */
    suspend fun validateFormSecurity(formSelector: String): FormSecurityResult
    
    /**
     * Check for SSL/TLS security
     */
    suspend fun checkSSLSecurity(): SSLSecurityResult
    
    /**
     * Monitor for suspicious behavior
     */
    suspend fun monitorSuspiciousActivity(): List<SecurityAlert>
    
    /**
     * Validate data privacy compliance
     */
    suspend fun checkPrivacyCompliance(): PrivacyComplianceResult
    
    /**
     * Get security recommendations
     */
    suspend fun getSecurityRecommendations(): List<SecurityRecommendation>
}

/**
 * Result of URL safety check
 */
data class SafetyResult(
    val url: String,
    val isSafe: Boolean,
    val riskLevel: RiskLevel,
    val threats: List<ThreatType>,
    val reasons: List<String>,
    val recommendations: List<String>
)

/**
 * Risk levels
 */
enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Types of threats
 */
enum class ThreatType {
    MALWARE,
    PHISHING,
    SUSPICIOUS_REDIRECT,
    UNTRUSTED_CERTIFICATE,
    MIXED_CONTENT,
    SUSPICIOUS_SCRIPTS,
    DATA_HARVESTING,
    CLICKJACKING,
    XSS_VULNERABILITY
}

/**
 * Security scan result
 */
data class SecurityScanResult(
    val scanTime: Long,
    val threatsDetected: List<SecurityThreat>,
    val overallRisk: RiskLevel,
    val recommendations: List<String>
)

/**
 * Security threat details
 */
data class SecurityThreat(
    val type: ThreatType,
    val severity: RiskLevel,
    val description: String,
    val location: String?, // CSS selector or URL
    val evidence: String?,
    val mitigation: String?
)

/**
 * Form security validation result
 */
data class FormSecurityResult(
    val formSelector: String,
    val isSecure: Boolean,
    val securityIssues: List<FormSecurityIssue>,
    val recommendations: List<String>
)

/**
 * Form security issues
 */
data class FormSecurityIssue(
    val type: FormSecurityIssueType,
    val severity: RiskLevel,
    val description: String,
    val fieldSelector: String?
)

/**
 * Types of form security issues
 */
enum class FormSecurityIssueType {
    NO_HTTPS,
    NO_CSRF_PROTECTION,
    SENSITIVE_DATA_UNENCRYPTED,
    WEAK_VALIDATION,
    AUTOCOMPLETE_ENABLED,
    NO_INPUT_SANITIZATION,
    SUSPICIOUS_ACTION_URL
}

/**
 * SSL/TLS security result
 */
data class SSLSecurityResult(
    val hasSSL: Boolean,
    val certificateValid: Boolean,
    val certificateIssuer: String?,
    val certificateExpiry: Long?,
    val tlsVersion: String?,
    val cipherSuite: String?,
    val securityIssues: List<SSLIssue>
)

/**
 * SSL security issues
 */
data class SSLIssue(
    val type: SSLIssueType,
    val severity: RiskLevel,
    val description: String
)

/**
 * Types of SSL issues
 */
enum class SSLIssueType {
    EXPIRED_CERTIFICATE,
    SELF_SIGNED_CERTIFICATE,
    WEAK_CIPHER,
    OUTDATED_TLS_VERSION,
    CERTIFICATE_MISMATCH,
    REVOKED_CERTIFICATE
}

/**
 * Security alert
 */
data class SecurityAlert(
    val id: String,
    val timestamp: Long,
    val type: AlertType,
    val severity: RiskLevel,
    val title: String,
    val description: String,
    val url: String?,
    val evidence: Map<String, Any> = emptyMap(),
    val actionRequired: Boolean = false
)

/**
 * Types of security alerts
 */
enum class AlertType {
    SUSPICIOUS_REDIRECT,
    UNUSUAL_NETWORK_ACTIVITY,
    POTENTIAL_DATA_BREACH,
    MALICIOUS_SCRIPT_DETECTED,
    UNAUTHORIZED_ACCESS_ATTEMPT,
    PRIVACY_VIOLATION,
    COMPLIANCE_VIOLATION
}

/**
 * Privacy compliance result
 */
data class PrivacyComplianceResult(
    val isCompliant: Boolean,
    val regulations: List<PrivacyRegulation>,
    val violations: List<PrivacyViolation>,
    val recommendations: List<String>
)

/**
 * Privacy regulations
 */
enum class PrivacyRegulation {
    GDPR,
    CCPA,
    COPPA,
    PIPEDA,
    LGPD
}

/**
 * Privacy violation
 */
data class PrivacyViolation(
    val regulation: PrivacyRegulation,
    val violationType: PrivacyViolationType,
    val description: String,
    val severity: RiskLevel,
    val evidence: String?
)

/**
 * Types of privacy violations
 */
enum class PrivacyViolationType {
    NO_PRIVACY_POLICY,
    NO_CONSENT_MECHANISM,
    EXCESSIVE_DATA_COLLECTION,
    NO_DATA_DELETION_OPTION,
    UNCLEAR_DATA_USAGE,
    THIRD_PARTY_SHARING_UNDISCLOSED,
    COOKIES_WITHOUT_CONSENT
}

/**
 * Security recommendation
 */
data class SecurityRecommendation(
    val id: String,
    val priority: RecommendationPriority,
    val category: RecommendationCategory,
    val title: String,
    val description: String,
    val actionSteps: List<String>,
    val impact: String
)

/**
 * Recommendation priorities
 */
enum class RecommendationPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Recommendation categories
 */
enum class RecommendationCategory {
    AUTHENTICATION,
    DATA_PROTECTION,
    NETWORK_SECURITY,
    PRIVACY_COMPLIANCE,
    MALWARE_PROTECTION,
    SECURE_COMMUNICATION
}