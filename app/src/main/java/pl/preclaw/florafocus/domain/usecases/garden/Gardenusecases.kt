package pl.preclaw.florafocus.domain.usecase.garden

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.GardenRepository
import javax.inject.Inject

// ==================== GARDEN ====================

/**
 * Get all gardens for user
 */
class GetGardensUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(userId: String): Flow<List<Garden>> {
        return gardenRepository.getGardens(userId)
    }
}

/**
 * Get garden by ID
 */
class GetGardenByIdUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(gardenId: String): Garden? {
        return gardenRepository.getGardenById(gardenId)
    }
}

/**
 * Create new garden
 */
class CreateGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(garden: Garden): Result<String> {
        return gardenRepository.createGarden(garden)
    }
}

/**
 * Update garden
 */
class UpdateGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(garden: Garden): Result<Unit> {
        return gardenRepository.updateGarden(garden)
    }
}

/**
 * Delete garden
 */
class DeleteGardenUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(gardenId: String): Result<Unit> {
        return gardenRepository.deleteGarden(gardenId)
    }
}

// ==================== AREAS ====================

/**
 * Get all areas in a garden
 */
class GetAreasUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(gardenId: String): Flow<List<GardenArea>> {
        return gardenRepository.getAreas(gardenId)
    }
}

/**
 * Get area by ID
 */
class GetAreaByIdUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(areaId: String): GardenArea? {
        return gardenRepository.getAreaById(areaId)
    }
}

/**
 * Create new area in garden
 */
class CreateAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(area: GardenArea): Result<String> {
        return gardenRepository.createArea(area)
    }
}

/**
 * Update area
 */
class UpdateAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(area: GardenArea): Result<Unit> {
        return gardenRepository.updateArea(area)
    }
}

/**
 * Delete area
 */
class DeleteAreaUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(areaId: String): Result<Unit> {
        return gardenRepository.deleteArea(areaId)
    }
}

// ==================== BEDS ====================

/**
 * Get all beds in an area
 */
class GetBedsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(areaId: String): Flow<List<Bed>> {
        return gardenRepository.getBeds(areaId)
    }
}

/**
 * Get bed by ID
 */
class GetBedByIdUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bedId: String): Bed? {
        return gardenRepository.getBedById(bedId)
    }
}

/**
 * Create new bed in area
 */
class CreateBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bed: Bed): Result<String> {
        return gardenRepository.createBed(bed)
    }
}

/**
 * Update bed
 */
class UpdateBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bed: Bed): Result<Unit> {
        return gardenRepository.updateBed(bed)
    }
}

/**
 * Delete bed
 */
class DeleteBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bedId: String): Result<Unit> {
        return gardenRepository.deleteBed(bedId)
    }
}

/**
 * Move bed to new position
 */
class MoveBedUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bedId: String, newPosition: Position2D): Result<Unit> {
        return gardenRepository.moveBed(bedId, newPosition)
    }
}

// ==================== CELLS ====================

/**
 * Get all cells in a bed
 */
class GetCellsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(bedId: String): Flow<List<BedCell>> {
        return gardenRepository.getCells(bedId)
    }
}

/**
 * Get cell by position
 */
class GetCellByPositionUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(bedId: String, row: Int, column: Int): BedCell? {
        return gardenRepository.getCellByPosition(bedId, row, column)
    }
}

/**
 * Assign plant to cell
 */
class AssignPlantToCellUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(cellId: String, plantId: String?): Result<Unit> {
        // Add to cell history if assigning a plant
        if (plantId != null) {
            gardenRepository.addPlantToCellHistory(cellId, plantId)
        }

        return gardenRepository.assignPlantToCell(cellId, plantId)
    }
}

/**
 * Get cell plant history (for rotation tracking)
 */
class GetCellPlantHistoryUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(cellId: String): List<String> {
        return gardenRepository.getCellPlantHistory(cellId)
    }
}

// ==================== COMPANION PLANTING ====================

/**
 * Validate companion planting for a cell
 *
 * Checks if the plant can be safely planted next to neighboring plants
 */
class ValidateCompanionPlantingUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(cellId: String, plantId: String): CompanionValidationResult {
        return gardenRepository.validateCompanionPlanting(cellId, plantId)
    }
}

// ==================== ROTATION ====================

/**
 * Validate crop rotation
 *
 * Ensures the same plant family isn't planted in the same location
 * in consecutive seasons
 */
class ValidateRotationUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(
        bedId: String,
        plantFamily: String,
        season: Int
    ): Boolean {
        return gardenRepository.validateRotation(bedId, plantFamily, season)
    }
}

/**
 * Create rotation plan
 */
class CreateRotationPlanUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(plan: RotationPlan): Result<String> {
        return gardenRepository.createRotationPlan(plan)
    }
}

/**
 * Get rotation plans for a bed
 */
class GetRotationPlansUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(bedId: String): Flow<List<RotationPlan>> {
        return gardenRepository.getRotationPlans(bedId)
    }
}

// ==================== DECORATIONS ====================

/**
 * Get decorations in an area
 */
class GetDecorationsUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    operator fun invoke(areaId: String): Flow<List<AreaDecoration>> {
        return gardenRepository.getDecorations(areaId)
    }
}

/**
 * Create decoration
 */
class CreateDecorationUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(decoration: AreaDecoration): Result<String> {
        return gardenRepository.createDecoration(decoration)
    }
}

/**
 * Update decoration
 */
class UpdateDecorationUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(decoration: AreaDecoration): Result<Unit> {
        return gardenRepository.updateDecoration(decoration)
    }
}

/**
 * Delete decoration
 */
class DeleteDecorationUseCase @Inject constructor(
    private val gardenRepository: GardenRepository
) {
    suspend operator fun invoke(decorationId: String): Result<Unit> {
        return gardenRepository.deleteDecoration(decorationId)
    }
}