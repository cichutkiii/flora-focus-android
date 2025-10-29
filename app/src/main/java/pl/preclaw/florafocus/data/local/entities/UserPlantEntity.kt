package pl.preclaw.florafocus.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pl.preclaw.florafocus.data.local.database.Converters

/**
 * User's individual plant instance
 * 
 * Represents a specific plant that the user is growing
 * Links to PlantCatalogEntity for reference data
 */
@Entity(
    tableName = "user_plants",
    foreignKeys = [
        ForeignKey(
            entity = PlantCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["catalogPlantId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("catalogPlantId"),
        Index("userId"),
        Index("areaObjectBedId")
    ]
)
@TypeConverters(Converters::class)
data class UserPlantEntity(
    @PrimaryKey
    val id: String,
    
    val userId: String,
    val catalogPlantId: String?, // Reference to PlantCatalogEntity
    
    // Custom identification
    val customName: String, // User's nickname for plant, e.g., "Big Tomato"
    val variety: String?, // Specific variety, e.g., "Malinowy Ożarowski"
    
    // Dates
    val acquisitionDate: Long, // When user got the plant/seeds
    val plantingDate: Long?, // When planted in final location
    
    // Location in garden (NEW structure from spec)
    val areaObjectBedId: String?, // ID of AreaObject.Bed where planted
    val cellRow: Int?, // Row in bed grid
    val cellColumn: Int?, // Column in bed grid
    
    // Growth tracking
    val currentPhaseId: String?, // Current GrowthPhaseData.id
    val currentPhaseName: GrowthPhaseName?, // Cached for quick access
    val phaseStartDate: Long?, // When current phase started
    
    // Care tracking
    val lastWateredDate: Long?,
    val lastFertilizedDate: Long?,
    
    // Health
    val healthStatus: HealthStatus = HealthStatus.HEALTHY,
    val lastHealthCheckDate: Long?,
    val healthScore: Int = 10, // 1-10 scale
    
    // Media & notes
    val imageUrls: List<String> = emptyList(),
    val notes: String?,
    
    // Metadata
    val isActive: Boolean = true, // False if plant died or was removed
    val harvestedDate: Long?, // When final harvest completed
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * History record of growth phases for a plant
 * Tracks when each phase started/ended
 */
@Entity(
    tableName = "plant_growth_history",
    foreignKeys = [
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
data class PlantGrowthHistoryEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val phaseId: String, // Reference to GrowthPhaseData.id
    val phaseName: GrowthPhaseName,
    
    val startDate: Long,
    val endDate: Long?, // null if current phase
    
    val userConfirmed: Boolean = false, // Did user manually confirm this phase?
    val autoDetected: Boolean = true, // Was it auto-detected by system?
    
    val notes: String?,
    val imageUrls: List<String> = emptyList(),
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Health record for a plant - tracks problems and symptoms
 */
@Entity(
    tableName = "health_records",
    foreignKeys = [
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
@TypeConverters(Converters::class)
data class HealthRecordEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val recordDate: Long,
    
    val healthScore: Int, // 1-10
    val symptoms: List<Symptom> = emptyList(),
    
    val diagnosis: String?,
    val possibleCauses: List<String> = emptyList(),
    val treatment: String?,
    val treatmentSteps: List<String> = emptyList(),
    
    val imageUrls: List<String> = emptyList(),
    
    val resolved: Boolean = false,
    val resolvedDate: Long?,
    
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Intervention record - any action taken on the plant
 */
@Entity(
    tableName = "interventions",
    foreignKeys = [
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId"), Index("interventionDate")]
)
@TypeConverters(Converters::class)
data class InterventionEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val interventionDate: Long,
    val interventionType: InterventionType,
    
    val description: String,
    val productUsed: String?, // Name of fertilizer/pesticide/etc
    val dosage: String?,
    
    val notes: String?,
    val imagesBefore: List<String> = emptyList(),
    val imagesAfter: List<String> = emptyList(),
    
    val effectiveness: Int?, // 1-5 stars rating
    val cost: Float?,
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Harvest record - tracking yields
 */
@Entity(
    tableName = "harvest_records",
    foreignKeys = [
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId"), Index("harvestDate")]
)
@TypeConverters(Converters::class)
data class HarvestRecordEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val harvestDate: Long,
    
    val quantity: Float,
    val unit: HarvestUnit,
    val quality: HarvestQuality,
    val usage: HarvestUsage,
    
    val marketValue: Float?, // Estimated value at market prices
    val notes: String?,
    val imageUrls: List<String> = emptyList(),
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Propagation record - tracking plant reproduction attempts
 */
@Entity(
    tableName = "propagation_records",
    foreignKeys = [
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentPlantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("parentPlantId"), Index("userId")]
)
@TypeConverters(Converters::class)
data class PropagationRecordEntity(
    @PrimaryKey
    val id: String,
    
    val userId: String,
    val parentPlantId: String, // The mother plant
    
    val method: PropagationMethod,
    val startDate: Long,
    val status: PropagationStatus,
    
    val successDate: Long?, // When successfully transplanted
    val failureDate: Long?,
    
    val resultingPlantId: String?, // Link to new UserPlantEntity if successful
    
    val notes: String?,
    val imageUrls: List<String> = emptyList(),
    
    val createdAt: Long = System.currentTimeMillis()
)

// ==================== ENUMS ====================

enum class HealthStatus {
    HEALTHY,
    NEEDS_ATTENTION,
    SICK,
    DYING,
    DEAD
}

enum class Symptom {
    YELLOW_LEAVES,
    BROWN_SPOTS,
    WILTING,
    PESTS_VISIBLE,
    MOLD,
    STUNTED_GROWTH,
    LEAF_DROP,
    HOLES_IN_LEAVES,
    DISCOLORATION,
    ROOT_ROT,
    POWDERY_MILDEW,
    BLOSSOM_END_ROT,
    CURLING_LEAVES,
    DRY_LEAVES,
    WEAK_STEMS,
    OTHER
}

enum class InterventionType {
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
    THINNING,
    PINCHING,
    OTHER
}

enum class HarvestUnit {
    KG,
    GRAMS,
    PIECES,
    BUNCHES,
    LITERS
}

enum class HarvestQuality {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR
}

enum class HarvestUsage {
    CONSUMED,
    PROCESSED,      // Canned, frozen, dried
    GIVEN_AWAY,
    SOLD,
    WASTED,
    COMPOSTED
}

enum class PropagationMethod {
    SEED,
    CUTTING,
    LAYERING,
    DIVISION,
    GRAFTING,
    OFFSET,
    BULB
}

enum class PropagationStatus {
    IN_PROGRESS,
    ROOTING,
    TRANSPLANTED,
    SUCCESS,
    FAILED
}
