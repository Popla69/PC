package com.popla.perplexitydemo.domain.repository

import com.popla.perplexitydemo.data.model.Conversation
import com.popla.perplexitydemo.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getAllConversations(): Flow<List<Conversation>>
    fun getActiveConversations(): Flow<List<Conversation>>
    suspend fun getConversationById(id: String): Conversation?
    suspend fun createConversation(conversation: Conversation)
    suspend fun updateConversation(conversation: Conversation)
    suspend fun deleteConversation(id: String)
    suspend fun deleteAllConversations()
    
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>>
    suspend fun getMessageById(id: String): Message?
    suspend fun addMessage(message: Message)
    suspend fun updateMessage(message: Message)
    suspend fun deleteMessage(id: String)
    suspend fun getMessageCount(conversationId: String): Int
}