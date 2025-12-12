package com.popla.perplexitydemo.webagent.domain

/**
 * Interface for automated form filling with user data matching
 */
interface FormFiller {
    /**
     * Fill a form with user data
     */
    suspend fun fillForm(
        formSelector: String,
        userData: UserData,
        fillStrategy: FillStrategy = FillStrategy.SMART_MATCH
    ): FormFillResult
    
    /**
     * Analyze a form and suggest data mappings
     */
    suspend fun analyzeForm(formSelector: String): FormAnalysis
    
    /**
     * Fill specific fields with provided data
     */
    suspend fun fillFields(fieldMappings: Map<String, String>): FormFillResult
    
    /**
     * Auto-detect and fill common form types
     */
    suspend fun autoFillCommonForms(userData: UserData): List<FormFillResult>
    
    /**
     * Validate form data before submission
     */
    suspend fun validateForm(formSelector: String): FormValidationResult
}

/**
 * User data for form filling
 */
data class UserData(
    val personalInfo: PersonalInfo? = null,
    val contactInfo: ContactInfo? = null,
    val addressInfo: AddressInfo? = null,
    val paymentInfo: PaymentInfo? = null,
    val customFields: Map<String, String> = emptyMap()
)

/**
 * Personal information
 */
data class PersonalInfo(
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val title: String? = null
)

/**
 * Contact information
 */
data class ContactInfo(
    val email: String? = null,
    val phone: String? = null,
    val alternatePhone: String? = null,
    val website: String? = null,
    val socialMedia: Map<String, String> = emptyMap()
)

/**
 * Address information
 */
data class AddressInfo(
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null,
    val addressLine2: String? = null
)

/**
 * Payment information (encrypted/tokenized in real implementation)
 */
data class PaymentInfo(
    val cardNumber: String? = null,
    val expiryMonth: String? = null,
    val expiryYear: String? = null,
    val cvv: String? = null,
    val cardholderName: String? = null,
    val billingAddress: AddressInfo? = null
)

/**
 * Form filling strategies
 */
enum class FillStrategy {
    EXACT_MATCH,      // Only fill fields with exact name matches
    SMART_MATCH,      // Use intelligent matching based on field attributes
    FUZZY_MATCH,      // Use fuzzy matching for field names
    AI_ASSISTED       // Use AI to determine best field mappings
}

/**
 * Result of form filling operation
 */
data class FormFillResult(
    val success: Boolean,
    val formSelector: String,
    val fieldsAttempted: Int,
    val fieldsSuccessful: Int,
    val fieldResults: List<FieldFillResult>,
    val errors: List<String> = emptyList(),
    val suggestions: List<String> = emptyList()
)

/**
 * Result for individual field filling
 */
data class FieldFillResult(
    val fieldSelector: String,
    val fieldName: String?,
    val fieldType: String,
    val success: Boolean,
    val value: String?,
    val error: String? = null,
    val confidence: Double = 1.0
)

/**
 * Form validation result
 */
data class FormValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>,
    val warnings: List<ValidationWarning>
)

/**
 * Validation error
 */
data class ValidationError(
    val fieldSelector: String,
    val fieldName: String?,
    val errorType: ValidationErrorType,
    val message: String
)

/**
 * Validation warning
 */
data class ValidationWarning(
    val fieldSelector: String,
    val fieldName: String?,
    val warningType: ValidationWarningType,
    val message: String
)

/**
 * Types of validation errors
 */
enum class ValidationErrorType {
    REQUIRED_FIELD_EMPTY,
    INVALID_FORMAT,
    VALUE_TOO_LONG,
    VALUE_TOO_SHORT,
    INVALID_OPTION,
    PATTERN_MISMATCH
}

/**
 * Types of validation warnings
 */
enum class ValidationWarningType {
    UNUSUAL_VALUE,
    POTENTIAL_TYPO,
    INCOMPLETE_DATA,
    SECURITY_CONCERN
}