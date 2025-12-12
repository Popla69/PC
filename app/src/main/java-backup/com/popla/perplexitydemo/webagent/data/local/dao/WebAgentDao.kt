package com.popla.perplexitydemo.webagent.data.local.dao

import androidx.room.*
import com.popla.perplexitydemo.webagent.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * DAO for web agent task executions
 */
@Dao
interface TaskExecutionDao {
    
    @Query("SELECT * FROM task_executions WHERE userId = :userId ORDER BY startTime DESC")
    fun getTaskExecutions(userId: String): Flow<List<TaskExecutionEntity>>
    
    @Query("SELECT * FROM task_executions WHERE id = :id")
    suspend fun getTaskExecution(id: String): TaskExecutionEntity?
    
    @Query("SELECT * FROM task_executions WHERE status = :status")
    suspend fun getTaskExecutionsByStatus(status: ExecutionStatus): List<TaskExecutionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskExecution(taskExecution: TaskExecutionEntity)
    
    @Update
    suspend fun updateTaskExecution(taskExecution: TaskExecutionEntity)
    
    @Delete
    suspend fun deleteTaskExecution(taskExecution: TaskExecutionEntity)
    
    @Query("DELETE FROM task_executions WHERE userId = :userId AND startTime < :before")
    suspend fun deleteOldTaskExecutions(userId: String, before: Instant)
}

/**
 * DAO for user commands
 */
@Dao
interface UserCommandDao {
    
    @Query("SELECT * FROM user_commands WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentCommands(userId: String, limit: Int = 50): List<UserCommandEntity>
    
    @Query("SELECT * FROM user_commands WHERE id = :id")
    suspend fun getCommand(id: String): UserCommandEntity?
    
    @Query("SELECT * FROM user_commands WHERE processed = 0 ORDER BY timestamp ASC")
    suspend fun getUnprocessedCommands(): List<UserCommandEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: UserCommandEntity)
    
    @Update
    suspend fun updateCommand(command: UserCommandEntity)
    
    @Query("DELETE FROM user_commands WHERE userId = :userId AND timestamp < :before")
    suspend fun deleteOldCommands(userId: String, before: Instant)
}

/**
 * DAO for monitoring sessions
 */
@Dao
interface MonitoringSessionDao {
    
    @Query("SELECT * FROM monitoring_sessions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getMonitoringSessions(userId: String): Flow<List<MonitoringSessionEntity>>
    
    @Query("SELECT * FROM monitoring_sessions WHERE status = :status")
    suspend fun getActiveMonitoringSessions(status: MonitoringStatus = MonitoringStatus.ACTIVE): List<MonitoringSessionEntity>
    
    @Query("SELECT * FROM monitoring_sessions WHERE id = :id")
    suspend fun getMonitoringSession(id: String): MonitoringSessionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonitoringSession(session: MonitoringSessionEntity)
    
    @Update
    suspend fun updateMonitoringSession(session: MonitoringSessionEntity)
    
    @Delete
    suspend fun deleteMonitoringSession(session: MonitoringSessionEntity)
}

/**
 * DAO for user preferences
 */
@Dao
interface UserPreferencesDao {
    
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getUserPreferences(userId: String): UserPreferencesEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreferences(preferences: UserPreferencesEntity)
    
    @Update
    suspend fun updateUserPreferences(preferences: UserPreferencesEntity)
    
    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deleteUserPreferences(userId: String)
}

/**
 * DAO for audit logs
 */
@Dao
interface AuditLogDao {
    
    @Query("SELECT * FROM audit_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getAuditLogs(userId: String, limit: Int = 1000): List<AuditLogEntity>
    
    @Query("SELECT * FROM audit_logs WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getSessionAuditLogs(sessionId: String): List<AuditLogEntity>
    
    @Query("SELECT * FROM audit_logs WHERE riskLevel = :riskLevel ORDER BY timestamp DESC")
    suspend fun getAuditLogsByRisk(riskLevel: RiskLevel): List<AuditLogEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(auditLog: AuditLogEntity)
    
    @Query("DELETE FROM audit_logs WHERE userId = :userId AND timestamp < :before")
    suspend fun deleteOldAuditLogs(userId: String, before: Instant)
    
    @Query("SELECT COUNT(*) FROM audit_logs WHERE userId = :userId AND riskLevel = :riskLevel AND timestamp > :since")
    suspend fun countRiskEvents(userId: String, riskLevel: RiskLevel, since: Instant): Int
}