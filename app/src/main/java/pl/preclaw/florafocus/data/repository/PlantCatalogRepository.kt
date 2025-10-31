package pl.preclaw.florafocus.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*

/**
 * Repository interface for Plant Catalog operations
 *
 * Handles read-only plant database (reference plants)
 * Separate from UserPlantRepository to follow Single Responsibility Principle
 */
interface PlantCatalogRepository {

    // ==================== QUERIES ====================

    fun getAllPlants(): Flow<List<Plant>>

    suspend fun getPlantById(plantId: String): Plant?

    fun getPlantByIdFlow(plantId: String): Flow<Plant?>

    fun searchPlants(query: String): Flow<List<Plant>>

    fun getPlantsByType(type: PlantType): Flow<List<Plant>>

    fun getPlantsByDifficulty(difficulty: GrowthDifficulty): Flow<List<Plant>>

    fun getPlantsByLightRequirements(light: LightRequirements): Flow<List<Plant>>

    fun getEdiblePlants(): Flow<List<Plant>>

    fun getPlantsByFamily(family: String): Flow<List<Plant>>

    suspend fun getCompanionPlants(plantId: String): List<Plant>

    suspend fun getIncompatiblePlants(plantId: String): List<Plant>

    fun getPlantsInSowingPeriod(): Flow<List<Plant>>

    fun getFilteredPlants(
        type: PlantType? = null,
        difficulty: GrowthDifficulty? = null,
        light: LightRequirements? = null,
        edibleOnly: Boolean = false
    ): Flow<List<Plant>>

    suspend fun getAllPlantFamilies(): List<String>

    suspend fun getPlantsByIds(plantIds: List<String>): List<Plant>

    suspend fun getPlantCount(): Int

    suspend fun plantExists(plantId: String): Boolean

    // ==================== ADVANCED FILTERS ====================

    fun filterPlants(
        types: List<PlantType>? = null,
        difficulties: List<GrowthDifficulty>? = null,
        lightRequirements: List<LightRequirements>? = null,
        edibleOnly: Boolean = false,
        searchQuery: String? = null
    ): Flow<List<Plant>>

    // ==================== FIREBASE SYNC ====================

    suspend fun syncCatalogFromFirebase(): Result<Unit>
}