package pl.preclaw.florafocus.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.data.local.entities.*

/**
 * Data Access Object for PlantCatalog
 * 
 * Provides methods to query the plant catalog database
 */
@Dao
interface PlantCatalogDao {

    // ==================== INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantCatalogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlants(plants: List<PlantCatalogEntity>)

    // ==================== UPDATE ====================

    @Update
    suspend fun updatePlant(plant: PlantCatalogEntity)

    // ==================== DELETE ====================

    @Delete
    suspend fun deletePlant(plant: PlantCatalogEntity)

    @Query("DELETE FROM plant_catalog WHERE id = :plantId")
    suspend fun deletePlantById(plantId: String)

    @Query("DELETE FROM plant_catalog")
    suspend fun deleteAllPlants()

    // ==================== BASIC QUERIES ====================

    /**
     * Get all plants from catalog
     */
    @Query("SELECT * FROM plant_catalog ORDER BY commonName ASC")
    fun getAllPlants(): Flow<List<PlantCatalogEntity>>

    /**
     * Get plant by ID
     */
    @Query("SELECT * FROM plant_catalog WHERE id = :plantId")
    suspend fun getPlantById(plantId: String): PlantCatalogEntity?

    @Query("SELECT * FROM plant_catalog WHERE id = :plantId")
    fun getPlantByIdFlow(plantId: String): Flow<PlantCatalogEntity?>

    /**
     * Get plant count for testing
     */
    @Query("SELECT COUNT(*) FROM plant_catalog")
    suspend fun getPlantCount(): Int

    /**
     * Get multiple plants by IDs
     */
    @Query("SELECT * FROM plant_catalog WHERE id IN (:plantIds)")
    suspend fun getPlantsByIds(plantIds: List<String>): List<PlantCatalogEntity>

    @Query("SELECT * FROM plant_catalog WHERE id IN (:plantIds)")
    fun getPlantsByIdsFlow(plantIds: List<String>): Flow<List<PlantCatalogEntity>>

    // ==================== SEARCH QUERIES ====================

    /**
     * Search plants by name (common or latin)
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE commonName LIKE '%' || :query || '%' 
        OR latinName LIKE '%' || :query || '%'
        OR family LIKE '%' || :query || '%'
        ORDER BY 
        CASE 
            WHEN commonName LIKE :query || '%' THEN 1
            WHEN latinName LIKE :query || '%' THEN 2
            WHEN commonName LIKE '%' || :query || '%' THEN 3
            ELSE 4
        END,
        commonName ASC
    """)
    fun searchPlants(query: String): Flow<List<PlantCatalogEntity>>

    /**
     * Advanced search with multiple criteria
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE (:nameQuery IS NULL OR commonName LIKE '%' || :nameQuery || '%' OR latinName LIKE '%' || :nameQuery || '%')
        AND (:plantType IS NULL OR plantType = :plantType)
        AND (:difficulty IS NULL OR growthDifficulty = :difficulty)
        AND (:lightReq IS NULL OR lightRequirements = :lightReq)
        AND (:edibleOnly = 0 OR edible = 1)
        AND (:family IS NULL OR family = :family)
        ORDER BY commonName ASC
    """)
    fun searchPlantsAdvanced(
        nameQuery: String?,
        plantType: PlantType?,
        difficulty: GrowthDifficulty?,
        lightReq: LightRequirements?,
        edibleOnly: Boolean = false,
        family: String?
    ): Flow<List<PlantCatalogEntity>>

    // ==================== FILTER QUERIES ====================

    /**
     * Filter by plant type
     */
    @Query("SELECT * FROM plant_catalog WHERE plantType = :type ORDER BY commonName ASC")
    fun getPlantsByType(type: PlantType): Flow<List<PlantCatalogEntity>>

    /**
     * Filter by difficulty
     */
    @Query("SELECT * FROM plant_catalog WHERE growthDifficulty = :difficulty ORDER BY commonName ASC")
    fun getPlantsByDifficulty(difficulty: GrowthDifficulty): Flow<List<PlantCatalogEntity>>

    /**
     * Filter by light requirements
     */
    @Query("SELECT * FROM plant_catalog WHERE lightRequirements = :light ORDER BY commonName ASC")
    fun getPlantsByLightRequirements(light: LightRequirements): Flow<List<PlantCatalogEntity>>

    /**
     * Get edible plants only
     */
    @Query("SELECT * FROM plant_catalog WHERE edible = 1 ORDER BY commonName ASC")
    fun getEdiblePlants(): Flow<List<PlantCatalogEntity>>

    /**
     * Get plants by family (for rotation planning)
     */
    @Query("SELECT * FROM plant_catalog WHERE family = :family ORDER BY commonName ASC")
    fun getPlantsByFamily(family: String): Flow<List<PlantCatalogEntity>>

    /**
     * Get all unique families
     */
    @Query("SELECT DISTINCT family FROM plant_catalog WHERE family IS NOT NULL ORDER BY family ASC")
    fun getAllFamilies(): Flow<List<String>>

    /**
     * Get plants by hardiness zone
     */
    @Query("SELECT * FROM plant_catalog WHERE hardiness LIKE '%' || :zone || '%' ORDER BY commonName ASC")
    fun getPlantsByHardinessZone(zone: String): Flow<List<PlantCatalogEntity>>

    // ==================== COMPANION PLANTING ====================

    @Query("SELECT companionPlantIds FROM plant_catalog WHERE id = :plantId")
    fun getCompanionPlantIds(plantId: String): Flow<List<String>>

    @Query("SELECT incompatiblePlantIds FROM plant_catalog WHERE id = :plantId")
    fun getIncompatiblePlantIds(plantId: String): Flow<List<String>>

    // ==================== SEASONAL QUERIES ====================

    /**
     * Get plants that can be sown in current period
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE sowingPeriodStart IS NOT NULL 
        AND sowingPeriodEnd IS NOT NULL
        AND (
            (sowingPeriodStart <= :currentDate AND sowingPeriodEnd >= :currentDate)
            OR (sowingPeriodStart > sowingPeriodEnd AND (sowingPeriodStart <= :currentDate OR sowingPeriodEnd >= :currentDate))
        )
        ORDER BY commonName ASC
    """)
    fun getPlantsForSowingPeriod(currentDate: String): Flow<List<PlantCatalogEntity>>

    /**
     * Get plants ready for harvest in current period
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE harvestPeriodStart IS NOT NULL 
        AND harvestPeriodEnd IS NOT NULL
        AND (
            (harvestPeriodStart <= :currentDate AND harvestPeriodEnd >= :currentDate)
            OR (harvestPeriodStart > harvestPeriodEnd AND (harvestPeriodStart <= :currentDate OR harvestPeriodEnd >= :currentDate))
        )
        ORDER BY commonName ASC
    """)
    fun getPlantsForHarvestPeriod(currentDate: String): Flow<List<PlantCatalogEntity>>

    // ==================== BEGINNER FRIENDLY ====================

    /**
     * Get beginner-friendly plants (easy difficulty)
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE growthDifficulty = 'EASY'
        ORDER BY commonName ASC
    """)
    fun getBeginnerFriendlyPlants(): Flow<List<PlantCatalogEntity>>

    // ==================== RECENT AND POPULAR ====================

    /**
     * Get recently added plants
     */
    @Query("""
        SELECT * FROM plant_catalog 
        ORDER BY createdAt DESC 
        LIMIT :limit
    """)
    fun getRecentlyAddedPlants(limit: Int = 10): Flow<List<PlantCatalogEntity>>

    /**
     * Get recently updated plants
     */
    @Query("""
        SELECT * FROM plant_catalog 
        ORDER BY updatedAt DESC 
        LIMIT :limit
    """)
    fun getRecentlyUpdatedPlants(limit: Int = 10): Flow<List<PlantCatalogEntity>>

    // ==================== STATISTICS ====================

    /**
     * Get count by plant type
     */
    @Query("SELECT plantType, COUNT(*) as count FROM plant_catalog GROUP BY plantType")
    fun getPlantCountByType(): Flow<Map<PlantType, Int>>

    /**
     * Get count by difficulty
     */
    @Query("SELECT growthDifficulty, COUNT(*) as count FROM plant_catalog GROUP BY growthDifficulty")
    fun getPlantCountByDifficulty(): Flow<Map<GrowthDifficulty, Int>>

    /**
     * Get count by family
     */
    @Query("SELECT family, COUNT(*) as count FROM plant_catalog WHERE family IS NOT NULL GROUP BY family ORDER BY count DESC")
    fun getPlantCountByFamily(): Flow<Map<String, Int>>
}
