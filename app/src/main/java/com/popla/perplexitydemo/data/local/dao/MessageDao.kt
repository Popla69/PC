package com.popla.perplexitydemo.data.local.dao

import androidx.room.*
import com.popla.perplexitydemo.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): Message?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessageForConversation(conversationId: String): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: String)

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    suspend fun getMessageCountForConversation(conversationId: String): Int

    @Query("SELECT * FROM messages WHERE isStreaming = 1")
    suspend fun getStreamingMessages(): List<Message>

    @Query("UPDATE messages SET isStreaming = 0 WHERE id = :messageId")
    suspend fun stopStreaming(messageId: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}