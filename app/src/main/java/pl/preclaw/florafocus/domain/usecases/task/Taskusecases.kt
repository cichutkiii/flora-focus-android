package pl.preclaw.florafocus.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.TaskRepository
import javax.inject.Inject

// ==================== BASIC CRUD ====================

/**
 * Get all tasks for user
 */
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.getTasks(userId)
    }
}

/**
 * Get task by ID
 */
class GetTaskByIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Task? {
        return taskRepository.getTaskById(taskId)
    }
}

/**
 * Create new task
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<String> {
        return taskRepository.createTask(task)
    }
}

/**
 * Update task
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return taskRepository.updateTask(task)
    }
}

/**
 * Delete task
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return taskRepository.deleteTask(taskId)
    }
}

// ==================== TASK QUERIES ====================

/**
 * Get tasks for today
 */
class GetTasksForTodayUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.getTasksForToday(userId)
    }
}

/**
 * Get upcoming tasks
 */
class GetUpcomingTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String, days: Int = 7): Flow<List<Task>> {
        return taskRepository.getUpcomingTasks(userId, days)
    }
}

/**
 * Get overdue tasks
 */
class GetOverdueTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.getOverdueTasks(userId)
    }
}

/**
 * Get tasks by plant
 */
class GetTasksByPlantUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(plantId: String): Flow<List<Task>> {
        return taskRepository.getTasksByPlant(plantId)
    }
}

/**
 * Get tasks by location (bed/area)
 */
class GetTasksByLocationUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(locationId: String): Flow<List<Task>> {
        return taskRepository.getTasksByLocation(locationId)
    }
}

/**
 * Get tasks by priority
 */
class GetTasksByPriorityUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String, priority: TaskPriority): Flow<List<Task>> {
        return taskRepository.getTasksByPriority(userId, priority)
    }
}

/**
 * Get tasks by type
 */
class GetTasksByTypeUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String, type: TaskType): Flow<List<Task>> {
        return taskRepository.getTasksByType(userId, type)
    }
}

/**
 * Get pending tasks
 */
class GetPendingTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.getPendingTasks(userId)
    }
}

/**
 * Get completed tasks
 */
class GetCompletedTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.getCompletedTasks(userId)
    }
}

// ==================== TASK ACTIONS ====================

/**
 * Complete task
 */
class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return taskRepository.completeTask(taskId)
    }
}

/**
 * Uncomplete task
 */
class UncompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return taskRepository.uncompleteTask(taskId)
    }
}

/**
 * Snooze task
 */
class SnoozeTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, days: Int): Result<Unit> {
        return taskRepository.snoozeTask(taskId, days)
    }
}

// ==================== RECURRING TASKS ====================

/**
 * Create recurring task
 */
class CreateRecurringTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        task: Task,
        pattern: RecurrencePattern,
        intervalDays: Int? = null
    ): Result<String> {
        return taskRepository.createRecurringTask(task, pattern, intervalDays)
    }
}

/**
 * Get recurring templates
 */
class GetRecurringTemplatesUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<RecurringTaskTemplate>> {
        return taskRepository.getRecurringTemplates(userId)
    }
}

/**
 * Create recurring template
 */
class CreateRecurringTemplateUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(template: RecurringTaskTemplate): Result<String> {
        return taskRepository.createRecurringTemplate(template)
    }
}

// ==================== AUTO-GENERATION ====================

/**
 * Generate tasks from growth phase
 *
 * Automatically creates tasks based on plant's current growth phase
 */
class GenerateTasksFromPhaseUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        plantId: String,
        phaseId: String,
        phaseName: GrowthPhaseName,
        autoTasks: List<AutoTask>
    ): Result<List<String>> {
        return taskRepository.generateTasksFromPhase(
            plantId = plantId,
            phaseId = phaseId,
            phaseName = phaseName,
            autoTasks = autoTasks
        )
    }
}

// ==================== STATISTICS ====================

/**
 * Get task statistics
 */
class GetTaskStatsUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(userId: String): TaskStats {
        return taskRepository.getTaskStats(userId)
    }
}

/**
 * Get task count by type
 */
class GetTaskCountByTypeUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(userId: String): Map<TaskType, Int> {
        return taskRepository.getTaskCountByType(userId)
    }
}

// ==================== COMPOSITE USE CASES ====================

/**
 * Get prioritized task list for dashboard
 *
 * Returns tasks sorted by priority and due date
 */
class GetPrioritizedTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.getPendingTasks(userId)
            .map { tasks ->
                tasks.sortedWith(
                    compareBy<Task> { task ->
                        when (task.priority) {
                            TaskPriority.URGENT -> 0
                            TaskPriority.HIGH -> 1
                            TaskPriority.MEDIUM -> 2
                            TaskPriority.LOW -> 3
                        }
                    }.thenBy { it.dueDate }
                )
            }
    }
}

/**
 * Get tasks grouped by location
 *
 * Groups tasks by bed/area for efficient garden management
 */
class GetTasksGroupedByLocationUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<Map<String, List<Task>>> {
        return taskRepository.getPendingTasks(userId)
            .map { tasks ->
                tasks
                    .filter { it.locationId != null }
                    .groupBy { it.locationName ?: "Unknown Location" }
            }
    }
}

/**
 * Get critical tasks (overdue + urgent)
 */
class GetCriticalTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String): Flow<List<Task>> {
        return taskRepository.getOverdueTasks(userId)
            .map { overdueTasks ->
                // Combine overdue with urgent tasks
                overdueTasks
            }
    }
}