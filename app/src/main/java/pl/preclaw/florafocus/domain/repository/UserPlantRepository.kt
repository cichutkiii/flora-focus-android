package pl.preclaw.florafocus.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*

/**
 * Repository interface for User Plant operations
 *
 * Handles user's actual plant instances with full tracking:
 * - Growth phases and history
 * - Health records and monitoring
 * - Interventions and treatments
 * - Harvest records
 * - Propagation tracking
 */
interface UserPlantRepository {

    // ==================== BASIC CRUD ====================

    /**
     * Get all user plants
     */
    fun getUserPlants(userId: String): Flow<List<UserPlant>>

    /**
     * Get user plant by ID
     */
    suspend fun getUserPlantById(plantId: String): UserPlant?

    /**
     * Get user plant by ID as Flow
     */
    fun getUserPlantByIdFlow(plantId: String): Flow<UserPlant?>

    /**
     * Create new user plant
     */
    suspend fun createUserPlant(plant: UserPlant): Result<String>

    /**
     * Update user plant
     */
    suspend fun updateUserPlant(plant: UserPlant): Result<Unit>

    /**
     * Delete user plant
     */
    suspend fun deleteUserPlant(plantId: String): Result<Unit>

    // ==================== QUERIES & FILTERS ====================

    /**
     * Get user plants by location (bed)
     */
    fun getUserPlantsByLocation(areaObjectBedId: String): Flow<List<UserPlant>>

    /**
     * Get user plants by health status
     */
    fun getUserPlantsByHealthStatus(userId: String, status: HealthStatus): Flow<List<UserPlant>>

    /**
     * Get user plants by growth phase
     */
    fun getUserPlantsByPhase(userId: String, phase: GrowthPhaseName): Flow<List<UserPlant>>

    /**
     * Search user plants by name/variety
     */
    fun searchUserPlants(userId: String, query: String): Flow<List<UserPlant>>

    /**
     * Get plants needing attention (unhealthy/overdue care)
     */
    suspend fun getPlantsNeedingAttention(userId: String): List<UserPlant>

    /**
     * Get user plant count
     */
    suspend fun getUserPlantCount(userId: String): Int

    /**
     * Get healthy plants count
     */
    suspend fun getHealthyPlantsCount(userId: String): Int

    // ==================== GROWTH PHASE TRACKING ====================

    /**
     * Update plant growth phase
     */
    suspend fun updatePlantPhase(
        plantId: String,
        newPhaseId: String,
        newPhaseName: GrowthPhaseName,
        confirmedByUser: Boolean
    ): Result<Unit>

    /**
     * Get plant growth history
     */
    fun getPlantGrowthHistory(plantId: String): Flow<List<PlantGrowthHistory>>

    /**
     * Add growth history record
     */
    suspend fun addGrowthHistory(history: GrowthHistory): Result<String>

    // ==================== HEALTH TRACKING ====================

    /**
     * Add health record
     */
    suspend fun addHealthRecord(record: HealthRecord): Result<String>

    /**
     * Get health records for plant
     */
    fun getHealthRecords(plantId: String): Flow<List<pl.preclaw.florafocus.data.repository.HealthRecord>>

    /**
     * Get latest health record
     */
    suspend fun getLatestHealthRecord(plantId: String): HealthRecord?

    /**
     * Mark health issue as resolved
     */
    suspend fun markHealthIssueResolved(recordId: String): Result<Unit>

    /**
     * Update plant health status
     */
    suspend fun updatePlantHealthStatus(
        plantId: String,
        status: HealthStatus,
        healthScore: Int
    ): Result<Unit>

    // ==================== INTERVENTIONS ====================

    /**
     * Add intervention record
     */
    suspend fun addIntervention(intervention: Intervention): Result<String>

    /**
     * Get interventions for plant
     */
    fun getInterventions(plantId: String): Flow<List<Intervention>>

    /**
     * Get recent interventions for user
     */
    fun getRecentInterventions(userId: String, days: Int = 30): Flow<List<Intervention>>

    /**
     * Update intervention completion
     */
    suspend fun updateInterventionCompletion(
        interventionId: String,
        completed: Boolean
    ): Result<Unit>

    // ==================== HARVEST TRACKING ====================

    /**
     * Add harvest record
     */
    suspend fun addHarvestRecord(harvest: HarvestRecord): Result<String>

    /**
     * Get harvest records for plant
     */
    fun getHarvestRecords(plantId: String): Flow<List<HarvestRecord>>

    /**
     * Get harvest records for user
     */
    fun getUserHarvestRecords(userId: String): Flow<List<HarvestRecord>>

    /**
     * Get total harvest for user (by crop type)
     */
    suspend fun getTotalHarvestForUser(userId: String): Map<String, Float>

    /**
     * Get harvest statistics
     */
    suspend fun getHarvestStats(userId: String): HarvestStats

    // ==================== PROPAGATION TRACKING ====================

    /**
     * Add propagation record
     */
    suspend fun addPropagationRecord(record: PropagationRecord): Result<String>

    /**
     * Get propagation records for parent plant
     */
    fun getPropagationRecords(parentPlantId: String): Flow<List<PropagationRecord>>

    /**
     * Get propagation records for user
     */
    fun getUserPropagationRecords(userId: String): Flow<List<PropagationRecord>>

    /**
     * Update propagation status
     */
    suspend fun updatePropagationStatus(
        recordId: String,
        status: PropagationStatus
    ): Result<Unit>

    // ==================== CARE TRACKING ====================

    /**
     * Update last watered date
     */
    suspend fun updateLastWatered(plantId: String, date: Long): Result<Unit>

    /**
     * Update last fertilized date
     */
    suspend fun updateLastFertilized(plantId: String, date: Long): Result<Unit>

    /**
     * Get plants needing watering
     */
    fun getPlantsNeedingWatering(userId: String): Flow<List<UserPlant>>

    /**
     * Get plants needing fertilizing
     */
    fun getPlantsNeedingFertilizing(userId: String): Flow<List<UserPlant>>

    // ==================== LOCATION MANAGEMENT ====================

    /**
     * Assign plant to cell
     */
    suspend fun assignPlantToCell(
        plantId: String,
        bedId: String,
        cellRow: Int,
        cellColumn: Int
    ): Result<Unit>

    /**
     * Remove plant from cell
     */
    suspend fun removePlantFromCell(plantId: String): Result<Unit>

    /**
     * Move plant to new cell
     */
    suspend fun movePlantToCell(
        plantId: String,
        newBedId: String,
        newCellRow: Int,
        newCellColumn: Int
    ): Result<Unit>

    // ==================== FIREBASE SYNC ====================

    /**
     * Sync user plants with Firebase
     */
    suspend fun syncUserPlantsWithFirebase(userId: String): Result<Unit>

    /**
     * Upload plant images to Firebase Storage
     */
    suspend fun uploadPlantImages(plantId: String, imageUris: List<String>): Result<List<String>>
}

// ==================== DATA CLASSES ====================

/**
 * Harvest statistics for user
 */
data class HarvestStats(
    val totalPlants: Int,
    val plantsHarvested: Int,
    val totalWeight: Float,
    val totalValue: Float,
    val mostProductivePlant: String?,
    val harvestsByMonth: Map<String, Float>
)