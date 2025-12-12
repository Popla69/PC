package com.popla.perplexitydemo.webagent.domain

/**
 * Interface for detecting and analyzing forms on web pages
 */
interface FormDetector {
    /**
     * Detect all forms on the current page
     */
    suspend fun detectForms(): List<DetectedFormInfo>
    
    /**
     * Analyze a specific form for field types and requirements
     */
    suspend fun analyzeForm(formSelector: String): FormAnalysis
    
    /**
     * Get intelligent field mappings for user data
     */
    suspend fun getFieldMappings(form: DetectedFormInfo, userData: Map<String, Any>): Map<String, String>
    
    /**
     * Detect form field types automatically
     */
    suspend fun detectFieldTypes(formSelector: String): List<FormFieldInfo>
}

/**
 * Detected form information
 */
data class DetectedFormInfo(
    val selector: String,
    val action: String?,
    val method: String,
    val fields: List<FormFieldInfo>,
    val submitButton: String?,
    val formType: FormType,
    val confidence: Double
)

/**
 * Form field information
 */
data class FormFieldInfo(
    val selector: String,
    val name: String?,
    val id: String?,
    val type: String,
    val label: String?,
    val placeholder: String?,
    val required: Boolean,
    val options: List<String> = emptyList(),
    val fieldType: FieldType,
    val confidence: Double
)

/**
 * Types of form fields
 */
enum class FieldType {
    FIRST_NAME,
    LAST_NAME,
    FULL_NAME,
    EMAIL,
    PHONE,
    ADDRESS,
    CITY,
    STATE,
    ZIP_CODE,
    COUNTRY,
    DATE_OF_BIRTH,
    GENDER,
    COMPANY,
    JOB_TITLE,
    WEBSITE,
    PASSWORD,
    CONFIRM_PASSWORD,
    CREDIT_CARD,
    EXPIRY_DATE,
    CVV,
    UNKNOWN
}

/**
 * Types of forms
 */
enum class FormType {
    CONTACT,
    REGISTRATION,
    LOGIN,
    CHECKOUT,
    SURVEY,
    APPLICATION,
    SEARCH,
    NEWSLETTER,
    UNKNOWN
}

/**
 * Form analysis result
 */
data class FormAnalysis(
    val formSelector: String,
    val formType: FormType,
    val fields: List<FormFieldInfo>,
    val requiredFields: List<String>,
    val optionalFields: List<String>,
    val estimatedCompletionTime: Int, // in seconds
    val complexity: FormComplexity
)

/**
 * Form complexity levels
 */
enum class FormComplexity {
    SIMPLE,    // 1-3 fields
    MEDIUM,    // 4-8 fields
    COMPLEX,   // 9+ fields
    MULTI_PAGE // Multiple pages/steps
}