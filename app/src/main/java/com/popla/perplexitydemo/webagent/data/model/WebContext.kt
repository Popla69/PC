package com.popla.perplexitydemo.webagent.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.Instant

/**
 * Represents the current web context and state
 */
@Parcelize
data class WebContext(
    val currentUrl: String?,
    val pageTitle: String?,
    val availableActions: List<AvailableAction>,
    val formFields: List<DetectedFormField>,
    val userGoal: String?,
    val sessionData: Map<String, String>,
    val lastUpdated: @RawValue Instant = Instant.now()
) : Parcelable

/**
 * Represents an action that can be performed on the current page
 */
@Parcelize
data class AvailableAction(
    val type: ActionType,
    val selector: String,
    val description: String,
    val confidence: Float
) : Parcelable

/**
 * Represents a form field detected on the current page
 */
@Parcelize
data class DetectedFormField(
    val selector: String,
    val type: FormFieldType,
    val label: String?,
    val placeholder: String?,
    val required: Boolean,
    val currentValue: String?,
    val suggestedUserData: String? // Matched user data for this field
) : Parcelable

enum class FormFieldType {
    TEXT,
    EMAIL,
    PASSWORD,
    PHONE,
    NUMBER,
    DATE,
    TIME,
    SELECT,
    CHECKBOX,
    RADIO,
    TEXTAREA,
    FILE,
    HIDDEN,
    UNKNOWN
}

/**
 * User preferences for web agent behavior
 */
@Parcelize
data class UserPreferences(
    val autoFillForms: Boolean = true,
    val confirmBeforeActions: Boolean = true,
    val saveCredentials: Boolean = false,
    val defaultTimeout: Long = 30000, // 30 seconds
    val preferredBrowser: String = "chromium",
    val enableScreenshots: Boolean = true,
    val maxConcurrentTasks: Int = 3
) : Parcelable

/**
 * Personal data for form filling
 */
@Parcelize
data class PersonalData(
    val name: String?,
    val email: String?,
    val phone: String?,
    val address: Address?,
    val paymentMethods: List<PaymentMethod>,
    val customFields: Map<String, String>
) : Parcelable

@Parcelize
data class Address(
    val street: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String?
) : Parcelable

@Parcelize
data class PaymentMethod(
    val type: PaymentType,
    val lastFourDigits: String?,
    val expiryMonth: Int?,
    val expiryYear: Int?,
    val isDefault: Boolean = false
) : Parcelable

enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    APPLE_PAY,
    GOOGLE_PAY,
    BANK_TRANSFER
}