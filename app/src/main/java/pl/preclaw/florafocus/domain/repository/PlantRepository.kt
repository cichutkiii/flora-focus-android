package pl.preclaw.florafocus.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*

/**
 * Repository interface for Plant operations
 * 
 * Defines the contract for plant data management
 * Implementation will handle both local (Room) and remote (Firebase) data sources
 */
interface PlantRepository {

    // ==================== PLANT CATALOG ====================

    /**
     * Get all plants from catalog
     */
    fun getAllPlants(): Flow<List<Plant>>

    /**
     * Get plant by ID
     */
    suspend fun getPlantById(plantId: String): Plant?

    /**
     * Get plant by ID as Flow (reactive)
     */
    fun getPlantByIdFlow(plantId: String): Flow<Plant?>

    /**
     * Search plants by name (common or latin)
     */
    fun searchPlants(query: String): Flow<List<Plant>>

    /**
     * Filter plants by type
     */
    fun getPlantsByType(type: PlantType): Flow<List<Plant>>

    /**
     * Filter plants by difficulty
     */
    fun getPlantsByDifficulty(difficulty: GrowthDifficulty): Flow<List<Plant>>

    /**
     * Filter plants by light requirements
     */
    fun getPlantsByLightRequirements(light: LightRequirements): Flow<List<Plant>>

    /**
     * Get edible plants only
     */
    fun getEdiblePlants(): Flow<List<Plant>>

    /**
     * Get plants by family (for rotation planning)
     */
    fun getPlantsByFamily(family: String): Flow<List<Plant>>

    /**
     * Get companion plants for a given plant
     */
    suspend fun getCompanionPlants(plantId: String): List<Plant>

    /**
     * Get incompatible plants for a given plant
     */
    suspend fun getIncompatiblePlants(plantId: String): List<Plant>

    /**
     * Get plants currently in sowing period
     */
    fun getPlantsInSowingPeriod(): Flow<List<Plant>>

    /**
     * Advanced filter with multiple criteria
     */
    fun getFilteredPlants(
        type: PlantType? = null,
        difficulty: GrowthDifficulty? = null,
        light: LightRequirements? = null,
        edibleOnly: Boolean = false
    ): Flow<List<Plant>>

    /**
     * Get all plant families (for rotation planning)
     */
    suspend fun getAllPlantFamilies(): List<String>

    /**
     * Get plants by multiple IDs
     */
    suspend fun getPlantsByIds(plantIds: List<String>): List<Plant>

    /**
     * Get count of plants in catalog
     */
    suspend fun getPlantCount(): Int

    /**
     * Check if plant exists
     */
    suspend fun plantExists(plantId: String): Boolean

    // ==================== USER PLANTS ====================

    /**
     * Get all user's plants
     */
    fun getUserPlants(userId: String): Flow<List<UserPlant>>

    /**
     * Get user's active plants only
     */
    fun getActiveUserPlants(userId: String): Flow<List<UserPlant>>

    /**
     * Get user plant by ID
     */
    suspend fun getUserPlantById(plantId: String): UserPlant?

    /**
     * Get user plant by ID as Flow
     */
    fun getUserPlantByIdFlow(plantId: String): Flow<UserPlant?>

    /**
     * Add new user plant
     */
    suspend fun addUserPlant(plant: UserPlant): Result<String>

    /**
     * Update user plant
     */
    suspend fun updateUserPlant(plant: UserPlant): Result<Unit>

    /**
     * Delete user plant
     */
    suspend fun deleteUserPlant(plantId: String): Result<Unit>

    /**
     * Get user plants by type
     */
    fun getUserPlantsByType(userId: String, type: PlantType): Flow<List<UserPlant>>

    /**
     * Get user plants in specific growth phase
     */
    fun getUserPlantsByPhase(userId: String, phase: GrowthPhaseName): Flow<List<UserPlant>>

    /**
     * Get user plants due for watering
     */
    fun getPlantsDueForWatering(userId: String, daysThreshold: Int): Flow<List<UserPlant>>

    /**
     * Get user plant count
     */
    suspend fun getUserPlantCount(userId: String): Int

    // ==================== GROWTH HISTORY ====================

    /**
     * Add growth history record
     */
    suspend fun addGrowthHistory(history: GrowthHistory): Result<Unit>

    /**
     * Get growth history for a plant
     */
    fun getGrowthHistory(plantId: String): Flow<List<GrowthHistory>>

    /**
     * Get current phase history
     */
    suspend fun getCurrentPhaseHistory(plantId: String): GrowthHistory?

    // ==================== HEALTH RECORDS ====================

    /**
     * Add health record
     */
    suspend fun addHealthRecord(record: HealthRecord): Result<Unit>

    /**
     * Update health record
     */
    suspend fun updateHealthRecord(record: HealthRecord): Result<Unit>

    /**
     * Get health records for a plant
     */
    fun getHealthRecords(plantId: String): Flow<List<HealthRecord>>

    /**
     * Get unresolved health issues
     */
    fun getUnresolvedHealthIssues(userId: String): Flow<List<HealthRecord>>

    // ==================== INTERVENTIONS ====================

    /**
     * Add intervention
     */
    suspend fun addIntervention(intervention: Intervention): Result<Unit>

    /**
     * Update intervention
     */
    suspend fun updateIntervention(intervention: Intervention): Result<Unit>

    /**
     * Get interventions for a plant
     */
    fun getInterventions(plantId: String): Flow<List<Intervention>>

    /**
     * Get recent interventions
     */
    fun getRecentInterventions(userId: String, days: Int): Flow<List<Intervention>>

    // ==================== HARVESTS ====================

    /**
     * Add harvest record
     */
    suspend fun addHarvestRecord(record: HarvestRecord): Result<Unit>

    /**
     * Update harvest record
     */
    suspend fun updateHarvestRecord(record: HarvestRecord): Result<Unit>

    /**
     * Get harvest records for a plant
     */
    fun getHarvestRecords(plantId: String): Flow<List<HarvestRecord>>

    /**
     * Get all harvests for user
     */
    fun getAllHarvestRecords(userId: String): Flow<List<HarvestRecord>>

    /**
     * Get harvest statistics
     */
    suspend fun getHarvestStatistics(userId: String, year: Int): HarvestStatistics

    // ==================== PROPAGATION ====================

    /**
     * Add propagation record
     */
    suspend fun addPropagationRecord(record: PropagationRecord): Result<Unit>

    /**
     * Update propagation record
     */
    suspend fun updatePropagationRecord(record: PropagationRecord): Result<Unit>

    /**
     * Get propagation records
     */
    fun getPropagationRecords(userId: String): Flow<List<PropagationRecord>>

    /**
     * Get propagation statistics
     */
    suspend fun getPropagationStatistics(userId: String): PropagationStatistics
}

/**
 * Harvest statistics
 */
data class HarvestStatistics(
    val totalHarvests: Int,
    val totalWeight: Float,
    val totalValue: Float,
    val byPlantType: Map<PlantType, Float>,
    val byMonth: Map<Int, Float>
)

/**
 * Propagation statistics
 */
data class PropagationStatistics(
    val totalAttempts: Int,
    val successfulAttempts: Int,
    val successRate: Float,
    val byMethod: Map<PropagationMethod, PropagationMethodStats>
)

data class PropagationMethodStats(
    val attempts: Int,
    val successes: Int,
    val successRate: Float
)
