package pl.preclaw.florafocus.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.model.RecurrencePattern

/**
 * Repository interface for Task operations
 */
interface TaskRepository {

    // ==================== BASIC CRUD ====================

    /**
     * Get all tasks for user
     */
    fun getTasks(userId: String): Flow<List<Task>>

    /**
     * Get task by ID
     */
    suspend fun getTaskById(taskId: String): Task?

    /**
     * Get task by ID as Flow
     */
    fun getTaskByIdFlow(taskId: String): Flow<Task?>

    /**
     * Create new task
     */
    suspend fun createTask(task: Task): Result<String>

    /**
     * Update task
     */
    suspend fun updateTask(task: Task): Result<Unit>

    /**
     * Delete task
     */
    suspend fun deleteTask(taskId: String): Result<Unit>

    // ==================== TASK QUERIES ====================

    /**
     * Get tasks for today
     */
    fun getTasksForToday(userId: String): Flow<List<Task>>

    /**
     * Get upcoming tasks (next N days)
     */
    fun getUpcomingTasks(userId: String, days: Int): Flow<List<Task>>

    /**
     * Get overdue tasks
     */
    fun getOverdueTasks(userId: String): Flow<List<Task>>

    /**
     * Get tasks for specific date
     */
    fun getTasksForDate(userId: String, date: Long): Flow<List<Task>>

    /**
     * Get tasks by type
     */
    fun getTasksByType(userId: String, type: TaskType): Flow<List<Task>>

    /**
     * Get tasks by priority
     */
    fun getTasksByPriority(userId: String, priority: TaskPriority): Flow<List<Task>>

    /**
     * Get tasks for specific plant
     */
    fun getTasksForPlant(plantId: String): Flow<List<Task>>

    /**
     * Get tasks for specific bed/location
     */
    fun getTasksByLocation(locationId: String): Flow<List<Task>>

    /**
     * Get pending tasks (not completed)
     */
    fun getPendingTasks(userId: String): Flow<List<Task>>

    /**
     * Get completed tasks
     */
    fun getCompletedTasks(userId: String): Flow<List<Task>>

    /**
     * Get tasks for specific bed
     */
    fun getTasksForBed(bedId: String): Flow<List<Task>>

    /**
     * Get pending tasks count
     */
    suspend fun getPendingTasksCount(userId: String): Int

    // ==================== TASK ACTIONS ====================

    /**
     * Complete task
     */
    suspend fun completeTask(taskId: String): Result<Unit>

    /**
     * Complete task with specific date
     */
    suspend fun completeTask(taskId: String, completedDate: Long): Result<Unit>

    /**
     * Uncomplete task (mark as pending again)
     */
    suspend fun uncompleteTask(taskId: String): Result<Unit>

    /**
     * Snooze task for N days
     */
    suspend fun snoozeTask(taskId: String, days: Int): Result<Unit>

    /**
     * Reschedule task to specific date
     */
    suspend fun rescheduleTask(taskId: String, newDueDate: Long): Result<Unit>

    // ==================== RECURRING TASKS ====================

    /**
     * Create recurring task
     */
    suspend fun createRecurringTask(
        task: Task,
        pattern: RecurrencePattern,
        intervalDays: Int? = null
    ): Result<String>

    /**
     * Get all recurring task templates
     */
    fun getRecurringTemplates(userId: String): Flow<List<RecurringTaskTemplate>>

    /**
     * Get recurring template by ID
     */
    suspend fun getRecurringTemplateById(templateId: String): RecurringTaskTemplate?

    /**
     * Create recurring template
     */
    suspend fun createRecurringTemplate(template: RecurringTaskTemplate): Result<String>

    /**
     * Update recurring template
     */
    suspend fun updateRecurringTemplate(template: RecurringTaskTemplate): Result<Unit>

    /**
     * Delete recurring template
     */
    suspend fun deleteRecurringTemplate(templateId: String): Result<Unit>

    /**
     * Generate next occurrence of recurring task
     */
    suspend fun generateNextRecurringTask(templateId: String): Result<String>

    // ==================== AUTO-GENERATION ====================

    /**
     * Generate tasks from plant growth phase
     */
    suspend fun generateTasksFromPhase(
        plantId: String,
        phaseId: String,
        phaseName: GrowthPhaseName,
        autoTasks: List<AutoTask>
    ): Result<List<String>>

    /**
     * Auto-generate tasks based on plant care schedule
     */
    suspend fun generateCareTasksForPlant(
        plantId: String,
        plantType: PlantType
    ): Result<List<String>>

    /**
     * Generate seasonal tasks for user
     */
    suspend fun generateSeasonalTasks(userId: String): Result<List<String>>

    // ==================== STATISTICS ====================

    /**
     * Get task statistics for user
     */
    suspend fun getTaskStats(userId: String): TaskStats

    /**
     * Get task count by type
     */
    suspend fun getTaskCountByType(userId: String): Map<TaskType, Int>

    /**
     * Get task count by priority
     */
    suspend fun getTaskCountByPriority(userId: String): Map<TaskPriority, Int>

    /**
     * Get completion rate for user
     */
    suspend fun getTaskCompletionRate(userId: String, days: Int = 30): Float

    /**
     * Get average completion time
     */
    suspend fun getAverageCompletionTime(userId: String): Long

    // ==================== SMART FEATURES ====================

    /**
     * Get tasks that can be rescheduled due to weather
     */
    fun getWeatherDependentTasks(userId: String): Flow<List<Task>>

    /**
     * Bulk reschedule weather-dependent tasks
     */
    suspend fun rescheduleWeatherDependentTasks(
        userId: String,
        weatherCondition: String,
        newDate: Long
    ): Result<Int>

    /**
     * Get suggested tasks for today based on priority and weather
     */
    suspend fun getSuggestedTasksForToday(userId: String): List<Task>

    // ==================== FIREBASE SYNC ====================

    /**
     * Sync tasks with Firebase
     */
    suspend fun syncTasksWithFirebase(userId: String): Result<Unit>

    /**
     * Backup user tasks to Firebase
     */
    suspend fun backupTasksToFirebase(userId: String): Result<Unit>
}

// ==================== DATA CLASSES ====================

/**
 * Task statistics
 */
data class TaskStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val overdueTasks: Int,
    val completionRate: Float,
    val averageCompletionTime: Long,
    val tasksByType: Map<TaskType, Int>,
    val tasksByPriority: Map<TaskPriority, Int>
)

/**
 * Recurrence patterns for recurring tasks
 */
enum class RecurrencePattern {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    SEASONAL,
    CUSTOM
}