package pl.preclaw.florafocus.domain.model

/**
 * Domain model for Garden
 * 
 * Top-level container for the user's garden
 */
data class Garden(
    val id: String,
    val userId: String,
    val name: String,
    val location: String?,
    val dimensions: Dimensions,
    val areas: List<GardenArea> = emptyList(),
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Garden area (section of garden)
 */
data class GardenArea(
    val id: String,
    val gardenId: String,
    val name: String,
    val position: Position2D,
    val size: Size2D,
    val rotation: Float,
    val areaType: AreaType,
    val sunExposure: SunExposure?,
    val soilType: String?,
    val soilPH: Float?,
    val beds: List<Bed> = emptyList(),
    val decorations: List<AreaDecoration> = emptyList(),
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Bed (growing area with grid)
 */
data class Bed(
    val id: String,
    val areaId: String,
    val name: String,
    val position: Position2D,
    val size: Size2D,
    val rotation: Float,
    val bedType: BedType,
    val gridRows: Int,
    val gridColumns: Int,
    val soilPH: Float?,
    val sunExposure: SunExposure?,
    val cells: List<BedCell> = emptyList(),
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Individual cell in bed grid
 */
data class BedCell(
    val id: String,
    val bedId: String,
    val rowIndex: Int,
    val columnIndex: Int,
    val currentPlantId: String?,
    val soilConditions: String?,
    val sunExposure: SunExposure?,
    val plantingHistory: List<PlantingHistoryEntry> = emptyList(),
    val notes: String?,
    val updatedAt: Long
)

/**
 * Planting history for rotation tracking
 */
data class PlantingHistoryEntry(
    val plantId: String,
    val plantFamily: String,
    val plantedDate: Long,
    val harvestedDate: Long?
)

/**
 * Area decoration (non-growing objects)
 */
data class AreaDecoration(
    val id: String,
    val areaId: String,
    val decorationType: DecorationType,
    val name: String?,
    val position: Position2D,
    val size: Size2D,
    val rotation: Float,
    val notes: String?,
    val createdAt: Long
)

/**
 * Rotation plan
 */
data class RotationPlan(
    val id: String,
    val gardenId: String,
    val planName: String,
    val seasonYear: Int,
    val seasonType: SeasonType,
    val rotationGroups: List<RotationGroup>,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Rotation group (family grouping)
 */
data class RotationGroup(
    val groupName: String,
    val plantFamily: String,
    val assignedBeds: List<String>,
    val rotationOrder: Int
)

// ==================== VALUE OBJECTS ====================

data class Position2D(
    val x: Float,
    val y: Float
)

data class Size2D(
    val width: Float,
    val height: Float
)

data class Dimensions(
    val width: Float,
    val height: Float,
    val unit: String = "m" // meters
)

// ==================== ENUMS ====================

enum class AreaType {
    VEGETABLE_GARDEN,
    FLOWER_GARDEN,
    HERB_GARDEN,
    ORCHARD,
    GREENHOUSE,
    RAISED_BEDS,
    CONTAINER_GARDEN,
    OTHER
}

enum class BedType {
    GROUND,
    RAISED,
    CONTAINER,
    GREENHOUSE_BED,
    HYDROPONIC
}

enum class SunExposure {
    FULL_SUN,          // 6+ hours
    PARTIAL_SUN,       // 4-6 hours
    PARTIAL_SHADE,     // 2-4 hours
    FULL_SHADE         // <2 hours
}

enum class DecorationType {
    TREE,
    POND,
    FOUNTAIN,
    COMPOST_BIN,
    SHED,
    PATH,
    FENCE,
    BENCH,
    OTHER
}

enum class SeasonType {
    SPRING,
    SUMMER,
    FALL,
    WINTER,
    YEAR_ROUND
}
