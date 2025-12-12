package com.popla.perplexitydemo.di

import android.content.Context
import androidx.room.Room
import com.popla.perplexitydemo.data.local.PerplexityDatabase
import com.popla.perplexitydemo.data.local.dao.ConversationDao
import com.popla.perplexitydemo.data.local.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PerplexityDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PerplexityDatabase::class.java,
            "perplexity_database"
        ).build()
    }

    @Provides
    fun provideConversationDao(database: PerplexityDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    fun provideMessageDao(database: PerplexityDatabase): MessageDao {
        return database.messageDao()
    }
}