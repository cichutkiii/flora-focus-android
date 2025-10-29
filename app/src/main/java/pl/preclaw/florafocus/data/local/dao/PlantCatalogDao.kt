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

    // ==================== QUERIES ====================

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
     * Search plants by name (common or latin)
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE commonName LIKE '%' || :query || '%' 
        OR latinName LIKE '%' || :query || '%'
        ORDER BY commonName ASC
    """)
    fun searchPlants(query: String): Flow<List<PlantCatalogEntity>>

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
     * Get companion plants for a given plant
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE id IN (:companionIds)
        ORDER BY commonName ASC
    """)
    suspend fun getCompanionPlants(companionIds: List<String>): List<PlantCatalogEntity>

    /**
     * Get incompatible plants for a given plant
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE id IN (:incompatibleIds)
        ORDER BY commonName ASC
    """)
    suspend fun getIncompatiblePlants(incompatibleIds: List<String>): List<PlantCatalogEntity>

    /**
     * Get plants currently in sowing period
     * (Checks if current date is between sowingPeriodStart and sowingPeriodEnd)
     * Note: This is a simplified check, actual implementation would need date comparison
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE sowingPeriodStart IS NOT NULL 
        AND sowingPeriodEnd IS NOT NULL
        ORDER BY commonName ASC
    """)
    fun getPlantsInSowingPeriod(): Flow<List<PlantCatalogEntity>>

    /**
     * Advanced filter with multiple criteria
     */
    @Query("""
        SELECT * FROM plant_catalog 
        WHERE (:type IS NULL OR plantType = :type)
        AND (:difficulty IS NULL OR growthDifficulty = :difficulty)
        AND (:light IS NULL OR lightRequirements = :light)
        AND (:edibleOnly = 0 OR edible = 1)
        ORDER BY commonName ASC
    """)
    fun getFilteredPlants(
        type: PlantType?,
        difficulty: GrowthDifficulty?,
        light: LightRequirements?,
        edibleOnly: Boolean
    ): Flow<List<PlantCatalogEntity>>

    /**
     * Get count of plants in catalog
     */
    @Query("SELECT COUNT(*) FROM plant_catalog")
    suspend fun getPlantCount(): Int

    /**
     * Check if plant exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM plant_catalog WHERE id = :plantId)")
    suspend fun plantExists(plantId: String): Boolean

    /**
     * Get all plant families (for rotation planning)
     */
    @Query("SELECT DISTINCT family FROM plant_catalog WHERE family IS NOT NULL ORDER BY family ASC")
    suspend fun getAllPlantFamilies(): List<String>

    /**
     * Get plants by multiple IDs
     */
    @Query("SELECT * FROM plant_catalog WHERE id IN (:plantIds)")
    suspend fun getPlantsByIds(plantIds: List<String>): List<PlantCatalogEntity>

    @Query("SELECT * FROM plant_catalog WHERE id IN (:plantIds)")
    fun getPlantsByIdsFlow(plantIds: List<String>): Flow<List<PlantCatalogEntity>>
}
