package com.popla.perplexitydemo.webagent.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a named entity extracted from natural language
 */
@Parcelize
data class Entity(
    val text: String,
    val type: EntityType,
    val confidence: Float,
    val startIndex: Int,
    val endIndex: Int
) : Parcelable

/**
 * Types of entities that can be extracted from text
 */
enum class EntityType {
    PERSON,
    ORGANIZATION,
    LOCATION,
    DATE,
    TIME,
    MONEY,
    EMAIL,
    PHONE,
    URL,
    PRODUCT,
    UNKNOWN
}

/**
 * Represents an ambiguity in natural language processing
 */
@Parcelize
data class Ambiguity(
    val text: String,
    val possibleMeanings: List<String>,
    val confidence: Float
) : Parcelable

/**
 * Extended action types for natural language processing
 */
enum class NLPActionType {
    FILL_FORM,
    SHOP,
    MONITOR,
    EXTRACT_DATA,
    SEARCH,
    BOOK_APPOINTMENT,
    SOCIAL_MEDIA_POST,
    UNKNOWN
}

/**
 * Represents the result of natural language processing
 */
@Parcelize
data class NLPResult(
    val id: String,
    val description: String,
    val action: NLPActionType,
    val target: String?,
    val parameters: Map<String, String>,
    val confidence: Float,
    val ambiguities: List<Ambiguity>
) : Parcelable