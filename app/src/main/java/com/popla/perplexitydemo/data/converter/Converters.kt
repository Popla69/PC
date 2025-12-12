package com.popla.perplexitydemo.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.popla.perplexitydemo.data.model.Citation
import com.popla.perplexitydemo.data.model.MessageRole
import com.popla.perplexitydemo.data.model.SearchMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(epochMilli: Long?): Instant? {
        return epochMilli?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromMessageRole(role: MessageRole): String {
        return role.name
    }

    @TypeConverter
    fun toMessageRole(roleString: String): MessageRole {
        return MessageRole.valueOf(roleString)
    }

    @TypeConverter
    fun fromSearchMode(mode: SearchMode): String {
        return mode.name
    }

    @TypeConverter
    fun toSearchMode(modeString: String): SearchMode {
        return SearchMode.valueOf(modeString)
    }

    @TypeConverter
    fun fromCitationList(citations: List<Citation>): String {
        return gson.toJson(citations)
    }

    @TypeConverter
    fun toCitationList(citationsString: String): List<Citation> {
        val listType = object : TypeToken<List<Citation>>() {}.type
        return gson.fromJson(citationsString, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromStringMap(map: Map<String, String>): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toStringMap(mapString: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(mapString, mapType) ?: emptyMap()
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(listString: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(listString, listType) ?: emptyList()
    }
}