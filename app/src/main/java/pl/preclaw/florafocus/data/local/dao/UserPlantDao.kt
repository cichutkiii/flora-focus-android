package pl.preclaw.florafocus.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.data.local.entities.*

/**
 * Data Access Object for UserPlant and related entities
 */
@Dao
interface UserPlantDao {

    // ==================== USER PLANT - INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: UserPlantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlants(plants: List<UserPlantEntity>)

    // ==================== USER PLANT - UPDATE ====================

    @Update
    suspend fun updatePlant(plant: UserPlantEntity)

    /**
     * Update plant location in garden
     */
    @Query("""
        UPDATE user_plants 
        SET areaObjectBedId = :bedId, 
            cellRow = :row, 
            cellColumn = :column,
            updatedAt = :timestamp
        WHERE id = :plantId
    """)
    suspend fun updatePlantLocation(
        plantId: String,
        bedId: String?,
        row: Int?,
        column: Int?,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update current growth phase
     */
    @Query("""
        UPDATE user_plants 
        SET currentPhaseId = :phaseId,
            currentPhaseName = :phaseName,
            phaseStartDate = :startDate,
            updatedAt = :timestamp
        WHERE id = :plantId
    """)
    suspend fun updateCurrentPhase(
        plantId: String,
        phaseId: String,
        phaseName: GrowthPhaseName,
        startDate: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update health status
     */
    @Query("""
        UPDATE user_plants 
        SET healthStatus = :status,
            healthScore = :score,
            lastHealthCheckDate = :checkDate,
            updatedAt = :timestamp
        WHERE id = :plantId
    """)
    suspend fun updateHealthStatus(
        plantId: String,
        status: HealthStatus,
        score: Int,
        checkDate: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update last watered date
     */
    @Query("UPDATE user_plants SET lastWateredDate = :date, updatedAt = :timestamp WHERE id = :plantId")
    suspend fun updateLastWatered(
        plantId: String,
        date: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update last fertilized date
     */
    @Query("UPDATE user_plants SET lastFertilizedDate = :date, updatedAt = :timestamp WHERE id = :plantId")
    suspend fun updateLastFertilized(
        plantId: String,
        date: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Mark plant as inactive (died or removed)
     */
    @Query("UPDATE user_plants SET isActive = 0, updatedAt = :timestamp WHERE id = :plantId")
    suspend fun markPlantInactive(plantId: String, timestamp: Long = System.currentTimeMillis())

    // ==================== USER PLANT - DELETE ====================

    @Delete
    suspend fun deletePlant(plant: UserPlantEntity)

    @Query("DELETE FROM user_plants WHERE id = :plantId")
    suspend fun deletePlantById(plantId: String)

    // ==================== USER PLANT - QUERIES ====================

    /**
     * Get all user plants
     */
    @Query("SELECT * FROM user_plants WHERE userId = :userId AND isActive = 1 ORDER BY customName ASC")
    fun getAllPlants(userId: String): Flow<List<UserPlantEntity>>

    /**
     * Get plant by ID
     */
    @Query("SELECT * FROM user_plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: String): UserPlantEntity?

    @Query("SELECT * FROM user_plants WHERE id = :plantId")
    fun getPlantByIdFlow(plantId: String): Flow<UserPlantEntity?>

    /**
     * Get plants by health status
     */
    @Query("SELECT * FROM user_plants WHERE userId = :userId AND healthStatus = :status AND isActive = 1 ORDER BY customName ASC")
    fun getPlantsByHealthStatus(userId: String, status: HealthStatus): Flow<List<UserPlantEntity>>

    /**
     * Get plants needing attention
     */
    @Query("""
        SELECT * FROM user_plants 
        WHERE userId = :userId 
        AND isActive = 1
        AND (healthStatus = 'NEEDS_ATTENTION' OR healthStatus = 'SICK')
        ORDER BY healthStatus DESC, customName ASC
    """)
    fun getPlantsNeedingAttention(userId: String): Flow<List<UserPlantEntity>>

    /**
     * Get plants in specific location
     */
    @Query("SELECT * FROM user_plants WHERE userId = :userId AND areaObjectBedId = :bedId AND isActive = 1 ORDER BY cellRow, cellColumn")
    fun getPlantsInBed(userId: String, bedId: String): Flow<List<UserPlantEntity>>

    /**
     * Get plants by catalog type
     */
    @Query("""
        SELECT up.* FROM user_plants up
        INNER JOIN plant_catalog pc ON up.catalogPlantId = pc.id
        WHERE up.userId = :userId AND pc.plantType = :type AND up.isActive = 1
        ORDER BY up.customName ASC
    """)
    fun getPlantsByType(userId: String, type: PlantType): Flow<List<UserPlantEntity>>

    /**
     * Get plants in specific growth phase
     */
    @Query("SELECT * FROM user_plants WHERE userId = :userId AND currentPhaseName = :phase AND isActive = 1 ORDER BY customName ASC")
    fun getPlantsByPhase(userId: String, phase: GrowthPhaseName): Flow<List<UserPlantEntity>>

    /**
     * Get count of user plants
     */
    @Query("SELECT COUNT(*) FROM user_plants WHERE userId = :userId AND isActive = 1")
    suspend fun getPlantCount(userId: String): Int

    /**
     * Get plants due for watering (last watered > X days ago)
     */
    @Query("""
        SELECT * FROM user_plants 
        WHERE userId = :userId 
        AND isActive = 1
        AND (lastWateredDate IS NULL OR lastWateredDate < :thresholdDate)
        ORDER BY lastWateredDate ASC NULLS FIRST
    """)
    fun getPlantsDueForWatering(userId: String, thresholdDate: Long): Flow<List<UserPlantEntity>>

    // ==================== GROWTH HISTORY ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowthHistory(history: PlantGrowthHistoryEntity)

    @Query("SELECT * FROM plant_growth_history WHERE plantId = :plantId ORDER BY startDate DESC")
    fun getGrowthHistory(plantId: String): Flow<List<PlantGrowthHistoryEntity>>

    @Query("SELECT * FROM plant_growth_history WHERE plantId = :plantId AND endDate IS NULL LIMIT 1")
    suspend fun getCurrentPhaseHistory(plantId: String): PlantGrowthHistoryEntity?

    /**
     * End current phase
     */
    @Query("UPDATE plant_growth_history SET endDate = :endDate WHERE id = :historyId")
    suspend fun endPhase(historyId: String, endDate: Long)

    // ==================== HEALTH RECORDS ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecordEntity)

    @Update
    suspend fun updateHealthRecord(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records WHERE plantId = :plantId ORDER BY recordDate DESC")
    fun getHealthRecords(plantId: String): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE plantId = :plantId AND resolved = 0 ORDER BY recordDate DESC")
    fun getUnresolvedHealthRecords(plantId: String): Flow<List<HealthRecordEntity>>

    @Query("UPDATE health_records SET resolved = 1, resolvedDate = :date WHERE id = :recordId")
    suspend fun markHealthRecordResolved(recordId: String, date: Long)

    // ==================== INTERVENTIONS ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntervention(intervention: InterventionEntity)

    @Query("SELECT * FROM interventions WHERE plantId = :plantId ORDER BY interventionDate DESC")
    fun getInterventions(plantId: String): Flow<List<InterventionEntity>>

    @Query("SELECT * FROM interventions WHERE plantId = :plantId AND interventionType = :type ORDER BY interventionDate DESC LIMIT 1")
    suspend fun getLastInterventionOfType(plantId: String, type: InterventionType): InterventionEntity?

    @Query("DELETE FROM interventions WHERE id = :interventionId")
    suspend fun deleteIntervention(interventionId: String)

    // ==================== HARVEST RECORDS ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvestRecord(record: HarvestRecordEntity)

    @Update
    suspend fun updateHarvestRecord(record: HarvestRecordEntity)

    @Query("SELECT * FROM harvest_records WHERE plantId = :plantId ORDER BY harvestDate DESC")
    fun getHarvestRecords(plantId: String): Flow<List<HarvestRecordEntity>>

    @Query("SELECT SUM(quantity) FROM harvest_records WHERE plantId = :plantId AND unit = :unit")
    suspend fun getTotalHarvest(plantId: String, unit: HarvestUnit): Float?

    @Query("""
        SELECT hr.* FROM harvest_records hr
        INNER JOIN user_plants up ON hr.plantId = up.id
        WHERE up.userId = :userId 
        AND hr.harvestDate >= :startDate 
        AND hr.harvestDate <= :endDate
        ORDER BY hr.harvestDate DESC
    """)
    fun getHarvestsInDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<HarvestRecordEntity>>

    @Query("DELETE FROM harvest_records WHERE id = :recordId")
    suspend fun deleteHarvestRecord(recordId: String)

    // ==================== PROPAGATION RECORDS ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPropagationRecord(record: PropagationRecordEntity)

    @Update
    suspend fun updatePropagationRecord(record: PropagationRecordEntity)

    @Query("SELECT * FROM propagation_records WHERE userId = :userId ORDER BY startDate DESC")
    fun getPropagationRecords(userId: String): Flow<List<PropagationRecordEntity>>

    @Query("SELECT * FROM propagation_records WHERE parentPlantId = :plantId ORDER BY startDate DESC")
    fun getPropagationRecordsForPlant(plantId: String): Flow<List<PropagationRecordEntity>>

    @Query("SELECT * FROM propagation_records WHERE userId = :userId AND status = :status ORDER BY startDate DESC")
    fun getPropagationRecordsByStatus(userId: String, status: PropagationStatus): Flow<List<PropagationRecordEntity>>

    @Query("DELETE FROM propagation_records WHERE id = :recordId")
    suspend fun deletePropagationRecord(recordId: String)

    // ==================== STATISTICS ====================

    /**
     * Get statistics for propagation success rate
     */
    @Query("""
        SELECT method, 
               COUNT(*) as totalAttempts,
               SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as successfulAttempts
        FROM propagation_records
        WHERE userId = :userId
        GROUP BY method
    """)
    suspend fun getPropagationStatsByMethod(userId: String): List<PropagationStatsResult>
}

/**
 * Result class for propagation statistics query
 */
data class PropagationStatsResult(
    val method: PropagationMethod,
    val totalAttempts: Int,
    val successfulAttempts: Int
)
