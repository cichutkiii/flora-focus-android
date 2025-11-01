package pl.preclaw.florafocus.data.local.entities

/**
 * Complete data types and enums for Flora Focus project
 * Based on technical specification
 */

// ==================== PLANT RELATED ENUMS ====================

enum class PlantType {
    VEGETABLE,
    FLOWER, 
    SHRUB,
    FRUIT_SHRUB,
    TREE,
    FRUIT_TREE,
    HERB,
    GRASS,
    SUCCULENT,
    BULB
}

enum class LightRequirements {
    FULL_SUN,     // 6+ hours direct sun
    PARTIAL_SUN,  // 3-6 hours direct sun  
    PARTIAL_SHADE, // 3-6 hours partial sun
    SHADE         // Less than 3 hours direct sun
}

enum class WateringFrequency {
    DAILY,
    EVERY_2_DAYS,
    EVERY_3_DAYS,
    WEEKLY,
    BI_WEEKLY,
    MONTHLY,
    AS_NEEDED,
    DROUGHT_TOLERANT
}

enum class GrowthDifficulty {
    EASY,      // Beginner friendly
    MEDIUM,    // Some experience needed
    HARD,      // Advanced gardener
    EXPERT     // Very challenging
}

// ==================== PLANT HEALTH & STATUS ====================

enum class HealthStatus {
    EXCELLENT,  // 9-10 points
    HEALTHY,    // 7-8 points
    GOOD,       // 5-6 points
    POOR,       // 3-4 points
    CRITICAL,   // 1-2 points
    DEAD        // 0 points
}

enum class GrowthPhaseName {
    GERMINATION,    // Kiełkowanie
    SEEDLING,       // Siewka
    VEGETATIVE,     // Wzrost wegetatywny
    FLOWERING,      // Kwitnienie
    FRUITING,       // Owocowanie
    RIPENING,       // Dojrzewanie
    HARVEST_READY,  // Gotowe do zbioru
    DORMANCY,       // Spoczynek
    DECLINE         // Zamieranie
}

enum class Symptom {
    YELLOWING_LEAVES,
    BROWN_SPOTS,
    WILTING,
    CURLED_LEAVES,
    HOLES_IN_LEAVES,
    STUNTED_GROWTH,
    SOFT_STEM,
    ROOT_ROT,
    MOLD,
    INSECT_DAMAGE,
    DISCOLORATION,
    DROOPING,
    BURNT_EDGES,
    STICKY_LEAVES
}

// ==================== GARDEN MANAGEMENT ====================

enum class GardenObjectType {
    TREE,
    POND,
    PATH,
    SHED,
    GREENHOUSE,
    COMPOST_BIN,
    FOUNTAIN,
    STATUE,
    FENCE,
    GATE,
    TOOL_STORAGE,
    IRRIGATION_SYSTEM
}

enum class BedType {
    RAISED,      // Grządka podwyższona
    GROUND,      // Grządka naziemna
    CONTAINER,   // Doniczka/pojemnik
    GREENHOUSE,  // Szklarnia
    POLYTUNNEL   // Tunel foliowy
}

enum class SunExposure {
    FULL_SUN,
    MORNING_SUN,
    AFTERNOON_SUN,
    PARTIAL_SHADE,
    FULL_SHADE,
    FILTERED_LIGHT
}

enum class DecorationType {
    WATER_FEATURE,
    SCULPTURE,
    SEATING,
    LIGHTING,
    PATHWAYS,
    BORDERS,
    ROCK_GARDEN,
    PERGOLA
}

enum class Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER
}

// ==================== TASKS & PLANNING ====================

enum class TaskType {
    WATERING,
    FERTILIZING,
    PRUNING,
    WEEDING,
    PEST_CONTROL,
    DISEASE_TREATMENT,
    HARVESTING,
    PLANTING,
    TRANSPLANTING,
    SOIL_PREPARATION,
    MULCHING,
    STAKING,
    DEAD_HEADING,
    GENERAL_MAINTENANCE,
    OBSERVATION,
    SEED_STARTING
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class RecurrencePattern {
    DAILY,
    WEEKLY,
    MONTHLY,
    SEASONAL,
    CUSTOM
}

// ==================== INTERVENTIONS & RECORDS ====================

enum class InterventionType {
    WATERING,
    FERTILIZER_APPLICATION,
    PEST_TREATMENT,
    DISEASE_TREATMENT,
    PRUNING,
    TRANSPLANTING,
    SOIL_AMENDMENT,
    MULCHING,
    STAKING,
    PHOTO_DOCUMENTATION,
    GROWTH_MEASUREMENT,
    HEALTH_CHECK,
    HARVESTING,
    OTHER
}

enum class HarvestQuality {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    DAMAGED
}

enum class HarvestUnit {
    PIECES,
    GRAMS,
    KILOGRAMS,
    LITERS,
    BUNCHES,
    STEMS,
    HEADS,
    PODS
}

enum class HarvestUsage {
    FRESH_CONSUMPTION,
    COOKING,
    PRESERVING,
    FREEZING,
    DRYING,
    SEEDS,
    COMPOSTING,
    SHARING,
    SELLING
}

// ==================== PROPAGATION ====================

enum class PropagationMethod {
    SEED,
    CUTTING,
    DIVISION,
    LAYERING,
    GRAFTING,
    BULB_OFFSET,
    LEAF_CUTTING,
    ROOT_CUTTING
}

enum class PropagationStatus {
    STARTED,
    ROOTING,
    TRANSPLANTED,
    ESTABLISHED,
    FAILED
}

// ==================== ROTATION PLANNING ====================

enum class RotationWarningType {
    SAME_FAMILY,     // Ta sama rodzina co poprzednia roślina
    TOO_SOON,        // Za wcześnie na tę rodzinę
    NUTRIENT_CONFLICT, // Konflikt wymagań odżywczych
    DISEASE_RISK,    // Ryzyko przeniesienia chorób
    PEST_RISK        // Ryzyko przeniesienia szkodników
}

// ==================== DATA CLASSES ====================

/**
 * 2D Position in canvas (meters from origin)
 */
data class Position2D(
    val x: Float,
    val y: Float
)

/**
 * 2D Size (width x height in meters)
 */
data class Size2D(
    val width: Float,
    val height: Float
)

/**
 * Growth phase definition from plant catalog
 */
data class GrowthPhaseData(
    val id: String,
    val phaseName: GrowthPhaseName,
    val displayName: String, // "Kiełkowanie", "Wzrost wegetatywny"
    val averageDurationDays: IntRange, // 7..14
    val description: String,
    val careInstructions: List<String>, // Co robić w tej fazie
    val visualIndicators: List<String>, // "Pierwsze liście", "Pojawienie się kwiatów"
    val autoTasks: List<AutoTaskData> = emptyList() // Automatyczne zadania
)

/**
 * Auto-generated task definition
 */
data class AutoTaskData(
    val taskTitle: String,
    val taskDescription: String,
    val taskType: TaskType,
    val triggerDay: Int, // Dzień od początku fazy (0 = pierwszy dzień)
    val priority: TaskPriority,
    val weatherDependent: Boolean = false
)

/**
 * Cell history record for rotation tracking
 */
data class CellHistoryRecord(
    val year: Int,
    val season: Season,
    val plantId: String?,
    val plantFamily: String?,
    val plantedDate: Long?,
    val harvestedDate: Long?,
    val yieldAmount: Float? = null,
    val yieldUnit: HarvestUnit? = null,
    val notes: String? = null
)

/**
 * Rotation warning details
 */
data class RotationWarning(
    val type: RotationWarningType,
    val message: String,
    val severity: TaskPriority, // LOW = info, MEDIUM = warning, HIGH = error
    val recommendation: String?
)
