package pl.preclaw.florafocus.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import pl.preclaw.florafocus.data.local.dao.TaskDao
import pl.preclaw.florafocus.data.mapper.TaskMapper
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.TaskRepository
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TaskRepository
 *
 * Manages tasks with support for:
 * - Manual and auto-generated tasks
 * - Recurring tasks
 * - Priority management
 * - Weather-dependent rescheduling
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore,
    private val taskMapper: TaskMapper
) : TaskRepository {

    companion object {
        private const val COLLECTION_TASKS = "tasks"
        private const val TAG = "TaskRepository"
    }

    // ==================== TASK CRUD ====================

    override fun getTasks(userId: String): Flow<List<Task>> {
        return taskDao.getUserTasks(userId)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)?.let {
            taskMapper.toDomain(it)
        }
    }

    override fun getTaskByIdFlow(taskId: String): Flow<Task?> {
        return taskDao.getTaskByIdFlow(taskId)
            .map { entity -> entity?.let { taskMapper.toDomain(it) } }
    }

    override suspend fun createTask(task: Task): Result<String> {
        return try {
            val entity = taskMapper.toEntity(task)
            taskDao.insertTask(entity)

            Timber.tag(TAG).d("Created task: ${task.title}")
            Result.success(task.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating task")
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val entity = taskMapper.toEntity(task)
            taskDao.updateTask(entity)

            Timber.tag(TAG).d("Updated task: ${task.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating task")
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            taskDao.deleteTaskById(taskId)

            Timber.tag(TAG).d("Deleted task: $taskId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting task")
            Result.failure(e)
        }
    }

    // ==================== TASK QUERIES ====================

    override fun getTasksByPlant(plantId: String): Flow<List<Task>> {
        return taskDao.getTasksForPlant(plantId)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override fun getTasksByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Task>> {
        return taskDao.getTasksByDateRange(userId, startDate, endDate)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override fun getTasksForToday(userId: String): Flow<List<Task>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        return getTasksByDateRange(userId, startOfDay, endOfDay)
    }

    override fun getUpcomingTasks(userId: String, days: Int): Flow<List<Task>> {
        val now = System.currentTimeMillis()
        val futureDate = now + (days * 24 * 60 * 60 * 1000L)

        return getTasksByDateRange(userId, now, futureDate)
    }

    override fun getOverdueTasks(userId: String): Flow<List<Task>> {
        return taskDao.getOverdueTasks(userId, System.currentTimeMillis())
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override fun getTasksByPriority(
        userId: String,
        priority: TaskPriority
    ): Flow<List<Task>> {
        return taskDao.getTasksByPriority(userId, priority)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override fun getTasksByType(
        userId: String,
        type: TaskType
    ): Flow<List<Task>> {
        return taskDao.getTasksByType(userId, type)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override fun getTasksByLocation(locationId: String): Flow<List<Task>> {
        return taskDao.getTasksByLocation(locationId)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override fun getCompletedTasks(userId: String): Flow<List<Task>> {
        return taskDao.getCompletedTasks(userId)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override fun getPendingTasks(userId: String): Flow<List<Task>> {
        return taskDao.getPendingTasks(userId)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    // ==================== TASK COMPLETION ====================

    override suspend fun completeTask(taskId: String): Result<Unit> {
        return try {
            val task = taskDao.getTaskById(taskId)
                ?: return Result.failure(Exception("Task not found"))

            val completedTask = task.copy(
                isCompleted = true,
                completedDate = System.currentTimeMillis()
            )

            taskDao.updateTask(completedTask)

            // Handle recurring tasks
            if (task.isRecurring && task.recurrencePattern != null) {
                createNextRecurringTask(task)
            }

            Timber.tag(TAG).d("Completed task: $taskId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error completing task")
            Result.failure(e)
        }
    }

    override suspend fun uncompleteTask(taskId: String): Result<Unit> {
        return try {
            val task = taskDao.getTaskById(taskId)
                ?: return Result.failure(Exception("Task not found"))

            taskDao.updateTask(
                task.copy(
                    isCompleted = false,
                    completedDate = null
                )
            )

            Timber.tag(TAG).d("Uncompleted task: $taskId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error uncompleting task")
            Result.failure(e)
        }
    }

    override suspend fun snoozeTask(taskId: String, days: Int): Result<Unit> {
        return try {
            val task = taskDao.getTaskById(taskId)
                ?: return Result.failure(Exception("Task not found"))

            val newDueDate = task.dueDate + (days * 24 * 60 * 60 * 1000L)

            taskDao.updateTask(
                task.copy(
                    dueDate = newDueDate,
                    originalDueDate = task.originalDueDate ?: task.dueDate
                )
            )

            Timber.tag(TAG).d("Snoozed task $taskId by $days days")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error snoozing task")
            Result.failure(e)
        }
    }

    // ==================== RECURRING TASKS ====================

    override suspend fun createRecurringTask(
        task: Task,
        pattern: RecurrencePattern,
        intervalDays: Int?
    ): Result<String> {
        return try {
            val recurringTask = task.copy(
                isRecurring = true,
                recurrencePattern = pattern,
                recurrenceIntervalDays = intervalDays
            )

            val entity = taskMapper.toEntity(recurringTask)
            taskDao.insertTask(entity)

            Timber.tag(TAG).d("Created recurring task: ${task.title}")
            Result.success(task.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating recurring task")
            Result.failure(e)
        }
    }

    private suspend fun createNextRecurringTask(completedTask: pl.preclaw.florafocus.data.local.entities.TaskEntity) {
        try {
            val pattern = completedTask.recurrencePattern ?: return
            val intervalDays = completedTask.recurrenceIntervalDays ?: return

            val nextDueDate = calculateNextDueDate(
                completedTask.dueDate,
                pattern,
                intervalDays
            )

            val nextTask = completedTask.copy(
                id = java.util.UUID.randomUUID().toString(),
                dueDate = nextDueDate,
                originalDueDate = null,
                isCompleted = false,
                completedDate = null
            )

            taskDao.insertTask(nextTask)

            Timber.tag(TAG).d("Created next recurring task instance")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating next recurring task")
        }
    }

    private fun calculateNextDueDate(
        currentDueDate: Long,
        pattern: RecurrencePattern,
        intervalDays: Int
    ): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDueDate

        when (pattern) {
            RecurrencePattern.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            RecurrencePattern.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            RecurrencePattern.BI_WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 2)
            RecurrencePattern.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            RecurrencePattern.CUSTOM -> calendar.add(Calendar.DAY_OF_MONTH, intervalDays)
        }

        return calendar.timeInMillis
    }

    // ==================== RECURRING TASK TEMPLATES ====================

    override suspend fun createRecurringTemplate(template: RecurringTaskTemplate): Result<String> {
        return try {
            val entity = taskMapper.toEntity(template)
            taskDao.insertRecurringTemplate(entity)

            Timber.tag(TAG).d("Created recurring template: ${template.title}")
            Result.success(template.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating recurring template")
            Result.failure(e)
        }
    }

    override fun getRecurringTemplates(userId: String): Flow<List<RecurringTaskTemplate>> {
        return taskDao.getRecurringTemplates(userId)
            .map { entities ->
                entities.map { taskMapper.toDomain(it) }
            }
    }

    override suspend fun updateRecurringTemplate(template: RecurringTaskTemplate): Result<Unit> {
        return try {
            val entity = taskMapper.toEntity(template)
            taskDao.updateRecurringTemplate(entity)

            Timber.tag(TAG).d("Updated recurring template: ${template.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating recurring template")
            Result.failure(e)
        }
    }

    override suspend fun deleteRecurringTemplate(templateId: String): Result<Unit> {
        return try {
            taskDao.deleteRecurringTemplateById(templateId)

            Timber.tag(TAG).d("Deleted recurring template: $templateId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting recurring template")
            Result.failure(e)
        }
    }

    // ==================== AUTO-GENERATION ====================

    override suspend fun generateTasksFromPhase(
        plantId: String,
        phaseId: String,
        phaseName: GrowthPhaseName,
        autoTasks: List<AutoTask>
    ): Result<List<String>> {
        return try {
            val taskIds = mutableListOf<String>()

            autoTasks.forEach { autoTask ->
                val taskId = java.util.UUID.randomUUID().toString()
                val dueDate = System.currentTimeMillis() +
                        (autoTask.triggerDayOffset * 24 * 60 * 60 * 1000L)

                val task = pl.preclaw.florafocus.data.local.entities.TaskEntity(
                    id = taskId,
                    userId = "", // Will be set by caller
                    plantId = plantId,
                    title = autoTask.taskTitle,
                    description = autoTask.taskDescription,
                    dueDate = dueDate,
                    dueDateFlexible = true,
                    originalDueDate = null,
                    completedDate = null,
                    isCompleted = false,
                    isRecurring = false,
                    recurrencePattern = null,
                    recurrenceIntervalDays = null,
                    priority = autoTask.priority,
                    taskType = autoTask.taskType,
                    autoGenerated = true,
                    generatedByPhaseId = phaseId,
                    weatherDependent = autoTask.taskType in listOf(
                        TaskType.WATERING,
                        TaskType.TRANSPLANTING,
                        TaskType.PEST_CONTROL
                    ),
                    locationId = null,
                    locationName = null,
                    assignedTo = null,
                    reminderEnabled = true,
                    reminderTimeBefore = 60 // 1 hour before
                )

                taskDao.insertTask(task)
                taskIds.add(taskId)
            }

            Timber.tag(TAG).d("Generated ${taskIds.size} auto-tasks for phase $phaseName")
            Result.success(taskIds)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error generating auto-tasks")
            Result.failure(e)
        }
    }

    // ==================== STATISTICS ====================

    override suspend fun getTaskStats(userId: String): TaskStats {
        return try {
            val total = taskDao.getTaskCount(userId)
            val completed = taskDao.getCompletedTaskCount(userId)
            val pending = taskDao.getPendingTaskCount(userId)
            val overdue = taskDao.getOverdueTaskCount(userId, System.currentTimeMillis())

            TaskStats(
                total = total,
                completed = completed,
                pending = pending,
                overdue = overdue,
                completionRate = if (total > 0) (completed.toFloat() / total.toFloat()) else 0f
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting task stats")
            TaskStats(0, 0, 0, 0, 0f)
        }
    }

    override suspend fun getTaskCountByType(userId: String): Map<TaskType, Int> {
        return try {
            val tasks = taskDao.getAllUserTasks(userId)
            tasks.groupBy { it.taskType }
                .mapValues { it.value.size }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting task count by type")
            emptyMap()
        }
    }

    // ==================== FIREBASE SYNC (Future) ====================

    override suspend fun syncTasksWithFirebase(userId: String): Result<Unit> {
        return try {
            // TODO: Implement Firebase sync in Phase II
            Timber.tag(TAG).d("Firebase sync not yet implemented")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error syncing with Firebase")
            Result.failure(e)
        }
    }
}