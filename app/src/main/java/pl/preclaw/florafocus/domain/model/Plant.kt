package pl.preclaw.florafocus.domain.model

/**
 * Domain model for Plant from catalog
 * 
 * Clean business model without any framework dependencies
 * Used throughout the app's business logic layer
 */
data class Plant(
    val id: String,
    val commonName: String,
    val latinName: String,
    val family: String,
    val plantType: PlantType,
    
    // Growing Requirements
    val lightRequirements: LightRequirements,
    val soilType: String?,
    val soilPHRange: Pair<Float, Float>?,
    val wateringFrequency: WateringFrequency,
    val growthDifficulty: GrowthDifficulty,
    
    // Properties
    val toxicity: Boolean,
    val edible: Boolean,
    val hardiness: String?,
    
    // Companion Planting
    val companionPlantIds: List<String>,
    val incompatiblePlantIds: List<String>,
    
    // Growing Periods
    val sowingPeriod: DateRange?,
    val harvestPeriod: DateRange?,
    val daysToHarvestRange: Pair<Int, Int>?,
    val averageYield: String?,
    
    // Growth Phases
    val growthPhases: List<GrowthPhase>,
    
    // Media
    val imageUrls: List<String>,
    val tags: List<String>,
    
    // Metadata
    val description: String?,
    val careInstructions: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Date range for sowing/harvest periods
 */
data class DateRange(
    val start: String, // Format: "MM-DD"
    val end: String    // Format: "MM-DD"
)

/**
 * Growth phase information
 */
data class GrowthPhase(
    val id: String,
    val phaseName: GrowthPhaseName,
    val displayName: String,
    val averageDurationRange: Pair<Int, Int>, // days (min, max)
    val description: String,
    val careInstructions: List<String>,
    val visualIndicators: List<String>,
    val autoTasks: List<AutoTask>
)

/**
 * Automatic task template
 */
data class AutoTask(
    val taskTitle: String,
    val taskDescription: String,
    val taskType: TaskType,
    val triggerDayOffset: Int,
    val priority: TaskPriority
)

// ==================== ENUMS ====================

enum class PlantType {
    VEGETABLE,
    HERB,
    FLOWER,
    SHRUB,
    FRUIT_SHRUB,
    TREE,
    FRUIT_TREE
}

enum class LightRequirements {
    FULL_SUN,
    PARTIAL_SHADE,
    FULL_SHADE
}

enum class WateringFrequency {
    DAILY,
    EVERY_2_DAYS,
    EVERY_3_DAYS,
    WEEKLY,
    BI_WEEKLY,
    MONTHLY,
    AS_NEEDED
}

enum class GrowthDifficulty {
    EASY,
    MEDIUM,
    HARD
}

enum class GrowthPhaseName {
    GERMINATION,
    SEEDLING,
    LEAF_GROWTH,
    FLOWERING,
    FRUITING,
    HARVEST,
    DORMANCY
}

enum class TaskType {
    WATERING,
    FERTILIZING,
    PRUNING,
    PEST_CONTROL,
    SOIL_CHECK,
    TRANSPLANTING,
    STAKING,
    THINNING,
    PINCHING,
    OBSERVATION,
    OTHER
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}
