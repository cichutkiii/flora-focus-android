package pl.preclaw.florafocus.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*

/**
 * Repository interface for Task operations
 */
interface TaskRepository {

    // ==================== TASKS ====================

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

    /**
     * Complete task
     */
    suspend fun completeTask(taskId: String, completedDate: Long): Result<Unit>

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
     * Get tasks for specific bed
     */
    fun getTasksForBed(bedId: String): Flow<List<Task>>

    /**
     * Get pending tasks count
     */
    suspend fun getPendingTasksCount(userId: String): Int

    // ==================== RECURRING TEMPLATES ====================

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
     * Generate tasks from recurring template
     */
    suspend fun generateTasksFromTemplate(templateId: String): Result<List<String>>

    // ==================== AUTO-GENERATION ====================

    /**
     * Generate tasks for plant based on growth phase
     */
    suspend fun generateTasksForPlant(
        plantId: String,
        phaseId: String
    ): Result<List<String>>

    /**
     * Generate watering tasks based on weather forecast
     */
    suspend fun generateWateringTasks(
        userId: String,
        forecastDays: Int
    ): Result<List<String>>

    /**
     * Reschedule weather-dependent tasks
     */
    suspend fun rescheduleWeatherDependentTasks(userId: String): Result<Int>

    // ==================== ANALYTICS ====================

    /**
     * Get task completion statistics
     */
    suspend fun getTaskStatistics(userId: String): TaskStatistics

    /**
     * Get task completion rate by type
     */
    suspend fun getCompletionRateByType(userId: String): Map<TaskType, Float>
}

/**
 * Task statistics
 */
data class TaskStatistics(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val overdueTasks: Int,
    val completionRate: Float,
    val averageCompletionTime: Long, // milliseconds
    val tasksByPriority: Map<TaskPriority, Int>,
    val tasksByType: Map<TaskType, Int>
)
