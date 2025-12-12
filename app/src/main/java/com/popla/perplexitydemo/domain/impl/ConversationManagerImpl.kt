package com.popla.perplexitydemo.domain.impl

import com.popla.perplexitydemo.data.local.dao.ConversationDao
import com.popla.perplexitydemo.data.local.dao.MessageDao
import com.popla.perplexitydemo.data.model.Message
import com.popla.perplexitydemo.domain.Conversation
import com.popla.perplexitydemo.domain.ConversationManager
import com.popla.perplexitydemo.domain.SearchMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ConversationManager
 */
@Singleton
class ConversationManagerImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ConversationManager {
    
    override suspend fun startConversation(): String {
        val conversationId = UUID.randomUUID().toString()
        // Create conversation in database if needed
        return conversationId
    }
    
    override suspend fun addMessage(conversationId: String, message: Message) {
        messageDao.insertMessage(message)
    }
    
    override suspend fun getMessages(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId)
    }
    
    override suspend fun getConversations(): Flow<List<Conversation>> {
        // For now, return empty flow - this would need a proper ConversationDao implementation
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    override suspend fun deleteConversation(conversationId: String) {
        messageDao.deleteMessagesForConversation(conversationId)
    }
    
    override suspend fun clearAllConversations() {
        messageDao.deleteAllMessages()
    }
}