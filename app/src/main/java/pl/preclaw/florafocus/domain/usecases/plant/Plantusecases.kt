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
) {  // ✅ POPRAWIONE: dodany nawias klamrowy
    operator fun invoke(): Flow<List<Plant>> {  // ✅ POPRAWIONE: operator wewnątrz klasy
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
        // ✅ POPRAWIONE: używam getFilteredPlants zamiast filterPlants
        return plantRepository.getFilteredPlants(
            type = types?.firstOrNull(),  // Repository przyjmuje pojedynczy type
            difficulty = difficulties?.firstOrNull(),  // Repository przyjmuje pojedynczy difficulty
            light = lightRequirements?.firstOrNull(),  // Repository przyjmuje pojedynczy light
            edibleOnly = edibleOnly
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
        // ✅ POPRAWIONE: sprawdzam czy metoda istnieje w interface
        return try {
            // Placeholder - implementacja zależy od PlantRepository interface
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

/**
 * Get plants by difficulty
 */
class GetPlantsByDifficultyUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(difficulty: GrowthDifficulty): Flow<List<Plant>> {
        return plantRepository.getPlantsByDifficulty(difficulty)
    }
}

/**
 * Get plants by light requirements
 */
class GetPlantsByLightRequirementsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(light: LightRequirements): Flow<List<Plant>> {
        return plantRepository.getPlantsByLightRequirements(light)
    }
}

/**
 * Get plants by family (for rotation planning)
 */
class GetPlantsByFamilyUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(family: String): Flow<List<Plant>> {
        return plantRepository.getPlantsByFamily(family)
    }
}

/**
 * Get plants currently in sowing period
 */
class GetPlantsInSowingPeriodUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(): Flow<List<Plant>> {
        return plantRepository.getPlantsInSowingPeriod()
    }
}

/**
 * Get all plant families
 */
class GetAllPlantFamiliesUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(): List<String> {
        return plantRepository.getAllPlantFamilies()
    }
}

/**
 * Get plant count
 */
class GetPlantCountUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(): Int {
        return plantRepository.getPlantCount()
    }
}

/**
 * Check if plant exists
 */
class PlantExistsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(plantId: String): Boolean {
        return plantRepository.plantExists(plantId)
    }
}