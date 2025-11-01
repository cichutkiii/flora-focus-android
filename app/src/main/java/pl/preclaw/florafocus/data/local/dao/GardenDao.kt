package pl.preclaw.florafocus.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.data.local.entities.*

/**
 * Data Access Object for Garden mapping
 */
@Dao
interface GardenDao {

    // ==================== GARDEN - INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGarden(garden: GardenEntity)

    // ==================== GARDEN - UPDATE ====================

    @Update
    suspend fun updateGarden(garden: GardenEntity)

    // ==================== GARDEN - DELETE ====================

    @Delete
    suspend fun deleteGarden(garden: GardenEntity)

    @Query("DELETE FROM gardens WHERE id = :gardenId")
    suspend fun deleteGardenById(gardenId: String)

    // ==================== GARDEN - QUERIES ====================

    /**
     * Get all gardens for user
     */
    @Query("SELECT * FROM gardens WHERE userId = :userId ORDER BY name ASC")
    fun getAllGardens(userId: String): Flow<List<GardenEntity>>

    /**
     * Get garden by ID
     */
    @Query("SELECT * FROM gardens WHERE id = :gardenId")
    suspend fun getGardenById(gardenId: String): GardenEntity?

    @Query("SELECT * FROM gardens WHERE id = :gardenId")
    fun getGardenByIdFlow(gardenId: String): Flow<GardenEntity?>

    /**
     * Get primary garden (first one)
     */
    @Query("SELECT * FROM gardens WHERE userId = :userId ORDER BY createdAt ASC LIMIT 1")
    suspend fun getPrimaryGarden(userId: String): GardenEntity?

    // ==================== AREA - INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArea(area: GardenAreaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAreas(areas: List<GardenAreaEntity>)

    // ==================== AREA - UPDATE ====================

    @Update
    suspend fun updateArea(area: GardenAreaEntity)

    // ==================== AREA - DELETE ====================

    @Delete
    suspend fun deleteArea(area: GardenAreaEntity)

    @Query("DELETE FROM garden_areas WHERE id = :areaId")
    suspend fun deleteAreaById(areaId: String)

    // ==================== AREA - QUERIES ====================

    /**
     * Get all areas in a garden
     */
    @Query("SELECT * FROM garden_areas WHERE gardenId = :gardenId ORDER BY name ASC")
    fun getAreasInGarden(gardenId: String): Flow<List<GardenAreaEntity>>

    /**
     * Get area by ID
     */
    @Query("SELECT * FROM garden_areas WHERE id = :areaId")
    suspend fun getAreaById(areaId: String): GardenAreaEntity?

    @Query("SELECT * FROM garden_areas WHERE id = :areaId")
    fun getAreaByIdFlow(areaId: String): Flow<GardenAreaEntity?>

    /**
     * Get count of areas in garden
     */
    @Query("SELECT COUNT(*) FROM garden_areas WHERE gardenId = :gardenId")
    suspend fun getAreaCount(gardenId: String): Int

    // ==================== BED - INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBed(bed: BedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeds(beds: List<BedEntity>)

    // ==================== BED - UPDATE ====================

    @Update
    suspend fun updateBed(bed: BedEntity)

    // ==================== BED - DELETE ====================

    @Delete
    suspend fun deleteBed(bed: BedEntity)

    @Query("DELETE FROM beds WHERE id = :bedId")
    suspend fun deleteBedById(bedId: String)

    // ==================== BED - QUERIES ====================

    /**
     * Get all beds in an area
     */
    @Query("SELECT * FROM beds WHERE areaId = :areaId ORDER BY name ASC")
    fun getBedsInArea(areaId: String): Flow<List<BedEntity>>

    /**
     * Get bed by ID
     */
    @Query("SELECT * FROM beds WHERE id = :bedId")
    suspend fun getBedById(bedId: String): BedEntity?

    @Query("SELECT * FROM beds WHERE id = :bedId")
    fun getBedByIdFlow(bedId: String): Flow<BedEntity?>

    /**
     * Get all beds in a garden (across all areas)
     */
    @Query("""
        SELECT b.* FROM beds b
        INNER JOIN garden_areas ga ON b.areaId = ga.id
        WHERE ga.gardenId = :gardenId
        ORDER BY b.name ASC
    """)
    fun getAllBedsInGarden(gardenId: String): Flow<List<BedEntity>>

    /**
     * Get beds by type
     */
    @Query("""
        SELECT b.* FROM beds b
        INNER JOIN garden_areas ga ON b.areaId = ga.id
        WHERE ga.gardenId = :gardenId AND b.bedType = :type
        ORDER BY b.name ASC
    """)
    fun getBedsByType(gardenId: String, type: BedType): Flow<List<BedEntity>>

    /**
     * Get count of beds in area
     */
    @Query("SELECT COUNT(*) FROM beds WHERE areaId = :areaId")
    suspend fun getBedCount(areaId: String): Int

    // ==================== BED CELL - INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBedCell(cell: BedCellEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBedCells(cells: List<BedCellEntity>)

    // ==================== BED CELL - UPDATE ====================

    @Update
    suspend fun updateBedCell(cell: BedCellEntity)

    /**
     * Assign plant to cell
     */
    @Query("""
        UPDATE bed_cells 
        SET currentPlantId = :plantId,
            updatedAt = :timestamp
        WHERE bedId = :bedId AND 'row' = :row AND 'column' = :column
    """)
    suspend fun assignPlantToCell(
        bedId: String,
        row: Int,
        column: Int,
        plantId: String?,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Clear plant from cell
     */
    @Query("""
        UPDATE bed_cells 
        SET currentPlantId = NULL,
            updatedAt = :timestamp
        WHERE id = :cellId
    """)
    suspend fun clearPlantFromCell(cellId: String, timestamp: Long = System.currentTimeMillis())

    // ==================== BED CELL - DELETE ====================

    @Delete
    suspend fun deleteBedCell(cell: BedCellEntity)

    @Query("DELETE FROM bed_cells WHERE id = :cellId")
    suspend fun deleteBedCellById(cellId: String)

    @Query("DELETE FROM bed_cells WHERE bedId = :bedId")
    suspend fun deleteAllCellsInBed(bedId: String)

    // ==================== BED CELL - QUERIES ====================

    /**
     * Get all cells in a bed
     */
    @Query("SELECT * FROM bed_cells WHERE bedId = :bedId ORDER BY 'row', 'column'")
    fun getCellsInBed(bedId: String): Flow<List<BedCellEntity>>

    /**
     * Get specific cell
     */
    @Query("SELECT * FROM bed_cells WHERE bedId = :bedId AND 'row' = :row AND 'column' = :column")
    suspend fun getCell(bedId: String, row: Int, column: Int): BedCellEntity?

    /**
     * Get cell by ID
     */
    @Query("SELECT * FROM bed_cells WHERE id = :cellId")
    suspend fun getCellById(cellId: String): BedCellEntity?

    /**
     * Get occupied cells in bed
     */
    @Query("SELECT * FROM bed_cells WHERE bedId = :bedId AND currentPlantId IS NOT NULL ORDER BY 'row', 'column'")
    fun getOccupiedCellsInBed(bedId: String): Flow<List<BedCellEntity>>

    /**
     * Get empty cells in bed
     */
    @Query("SELECT * FROM bed_cells WHERE bedId = :bedId AND currentPlantId IS NULL ORDER BY 'row', 'column'")
    fun getEmptyCellsInBed(bedId: String): Flow<List<BedCellEntity>>

    /**
     * Get cells adjacent to a position (for companion planting validation)
     * Note: This is a simplified query - actual implementation would need more complex logic
     */
    @Query("""
        SELECT * FROM bed_cells 
        WHERE bedId = :bedId 
        AND currentPlantId IS NOT NULL
        AND (
            ('row' = :row AND (`column` = :col - 1 OR 'column' = :col + 1))
            OR
            ('column' = :col AND ('row' = :row - 1 OR 'row' = :row + 1))
        )
    """)
    suspend fun getAdjacentOccupiedCells(bedId: String, row: Int, col: Int): List<BedCellEntity>

    /**
     * Get cell occupancy statistics
     */
    /**
     * Get beds in area - synchronous for mapper
     */
    @Query("SELECT * FROM beds WHERE areaId = :areaId ORDER BY name ASC")
    suspend fun getBedsInAreaSync(areaId: String): List<BedEntity>

    /**
     * Get cells in bed - synchronous for mapper
     */
    @Query("SELECT * FROM bed_cells WHERE bedId = :bedId ORDER BY 'row', 'column' ASC")
    suspend fun getCellsInBedSync(bedId: String): List<BedCellEntity>

    /**
     * Get decorations in area - synchronous for mapper
     */
    @Query("SELECT * FROM area_decorations WHERE areaId = :areaId")
    suspend fun getDecorationsInAreaSync(areaId: String): List<AreaDecorationEntity>

    /**
     * Insert cells - batch
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCells(cells: List<BedCellEntity>)
    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN currentPlantId IS NOT NULL THEN 1 ELSE 0 END) as occupied
        FROM bed_cells
        WHERE bedId = :bedId
    """)
    suspend fun getCellOccupancyStats(bedId: String): CellOccupancyStats

    // ==================== AREA DECORATION - INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecoration(decoration: AreaDecorationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecorations(decorations: List<AreaDecorationEntity>)

    // ==================== AREA DECORATION - UPDATE ====================

    @Update
    suspend fun updateDecoration(decoration: AreaDecorationEntity)

    // ==================== AREA DECORATION - DELETE ====================

    @Delete
    suspend fun deleteDecoration(decoration: AreaDecorationEntity)

    @Query("DELETE FROM area_decorations WHERE id = :decorationId")
    suspend fun deleteDecorationById(decorationId: String)

    // ==================== AREA DECORATION - QUERIES ====================

    /**
     * Get decorations in area
     */
    @Query("SELECT * FROM area_decorations WHERE areaId = :areaId")
    fun getDecorationsInArea(areaId: String): Flow<List<AreaDecorationEntity>>

    /**
     * Get decorations by type
     */
    @Query("SELECT * FROM area_decorations WHERE areaId = :areaId AND DecType = :type")
    fun getDecorationsByType(areaId: String, type: DecorationType): Flow<List<AreaDecorationEntity>>

    // ==================== ROTATION PLAN - INSERT ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRotationPlan(plan: RotationPlanEntity)

    // ==================== ROTATION PLAN - UPDATE ====================

    @Update
    suspend fun updateRotationPlan(plan: RotationPlanEntity)

    // ==================== ROTATION PLAN - DELETE ====================

    @Delete
    suspend fun deleteRotationPlan(plan: RotationPlanEntity)

    @Query("DELETE FROM rotation_plans WHERE id = :planId")
    suspend fun deleteRotationPlanById(planId: String)

    // ==================== ROTATION PLAN - QUERIES ====================

    /**
     * Get rotation plans for bed
     */
    @Query("SELECT * FROM rotation_plans WHERE bedId = :bedId ORDER BY year DESC, season DESC")
    fun getRotationPlansForBed(bedId: String): Flow<List<RotationPlanEntity>>

    /**
     * Get rotation plan for specific season
     */
    @Query("SELECT * FROM rotation_plans WHERE bedId = :bedId AND year = :year AND season = :season")
    suspend fun getRotationPlan(bedId: String, year: Int, season: Season): RotationPlanEntity?

    /**
     * Get current season's plan
     */
    @Query("SELECT * FROM rotation_plans WHERE bedId = :bedId AND year = :year AND season = :season LIMIT 1")
    suspend fun getCurrentRotationPlan(bedId: String, year: Int, season: Season): RotationPlanEntity?

    /**
     * Get plans with warnings
     */
    @Query("SELECT * FROM rotation_plans WHERE bedId = :bedId AND rotationWarnings IS NOT NULL ORDER BY year DESC, season DESC")
    fun getRotationPlansWithWarnings(bedId: String): Flow<List<RotationPlanEntity>>
}

/**
 * Result class for cell occupancy statistics
 */
data class CellOccupancyStats(
    val total: Int,
    val occupied: Int
) {
    val empty: Int
        get() = total - occupied
    
    val occupancyRate: Float
        get() = if (total > 0) (occupied.toFloat() / total.toFloat()) * 100 else 0f
}
