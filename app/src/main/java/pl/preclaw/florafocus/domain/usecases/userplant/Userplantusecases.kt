package pl.preclaw.florafocus.domain.usecase.userplant

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.UserPlantRepository
import javax.inject.Inject

/**
 * Get all user plants
 */
class GetUserPlantsUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(userId: String): Flow<List<UserPlant>> {
        return userPlantRepository.getUserPlants(userId)
    }
}

/**
 * Get user plant by ID
 */
class GetUserPlantByIdUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(plantId: String): UserPlant? {
        return userPlantRepository.getUserPlantById(plantId)
    }
}

/**
 * Create new user plant
 */
class CreateUserPlantUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(plant: UserPlant): Result<String> {
        return userPlantRepository.createUserPlant(plant)
    }
}

/**
 * Update user plant
 */
class UpdateUserPlantUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(plant: UserPlant): Result<Unit> {
        return userPlantRepository.updateUserPlant(plant)
    }
}

/**
 * Delete user plant
 */
class DeleteUserPlantUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(plantId: String): Result<Unit> {
        return userPlantRepository.deleteUserPlant(plantId)
    }
}

/**
 * Get user plants by location (bed)
 */
class GetUserPlantsByLocationUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(bedId: String): Flow<List<UserPlant>> {
        return userPlantRepository.getUserPlantsByLocation(bedId)
    }
}

/**
 * Get user plants by health status
 */
class GetUserPlantsByHealthUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(userId: String, status: HealthStatus): Flow<List<UserPlant>> {
        return userPlantRepository.getUserPlantsByHealthStatus(userId, status)
    }
}

/**
 * Get plants needing attention
 */
class GetPlantsNeedingAttentionUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(userId: String): List<UserPlant> {
        return userPlantRepository.getPlantsNeedingAttention(userId)
    }
}

/**
 * Search user plants
 */
class SearchUserPlantsUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(userId: String, query: String): Flow<List<UserPlant>> {
        return userPlantRepository.searchUserPlants(userId, query)
    }
}

/**
 * Update plant growth phase
 */
class UpdatePlantPhaseUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(
        plantId: String,
        newPhaseId: String,
        newPhaseName: GrowthPhaseName,
        confirmedByUser: Boolean = false
    ): Result<Unit> {
        return userPlantRepository.updatePlantPhase(
            plantId = plantId,
            newPhaseId = newPhaseId,
            newPhaseName = newPhaseName,
            confirmedByUser = confirmedByUser
        )
    }
}

/**
 * Get plant growth history
 */
class GetPlantGrowthHistoryUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(plantId: String): Flow<List<GrowthHistory>> {
        return userPlantRepository.getPlantGrowthHistory(plantId)
    }
}

/**
 * Add health record
 */
class AddHealthRecordUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(record: HealthRecord): Result<String> {
        return userPlantRepository.addHealthRecord(record)
    }
}

/**
 * Get health records
 */
class GetHealthRecordsUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(plantId: String): Flow<List<HealthRecord>> {
        return userPlantRepository.getHealthRecords(plantId)
    }
}

/**
 * Mark health issue as resolved
 */
class MarkHealthIssueResolvedUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(recordId: String): Result<Unit> {
        return userPlantRepository.markHealthIssueResolved(recordId)
    }
}

/**
 * Add intervention
 */
class AddInterventionUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(intervention: Intervention): Result<String> {
        return userPlantRepository.addIntervention(intervention)
    }
}

/**
 * Get interventions
 */
class GetInterventionsUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(plantId: String): Flow<List<Intervention>> {
        return userPlantRepository.getInterventions(plantId)
    }
}

/**
 * Add harvest record
 */
class AddHarvestRecordUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(harvest: HarvestRecord): Result<String> {
        return userPlantRepository.addHarvestRecord(harvest)
    }
}

/**
 * Get harvest records
 */
class GetHarvestRecordsUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(plantId: String): Flow<List<HarvestRecord>> {
        return userPlantRepository.getHarvestRecords(plantId)
    }
}

/**
 * Get total harvest for user
 */
class GetTotalHarvestUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(userId: String): Map<String, Float> {
        return userPlantRepository.getTotalHarvestForUser(userId)
    }
}

/**
 * Add propagation record
 */
class AddPropagationRecordUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(record: PropagationRecord): Result<String> {
        return userPlantRepository.addPropagationRecord(record)
    }
}

/**
 * Get propagation records
 */
class GetPropagationRecordsUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    operator fun invoke(parentPlantId: String): Flow<List<PropagationRecord>> {
        return userPlantRepository.getPropagationRecords(parentPlantId)
    }
}

/**
 * Get user plant statistics
 */
class GetUserPlantStatsUseCase @Inject constructor(
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(userId: String): UserPlantStats {
        val totalCount = userPlantRepository.getUserPlantCount(userId)
        val healthyCount = userPlantRepository.getHealthyPlantsCount(userId)
        val needingAttentionCount = totalCount - healthyCount

        return UserPlantStats(
            totalPlants = totalCount,
            healthyPlants = healthyCount,
            plantsNeedingAttention = needingAttentionCount,
            healthRate = if (totalCount > 0) (healthyCount.toFloat() / totalCount.toFloat()) else 0f
        )
    }
}

data class UserPlantStats(
    val totalPlants: Int,
    val healthyPlants: Int,
    val plantsNeedingAttention: Int,
    val healthRate: Float
)