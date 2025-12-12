package com.popla.perplexitydemo.webagent.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.popla.perplexitydemo.data.local.PerplexityDatabase
import com.popla.perplexitydemo.webagent.data.local.dao.*
import com.popla.perplexitydemo.webagent.domain.*
import com.popla.perplexitydemo.webagent.domain.impl.*
import javax.inject.Singleton

/**
 * Hilt module for Web Agent dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
object WebAgentModule {
    
    @Provides
    @Singleton
    fun provideTaskExecutionDao(database: PerplexityDatabase): TaskExecutionDao {
        return database.taskExecutionDao()
    }
    
    @Provides
    @Singleton
    fun provideUserCommandDao(database: PerplexityDatabase): UserCommandDao {
        return database.userCommandDao()
    }
    
    @Provides
    @Singleton
    fun provideMonitoringSessionDao(database: PerplexityDatabase): MonitoringSessionDao {
        return database.monitoringSessionDao()
    }
    
    @Provides
    @Singleton
    fun provideUserPreferencesDao(database: PerplexityDatabase): UserPreferencesDao {
        return database.userPreferencesDao()
    }
    
    @Provides
    @Singleton
    fun provideAuditLogDao(database: PerplexityDatabase): AuditLogDao {
        return database.auditLogDao()
    }
    
    @Provides
    @Singleton
    fun provideNaturalLanguageProcessor(
        @ApplicationContext context: Context
    ): NaturalLanguageProcessor {
        return NaturalLanguageProcessorImpl(context)
    }
    
    // TODO: Enable after fixing TaskInterpreterImpl compilation issues
    // @Provides
    // @Singleton
    // fun provideTaskInterpreter(): TaskInterpreter {
    //     return TaskInterpreterImpl()
    // }
    
    // TODO: Enable after fixing compilation issues
    // @Provides
    // @Singleton
    // fun provideWebEngine(
    //     @ApplicationContext context: Context
    // ): WebEngine {
    //     return WebEngineImpl(context)
    // }
    
    // @Provides
    // @Singleton
    // fun provideFormDetector(
    //     webEngine: WebEngine
    // ): FormDetector {
    //     return FormDetectorImpl(webEngine)
    // }
    
    // @Provides
    // @Singleton
    // fun provideFormFiller(
    //     webEngine: WebEngine
    // ): FormFiller {
    //     return FormFillerImpl(webEngine)
    // }
    
    // @Provides
    // @Singleton
    // fun provideSafetyMonitor(
    //     webEngine: WebEngine
    // ): SafetyMonitor {
    //     return SafetyMonitorImpl(webEngine)
    // }
    
    // @Provides
    // @Singleton
    // fun provideWebScanner(
    //     webEngine: WebEngine
    // ): WebScanner {
    //     return WebScannerImpl(webEngine)
    // }
    
    // @Provides
    // @Singleton
    // fun provideContextManager(
    //     webEngine: WebEngine,
    //     formDetector: FormDetector
    // ): ContextManager {
    //     return ContextManagerImpl(webEngine, formDetector)
    // }
    
    // @Provides
    // @Singleton
    // fun provideActionExecutor(
    //     webEngine: WebEngine
    // ): ActionExecutor {
    //     return ActionExecutorImpl(webEngine)
    // }
    
    // TODO: Implement missing classes
    // @Provides
    // @Singleton
    // fun provideContextManager(
    //     userPreferencesDao: UserPreferencesDao,
    //     taskExecutionDao: TaskExecutionDao
    // ): ContextManager {
    //     return ContextManagerImpl(userPreferencesDao, taskExecutionDao)
    // }
    
    // @Provides
    // @Singleton
    // fun provideSafetyMonitor(
    //     auditLogDao: AuditLogDao,
    //     @ApplicationContext context: Context
    // ): SafetyMonitor {
    //     return SafetyMonitorImpl(auditLogDao, context)
    // }
    
    // @Provides
    // @Singleton
    // fun provideWebScanner(
    //     webEngine: WebEngine,
    //     monitoringSessionDao: MonitoringSessionDao
    // ): WebScanner {
    //     return WebScannerImpl(webEngine, monitoringSessionDao)
    // }
    
    // @Provides
    // @Singleton
    // fun provideAIWebAgent(
    //     naturalLanguageProcessor: NaturalLanguageProcessor,
    //     taskInterpreter: TaskInterpreter,
    //     actionExecutor: ActionExecutor,
    //     contextManager: ContextManager,
    //     safetyMonitor: SafetyMonitor,
    //     webScanner: WebScanner,
    //     taskExecutionDao: TaskExecutionDao,
    //     userCommandDao: UserCommandDao
    // ): AIWebAgent {
    //     return AIWebAgentImpl(
    //         naturalLanguageProcessor,
    //         taskInterpreter,
    //         actionExecutor,
    //         contextManager,
    //         safetyMonitor,
    //         webScanner,
    //         taskExecutionDao,
    //         userCommandDao
    //     )
    // }
}