package com.popla.perplexitydemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val searchMode: SearchMode = SearchMode.GENERAL,
    val messageCount: Int = 0,
    val isArchived: Boolean = false
)

enum class SearchMode {
    GENERAL, ACADEMIC, WRITING, MATH, PROGRAMMING, CREATIVE
}