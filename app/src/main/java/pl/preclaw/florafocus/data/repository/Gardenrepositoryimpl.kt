package pl.preclaw.florafocus.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import pl.preclaw.florafocus.data.local.dao.GardenDao
import pl.preclaw.florafocus.data.mapper.GardenMapper
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.GardenRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GardenRepository
 *
 * Manages the hierarchical garden structure:
 * Garden → Areas → Beds → Cells
 * Plus decorations and rotation planning
 */
@Singleton
class GardenRepositoryImpl @Inject constructor(
    private val gardenDao: GardenDao,
    private val firestore: FirebaseFirestore,
    private val gardenMapper: GardenMapper
) : GardenRepository {

    companion object {
        private const val COLLECTION_GARDENS = "gardens"
        private const val TAG = "GardenRepository"
    }

    // ==================== GARDEN ====================

    override fun getGardens(userId: String): Flow<List<Garden>> {
        return gardenDao.getAllGardens(userId)
            .map { entities ->
                entities.map { gardenMapper.toDomain(it) }
            }
    }

    override suspend fun getGardenById(gardenId: String): Garden? {
        return gardenDao.getGardenById(gardenId)?.let {
            gardenMapper.toDomain(it)
        }
    }

    override fun getGardenByIdFlow(gardenId: String): Flow<Garden?> {
        return gardenDao.getGardenByIdFlow(gardenId)
            .map { entity -> entity?.let { gardenMapper.toDomain(it) } }
    }

    override suspend fun createGarden(garden: Garden): Result<String> {
        return try {
            val entity = gardenMapper.toEntity(garden)
            gardenDao.insertGarden(entity)

            Timber.tag(TAG).d("Created garden: ${garden.name}")
            Result.success(garden.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating garden")
            Result.failure(e)
        }
    }

    override suspend fun updateGarden(garden: Garden): Result<Unit> {
        return try {
            val entity = gardenMapper.toEntity(garden)
            gardenDao.updateGarden(entity)

            Timber.tag(TAG).d("Updated garden: ${garden.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating garden")
            Result.failure(e)
        }
    }

    override suspend fun deleteGarden(gardenId: String): Result<Unit> {
        return try {
            gardenDao.deleteGardenById(gardenId)

            Timber.tag(TAG).d("Deleted garden: $gardenId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting garden")
            Result.failure(e)
        }
    }

    // ==================== GARDEN AREAS ====================

    override fun getAreas(gardenId: String): Flow<List<GardenArea>> {
        return gardenDao.getAreasInGarden(gardenId)
            .map { areaEntities ->
                areaEntities.map { areaEntity ->
                    // Pobierz beds
                    val beds = gardenDao.getBedsInAreaSync(areaEntity.id)
                        .map { bedEntity ->
                            val cells = gardenDao.getCellsInBedSync(bedEntity.id)
                            gardenMapper.toDomain(bedEntity, cells)
                        }

                    // Pobierz decorations
                    val decorations = gardenDao.getDecorationsInAreaSync(areaEntity.id)
                        .map { gardenMapper.toDomain(it) }

                    // Połącz w objects
                    val objects: List<AreaObject> = beds + decorations

                    gardenMapper.toDomain(areaEntity, objects)
                }
            }
    }
    override suspend fun getAreaById(areaId: String): GardenArea? {
        return gardenDao.getAreaById(areaId)?.let {
            gardenMapper.toDomain(it)
        }
    }

    override fun getAreaByIdFlow(areaId: String): Flow<GardenArea?> {
        return gardenDao.getAreaByIdFlow(areaId)
            .map { entity -> entity?.let { gardenMapper.toDomain(it) } }
    }

    override suspend fun createArea(area: GardenArea): Result<String> {
        return try {
            val entity = gardenMapper.toEntity(area)
            gardenDao.insertArea(entity)

            Timber.tag(TAG).d("Created area: ${area.name}")
            Result.success(area.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating area")
            Result.failure(e)
        }
    }

    override suspend fun updateArea(area: GardenArea): Result<Unit> {
        return try {
            val entity = gardenMapper.toEntity(area)
            gardenDao.updateArea(entity)

            Timber.tag(TAG).d("Updated area: ${area.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating area")
            Result.failure(e)
        }
    }

    override suspend fun deleteArea(areaId: String): Result<Unit> {
        return try {
            gardenDao.deleteAreaById(areaId)

            Timber.tag(TAG).d("Deleted area: $areaId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting area")
            Result.failure(e)
        }
    }

    // ==================== BEDS ====================

    override fun getBeds(areaId: String): Flow<List<Bed>> {
        return gardenDao.getBedsInArea(areaId)
            .map { entities ->
                entities.map { gardenMapper.toDomain(it) }
            }
    }

    override suspend fun getBedById(bedId: String): Bed? {
        return gardenDao.getBedById(bedId)?.let {
            gardenMapper.toDomain(it)
        }
    }

    override fun getBedByIdFlow(bedId: String): Flow<Bed?> {
        return gardenDao.getBedByIdFlow(bedId)
            .map { entity -> entity?.let { gardenMapper.toDomain(it) } }
    }

    override suspend fun createBed(bed: AreaObject.Bed): Result<String> {
        return try {
            val bedEntity = gardenMapper.toEntity(bed)
            val cellEntities = gardenMapper.toCellEntities(bed)

            gardenDao.insertBed(bedEntity)
            gardenDao.insertCells(cellEntities)

            Timber.tag(TAG).d("Created bed: ${bed.name} with ${cellEntities.size} cells")
            Result.success(bed.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating bed")
            Result.failure(e)
        }
    } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating bed")
            Result.failure(e)
        }
    }

    override suspend fun updateBed(bed: Bed): Result<Unit> {
        return try {
            val entity = gardenMapper.toEntity(bed)
            gardenDao.updateBed(entity)

            Timber.tag(TAG).d("Updated bed: ${bed.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating bed")
            Result.failure(e)
        }
    }

    override suspend fun deleteBed(bedId: String): Result<Unit> {
        return try {
            gardenDao.deleteBedById(bedId)

            Timber.tag(TAG).d("Deleted bed: $bedId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting bed")
            Result.failure(e)
        }
    }

    override suspend fun moveBed(bedId: String, newPosition: Position2D): Result<Unit> {
        return try {
            val bed = gardenDao.getBedById(bedId)
                ?: return Result.failure(Exception("Bed not found"))

            gardenDao.updateBed(
                bed.copy(
                    pos_x = newPosition.x,
                    pos_y = newPosition.y
                )
            )

            Timber.tag(TAG).d("Moved bed $bedId to ${newPosition.x}, ${newPosition.y}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error moving bed")
            Result.failure(e)
        }
    }

    // ==================== BED CELLS ====================

    override fun getCells(bedId: String): Flow<List<BedCell>> {
        return gardenDao.getCellsInBed(bedId)
            .map { entities ->
                entities.map { gardenMapper.toDomain(it) }
            }
    }

    override suspend fun getCellByPosition(bedId: String, row: Int, column: Int): BedCell? {
        return gardenDao.getCellByPosition(bedId, row, column)?.let {
            gardenMapper.toDomain(it)
        }
    }

    override suspend fun getCellById(cellId: String): BedCell? {
        return gardenDao.getCellById(cellId)?.let {
            gardenMapper.toDomain(it)
        }
    }

    override suspend fun assignPlantToCell(
        cellId: String,
        plantId: String?
    ): Result<Unit> {
        return try {
            val cell = gardenDao.getCellById(cellId)
                ?: return Result.failure(Exception("Cell not found"))

            gardenDao.updateCell(
                cell.copy(plantId = plantId)
            )

            Timber.tag(TAG).d("Assigned plant $plantId to cell $cellId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error assigning plant to cell")
            Result.failure(e)
        }
    }

    override suspend fun updateCell(cell: BedCell): Result<Unit> {
        return try {
            val entity = gardenMapper.toEntity(cell)
            gardenDao.updateCell(entity)

            Timber.tag(TAG).d("Updated cell: ${cell.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating cell")
            Result.failure(e)
        }
    }

    override suspend fun getCellPlantHistory(cellId: String): List<String> {
        return try {
            gardenDao.getCellById(cellId)?.plantHistory ?: emptyList()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting cell history")
            emptyList()
        }
    }

    override suspend fun addPlantToCellHistory(cellId: String, plantId: String): Result<Unit> {
        return try {
            val cell = gardenDao.getCellById(cellId)
                ?: return Result.failure(Exception("Cell not found"))

            val updatedHistory = (cell.plantHistory + plantId).distinct()

            gardenDao.updateCell(
                cell.copy(plantHistory = updatedHistory)
            )

            Timber.tag(TAG).d("Added plant $plantId to cell history")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error adding to cell history")
            Result.failure(e)
        }
    }

    // ==================== DECORATIONS ====================

    override fun getDecorations(areaId: String): Flow<List<AreaDecoration>> {
        return gardenDao.getDecorationsInArea(areaId)
            .map { entities ->
                entities.map { gardenMapper.toDomain(it) }
            }
    }

    override suspend fun createDecoration(decoration: AreaDecoration): Result<String> {
        return try {
            val entity = gardenMapper.toEntity(decoration)
            gardenDao.insertDecoration(entity)

            Timber.tag(TAG).d("Created decoration: ${decoration.type}")
            Result.success(decoration.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating decoration")
            Result.failure(e)
        }
    }

    override suspend fun updateDecoration(decoration: AreaDecoration): Result<Unit> {
        return try {
            val entity = gardenMapper.toEntity(decoration)
            gardenDao.updateDecoration(entity)

            Timber.tag(TAG).d("Updated decoration: ${decoration.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating decoration")
            Result.failure(e)
        }
    }

    override suspend fun deleteDecoration(decorationId: String): Result<Unit> {
        return try {
            gardenDao.deleteDecorationById(decorationId)

            Timber.tag(TAG).d("Deleted decoration: $decorationId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting decoration")
            Result.failure(e)
        }
    }

    // ==================== ROTATION PLANNING ====================

    override suspend fun createRotationPlan(plan: RotationPlan): Result<String> {
        return try {
            val entity = gardenMapper.toEntity(plan)
            gardenDao.insertRotationPlan(entity)

            Timber.tag(TAG).d("Created rotation plan for bed ${plan.bedId}")
            Result.success(plan.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating rotation plan")
            Result.failure(e)
        }
    }

    override fun getRotationPlans(bedId: String): Flow<List<RotationPlan>> {
        return gardenDao.getRotationPlans(bedId)
            .map { entities ->
                entities.map { gardenMapper.toDomain(it) }
            }
    }

    override suspend fun updateRotationPlan(plan: RotationPlan): Result<Unit> {
        return try {
            val entity = gardenMapper.toEntity(plan)
            gardenDao.updateRotationPlan(entity)

            Timber.tag(TAG).d("Updated rotation plan: ${plan.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating rotation plan")
            Result.failure(e)
        }
    }

    override suspend fun deleteRotationPlan(planId: String): Result<Unit> {
        return try {
            gardenDao.deleteRotationPlanById(planId)

            Timber.tag(TAG).d("Deleted rotation plan: $planId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting rotation plan")
            Result.failure(e)
        }
    }

    override suspend fun validateRotation(
        bedId: String,
        plantFamily: String,
        season: Int
    ): Boolean {
        return try {
            // Get all rotation plans for the bed
            val plans = gardenDao.getRotationPlansList(bedId)

            // Check if same family was planted in previous season
            val previousSeasonPlans = plans.filter { it.season == season - 1 }
            val familiesInPreviousSeason = previousSeasonPlans.map { it.plantFamily }

            // Rotation is valid if family was NOT in previous season
            !familiesInPreviousSeason.contains(plantFamily)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error validating rotation")
            true // Allow by default if error
        }
    }

    // ==================== COMPANION PLANTING ====================

    override suspend fun validateCompanionPlanting(
        cellId: String,
        plantId: String
    ): CompanionValidationResult {
        return try {
            // Get the cell and its neighbors
            val cell = gardenDao.getCellById(cellId)
                ?: return CompanionValidationResult(
                    isValid = true,
                    companionPlants = emptyList(),
                    incompatiblePlants = emptyList()
                )

            // Get neighboring cells (8 directions)
            val neighbors = getNeighboringCells(cell.bedId, cell.row, cell.column)
            val neighborPlantIds = neighbors.mapNotNull { it.plantId }

            if (neighborPlantIds.isEmpty()) {
                return CompanionValidationResult(
                    isValid = true,
                    companionPlants = emptyList(),
                    incompatiblePlants = emptyList()
                )
            }

            // This would need PlantCatalogDao to check companion/incompatible lists
            // For now, return a simplified result
            // TODO: Inject PlantCatalogDao and implement full validation

            CompanionValidationResult(
                isValid = true,
                companionPlants = emptyList(),
                incompatiblePlants = emptyList()
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error validating companion planting")
            CompanionValidationResult(
                isValid = true,
                companionPlants = emptyList(),
                incompatiblePlants = emptyList()
            )
        }
    }

    private suspend fun getNeighboringCells(bedId: String, row: Int, column: Int): List<pl.preclaw.florafocus.data.local.entities.BedCellEntity> {
        val neighbors = mutableListOf<pl.preclaw.florafocus.data.local.entities.BedCellEntity>()

        for (dRow in -1..1) {
            for (dCol in -1..1) {
                if (dRow == 0 && dCol == 0) continue // Skip center cell

                val cell = gardenDao.getCellByPosition(bedId, row + dRow, column + dCol)
                cell?.let { neighbors.add(it) }
            }
        }

        return neighbors
    }

    // ==================== FIREBASE SYNC (Future) ====================

    override suspend fun syncGardenWithFirebase(userId: String): Result<Unit> {
        return try {
            // TODO: Implement Firebase sync in Phase II
            Timber.tag(TAG).d("Firebase sync not yet implemented")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error syncing with Firebase")
            Result.failure(e)
        }
    }
}