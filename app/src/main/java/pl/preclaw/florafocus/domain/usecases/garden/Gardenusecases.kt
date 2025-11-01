package pl.preclaw.florafocus.domain.usecase.garden

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
        // ✅ POPRAWIONE: getBeds zamiast getBedsInArea
        return gardenRepository.getBeds(areaId)
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
        // ✅ POPRAWIONE: getCells zamiast getCellsInBed
        return gardenRepository.getCells(bedId)
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
        // ✅ IMPLEMENTACJA: filtruj getCells po occupied
        return gardenRepository.getCells(bedId)
            .map { cells ->
                cells.filter { it.currentPlantId != null }
            }
    }
}

class GetEmptyCellsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(bedId: String): Flow<List<BedCell>> {
        // ✅ IMPLEMENTACJA: filtruj getCells po empty
        return gardenRepository.getCells(bedId)
            .map { cells ->
                cells.filter { it.currentPlantId == null }
            }
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
        row: Int,
        column: Int,
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
                rowIndex = row,
                columnIndex = column,
                proposedPlantId = userPlant.catalogPlantId
            )

            if (!validationResult.isCompatible) {
                return Result.failure(Exception("Companion planting validation failed: ${validationResult.warnings}"))
            }

            // 5. Update cell with plant
            val updatedCell = cell.copy(
                currentPlantId = userPlantId,
                updatedAt = System.currentTimeMillis()
            )

            gardenRepository.updateCell(updatedCell)

            Result.success(Unit)  // ✅ DODAJ TĘ LINIĘ
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Validate companion planting
 */
class ValidateCompanionPlantingUseCase @Inject constructor(
    private val gardenRepository: GardenRepository,
    private val plantCatalogRepository: PlantCatalogRepository,
    private val userPlantRepository: UserPlantRepository  // ✅ DODAJ TO
) {

    suspend operator fun invoke(
        bedId: String,
        rowIndex: Int,  // ✅ POPRAWIONE: row zamiast rowIndex
        columnIndex: Int,  // ✅ POPRAWIONE: column zamiast columnIndex
        proposedPlantId: String?
    ): CompanionPlantingResult {
        return try {
            if (proposedPlantId == null) {
                return CompanionPlantingResult(
                    isCompatible = true,
                    warnings = emptyList()
                )
            }

            // Get proposed plant
            val proposedPlant = plantCatalogRepository.getPlantById(proposedPlantId)
                ?: return CompanionPlantingResult(
                    isCompatible = false,
                    warnings = listOf("Plant not found in catalog")
                )

            // Get neighboring cells
            val bed = gardenRepository.getBedById(bedId) ?: return CompanionPlantingResult(
                isCompatible = false,
                warnings = listOf("Bed not found")
            )
            val neighboringCells = bed.cells.filter { (position, cell) ->
                val (cellRow, cellCol) = position
                val rowDiff = kotlin.math.abs(cellRow - rowIndex)
                val colDiff = kotlin.math.abs(cellCol - columnIndex)
                (rowDiff <= 1 && colDiff <= 1) && !(rowDiff == 0 && colDiff == 0)
            }.values.toList()

            val warnings = mutableListOf<String>()
            var isCompatible = true

            // Check each neighboring plant
            for (neighborCell in neighboringCells) {
                if (neighborCell.currentPlantId != null) {
                    val neighborPlant = userPlantRepository.getUserPlantById(neighborCell.currentPlantId!!)
                    if (neighborPlant?.catalogPlantId != null) {
                        val neighborCatalogPlant = plantCatalogRepository.getPlantById(neighborPlant.catalogPlantId)

                        // Check incompatibility
                        if (neighborCatalogPlant?.incompatiblePlantIds?.contains(proposedPlantId) == true ||
                            proposedPlant.incompatiblePlantIds.contains(neighborPlant.catalogPlantId)) {
                            isCompatible = false
                            warnings.add("Incompatible with ${neighborCatalogPlant?.commonName}")
                        }
                    }
                }
            }

            CompanionPlantingResult(
                isCompatible = isCompatible,
                warnings = warnings
            )
        } catch (e: Exception) {
            CompanionPlantingResult(
                isCompatible = false,
                warnings = listOf("Validation error: ${e.message}")
            )
        }
    }

}

data class CompanionPlantingResult(
    val isCompatible: Boolean,
    val warnings: List<String> = emptyList(),
    val benefits: List<String> = emptyList()
)
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
            val newPosition = Position2D(newXPosition, newYPosition)
            gardenRepository.moveBed(bedId, newPosition)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ==================== DATA CLASSES ====================

data class AreaConfig(
    val name: String,
    val size: Float,
    val sunExposure: SunExposure,
    val xPosition: Float,
    val yPosition: Float,
    val width: Float,
    val height: Float
)



data class CellOccupancyStats(
    val totalCells: Int,
    val occupiedCells: Int,
    val emptyCells: Int
) {
    val occupancyPercentage: Float
        get() = if (totalCells > 0) (occupiedCells.toFloat() / totalCells) * 100 else 0f
}
