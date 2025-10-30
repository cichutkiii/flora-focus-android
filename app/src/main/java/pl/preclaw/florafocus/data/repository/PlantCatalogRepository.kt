package pl.preclaw.florafocus.data.repository


import pl.preclaw.florafocus.data.local.entities.PlantCatalogEntity
import kotlinx.coroutines.flow.Flow

interface PlantCatalogRepository {
    /**
     * Get all plants from catalog
     */
    fun getAllPlants(): Flow<List<PlantCatalogEntity>>

    /**
     * Get single plant by ID
     */
    fun getPlantById(id: Long): Flow<PlantCatalogEntity?>

    /**
     * Search plants by name (case-insensitive)
     */
    fun searchPlants(query: String): Flow<List<PlantCatalogEntity>>

    /**
     * Get plants by category
     */
    fun getPlantsByCategory(category: String): Flow<List<PlantCatalogEntity>>

    /**
     * Get plants by difficulty
     */
    fun getPlantsByDifficulty(difficulty: String): Flow<List<PlantCatalogEntity>>

    /**
     * Refresh catalog from remote source (future implementation)
     */
    suspend fun refreshCatalog()
}