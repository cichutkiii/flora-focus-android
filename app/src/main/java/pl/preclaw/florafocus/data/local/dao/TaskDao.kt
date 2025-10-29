package pl.preclaw.florafocus.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.data.local.entities.*

/**
 * Data Access Object for Tasks
 */
@Dao
interface TaskDao {

    // ==================== INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringTemplate(template: RecurringTaskTemplateEntity)

    // ==================== UPDATE ====================

    @Update
    suspend fun updateTask(task: TaskEntity)

    /**
     * Mark task as completed
     */
    @Query("""
        UPDATE tasks 
        SET isCompleted = 1, 
            completedDate = :completionDate,
            updatedAt = :timestamp
        WHERE id = :taskId
    """)
    suspend fun markTaskCompleted(
        taskId: String,
        completionDate: Long = System.currentTimeMillis(),
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Mark task as incomplete
     */
    @Query("""
        UPDATE tasks 
        SET isCompleted = 0, 
            completedDate = NULL,
            updatedAt = :timestamp
        WHERE id = :taskId
    """)
    suspend fun markTaskIncomplete(taskId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Reschedule task (e.g., due to weather)
     */
    @Query("""
        UPDATE tasks 
        SET dueDate = :newDueDate,
            updatedAt = :timestamp
        WHERE id = :taskId
    """)
    suspend fun rescheduleTask(
        taskId: String,
        newDueDate: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update task priority
     */
    @Query("UPDATE tasks SET priority = :priority, updatedAt = :timestamp WHERE id = :taskId")
    suspend fun updatePriority(
        taskId: String,
        priority: TaskPriority,
        timestamp: Long = System.currentTimeMillis()
    )

    // ==================== DELETE ====================

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    /**
     * Delete completed tasks older than date
     */
    @Query("DELETE FROM tasks WHERE isCompleted = 1 AND completedDate < :beforeDate")
    suspend fun deleteOldCompletedTasks(beforeDate: Long)

    // ==================== QUERIES - BASIC ====================

    /**
     * Get all tasks for user
     */
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC, priority DESC")
    fun getAllTasks(userId: String): Flow<List<TaskEntity>>

    /**
     * Get task by ID
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskByIdFlow(taskId: String): Flow<TaskEntity?>

    // ==================== QUERIES - BY STATUS ====================

    /**
     * Get pending (incomplete) tasks
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0 
        ORDER BY priority DESC, dueDate ASC
    """)
    fun getPendingTasks(userId: String): Flow<List<TaskEntity>>

    /**
     * Get completed tasks
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 1 
        ORDER BY completedDate DESC
    """)
    fun getCompletedTasks(userId: String): Flow<List<TaskEntity>>

    // ==================== QUERIES - BY DATE ====================

    /**
     * Get tasks due today
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND dueDate >= :startOfDay 
        AND dueDate < :endOfDay
        ORDER BY priority DESC, dueDate ASC
    """)
    fun getTasksDueToday(userId: String, startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    /**
     * Get overdue tasks
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND dueDate < :currentDate
        ORDER BY priority DESC, dueDate ASC
    """)
    fun getOverdueTasks(userId: String, currentDate: Long): Flow<List<TaskEntity>>

    /**
     * Get tasks in date range
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND dueDate >= :startDate 
        AND dueDate <= :endDate
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getTasksInDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<TaskEntity>>

    /**
     * Get upcoming tasks (next 7 days)
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND dueDate >= :currentDate 
        AND dueDate <= :weekFromNow
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getUpcomingTasks(userId: String, currentDate: Long, weekFromNow: Long): Flow<List<TaskEntity>>

    // ==================== QUERIES - BY PRIORITY ====================

    /**
     * Get tasks by priority
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND priority = :priority
        ORDER BY dueDate ASC
    """)
    fun getTasksByPriority(userId: String, priority: TaskPriority): Flow<List<TaskEntity>>

    /**
     * Get critical tasks
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND priority = 'CRITICAL'
        ORDER BY dueDate ASC
    """)
    fun getCriticalTasks(userId: String): Flow<List<TaskEntity>>

    // ==================== QUERIES - BY TYPE ====================

    /**
     * Get tasks by type
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND taskType = :type
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getTasksByType(userId: String, type: TaskType): Flow<List<TaskEntity>>

    // ==================== QUERIES - BY PLANT ====================

    /**
     * Get tasks for specific plant
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE plantId = :plantId 
        AND isCompleted = 0
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getTasksForPlant(plantId: String): Flow<List<TaskEntity>>

    /**
     * Get completed tasks for plant
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE plantId = :plantId 
        AND isCompleted = 1
        ORDER BY completedDate DESC
    """)
    fun getCompletedTasksForPlant(plantId: String): Flow<List<TaskEntity>>

    // ==================== QUERIES - BY LOCATION ====================

    /**
     * Get tasks for specific location (bed or area)
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId
        AND locationId = :locationId
        AND isCompleted = 0
        ORDER BY priority DESC, dueDate ASC
    """)
    fun getTasksForLocation(userId: String, locationId: String): Flow<List<TaskEntity>>

    /**
     * Group tasks by location
     * Note: This returns all tasks, client should group them
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId
        AND isCompleted = 0
        AND locationId IS NOT NULL
        ORDER BY locationId, priority DESC, dueDate ASC
    """)
    fun getTasksGroupedByLocation(userId: String): Flow<List<TaskEntity>>

    // ==================== QUERIES - SPECIAL ====================

    /**
     * Get auto-generated tasks
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND autoGenerated = 1
        AND isCompleted = 0
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getAutoGeneratedTasks(userId: String): Flow<List<TaskEntity>>

    /**
     * Get weather-dependent tasks
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND weatherDependent = 1
        AND isCompleted = 0
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getWeatherDependentTasks(userId: String): Flow<List<TaskEntity>>

    /**
     * Get flexible tasks (can be rescheduled)
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId 
        AND dueDateFlexible = 1
        AND isCompleted = 0
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getFlexibleTasks(userId: String): Flow<List<TaskEntity>>

    // ==================== QUERIES - RECURRING TEMPLATES ====================

    @Query("SELECT * FROM recurring_task_templates WHERE userId = :userId AND isActive = 1")
    fun getActiveRecurringTemplates(userId: String): Flow<List<RecurringTaskTemplateEntity>>

    @Query("SELECT * FROM recurring_task_templates WHERE id = :templateId")
    suspend fun getRecurringTemplateById(templateId: String): RecurringTaskTemplateEntity?

    @Update
    suspend fun updateRecurringTemplate(template: RecurringTaskTemplateEntity)

    @Query("UPDATE recurring_task_templates SET isActive = 0 WHERE id = :templateId")
    suspend fun deactivateRecurringTemplate(templateId: String)

    @Query("UPDATE recurring_task_templates SET lastGeneratedDate = :date WHERE id = :templateId")
    suspend fun updateLastGeneratedDate(templateId: String, date: Long)

    // ==================== STATISTICS ====================

    /**
     * Get task completion rate
     */
    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed
        FROM tasks
        WHERE userId = :userId
        AND dueDate >= :startDate
        AND dueDate <= :endDate
    """)
    suspend fun getTaskCompletionStats(userId: String, startDate: Long, endDate: Long): TaskCompletionStats

    /**
     * Get count of pending tasks
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 0")
    suspend fun getPendingTaskCount(userId: String): Int

    /**
     * Get count of overdue tasks
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 0 AND dueDate < :currentDate")
    suspend fun getOverdueTaskCount(userId: String, currentDate: Long): Int

    /**
     * Get count of tasks due today
     */
    @Query("""
        SELECT COUNT(*) FROM tasks 
        WHERE userId = :userId 
        AND isCompleted = 0
        AND dueDate >= :startOfDay 
        AND dueDate < :endOfDay
    """)
    suspend fun getTasksDueTodayCount(userId: String, startOfDay: Long, endOfDay: Long): Int
}

/**
 * Result class for task completion statistics
 */
data class TaskCompletionStats(
    val total: Int,
    val completed: Int
) {
    val completionRate: Float
        get() = if (total > 0) (completed.toFloat() / total.toFloat()) * 100 else 0f
}
