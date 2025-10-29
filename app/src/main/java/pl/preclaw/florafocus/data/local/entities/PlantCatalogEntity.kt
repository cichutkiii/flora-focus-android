package pl.preclaw.florafocus.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pl.preclaw.florafocus.data.local.database.Converters

/**
 * Plant from the general catalog (template/reference plant)
 * 
 * This represents the "master data" for a plant species/variety
 * Users will create UserPlantEntity instances based on these templates
 */
@Entity(tableName = "plant_catalog")
@TypeConverters(Converters::class)
data class PlantCatalogEntity(
    @PrimaryKey
    val id: String,
    
    // Basic Info
    val commonName: String,
    val latinName: String,
    val family: String, // e.g., "Solanaceae", "Brassicaceae"
    val plantType: PlantType,
    
    // Growing Requirements
    val lightRequirements: LightRequirements,
    val soilType: String?,
    val soilPHMin: Float?,
    val soilPHMax: Float?,
    val wateringFrequency: WateringFrequency,
    val growthDifficulty: GrowthDifficulty,
    
    // Plant Properties
    val toxicity: Boolean = false,
    val edible: Boolean = false,
    val hardiness: String?, // USDA zone, e.g., "5-9"
    
    // Companion Planting
    val companionPlantIds: List<String> = emptyList(), // IDs of compatible plants
    val incompatiblePlantIds: List<String> = emptyList(), // IDs of incompatible plants
    
    // Growing Periods
    val sowingPeriodStart: String?, // Format: "MM-DD" e.g., "04-01"
    val sowingPeriodEnd: String?, // Format: "MM-DD" e.g., "05-15"
    val harvestPeriodStart: String?, // Format: "MM-DD"
    val harvestPeriodEnd: String?, // Format: "MM-DD"
    val daysToHarvestMin: Int?,
    val daysToHarvestMax: Int?,
    val averageYield: String?, // e.g., "5-8 kg per plant"
    
    // Growth Phases (JSON stored as string, converted by TypeConverter)
    val growthPhases: List<GrowthPhaseData> = emptyList(),
    
    // Media
    val imageUrls: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    
    // Metadata
    val description: String?,
    val careInstructions: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Growth phase information for a plant
 * Embedded in PlantCatalogEntity
 */
data class GrowthPhaseData(
    val id: String,
    val phaseName: GrowthPhaseName,
    val displayName: String, // Localized name
    val averageDurationDaysMin: Int,
    val averageDurationDaysMax: Int,
    val description: String?,
    val careInstructions: List<String> = emptyList(),
    val visualIndicators: List<String> = emptyList(), // e.g., "First leaves appear"
    val autoTasks: List<AutoTaskData> = emptyList()
)

/**
 * Automatic task that should be created when a plant enters a phase
 */
data class AutoTaskData(
    val taskTitle: String,
    val taskDescription: String,
    val taskType: TaskType,
    val triggerDayOffset: Int, // Days from phase start (0 = first day)
    val priority: TaskPriority
)

// ==================== ENUMS ====================

enum class PlantType {
    VEGETABLE,
    FLOWER,
    SHRUB,
    FRUIT_SHRUB,
    TREE,
    FRUIT_TREE,
    HERB
}

enum class LightRequirements {
    FULL_SUN,      // 6+ hours direct sun
    PARTIAL_SUN,   // 3-6 hours sun
    SHADE          // <3 hours sun
}

enum class WateringFrequency {
    DAILY,
    EVERY_2_DAYS,
    EVERY_3_DAYS,
    TWICE_WEEKLY,
    WEEKLY,
    EVERY_2_WEEKS,
    MONTHLY,
    WHEN_DRY
}

enum class GrowthDifficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}

enum class GrowthPhaseName {
    GERMINATION,        // Kiełkowanie
    VEGETATIVE,         // Wzrost wegetatywny
    FLOWERING,          // Kwitnienie
    FRUITING,           // Owocowanie
    RIPENING,           // Dojrzewanie
    HARVEST,            // Zbiór
    DORMANCY,           // Spoczynek zimowy
    SPROUTING,          // Pąkowanie (trees/shrubs)
    ROOT_DEVELOPMENT,   // Rozwój korzeni/bulw
    LEAF_GROWTH,        // Wzrost liści
    SENESCENCE          // Opadanie liści/koniec cyklu
}

enum class TaskType {
    WATERING,
    FERTILIZING,
    SPRAYING,
    PRUNING,
    STAKING,
    TRANSPLANTING,
    WEEDING,
    MULCHING,
    PEST_CONTROL,
    DISEASE_TREATMENT,
    HARVESTING,
    SOWING,
    THINNING,
    PINCHING,          // Pasynkowanie
    SUPPORT_CHECK,
    SOIL_CHECK,
    CUSTOM
}

enum class TaskPriority {
    CRITICAL,   // Must do today (frost warning, plant dying)
    HIGH,       // Do within 1-2 days
    MEDIUM,     // Do this week
    LOW         // Good practice, but can wait
}
