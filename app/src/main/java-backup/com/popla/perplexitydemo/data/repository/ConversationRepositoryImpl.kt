package com.popla.perplexitydemo.data.repository

import com.popla.perplexitydemo.data.local.dao.ConversationDao
import com.popla.perplexitydemo.data.local.dao.MessageDao
import com.popla.perplexitydemo.data.model.Conversation
import com.popla.perplexitydemo.data.model.Message
import com.popla.perplexitydemo.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ConversationRepository {
    
    override fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations()
    }
    
    override fun getActiveConversations(): Flow<List<Conversation>> {
        return conversationDao.getActiveConversations()
    }
    
    override suspend fun getConversationById(id: String): Conversation? {
        return conversationDao.getConversationById(id)
    }
    
    override suspend fun createConversation(conversation: Conversation) {
        conversationDao.insertConversation(conversation)
    }
    
    override suspend fun updateConversation(conversation: Conversation) {
        conversationDao.updateConversation(conversation)
    }
    
    override suspend fun deleteConversation(id: String) {
        messageDao.deleteMessagesForConversation(id)
        conversationDao.deleteConversation(id)
    }
    
    override suspend fun deleteAllConversations() {
        messageDao.deleteAllMessages()
        conversationDao.deleteAllConversations()
    }
    
    override fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId)
    }
    
    override suspend fun getMessageById(id: String): Message? {
        return messageDao.getMessageById(id)
    }
    
    override suspend fun addMessage(message: Message) {
        messageDao.insertMessage(message)
    }
    
    override suspend fun updateMessage(message: Message) {
        messageDao.updateMessage(message)
    }
    
    override suspend fun deleteMessage(id: String) {
        messageDao.deleteMessage(id)
    }
    
    override suspend fun getMessageCount(conversationId: String): Int {
        return messageDao.getMessageCountForConversation(conversationId)
    }
}