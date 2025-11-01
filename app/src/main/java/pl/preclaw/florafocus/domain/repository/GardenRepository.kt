package pl.preclaw.florafocus.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*

/**
 * Repository interface for Garden operations
 *
 * Manages garden structure, areas, beds, cells, and rotation planning
 */
interface GardenRepository {

    // ==================== GARDEN ====================

    /**
     * Get all gardens for user
     */
    fun getGardens(userId: String): Flow<List<Garden>>

    /**
     * Get garden by ID
     */
    suspend fun getGardenById(gardenId: String): Garden?

    /**
     * Get garden by ID as Flow
     */
    fun getGardenByIdFlow(gardenId: String): Flow<Garden?>

    /**
     * Create new garden
     */
    suspend fun createGarden(garden: Garden): Result<String>

    /**
     * Update garden
     */
    suspend fun updateGarden(garden: Garden): Result<Unit>

    /**
     * Delete garden
     */
    suspend fun deleteGarden(gardenId: String): Result<Unit>

    // ==================== GARDEN AREAS ====================

    /**
     * Get all areas in a garden
     */
    fun getAreas(gardenId: String): Flow<List<GardenArea>>

    /**
     * Get area by ID
     */
    suspend fun getAreaById(areaId: String): GardenArea?

    /**
     * Get area by ID as Flow
     */
    fun getAreaByIdFlow(areaId: String): Flow<GardenArea?>

    /**
     * Create new area
     */
    suspend fun createArea(area: GardenArea): Result<String>

    /**
     * Update area
     */
    suspend fun updateArea(area: GardenArea): Result<Unit>

    /**
     * Delete area
     */
    suspend fun deleteArea(areaId: String): Result<Unit>

    // ==================== BEDS ====================

    /**
     * Get all beds in an area
     */
    fun getBeds(areaId: String): Flow<List<Bed>>

    /**
     * Get bed by ID
     */
    suspend fun getBedById(bedId: String): Bed?

    /**
     * Get bed by ID as Flow
     */
    fun getBedByIdFlow(bedId: String): Flow<Bed?>

    /**
     * Create new bed
     */
    suspend fun createBed(bed: Bed): Result<String>

    /**
     * Update bed
     */
    suspend fun updateBed(bed: Bed): Result<Unit>

    /**
     * Delete bed
     */
    suspend fun deleteBed(bedId: String): Result<Unit>

    /**
     * Move bed to new position
     */
    suspend fun moveBed(bedId: String, newPosition: Position2D): Result<Unit>

    // ==================== BED CELLS ====================

    /**
     * Get all cells in a bed
     */
    fun getCells(bedId: String): Flow<List<BedCell>>

    /**
     * Get cell by position
     */
    suspend fun getCellByPosition(bedId: String, row: Int, column: Int): BedCell?

    /**
     * Get cell by ID
     */
    suspend fun getCellById(cellId: String): BedCell?

    /**
     * Update cell
     */
    suspend fun updateCell(cell: BedCell): Result<Unit>

    /**
     * Plant in cell
     */
    suspend fun plantInCell(
        cellId: String,
        plantId: String,
        plantedDate: Long
    ): Result<Unit>

    /**
     * Remove plant from cell
     */
    suspend fun removePlantFromCell(
        cellId: String,
        harvestedDate: Long?
    ): Result<Unit>

    /**
     * Get cell planting history
     */
    suspend fun getCellPlantingHistory(cellId: String): List<PlantingHistoryEntry>

    /**
     * Get available cells in bed
     */
    fun getAvailableCells(bedId: String): Flow<List<BedCell>>

    /**
     * Get occupied cells in bed
     */
    fun getOccupiedCells(bedId: String): Flow<List<BedCell>>

    // ==================== DECORATIONS ====================

    /**
     * Get decorations in area
     */
    fun getDecorations(areaId: String): Flow<List<AreaDecoration>>

    /**
     * Add decoration
     */
    suspend fun addDecoration(decoration: AreaDecoration): Result<String>

    /**
     * Update decoration
     */
    suspend fun updateDecoration(decoration: AreaDecoration): Result<Unit>

    /**
     * Delete decoration
     */
    suspend fun deleteDecoration(decorationId: String): Result<Unit>

    // ==================== ROTATION PLANNING ====================

    /**
     * Get rotation plans for garden
     */
    fun getRotationPlans(gardenId: String): Flow<List<RotationPlan>>

    /**
     * Get rotation plan by ID
     */
    suspend fun getRotationPlanById(planId: String): RotationPlan?

    /**
     * Create rotation plan
     */
    suspend fun createRotationPlan(plan: RotationPlan): Result<String>

    /**
     * Update rotation plan
     */
    suspend fun updateRotationPlan(plan: RotationPlan): Result<Unit>

    /**
     * Delete rotation plan
     */
    suspend fun deleteRotationPlan(planId: String): Result<Unit>

    // ==================== VALIDATION & ANALYTICS ====================

    /**
     * Validate companion planting
     * Returns validation result with warnings/errors
     */
    suspend fun validateCompanionPlanting(
        bedId: String,
        cellRow: Int,
        cellColumn: Int,
        plantCatalogId: String
    ): CompanionValidationResult

    /**
     * Validate rotation (check if same family planted recently)
     */
    suspend fun validateRotation(
        cellId: String,
        plantFamily: String
    ): RotationValidationResult

    /**
     * Get garden statistics
     */
    suspend fun getGardenStatistics(gardenId: String): GardenStatistics
}

// ==================== DATA CLASSES ====================

/**
 * Companion planting validation result
 */
data class CompanionValidationResult(
    val isValid: Boolean,
    val goodCompanions: List<CompanionInfo>,
    val badCompanions: List<CompanionInfo>,
    val warnings: List<String>
)

data class CompanionInfo(
    val plantId: String,
    val plantName: String,
    val cellPosition: Pair<Int, Int>,
    val relationship: CompanionRelationship
)

enum class CompanionRelationship {
    BENEFICIAL,
    INCOMPATIBLE,
    NEUTRAL
}

/**
 * Rotation validation result
 */
data class RotationValidationResult(
    val isValid: Boolean,
    val lastPlantedFamily: String?,
    val lastPlantedDate: Long?,
    val yearsSinceLastPlanting: Int?,
    val recommendations: List<String>
)

/**
 * Garden statistics
 */
data class GardenStatistics(
    val totalBeds: Int,
    val totalCells: Int,
    val occupiedCells: Int,
    val availableCells: Int,
    val plantsByFamily: Map<String, Int>,
    val utilizationRate: Float // percentage
)

/**
 * Area decoration - objects that don't grow plants
 */
data class AreaDecoration(
    val id: String,
    val areaId: String,
    val name: String,
    val decorationType: DecorationType,
    val position: Position2D,
    val size: Size2D,
    val rotation: Float = 0f,
    val notes: String? = null,
    val createdAt: Long
)

enum class DecorationType {
    PATH,
    WATER_FEATURE,
    STONE,
    BORDER,
    COMPOST_BIN,
    SHED,
    GREENHOUSE,
    OTHER
}