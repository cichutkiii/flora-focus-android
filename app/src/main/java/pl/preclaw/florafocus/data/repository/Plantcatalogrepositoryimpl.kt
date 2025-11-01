package pl.preclaw.florafocus.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pl.preclaw.florafocus.data.local.dao.PlantCatalogDao
import pl.preclaw.florafocus.data.mapper.PlantMapper
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.PlantCatalogRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PlantCatalogRepository
 *
 * Handles plant catalog operations using Room database
 * Maps between entity and domain models using PlantMapper
 */
@Singleton
class PlantCatalogRepositoryImpl @Inject constructor(
    private val plantCatalogDao: PlantCatalogDao
) : PlantCatalogRepository {

    // ==================== QUERIES ====================

    override fun getAllPlants(): Flow<List<Plant>> {
        return plantCatalogDao.getAllPlants().map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override suspend fun getPlantById(plantId: String?): Plant? {
        if (plantId == null) return null
        return plantCatalogDao.getPlantById(plantId)?.let { PlantMapper.toDomain(it) }
    }

    override fun getPlantByIdFlow(plantId: String): Flow<Plant?> {
        return plantCatalogDao.getPlantByIdFlow(plantId).map { entity ->
            entity?.let { PlantMapper.toDomain(it) }
        }
    }

    override fun searchPlants(query: String): Flow<List<Plant>> {
        return plantCatalogDao.searchPlants(query).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override fun getPlantsByType(type: PlantType): Flow<List<Plant>> {
        val entityType = pl.preclaw.florafocus.data.local.entities.PlantType.valueOf(type.name)
        return plantCatalogDao.getPlantsByType(entityType).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override fun getPlantsByDifficulty(difficulty: GrowthDifficulty): Flow<List<Plant>> {
        val entityDifficulty = pl.preclaw.florafocus.data.local.entities.GrowthDifficulty.valueOf(difficulty.name)
        return plantCatalogDao.getPlantsByDifficulty(entityDifficulty).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override fun getPlantsByLightRequirements(light: LightRequirements): Flow<List<Plant>> {
        val entityLight = when(light) {
            LightRequirements.FULL_SUN -> pl.preclaw.florafocus.data.local.entities.LightRequirements.FULL_SUN
            LightRequirements.PARTIAL_SHADE -> pl.preclaw.florafocus.data.local.entities.LightRequirements.PARTIAL_SHADE
            LightRequirements.FULL_SHADE -> pl.preclaw.florafocus.data.local.entities.LightRequirements.SHADE
        }
        return plantCatalogDao.getPlantsByLightRequirements(entityLight).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override fun getEdiblePlants(): Flow<List<Plant>> {
        return plantCatalogDao.getEdiblePlants().map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override fun getPlantsByFamily(family: String): Flow<List<Plant>> {
        return plantCatalogDao.getPlantsByFamily(family).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override suspend fun getCompanionPlants(plantId: String): List<Plant> {
        val plant = plantCatalogDao.getPlantById(plantId) ?: return emptyList()

        if (plant.companionPlantIds.isEmpty()) return emptyList()

        return plantCatalogDao.getPlantsByIds(plant.companionPlantIds).map { PlantMapper.toDomain(it) }
    }

    override suspend fun getIncompatiblePlants(plantId: String): List<Plant> {
        val plant = plantCatalogDao.getPlantById(plantId) ?: return emptyList()

        if (plant.incompatiblePlantIds.isEmpty()) return emptyList()

        return plantCatalogDao.getPlantsByIds(plant.incompatiblePlantIds).map { PlantMapper.toDomain(it) }
    }

    override fun getPlantsInSowingPeriod(): Flow<List<Plant>> {
        // Pobierz aktualną datę w formacie MM-DD
        val currentDate = java.text.SimpleDateFormat("MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        return plantCatalogDao.getPlantsForSowingPeriod(currentDate).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override fun getFilteredPlants(
        type: PlantType?,
        difficulty: GrowthDifficulty?,
        light: LightRequirements?,
        edibleOnly: Boolean
    ): Flow<List<Plant>> {
        val entityType = type?.let { pl.preclaw.florafocus.data.local.entities.PlantType.valueOf(it.name) }
        val entityDifficulty = difficulty?.let { pl.preclaw.florafocus.data.local.entities.GrowthDifficulty.valueOf(it.name) }
        val entityLight = light?.let { domainLight ->
            when(domainLight) {
                LightRequirements.FULL_SUN -> pl.preclaw.florafocus.data.local.entities.LightRequirements.FULL_SUN
                LightRequirements.PARTIAL_SHADE -> pl.preclaw.florafocus.data.local.entities.LightRequirements.PARTIAL_SHADE
                LightRequirements.FULL_SHADE -> pl.preclaw.florafocus.data.local.entities.LightRequirements.SHADE
            }
        }

        return plantCatalogDao.searchPlantsAdvanced(
            nameQuery = null,
            plantType = entityType,
            difficulty = entityDifficulty,
            lightReq = entityLight,
            edibleOnly = edibleOnly,
            family = null
        ).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    override suspend fun getAllPlantFamilies(): List<String> {
        // Użyj Flow i konwertuj na listę
        return try {
            plantCatalogDao.getAllFamilies().first()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPlantsByIds(plantIds: List<String>): List<Plant> {
        return plantCatalogDao.getPlantsByIds(plantIds).map { PlantMapper.toDomain(it) }
    }

    override suspend fun getPlantCount(): Int {
        return plantCatalogDao.getPlantCount()
    }

    override suspend fun plantExists(plantId: String): Boolean {
        return plantCatalogDao.getPlantById(plantId) != null
    }

    // ==================== ADVANCED FILTERS ====================

    override fun filterPlants(
        types: List<PlantType>?,
        difficulties: List<GrowthDifficulty>?,
        lightRequirements: List<LightRequirements>?,
        edibleOnly: Boolean,
        searchQuery: String?
    ): Flow<List<Plant>> {
        // Dla tej metody użyjemy searchPlantsAdvanced z pierwszym typem z listy
        val entityType = types?.firstOrNull()?.let { pl.preclaw.florafocus.data.local.entities.PlantType.valueOf(it.name) }
        val entityDifficulty = difficulties?.firstOrNull()?.let { pl.preclaw.florafocus.data.local.entities.GrowthDifficulty.valueOf(it.name) }
        val entityLight = lightRequirements?.firstOrNull()?.let { domainLight ->
            when(domainLight) {
                LightRequirements.FULL_SUN -> pl.preclaw.florafocus.data.local.entities.LightRequirements.FULL_SUN
                LightRequirements.PARTIAL_SHADE -> pl.preclaw.florafocus.data.local.entities.LightRequirements.PARTIAL_SHADE
                LightRequirements.FULL_SHADE -> pl.preclaw.florafocus.data.local.entities.LightRequirements.SHADE
            }
        }

        return plantCatalogDao.searchPlantsAdvanced(
            nameQuery = searchQuery,
            plantType = entityType,
            difficulty = entityDifficulty,
            lightReq = entityLight,
            edibleOnly = edibleOnly,
            family = null
        ).map { entities ->
            entities.map { PlantMapper.toDomain(it) }
        }
    }

    // ==================== FIREBASE SYNC ====================

    override suspend fun syncCatalogFromFirebase(): Result<Unit> {
        return try {
            // TODO: Implement Firebase sync in Phase II
            // For now, just return success
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}