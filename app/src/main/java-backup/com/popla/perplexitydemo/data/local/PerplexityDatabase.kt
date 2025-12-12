package com.popla.perplexitydemo.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.popla.perplexitydemo.data.converter.Converters
import com.popla.perplexitydemo.data.local.dao.ConversationDao
import com.popla.perplexitydemo.data.local.dao.MessageDao
import com.popla.perplexitydemo.data.model.Conversation
import com.popla.perplexitydemo.data.model.Message
import com.popla.perplexitydemo.webagent.data.local.dao.*
import com.popla.perplexitydemo.webagent.data.local.entity.*

@Database(
    entities = [
        Conversation::class, 
        Message::class,
        TaskExecutionEntity::class,
        UserCommandEntity::class,
        MonitoringSessionEntity::class,
        UserPreferencesEntity::class,
        AuditLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PerplexityDatabase : RoomDatabase() {
    
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    
    // Web Agent DAOs
    abstract fun taskExecutionDao(): TaskExecutionDao
    abstract fun userCommandDao(): UserCommandDao
    abstract fun monitoringSessionDao(): MonitoringSessionDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun auditLogDao(): AuditLogDao
    
    companion object {
        @Volatile
        private var INSTANCE: PerplexityDatabase? = null
        
        fun getDatabase(context: Context): PerplexityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PerplexityDatabase::class.java,
                    "perplexity_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}