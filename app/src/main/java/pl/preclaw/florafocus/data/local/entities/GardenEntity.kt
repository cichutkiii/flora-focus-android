package pl.preclaw.florafocus.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pl.preclaw.florafocus.data.local.database.Converters

/**
 * Garden - Top level container (Canvas 2D)
 * 
 * Represents the entire garden space
 */
@Entity(
    tableName = "gardens",
    indices = [Index("userId")]
)
@TypeConverters(Converters::class)
data class GardenEntity(
    @PrimaryKey
    val id: String,
    
    val userId: String,
    val name: String,
    
    // Total dimensions in meters
    val totalWidthMeters: Float,
    val totalLengthMeters: Float,
    
    // Decorative objects outside areas (trees, ponds, paths)
    val decorativeObjects: List<GardenObject2D> = emptyList(),
    
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Decorative 2D object in garden (tree, pond, path, etc.)
 * Not a growing area, just visual/organizational
 */
data class GardenObject2D(
    val id: String,
    val type: GardenObjectType,
    val position: Position2D,
    val size: Size2D,
    val rotation: Float = 0f, // Degrees
    val metadata: Map<String, String> = emptyMap() // Custom properties
)

/**
 * Garden Area - Section of garden (Canvas 2D)
 * 
 * Examples: "Vegetable Garden", "Greenhouse", "Ornamental Section"
 */
@Entity(
    tableName = "garden_areas",
    foreignKeys = [
        ForeignKey(
            entity = GardenEntity::class,
            parentColumns = ["id"],
            childColumns = ["gardenId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("gardenId")]
)
@TypeConverters(Converters::class)
data class GardenAreaEntity(
    @PrimaryKey
    val id: String,
    
    val gardenId: String,
    val name: String,
    
    // Position and size within garden canvas
    @Embedded(prefix = "pos_")
    val position: Position2D,
    
    @Embedded(prefix = "size_")
    val size: Size2D,
    
    val rotation: Float = 0f,
    
    // Environmental conditions
    val sunExposure: SunExposure? = null,
    val soilType: String? = null,
    val soilPH: Float? = null,
    
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Bed (Growing area with grid) inside an Area
 * 
 * This is where plants actually grow
 * Contains a grid system for precise plant placement
 */
@Entity(
    tableName = "beds",
    foreignKeys = [
        ForeignKey(
            entity = GardenAreaEntity::class,
            parentColumns = ["id"],
            childColumns = ["areaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("areaId")]
)
@TypeConverters(Converters::class)
data class BedEntity(
    @PrimaryKey
    val id: String,
    
    val areaId: String,
    val name: String,
    
    // Position within area canvas
    @Embedded(prefix = "pos_")
    val position: Position2D,
    
    @Embedded(prefix = "size_")
    val size: Size2D,
    
    val rotation: Float = 0f,
    
    // Type and structure
    val bedType: BedType,
    
    // Grid configuration
    val gridRows: Int,
    val gridColumns: Int,
    
    // Environmental conditions (can override area defaults)
    val soilPH: Float? = null,
    val sunExposure: SunExposure? = null,
    
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
{
    // Helper function - nie przechowywane w DB
    fun toCellsMap(cells: List<BedCellEntity>): Map<Pair<Int, Int>, BedCellEntity> {
        return cells.associateBy { Pair(it.rowIndex, it.columnIndex) }
    }
}
/**
 * Individual cell in a bed's grid
 * Tracks what's planted and history for rotation
 */
@Entity(
    tableName = "bed_cells",
    foreignKeys = [
        ForeignKey(
            entity = BedEntity::class,
            parentColumns = ["id"],
            childColumns = ["bedId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["currentPlantId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("bedId"),
        Index("currentPlantId"),
        Index(value = ["bedId", "rowIndex", "columnIndex"], unique = true)
    ]
)
@TypeConverters(Converters::class)
data class BedCellEntity(
    @PrimaryKey
    val id: String,
    
    val bedId: String,
    val rowIndex: Int,
    val columnIndex: Int,
    
    // Current plant in this cell
    val currentPlantId: String? = null,
    
    // Local conditions (can override bed defaults)
    val soilConditions: String? = null,
    val sunExposure: SunExposure? = null,
    
    // Planting history for rotation tracking
    val plantingHistory: List<CellHistoryRecord> = emptyList(),
    
    val notes: String?,
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * History record for crop rotation in a cell
 */
data class CellHistoryRecord(
    val plantId: String,
    val plantCatalogId: String,
    val plantFamily: String, // e.g., "Solanaceae" - crucial for rotation
    val plantedDate: Long,
    val harvestedDate: Long?,
    val season: Int, // Year
    val yieldKg: Float? = null
)

/**
 * Decoration object within an area (path, compost, border, etc.)
 *
 * Domain representation: AreaObject.Decoration
 */
@Entity(
    tableName = "area_decorations",
    foreignKeys = [
        ForeignKey(
            entity = GardenAreaEntity::class,
            parentColumns = ["id"],
            childColumns = ["areaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("areaId")]
)
@TypeConverters(Converters::class)
data class AreaDecorationEntity(
    @PrimaryKey
    val id: String,
    
    val areaId: String,
    val decorationType: DecorationType,
    
    @Embedded(prefix = "pos_")
    val position: Position2D,
    
    @Embedded(prefix = "size_")
    val size: Size2D,
    
    val rotation: Float = 0f,
    val metadata: Map<String, String> = emptyMap(),
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Rotation plan for a bed
 * Plans what to plant in future seasons
 */
@Entity(
    tableName = "rotation_plans",
    foreignKeys = [
        ForeignKey(
            entity = BedEntity::class,
            parentColumns = ["id"],
            childColumns = ["bedId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bedId"), Index("year"), Index("season")]
)
@TypeConverters(Converters::class)
data class RotationPlanEntity(
    @PrimaryKey
    val id: String,
    
    val bedId: String,
    val year: Int,
    val season: Season,
    
    // Planned plants: "row,col" -> plantCatalogId
    val plannedPlants: Map<String, String> = emptyMap(),
    
    val notes: String?,
    val warnings: List<RotationWarning> = emptyList(),
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Warning about rotation issues
 */
data class RotationWarning(
    val cellPosition: String, // "row,col"
    val warningType: RotationWarningType,
    val message: String,
    val lastPlantFamily: String?,
    val recommendedAlternatives: List<String> = emptyList() // Plant IDs
)

// ==================== VALUE CLASSES ====================

/**
 * 2D Position in meters from top-left corner
 */
data class Position2D(
    val x: Float,
    val y: Float
)

/**
 * Size in meters
 */
data class Size2D(
    val width: Float,
    val height: Float
)

/**
 * Decorative 2D object in garden (tree, pond, path, etc.)
 * Not a growing area, just visual/organizational
 *
 * NOTE: These are objects at Garden level (outside areas)
 * For objects inside areas, use AreaDecorationEntity
 */
enum class GardenObjectType {
    TREE,
    SHRUB,
    POND,
    PATH,
    FENCE,
    SHED,
    COMPOST_BIN,
    STATUE,
    BENCH,
    FOUNTAIN,
    OTHER
}

enum class BedType {
    RAISED_BED,     // Podniesiona grządka
    IN_GROUND,      // W ziemi
    CONTAINER,      // Doniczka/skrzynia
    GREENHOUSE,     // Szklarnia
    VERTICAL,       // Ogród wertykalny
    ROW            // Rząd (traditional farming)
}

enum class SunExposure {
    FULL_SUN,       // 6+ hours direct sun
    PARTIAL_SUN,    // 3-6 hours sun
    PARTIAL_SHADE,  // 3-6 hours, mostly shade
    FULL_SHADE      // <3 hours sun
}

enum class DecorationType {
    PATH,
    WATER_FEATURE,
    STONE,
    BORDER,
    COMPOST_BIN,
    MULCH_AREA,
    PATIO,
    OTHER
}

enum class Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER
}

enum class RotationWarningType {
    SAME_FAMILY,        // Same plant family as last season
    TOO_FREQUENT,       // Planted too often in this spot
    SOIL_DEPLETION,     // Heavy feeders in a row
    DISEASE_RISK,       // Disease buildup risk
    INCOMPATIBLE_SUCCESSION // Bad succession after specific plant
}
