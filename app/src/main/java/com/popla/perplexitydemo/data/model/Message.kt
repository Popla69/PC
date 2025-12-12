package com.popla.perplexitydemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.popla.perplexitydemo.data.converter.Converters
import java.time.LocalDateTime

@Entity(tableName = "messages")
@TypeConverters(Converters::class)
data class Message(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val content: String,
    val role: MessageRole,
    val timestamp: LocalDateTime,
    val citations: List<Citation> = emptyList(),
    val isStreaming: Boolean = false,
    val hasError: Boolean = false,
    val metadata: Map<String, String> = emptyMap()
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

data class Citation(
    val id: String,
    val title: String,
    val url: String,
    val snippet: String,
    val domain: String,
    val publishedDate: String? = null,
    val relevanceScore: Double = 0.0
)