package com.popla.perplexitydemo.webagent.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a parsed user intent for web automation tasks
 */
@Parcelize
data class TaskIntent(
    val action: ActionType,
    val target: String?,
    val parameters: Map<String, String>,
    val confidence: Float,
    val ambiguities: List<Ambiguity>
) : Parcelable

/**
 * Types of actions the AI Web Agent can perform
 */
enum class ActionType {
    FILL_FORM,
    NAVIGATE,
    CLICK,
    SEARCH,
    MONITOR,
    EXTRACT_DATA,
    SHOP,
    BOOK_APPOINTMENT,
    SOCIAL_MEDIA_POST,
    UNKNOWN
}

/**
 * Represents an ambiguity in user intent that needs clarification
 */
@Parcelize
data class Ambiguity(
    val field: String,
    val possibleValues: List<String>,
    val question: String
) : Parcelable

/**
 * Represents an extracted entity from user input
 */
@Parcelize
data class Entity(
    val type: EntityType,
    val value: String,
    val confidence: Float,
    val startIndex: Int,
    val endIndex: Int
) : Parcelable

/**
 * Types of entities that can be extracted from user input
 */
enum class EntityType {
    PERSON_NAME,
    EMAIL,
    PHONE,
    ADDRESS,
    DATE,
    TIME,
    PRICE,
    PRODUCT,
    WEBSITE_URL,
    FORM_FIELD,
    UNKNOWN
}