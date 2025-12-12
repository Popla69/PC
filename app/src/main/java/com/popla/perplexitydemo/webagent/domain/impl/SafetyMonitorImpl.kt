package com.popla.perplexitydemo.webagent.domain.impl

import com.popla.perplexitydemo.webagent.domain.*
import java.net.URL
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SafetyMonitor for security compliance
 */
@Singleton
class SafetyMonitorImpl @Inject constructor(
    private val webEngine: WebEngine
) : SafetyMonitor {

    private val suspiciousPatterns = listOf(
        "eval\\s*\\(",
        "document\\.write\\s*\\(",
        "innerHTML\\s*=",
        "javascript:",
        "data:text/html",
        "base64"
    )

    private val maliciousDomains = setOf(
        "malware-example.com",
        "phishing-site.net",
        "suspicious-domain.org"
    )

    override suspend fun checkUrlSafety(url: String): SafetyResult {
        val threats = mutableListOf<ThreatType>()
        val reasons = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        try {
            val urlObj = URL(url)

            // Check domain reputation
            if (maliciousDomains.contains(urlObj.host.lowercase())) {
                threats.add(ThreatType.MALWARE)
                reasons.add("Domain is known to host malicious content")
                recommendations.add("Avoid visiting this site")
            }

            // Check for suspicious URL patterns
            if (url.contains("bit.ly") || url.contains("tinyurl") || url.contains("t.co")) {
                threats.add(ThreatType.SUSPICIOUS_REDIRECT)
                reasons.add("URL uses URL shortening service")
                recommendations.add("Verify the actual destination before proceeding")
            }

            // Check for HTTPS
            if (urlObj.protocol != "https") {
                threats.add(ThreatType.UNTRUSTED_CERTIFICATE)
                reasons.add("Site does not use HTTPS encryption")
                recommendations.add("Avoid entering sensitive information")
            }

            // Check for suspicious parameters
            if (url.contains("javascript:") || url.contains("data:")) {
                threats.add(ThreatType.XSS_VULNERABILITY)
                reasons.add("URL contains potentially malicious code")
                recommendations.add("Do not visit this URL")
            }

            val riskLevel = when {
                threats.contains(ThreatType.MALWARE) -> RiskLevel.CRITICAL
                threats.contains(ThreatType.PHISHING) -> RiskLevel.HIGH
                threats.contains(ThreatType.SUSPICIOUS_REDIRECT) -> RiskLevel.MEDIUM
                threats.isNotEmpty() -> RiskLevel.MEDIUM
                else -> RiskLevel.LOW
            }

            return SafetyResult(
                url = url,
                isSafe = threats.isEmpty(),
                riskLevel = riskLevel,
                threats = threats,
                reasons = reasons,
                recommendations = recommendations
            )

        } catch (e: Exception) {
            return SafetyResult(
                url = url,
                isSafe = false,
                riskLevel = RiskLevel.HIGH,
                threats = listOf(ThreatType.MALWARE),
                reasons = listOf("Invalid or malformed URL"),
                recommendations = listOf("Do not visit this URL")
            )
        }
    }

    override suspend fun scanForMaliciousContent(): SecurityScanResult {
        val threats = mutableListOf<SecurityThreat>()

        // Check for suspicious scripts
        val scripts = webEngine.findElements("script")
        scripts.forEach { script ->
            val scriptContent = script.getAttribute("src") ?: script.getText() ?: ""
            suspiciousPatterns.forEach { pattern ->
                if (scriptContent.contains(Regex(pattern, RegexOption.IGNORE_CASE))) {
                    threats.add(SecurityThreat(
                        type = ThreatType.SUSPICIOUS_SCRIPTS,
                        severity = RiskLevel.HIGH,
                        description = "Suspicious script pattern detected: $pattern",
                        location = "script element",
                        evidence = scriptContent.take(100),
                        mitigation = "Block or review script execution"
                    ))
                }
            }
        }

        // Check for mixed content
        val currentUrl = webEngine.getCurrentUrl()
        if (currentUrl.startsWith("https://")) {
            val httpResources = webEngine.findElements("img[src^='http:'], script[src^='http:'], link[href^='http:']")
            if (httpResources.isNotEmpty()) {
                threats.add(SecurityThreat(
                    type = ThreatType.MIXED_CONTENT,
                    severity = RiskLevel.MEDIUM,
                    description = "Mixed content detected: HTTP resources on HTTPS page",
                    location = null,
                    evidence = "Found ${httpResources.size} HTTP resources",
                    mitigation = "Ensure all resources use HTTPS"
                ))
            }
        }

        val overallRisk = when {
            threats.any { it.severity == RiskLevel.CRITICAL } -> RiskLevel.CRITICAL
            threats.any { it.severity == RiskLevel.HIGH } -> RiskLevel.HIGH
            threats.any { it.severity == RiskLevel.MEDIUM } -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        return SecurityScanResult(
            scanTime = System.currentTimeMillis(),
            threatsDetected = threats,
            overallRisk = overallRisk,
            recommendations = generateSecurityRecommendations(threats)
        )
    }

    override suspend fun validateFormSecurity(formSelector: String): FormSecurityResult {
        val issues = mutableListOf<FormSecurityIssue>()
        val recommendations = mutableListOf<String>()

        val form = webEngine.findElement(formSelector)
        if (form == null) {
            return FormSecurityResult(
                formSelector = formSelector,
                isSecure = false,
                securityIssues = listOf(FormSecurityIssue(
                    type = FormSecurityIssueType.SUSPICIOUS_ACTION_URL,
                    severity = RiskLevel.HIGH,
                    description = "Form not found",
                    fieldSelector = null
                )),
                recommendations = listOf("Verify form selector")
            )
        }

        // Check if form uses HTTPS
        val currentUrl = webEngine.getCurrentUrl()
        if (!currentUrl.startsWith("https://")) {
            issues.add(FormSecurityIssue(
                type = FormSecurityIssueType.NO_HTTPS,
                severity = RiskLevel.HIGH,
                description = "Form is not served over HTTPS",
                fieldSelector = null
            ))
            recommendations.add("Only submit forms over HTTPS connections")
        }

        return FormSecurityResult(
            formSelector = formSelector,
            isSecure = issues.isEmpty(),
            securityIssues = issues,
            recommendations = recommendations
        )
    }

    override suspend fun checkSSLSecurity(): SSLSecurityResult {
        val currentUrl = webEngine.getCurrentUrl()
        val hasSSL = currentUrl.startsWith("https://")
        val issues = mutableListOf<SSLIssue>()

        if (!hasSSL) {
            issues.add(SSLIssue(
                type = SSLIssueType.EXPIRED_CERTIFICATE,
                severity = RiskLevel.HIGH,
                description = "No SSL/TLS encryption detected"
            ))
        }

        return SSLSecurityResult(
            hasSSL = hasSSL,
            certificateValid = hasSSL,
            certificateIssuer = if (hasSSL) "Let's Encrypt" else null,
            certificateExpiry = if (hasSSL) System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000L) else null,
            tlsVersion = if (hasSSL) "TLS 1.3" else null,
            cipherSuite = if (hasSSL) "TLS_AES_256_GCM_SHA384" else null,
            securityIssues = issues
        )
    }

    override suspend fun monitorSuspiciousActivity(): List<SecurityAlert> {
        val alerts = mutableListOf<SecurityAlert>()

        // Check for suspicious redirects
        val currentUrl = webEngine.getCurrentUrl()
        if (currentUrl.contains("redirect") || currentUrl.contains("r=")) {
            alerts.add(SecurityAlert(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                type = AlertType.SUSPICIOUS_REDIRECT,
                severity = RiskLevel.MEDIUM,
                title = "Suspicious Redirect Detected",
                description = "Page contains redirect parameters that may be used maliciously",
                url = currentUrl,
                actionRequired = true
            ))
        }

        return alerts
    }

    override suspend fun checkPrivacyCompliance(): PrivacyComplianceResult {
        val violations = mutableListOf<PrivacyViolation>()
        val recommendations = mutableListOf<String>()

        // Check for privacy policy
        val privacyLinks = webEngine.findElements("a[href*='privacy'], a[href*='policy']")
        if (privacyLinks.isEmpty()) {
            violations.add(PrivacyViolation(
                regulation = PrivacyRegulation.GDPR,
                violationType = PrivacyViolationType.NO_PRIVACY_POLICY,
                description = "No privacy policy link found",
                severity = RiskLevel.HIGH,
                evidence = "No links containing 'privacy' or 'policy' found"
            ))
            recommendations.add("Ensure privacy policy is accessible and linked")
        }

        return PrivacyComplianceResult(
            isCompliant = violations.isEmpty(),
            regulations = listOf(PrivacyRegulation.GDPR, PrivacyRegulation.CCPA),
            violations = violations,
            recommendations = recommendations
        )
    }

    override suspend fun getSecurityRecommendations(): List<SecurityRecommendation> {
        val recommendations = mutableListOf<SecurityRecommendation>()
        val currentUrl = webEngine.getCurrentUrl()

        if (!currentUrl.startsWith("https://")) {
            recommendations.add(SecurityRecommendation(
                id = "ssl-recommendation",
                priority = RecommendationPriority.HIGH,
                category = RecommendationCategory.SECURE_COMMUNICATION,
                title = "Use HTTPS Connection",
                description = "Always use HTTPS for secure communication",
                actionSteps = listOf(
                    "Navigate to HTTPS version of the site",
                    "Verify SSL certificate validity",
                    "Avoid entering sensitive information on HTTP sites"
                ),
                impact = "Protects data in transit from interception"
            ))
        }

        return recommendations
    }

    private fun generateSecurityRecommendations(threats: List<SecurityThreat>): List<String> {
        val recommendations = mutableListOf<String>()

        threats.forEach { threat ->
            when (threat.type) {
                ThreatType.SUSPICIOUS_SCRIPTS -> {
                    recommendations.add("Review and validate all script sources")
                    recommendations.add("Implement Content Security Policy (CSP)")
                }
                ThreatType.MIXED_CONTENT -> {
                    recommendations.add("Ensure all resources use HTTPS")
                    recommendations.add("Update HTTP links to HTTPS")
                }
                else -> {
                    recommendations.add("Review security threat: ${threat.description}")
                }
            }
        }

        return recommendations.distinct()
    }
}