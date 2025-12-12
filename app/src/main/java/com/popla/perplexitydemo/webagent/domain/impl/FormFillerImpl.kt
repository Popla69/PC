package com.popla.perplexitydemo.webagent.domain.impl

import com.popla.perplexitydemo.webagent.domain.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FormFiller for automated form filling
 */
@Singleton
class FormFillerImpl @Inject constructor(
    private val webEngine: WebEngine
) : FormFiller {

    override suspend fun fillForm(
        formSelector: String,
        userData: UserData,
        fillStrategy: FillStrategy
    ): FormFillResult {
        return try {
            val analysis = analyzeForm(formSelector)
            val fieldMappings = generateFieldMappings(analysis, userData, fillStrategy)
            val fieldResults = mutableListOf<FieldFillResult>()
            var successCount = 0

            fieldMappings.forEach { (fieldSelector, value) ->
                val field = analysis.fields.find { it.selector == fieldSelector }
                val result = fillSingleField(fieldSelector, value, field)
                fieldResults.add(result)
                if (result.success) successCount++
            }

            FormFillResult(
                success = successCount > 0,
                formSelector = formSelector,
                fieldsAttempted = fieldMappings.size,
                fieldsSuccessful = successCount,
                fieldResults = fieldResults
            )
        } catch (e: Exception) {
            FormFillResult(
                success = false,
                formSelector = formSelector,
                fieldsAttempted = 0,
                fieldsSuccessful = 0,
                fieldResults = emptyList(),
                errors = listOf("Failed to fill form: ${e.message}")
            )
        }
    }

    override suspend fun analyzeForm(formSelector: String): FormAnalysis {
        val formElements = webEngine.findElements(formSelector)
        if (formElements.isEmpty()) {
            throw IllegalArgumentException("Form not found: $formSelector")
        }

        val inputElements = webEngine.findElements("$formSelector input, $formSelector select, $formSelector textarea")
        val fields = inputElements.mapIndexed { index, element ->
            FormFieldInfo(
                selector = "$formSelector input:nth-of-type(${index + 1})",
                name = element.getAttribute("name"),
                id = element.getAttribute("id"),
                type = element.getAttribute("type") ?: "text",
                label = element.getAttribute("placeholder"),
                placeholder = element.getAttribute("placeholder"),
                required = element.getAttribute("required") != null,
                options = emptyList(),
                fieldType = FieldType.UNKNOWN,
                confidence = 0.8
            )
        }

        val formType = FormType.UNKNOWN
        val complexity = when {
            fields.size <= 3 -> FormComplexity.SIMPLE
            fields.size <= 10 -> FormComplexity.MEDIUM
            else -> FormComplexity.COMPLEX
        }

        return FormAnalysis(
            formSelector = formSelector,
            formType = formType,
            fields = fields,
            requiredFields = fields.filter { it.required }.map { it.selector },
            optionalFields = fields.filter { !it.required }.map { it.selector },
            estimatedCompletionTime = 60,
            complexity = complexity
        )
    }

    override suspend fun fillFields(fieldMappings: Map<String, String>): FormFillResult {
        val fieldResults = mutableListOf<FieldFillResult>()
        var successCount = 0

        fieldMappings.forEach { (selector, value) ->
            val result = fillSingleField(selector, value, null)
            fieldResults.add(result)
            if (result.success) successCount++
        }

        return FormFillResult(
            success = successCount > 0,
            formSelector = "manual",
            fieldsAttempted = fieldMappings.size,
            fieldsSuccessful = successCount,
            fieldResults = fieldResults
        )
    }

    override suspend fun autoFillCommonForms(userData: UserData): List<FormFillResult> {
        val results = mutableListOf<FormFillResult>()
        val commonFormSelectors = listOf("form", ".contact-form", "#contact", ".signup-form")

        commonFormSelectors.forEach { selector ->
            val forms = webEngine.findElements(selector)
            forms.forEach { _ ->
                try {
                    val result = fillForm(selector, userData, FillStrategy.SMART_MATCH)
                    if (result.fieldsSuccessful > 0) {
                        results.add(result)
                    }
                } catch (e: Exception) {
                    // Continue with other forms
                }
            }
        }

        return results
    }

    override suspend fun validateForm(formSelector: String): FormValidationResult {
        val analysis = analyzeForm(formSelector)
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()

        analysis.fields.forEach { field ->
            val element = webEngine.findElement(field.selector)
            if (element != null) {
                val value = element.getText()
                
                // Check required fields
                if (field.required && value.isNullOrBlank()) {
                    errors.add(ValidationError(
                        fieldSelector = field.selector,
                        fieldName = field.name,
                        errorType = ValidationErrorType.REQUIRED_FIELD_EMPTY,
                        message = "Required field is empty"
                    ))
                }
            }
        }

        return FormValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    private suspend fun fillSingleField(
        fieldSelector: String,
        value: String,
        field: FormFieldInfo?
    ): FieldFillResult {
        return try {
            val element = webEngine.findElement(fieldSelector)
            if (element == null) {
                return FieldFillResult(
                    fieldSelector = fieldSelector,
                    fieldName = field?.name,
                    fieldType = field?.type ?: "unknown",
                    success = false,
                    value = value,
                    error = "Element not found"
                )
            }

            element.clear()
            element.type(value)

            FieldFillResult(
                fieldSelector = fieldSelector,
                fieldName = field?.name,
                fieldType = field?.type ?: "text",
                success = true,
                value = value,
                confidence = 0.8
            )
        } catch (e: Exception) {
            FieldFillResult(
                fieldSelector = fieldSelector,
                fieldName = field?.name,
                fieldType = field?.type ?: "unknown",
                success = false,
                value = value,
                error = e.message
            )
        }
    }

    private fun generateFieldMappings(
        analysis: FormAnalysis,
        userData: UserData,
        strategy: FillStrategy
    ): Map<String, String> {
        val mappings = mutableMapOf<String, String>()

        analysis.fields.forEach { field ->
            val value = findBestMatch(field, userData)
            if (value != null) {
                mappings[field.selector] = value
            }
        }

        return mappings
    }

    private fun findBestMatch(field: FormFieldInfo, userData: UserData): String? {
        val fieldName = field.name?.lowercase() ?: field.placeholder?.lowercase() ?: ""

        return when {
            fieldName.contains("email") -> userData.contactInfo?.email
            fieldName.contains("phone") -> userData.contactInfo?.phone
            fieldName.contains("name") -> userData.personalInfo?.fullName
            fieldName.contains("address") -> userData.addressInfo?.street
            fieldName.contains("city") -> userData.addressInfo?.city
            fieldName.contains("zip") -> userData.addressInfo?.zipCode
            else -> null
        }
    }
}