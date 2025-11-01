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
    
    // Garden-wide settings
    val defaultSoilType: String?,
    val defaultSunExposure: SunExposure?,
    val climateZone: String?, // USDA hardiness zone
    val location: String?, // City, region
    
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
    val drainage: String? = null, // "good", "poor", "excellent"
    val microclimate: String? = null, // User notes about this area
    
    // Area settings
    val isGreenhouse: Boolean = false,
    val hasIrrigation: Boolean = false,
    val hasCompost: Boolean = false,
    
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Bed - Individual growing bed within an area
 * 
 * This is where actual plants are grown in a grid system
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
    
    // Position within area
    @Embedded(prefix = "pos_")
    val position: Position2D,
    
    @Embedded(prefix = "size_")
    val size: Size2D,
    
    val rotation: Float = 0f,
    
    // Bed properties
    val bedType: BedType,
    val heightCm: Float? = null, // For raised beds
    val materialType: String? = null, // "wood", "stone", "metal"
    
    // Grid system
    val gridRows: Int,
    val gridColumns: Int,
    val cellSizeWidthCm: Float, // Each cell width in centimeters
    val cellSizeLengthCm: Float, // Each cell length in centimeters
    
    // Soil properties
    val soilType: String? = null,
    val soilPH: Float? = null,
    val lastSoilTest: Long? = null,
    val soilAmendments: List<String> = emptyList(), // "compost", "lime", "fertilizer"
    
    // Infrastructure
    val hasIrrigation: Boolean = false,
    val irrigationType: String? = null, // "drip", "sprinkler", "manual"
    val hasMulch: Boolean = false,
    val mulchType: String? = null,
    
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Individual cell within a bed grid
 * 
 * This is the smallest unit where plants are tracked
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
        Index(value = ["bedId", "row", "column"], unique = true) // Unique position per bed
    ]
)
@TypeConverters(Converters::class)
data class BedCellEntity(
    @PrimaryKey
    val id: String,
    
    val bedId: String,
    val row: Int, // 0-based row index
    val column: Int, // 0-based column index
    
    // Current occupation
    val currentPlantId: String? = null, // UserPlant currently in this cell
    val plantedDate: Long? = null,
    val expectedHarvestDate: Long? = null,
    
    // Cell-specific conditions
    val sunExposure: SunExposure? = null, // Override area/bed defaults
    val soilCondition: String? = null, // Cell-specific soil notes
    val drainage: String? = null,
    
    // History for rotation planning
    val plantingHistory: List<CellHistoryRecord> = emptyList(),
    
    // Status
    val isBlocked: Boolean = false, // Temporarily unusable
    val blockReason: String? = null, // "damaged", "under repair", "soil rest"
    
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Decorative elements within areas (non-growing objects)
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
    val name: String,
    val DecType: DecorationType,
    
    @Embedded(prefix = "pos_")
    val position: Position2D,
    
    @Embedded(prefix = "size_")
    val size: Size2D,
    
    val rotation: Float = 0f,
    
    // Decoration properties
    val material: String? = null,
    val color: String? = null,
    val style: String? = null,
    
    // Maintenance
    val requiresMaintenance: Boolean = false,
    val lastMaintenanceDate: Long? = null,
    val nextMaintenanceDate: Long? = null,
    
    val notes: String? = null,
    val imageUrls: List<String> = emptyList(),
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Rotation planning for beds/cells
 * 
 * Tracks what should be planted where and when for optimal rotation
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
    indices = [
        Index("bedId"),
        Index("year"),
        Index("season")
    ]
)
@TypeConverters(Converters::class)
data class RotationPlanEntity(
    @PrimaryKey
    val id: String,
    
    val bedId: String,
    val year: Int,
    val season: Season,
    
    // Planned rotation
    val plannedPlantFamily: String? = null, // "Solanaceae", "Brassicaceae"
    val plannedPlantType: PlantType? = null,
    val suggestedPlants: List<String> = emptyList(), // Plant catalog IDs
    
    // Rotation validation
    val rotationWarnings: List<RotationWarning> = emptyList(),
    val isRecommended: Boolean = true,
    
    // Soil preparation plan
    val soilPreparation: List<String> = emptyList(), // "add compost", "lime application"
    val preparationStartDate: Long? = null,
    val preparationCompleted: Boolean = false,
    
    // Success tracking
    val wasImplemented: Boolean = false,
    val actualPlantFamily: String? = null,
    val implementationNotes: String? = null,
    val yieldResults: String? = null, // Success assessment after harvest
    
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
