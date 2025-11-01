package pl.preclaw.florafocus.domain.usecase.plant

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.PlantCatalogRepository
import pl.preclaw.florafocus.domain.repository.PlantRepository
import javax.inject.Inject

/**
 * Get all plants from catalog
 */
class GetAllPlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
)
    suspend operator fun invoke(): Flow<List<Plant>> {
        return plantRepository.getAllPlants()
    }
}

/**
 * Get plant by ID
 */
class GetPlantByIdUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(plantId: String): Plant? {
        return plantRepository.getPlantById(plantId)
    }
}

/**
 * Search plants by name
 */
class SearchPlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(query: String): Flow<List<Plant>> {
        return plantRepository.searchPlants(query)
    }
}

/**
 * Filter plants with advanced criteria
 */
class FilterPlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(
        types: List<PlantType>? = null,
        difficulties: List<GrowthDifficulty>? = null,
        lightRequirements: List<LightRequirements>? = null,
        edibleOnly: Boolean = false,
        searchQuery: String? = null
    ): Flow<List<Plant>> {
        return plantRepository.filterPlants(
            types = types,
            difficulties = difficulties,
            lightRequirements = lightRequirements,
            edibleOnly = edibleOnly,
            searchQuery = searchQuery
        )
    }
}

/**
 * Get companion plants for a specific plant
 */
class GetCompanionPlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(plantId: String): List<Plant> {
        return plantRepository.getCompanionPlants(plantId)
    }
}

/**
 * Get incompatible plants for a specific plant
 */
class GetIncompatiblePlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(plantId: String): List<Plant> {
        return plantRepository.getIncompatiblePlants(plantId)
    }
}

/**
 * Sync plant catalog from Firebase
 */
class SyncPlantCatalogUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return plantRepository.syncCatalogFromFirebase()
    }
}

/**
 * Get plants by type
 */
class GetPlantsByTypeUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(type: PlantType): Flow<List<Plant>> {
        return plantRepository.getPlantsByType(type)
    }
}

/**
 * Get edible plants only
 */
class GetEdiblePlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(): Flow<List<Plant>> {
        return plantRepository.getEdiblePlants()
    }
}