package pl.preclaw.florafocus.domain.model

/**
 * Domain model for user's individual plant instance
 * 
 * Represents a specific plant that the user is growing
 */
data class UserPlant(
    val id: String,
    val userId: String,
    val catalogPlantId: String?,
    
    // Custom identification
    val customName: String,
    val variety: String?,
    
    // Dates
    val acquisitionDate: Long,
    val plantingDate: Long?,
    
    // Location in garden
    val location: PlantLocation?,
    
    // Growth tracking
    val currentPhase: PhaseInfo?,
    
    // Care tracking
    val lastWateredDate: Long?,
    val lastFertilizedDate: Long?,
    
    // Health
    val healthStatus: HealthStatus,
    val lastHealthCheckDate: Long?,
    val healthScore: Int,
    
    // Media & notes
    val imageUrls: List<String>,
    val notes: String?,
    
    // Metadata
    val isActive: Boolean,
    val harvestedDate: Long?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Plant location in garden
 */
data class PlantLocation(
    val bedId: String,
    val cellRow: Int,
    val cellColumn: Int
)

/**
 * Current phase information
 */
data class PhaseInfo(
    val phaseId: String,
    val phaseName: GrowthPhaseName,
    val startDate: Long
)

/**
 * Growth history record
 */
data class GrowthHistory(
    val id: String,
    val plantId: String,
    val phaseId: String,
    val phaseName: GrowthPhaseName,
    val startDate: Long,
    val endDate: Long?,
    val userConfirmed: Boolean,
    val autoDetected: Boolean,
    val notes: String?,
    val imageUrls: List<String>,
    val createdAt: Long
)

/**
 * Health record
 */
data class HealthRecord(
    val id: String,
    val plantId: String,
    val recordDate: Long,
    val healthScore: Int,
    val symptoms: List<Symptom>,
    val diagnosis: String?,
    val possibleCauses: List<String>,
    val treatment: String?,
    val treatmentSteps: List<String>,
    val imageUrls: List<String>,
    val resolved: Boolean,
    val resolvedDate: Long?,
    val notes: String?,
    val createdAt: Long
)

/**
 * Intervention record
 */
data class Intervention(
    val id: String,
    val plantId: String,
    val interventionDate: Long,
    val interventionType: InterventionType,
    val details: String?,
    val products: List<ProductUsed>,
    val nextScheduledDate: Long?,
    val completed: Boolean,
    val notes: String?,
    val imageUrls: List<String>,
    val createdAt: Long
)

/**
 * Product used in intervention
 */
data class ProductUsed(
    val productName: String,
    val amount: String,
    val unit: String
)

/**
 * Harvest record
 */
data class HarvestRecord(
    val id: String,
    val plantId: String,
    val harvestDate: Long,
    val amount: Float,
    val unit: HarvestUnit,
    val quality: HarvestQuality,
    val usage: HarvestUsage,
    val marketValue: Float?,
    val notes: String?,
    val imageUrls: List<String>,
    val createdAt: Long
)

/**
 * Propagation record
 */
data class PropagationRecord(
    val id: String,
    val parentPlantId: String,
    val method: PropagationMethod,
    val startDate: Long,
    val status: PropagationStatus,
    val successDate: Long?,
    val failureDate: Long?,
    val resultingPlantId: String?,
    val notes: String?,
    val imageUrls: List<String>,
    val createdAt: Long
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
    PROCESSED,
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
