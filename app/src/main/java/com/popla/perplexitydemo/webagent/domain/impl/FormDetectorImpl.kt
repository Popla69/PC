package com.popla.perplexitydemo.webagent.domain.impl

import com.popla.perplexitydemo.webagent.domain.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FormDetector for form detection and analysis
 */
@Singleton
class FormDetectorImpl @Inject constructor(
    private val webEngine: WebEngine
) : FormDetector {

    private val fieldTypePatterns = mapOf(
        FieldType.FIRST_NAME to listOf("first.*name", "fname", "given.*name"),
        FieldType.LAST_NAME to listOf("last.*name", "lname", "family.*name", "surname"),
        FieldType.FULL_NAME to listOf("full.*name", "name", "your.*name"),
        FieldType.EMAIL to listOf("email", "e-mail", "mail"),
        FieldType.PHONE to listOf("phone", "mobile", "tel", "telephone"),
        FieldType.ADDRESS to listOf("address", "street", "addr"),
        FieldType.CITY to listOf("city", "town"),
        FieldType.STATE to listOf("state", "province", "region"),
        FieldType.ZIP_CODE to listOf("zip", "postal", "postcode"),
        FieldType.COUNTRY to listOf("country", "nation"),
        FieldType.DATE_OF_BIRTH to listOf("birth", "dob", "birthday"),
        FieldType.GENDER to listOf("gender", "sex"),
        FieldType.COMPANY to listOf("company", "organization", "employer"),
        FieldType.JOB_TITLE to listOf("job", "title", "position"),
        FieldType.WEBSITE to listOf("website", "url", "site"),
        FieldType.PASSWORD to listOf("password", "pass", "pwd"),
        FieldType.CONFIRM_PASSWORD to listOf("confirm.*password", "repeat.*password"),
        FieldType.CREDIT_CARD to listOf("card.*number", "credit.*card", "cc.*number"),
        FieldType.EXPIRY_DATE to listOf("expiry", "expiration", "exp.*date"),
        FieldType.CVV to listOf("cvv", "cvc", "security.*code")
    )

    override suspend fun detectForms(): List<DetectedFormInfo> {
        val forms = webEngine.findElements("form")
        return forms.mapIndexed { index, formElement ->
            val formSelector = "form:nth-of-type(${index + 1})"
            val analysis = analyzeForm(formSelector)
            
            DetectedFormInfo(
                selector = formSelector,
                action = formElement.getAttribute("action"),
                method = formElement.getAttribute("method") ?: "GET",
                fields = analysis.fields,
                submitButton = findSubmitButton(formSelector),
                formType = analysis.formType,
                confidence = calculateFormConfidence(analysis)
            )
        }
    }

    override suspend fun analyzeForm(formSelector: String): FormAnalysis {
        val fieldInfos = detectFieldTypes(formSelector)
        val formType = determineFormType(fieldInfos)
        val requiredFields = fieldInfos.filter { it.required }.map { it.selector }
        val optionalFields = fieldInfos.filter { !it.required }.map { it.selector }
        
        val complexity = when {
            fieldInfos.size <= 3 -> FormComplexity.SIMPLE
            fieldInfos.size <= 8 -> FormComplexity.MEDIUM
            fieldInfos.size <= 15 -> FormComplexity.COMPLEX
            else -> FormComplexity.MULTI_PAGE
        }
        
        val estimatedTime = calculateEstimatedTime(fieldInfos, complexity)
        
        return FormAnalysis(
            formSelector = formSelector,
            formType = formType,
            fields = fieldInfos,
            requiredFields = requiredFields,
            optionalFields = optionalFields,
            estimatedCompletionTime = estimatedTime,
            complexity = complexity
        )
    }

    override suspend fun getFieldMappings(
        form: DetectedFormInfo, 
        userData: Map<String, Any>
    ): Map<String, String> {
        val mappings = mutableMapOf<String, String>()
        
        form.fields.forEach { field ->
            val value = findBestMatch(field, userData)
            if (value != null) {
                mappings[field.selector] = value
            }
        }
        
        return mappings
    }

    override suspend fun detectFieldTypes(formSelector: String): List<FormFieldInfo> {
        val inputElements = webEngine.findElements(
            "$formSelector input, $formSelector select, $formSelector textarea"
        )
        
        return inputElements.mapIndexed { index, element ->
            val fieldType = determineFieldType(element)
            val confidence = calculateFieldConfidence(element, fieldType)
            val elementSelector = "${formSelector} input:nth-of-type(${index + 1})"
            
            FormFieldInfo(
                selector = elementSelector,
                name = element.getAttribute("name"),
                id = element.getAttribute("id"),
                type = element.getAttribute("type") ?: "text",
                label = findFieldLabel(element),
                placeholder = element.getAttribute("placeholder"),
                required = element.getAttribute("required") != null,
                options = if (element.getAttribute("tagName")?.lowercase() == "select") getSelectOptions(element) else emptyList(),
                fieldType = fieldType,
                confidence = confidence
            )
        }
    }

    private suspend fun findSubmitButton(formSelector: String): String? {
        val submitButtons = webEngine.findElements(
            "$formSelector input[type='submit'], $formSelector button[type='submit'], $formSelector button:not([type])"
        )
        return submitButtons.firstOrNull()?.let { "${formSelector} button:nth-of-type(1)" }
    }

    private fun determineFormType(fields: List<FormFieldInfo>): FormType {
        val fieldTypes = fields.map { it.fieldType }
        
        return when {
            fieldTypes.contains(FieldType.PASSWORD) && fieldTypes.contains(FieldType.EMAIL) -> {
                if (fieldTypes.contains(FieldType.CONFIRM_PASSWORD)) FormType.REGISTRATION else FormType.LOGIN
            }
            fieldTypes.contains(FieldType.CREDIT_CARD) -> FormType.CHECKOUT
            fieldTypes.contains(FieldType.FIRST_NAME) && fieldTypes.contains(FieldType.LAST_NAME) -> {
                if (fieldTypes.size > 5) FormType.APPLICATION else FormType.CONTACT
            }
            fieldTypes.contains(FieldType.EMAIL) && fieldTypes.size <= 2 -> FormType.NEWSLETTER
            fieldTypes.any { it.name.contains("search", ignoreCase = true) } -> FormType.SEARCH
            fieldTypes.size > 8 -> FormType.SURVEY
            else -> FormType.UNKNOWN
        }
    }

    private suspend fun determineFieldType(element: WebElement): FieldType {
        val identifiers = listOfNotNull(
            element.getAttribute("name")?.lowercase(),
            element.getAttribute("id")?.lowercase(),
            element.getAttribute("placeholder")?.lowercase(),
            findFieldLabel(element)?.lowercase()
        )
        
        // Check input type first
        val inputType = element.getAttribute("type")?.lowercase()
        when (inputType) {
            "email" -> return FieldType.EMAIL
            "tel", "phone" -> return FieldType.PHONE
            "password" -> {
                return if (identifiers.any { it.contains("confirm") || it.contains("repeat") }) {
                    FieldType.CONFIRM_PASSWORD
                } else {
                    FieldType.PASSWORD
                }
            }
            "date" -> return FieldType.DATE_OF_BIRTH
            "url" -> return FieldType.WEBSITE
        }
        
        // Pattern matching
        fieldTypePatterns.forEach { (fieldType, patterns) ->
            patterns.forEach { pattern ->
                if (identifiers.any { it.contains(Regex(pattern, RegexOption.IGNORE_CASE)) }) {
                    return fieldType
                }
            }
        }
        
        return FieldType.UNKNOWN
    }

    private suspend fun findFieldLabel(element: WebElement): String? {
        // Try to find associated label
        val id = element.getAttribute("id")
        if (id != null) {
            val label = webEngine.findElement("label[for='$id']")
            if (label != null) {
                return label.getText()
            }
        }
        
        // Try to find parent label (simplified for now)
        return element.getAttribute("aria-label") ?: element.getAttribute("placeholder")
    }

    private suspend fun getSelectOptions(element: WebElement): List<String> {
        // Simplified - would need to find options within the select element
        return emptyList()
    }

    private fun calculateFormConfidence(analysis: FormAnalysis): Double {
        val fieldConfidences = analysis.fields.map { it.confidence }
        return if (fieldConfidences.isNotEmpty()) {
            fieldConfidences.average()
        } else {
            0.0
        }
    }

    private suspend fun calculateFieldConfidence(element: WebElement, fieldType: FieldType): Double {
        var confidence = 0.5 // Base confidence
        
        // Increase confidence based on available information
        if (element.getAttribute("name") != null) confidence += 0.2
        if (element.getAttribute("id") != null) confidence += 0.1
        if (element.getAttribute("placeholder") != null) confidence += 0.1
        if (fieldType != FieldType.UNKNOWN) confidence += 0.1
        
        return confidence.coerceAtMost(1.0)
    }

    private fun calculateEstimatedTime(fields: List<FormFieldInfo>, complexity: FormComplexity): Int {
        val baseTime = when (complexity) {
            FormComplexity.SIMPLE -> 30
            FormComplexity.MEDIUM -> 60
            FormComplexity.COMPLEX -> 120
            FormComplexity.MULTI_PAGE -> 180
        }
        
        // Add time for complex field types
        val complexFields = fields.count { 
            it.fieldType in listOf(FieldType.ADDRESS, FieldType.CREDIT_CARD, FieldType.DATE_OF_BIRTH)
        }
        
        return baseTime + (complexFields * 15)
    }

    private fun findBestMatch(field: FormFieldInfo, userData: Map<String, Any>): String? {
        return when (field.fieldType) {
            FieldType.FIRST_NAME -> userData["firstName"] as? String
            FieldType.LAST_NAME -> userData["lastName"] as? String
            FieldType.FULL_NAME -> userData["fullName"] as? String ?: 
                "${userData["firstName"] ?: ""} ${userData["lastName"] ?: ""}".trim().takeIf { it.isNotBlank() }
            FieldType.EMAIL -> userData["email"] as? String
            FieldType.PHONE -> userData["phone"] as? String
            FieldType.ADDRESS -> userData["address"] as? String
            FieldType.CITY -> userData["city"] as? String
            FieldType.STATE -> userData["state"] as? String
            FieldType.ZIP_CODE -> userData["zipCode"] as? String
            FieldType.COUNTRY -> userData["country"] as? String
            FieldType.DATE_OF_BIRTH -> userData["dateOfBirth"] as? String
            FieldType.GENDER -> userData["gender"] as? String
            FieldType.COMPANY -> userData["company"] as? String
            FieldType.JOB_TITLE -> userData["jobTitle"] as? String
            FieldType.WEBSITE -> userData["website"] as? String
            else -> null
        }
    }
}