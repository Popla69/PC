package com.popla.perplexitydemo.domain

import com.popla.perplexitydemo.data.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing conversations and chat sessions
 */
interface ConversationManager {
    /**
     * Start a new conversation
     */
    suspend fun startConversation(): String
    
    /**
     * Add a message to a conversation
     */
    suspend fun addMessage(conversationId: String, message: Message)
    
    /**
     * Get messages from a conversation
     */
    suspend fun getMessages(conversationId: String): Flow<List<Message>>
    
    /**
     * Get all conversations
     */
    suspend fun getConversations(): Flow<List<Conversation>>
    
    /**
     * Delete a conversation
     */
    suspend fun deleteConversation(conversationId: String)
    
    /**
     * Clear all conversations
     */
    suspend fun clearAllConversations()
}

/**
 * Represents a conversation
 */
data class Conversation(
    val id: String,
    val title: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val messageCount: Int,
    val searchMode: SearchMode = SearchMode.GENERAL
)

/**
 * Search modes for conversations
 */
enum class SearchMode {
    GENERAL,
    ACADEMIC,
    NEWS,
    SHOPPING,
    IMAGES,
    VIDEOS
}