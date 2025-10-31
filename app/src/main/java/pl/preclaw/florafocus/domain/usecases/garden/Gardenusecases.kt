package pl.preclaw.florafocus.domain.usecase.garden

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.GardenRepository
import pl.preclaw.florafocus.domain.repository.PlantCatalogRepository
import pl.preclaw.florafocus.domain.repository.UserPlantRepository
import java.util.UUID
import javax.inject.Inject

// ==================== BASIC GARDEN CRUD ====================

class GetGardensUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(userId: String): Flow<List<Garden>> {
        return gardenRepository.getGardens(userId)
    }
}

class GetGardenByIdUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(gardenId: String): Garden? {
        return gardenRepository.getGardenById(gardenId)
    }
}

class CreateGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(garden: Garden): Result<String> {
        return gardenRepository.createGarden(garden)
    }
}

class UpdateGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(garden: Garden): Result<Unit> {
        return gardenRepository.updateGarden(garden)
    }
}

class DeleteGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(gardenId: String): Result<Unit> {
        return gardenRepository.deleteGarden(gardenId)
    }
}

// ==================== AREA MANAGEMENT ====================

class GetAreasUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(gardenId: String): Flow<List<GardenArea>> {
        return gardenRepository.getAreas(gardenId)
    }
}

class GetAreaByIdUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(areaId: String): GardenArea? {
        return gardenRepository.getAreaById(areaId)
    }
}

class CreateAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(area: GardenArea): Result<String> {
        return gardenRepository.createArea(area)
    }
}

class UpdateAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(area: GardenArea): Result<Unit> {
        return gardenRepository.updateArea(area)
    }
}

class DeleteAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(areaId: String): Result<Unit> {
        return gardenRepository.deleteArea(areaId)
    }
}

// ==================== BED MANAGEMENT ====================

class GetBedsInAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(areaId: String): Flow<List<Bed>> {
        return gardenRepository.getBedsInArea(areaId)
    }
}

class GetBedByIdUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bedId: String): Bed? {
        return gardenRepository.getBedById(bedId)
    }
}

class CreateBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bed: Bed): Result<String> {
        return gardenRepository.createBed(bed)
    }
}

class UpdateBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bed: Bed): Result<Unit> {
        return gardenRepository.updateBed(bed)
    }
}

class DeleteBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bedId: String): Result<Unit> {
        return gardenRepository.deleteBed(bedId)
    }
}

// ==================== CELL MANAGEMENT ====================

class GetCellsInBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(bedId: String): Flow<List<BedCell>> {
        return gardenRepository.getCellsInBed(bedId)
    }
}

class GetCellByIdUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(cellId: String): BedCell? {
        return gardenRepository.getCellById(cellId)
    }
}

class UpdateCellUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(cell: BedCell): Result<Unit> {
        return gardenRepository.updateCell(cell)
    }
}

class GetOccupiedCellsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(bedId: String): Flow<List<BedCell>> {
        return gardenRepository.getOccupiedCells(bedId)
    }
}

class GetEmptyCellsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(bedId: String): Flow<List<BedCell>> {
        return gardenRepository.getEmptyCells(bedId)
    }
}

// ==================== COMPLEX BUSINESS LOGIC ====================

/**
 * Assign plant to cell with full validation
 *
 * Business logic:
 * 1. Verify cell exists and is empty
 * 2. Verify plant exists
 * 3. Check companion planting rules
 * 4. Update cell with plant
 */
class AssignPlantToCellUseCase @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val userPlantRepository: UserPlantRepository,
    private val validateCompanionPlantingUseCase: ValidateCompanionPlantingUseCase
) {
    suspend operator fun invoke(
        cellId: String,
        userPlantId: String
    ): Result<Unit> {
        return try {
            // 1. Get cell
            val cell = gardenRepository.getCellById(cellId)
                ?: return Result.failure(Exception("Cell not found: $cellId"))

            // 2. Check if cell is empty
            if (cell.currentPlantId != null) {
                return Result.failure(Exception("Cell is already occupied"))
            }

            // 3. Get user plant
            val userPlant = userPlantRepository.getUserPlantById(userPlantId)
                ?: return Result.failure(Exception("Plant not found: $userPlantId"))

            // 4. Validate companion planting
            val validationResult = validateCompanionPlantingUseCase(
                bedId = cell.bedId,
                rowIndex = cell.rowIndex,
                columnIndex = cell.columnIndex,
                proposedPlantId = userPlant.catalogPlantId
            )

            if (!validationResult.isCompatible) {
                return Result.failure(Exception("Companion planting validation failed: ${validationResult.warnings}"))
            }

            // 5. Update cell
            val updatedCell = cell.copy(
                currentPlantId = userPlantId,
                plantedDate = System.currentTimeMillis()
            )

            gardenRepository.updateCell(updatedCell)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Remove plant from cell
 */
class RemovePlantFromCellUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(
        cellId: String,
        recordHistory: Boolean = true
    ): Result<Unit> {
        return try {
            val cell = gardenRepository.getCellById(cellId)
                ?: return Result.failure(Exception("Cell not found"))

            if (cell.currentPlantId == null) {
                return Result.failure(Exception("Cell is already empty"))
            }

            // Record to history if needed
            if (recordHistory && cell.plantedDate != null) {
                val historyEntry = CellHistory(
                    plantId = cell.currentPlantId,
                    plantedDate = cell.plantedDate,
                    removedDate = System.currentTimeMillis()
                )
                val updatedHistory = cell.history + historyEntry

                val updatedCell = cell.copy(
                    currentPlantId = null,
                    plantedDate = null,
                    history = updatedHistory
                )
                gardenRepository.updateCell(updatedCell)
            } else {
                val updatedCell = cell.copy(
                    currentPlantId = null,
                    plantedDate = null
                )
                gardenRepository.updateCell(updatedCell)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Validate companion planting for a specific cell
 *
 * Checks adjacent cells for plant compatibility
 */
class ValidateCompanionPlantingUseCase @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val plantCatalogRepository: PlantCatalogRepository
) {
    suspend operator fun invoke(
        bedId: String,
        rowIndex: Int,
        columnIndex: Int,
        proposedPlantId: String
    ): CompanionPlantingResult {
        try {
            // Get proposed plant
            val proposedPlant = plantCatalogRepository.getPlantById(proposedPlantId)
                ?: return CompanionPlantingResult(
                    isCompatible = false,
                    warnings = listOf("Proposed plant not found in catalog")
                )

            // Get adjacent cells
            val adjacentCells = gardenRepository.getAdjacentCells(bedId, rowIndex, columnIndex)

            val warnings = mutableListOf<String>()
            val benefits = mutableListOf<String>()
            var hasIncompatible = false

            // Check each adjacent cell
            for (adjacentCell in adjacentCells) {
                if (adjacentCell.currentPlantId == null) continue

                // Get user plant from cell
                val userPlant = gardenRepository.getUserPlantById(adjacentCell.currentPlantId)
                    ?: continue

                // Get catalog plant
                val adjacentCatalogPlant = plantCatalogRepository.getPlantById(userPlant.catalogPlantId)
                    ?: continue

                // Check if incompatible
                if (proposedPlant.incompatiblePlantIds.contains(adjacentCatalogPlant.id)) {
                    hasIncompatible = true
                    warnings.add("Incompatible with ${adjacentCatalogPlant.commonName} at [${adjacentCell.rowIndex}, ${adjacentCell.columnIndex}]")
                }

                // Check if companion
                if (proposedPlant.companionPlantIds.contains(adjacentCatalogPlant.id)) {
                    benefits.add("Good companion with ${adjacentCatalogPlant.commonName} at [${adjacentCell.rowIndex}, ${adjacentCell.columnIndex}]")
                }
            }

            return CompanionPlantingResult(
                isCompatible = !hasIncompatible,
                warnings = warnings,
                benefits = benefits
            )
        } catch (e: Exception) {
            return CompanionPlantingResult(
                isCompatible = false,
                warnings = listOf("Error validating companion planting: ${e.message}")
            )
        }
    }
}

/**
 * Get all plants adjacent to a specific cell
 */
class GetAdjacentPlantsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val userPlantRepository: UserPlantRepository
) {
    suspend operator fun invoke(
        bedId: String,
        rowIndex: Int,
        columnIndex: Int
    ): List<UserPlant> {
        val adjacentCells = gardenRepository.getAdjacentCells(bedId, rowIndex, columnIndex)

        return adjacentCells.mapNotNull { cell ->
            cell.currentPlantId?.let { plantId ->
                userPlantRepository.getUserPlantById(plantId)
            }
        }
    }
}

/**
 * Get cell occupancy statistics for a bed
 */
class GetCellOccupancyStatsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bedId: String): CellOccupancyStats {
        return gardenRepository.getCellOccupancyStats(bedId)
    }
}

/**
 * Create garden with initial structure
 *
 * Business logic:
 * 1. Create garden
 * 2. Create default areas
 * 3. Return complete structure
 */
class CreateGardenWithStructureUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(
        userId: String,
        gardenName: String,
        totalArea: Float,
        initialAreas: List<AreaConfig>
    ): Result<Garden> {
        return try {
            // Create garden
            val gardenId = UUID.randomUUID().toString()
            val garden = Garden(
                id = gardenId,
                userId = userId,
                name = gardenName,
                totalArea = totalArea,
                location = null,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            gardenRepository.createGarden(garden).getOrThrow()

            // Create areas
            for (areaConfig in initialAreas) {
                val area = GardenArea(
                    id = UUID.randomUUID().toString(),
                    gardenId = gardenId,
                    name = areaConfig.name,
                    areaSize = areaConfig.size,
                    sunExposure = areaConfig.sunExposure,
                    xPosition = areaConfig.xPosition,
                    yPosition = areaConfig.yPosition,
                    width = areaConfig.width,
                    height = areaConfig.height,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                gardenRepository.createArea(area)
            }

            Result.success(garden)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Move/reposition bed within area
 */
class MoveBedInAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(
        bedId: String,
        newXPosition: Float,
        newYPosition: Float
    ): Result<Unit> {
        return try {
            val bed = gardenRepository.getBedById(bedId)
                ?: return Result.failure(Exception("Bed not found"))

            val updatedBed = bed.copy(
                xPosition = newXPosition,
                yPosition = newYPosition,
                updatedAt = System.currentTimeMillis()
            )

            gardenRepository.updateBed(updatedBed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ==================== DATA CLASSES FOR USE CASES ====================

data class AreaConfig(
    val name: String,
    val size: Float,
    val sunExposure: SunExposure,
    val xPosition: Float,
    val yPosition: Float,
    val width: Float,
    val height: Float
)

data class CompanionPlantingResult(
    val isCompatible: Boolean,
    val warnings: List<String> = emptyList(),
    val benefits: List<String> = emptyList()
)

data class CellHistory(
    val plantId: String,
    val plantedDate: Long,
    val removedDate: Long
)

data class CellOccupancyStats(
    val totalCells: Int,
    val occupiedCells: Int,
    val emptyCells: Int
) {
    val occupancyPercentage: Float
        get() = if (totalCells > 0) (occupiedCells.toFloat() / totalCells) * 100 else 0f
}