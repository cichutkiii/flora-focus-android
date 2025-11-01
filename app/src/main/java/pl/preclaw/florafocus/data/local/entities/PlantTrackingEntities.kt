package pl.preclaw.florafocus.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pl.preclaw.florafocus.data.local.database.Converters

/**
 * Growth history tracking for user plants
 * 
 * Records when plants transition between growth phases
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
    indices = [
        Index("plantId"),
        Index("phaseStartDate")
    ]
)
@TypeConverters(Converters::class)
data class PlantGrowthHistoryEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val phaseId: String, // Reference to GrowthPhaseData.id
    val phaseName: GrowthPhaseName,
    
    val phaseStartDate: Long,
    val phaseEndDate: Long? = null, // Null if still in this phase
    val actualDurationDays: Int? = null,
    
    // Measurements during this phase
    val startHeightCm: Float? = null,
    val endHeightCm: Float? = null,
    val startSpreadCm: Float? = null,
    val endSpreadCm: Float? = null,
    
    // Phase-specific observations
    val keyMilestones: List<String> = emptyList(), // "first true leaves", "flower buds"
    val observations: String? = null,
    val challenges: String? = null,
    
    // Media
    val photoUrls: List<String> = emptyList(),
    
    // Auto-transition or manual
    val autoTransitioned: Boolean = false,
    val transitionTrigger: String? = null, // "time-based", "milestone-based", "manual"
    
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Health records for tracking plant wellbeing
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
    indices = [
        Index("plantId"),
        Index("checkDate"),
        Index("healthStatus")
    ]
)
@TypeConverters(Converters::class)
data class HealthRecordEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val checkDate: Long,
    
    val healthStatus: HealthStatus,
    val healthScore: Int, // 1-10 scale
    val previousHealthScore: Int? = null,
    
    // Symptoms and observations
    val symptoms: List<Symptom> = emptyList(),
    val newSymptoms: List<Symptom> = emptyList(), // Since last check
    val resolvedSymptoms: List<Symptom> = emptyList(),
    
    // Detailed observations
    val leafCondition: String? = null, // "healthy", "yellowing", "spotted"
    val stemCondition: String? = null,
    val rootCondition: String? = null, // If visible
    val overallVigor: String? = null, // "excellent", "good", "declining"
    
    // Environmental factors
    val recentWeather: String? = null,
    val wateringStatus: String? = null, // "adequate", "overwatered", "underwatered"
    val lightExposure: String? = null,
    
    // Suspected issues
    val suspectedPests: List<String> = emptyList(),
    val suspectedDiseases: List<String> = emptyList(),
    val suspectedDeficiencies: List<String> = emptyList(),
    
    // Media
    val photoUrls: List<String> = emptyList(),
    
    val notes: String? = null,
    val checkedBy: String? = null, // Person who did the check
    
    val createdAt: Long = System.currentTimeMillis(),
    val resolved: Boolean = false,
    val resolvedDate: Long? = null,
    val resolvedBy: String? = null,
    val resolutionNotes: String? = null
)

/**
 * Interventions and treatments applied to plants
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
    indices = [
        Index("plantId"),
        Index("interventionDate"),
        Index("interventionType")
    ]
)
@TypeConverters(Converters::class)
data class InterventionEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val interventionDate: Long,
    val interventionType: InterventionType,
    
    // Intervention details
    val title: String, // "Watering", "Fertilizer application", "Pest treatment"
    val description: String? = null,
    
    // Products used
    val productsUsed: List<String> = emptyList(), // "Miracle-Gro", "Neem oil"
    val quantities: Map<String, String> = emptyMap(), // Product -> quantity
    val applicationMethod: String? = null, // "spray", "drench", "granular"
    
    // Conditions
    val weatherConditions: String? = null,
    val temperature: Float? = null,
    val humidity: Float? = null,
    
    // Timing
    val timeOfDay: String? = null, // "morning", "evening"
    val durationMinutes: Int? = null,
    
    // Results tracking
    val immediateResults: String? = null,
    val followUpDate: Long? = null,
    val followUpNotes: String? = null,
    val effectiveness: Int? = null, // 1-5 scale
    
    // Cost tracking
    val cost: Float? = null,
    val currency: String? = "PLN",
    
    // Safety
    val safetyPrecautions: List<String> = emptyList(),
    val harvestDelay: Int? = null, // Days to wait before harvesting
    
    // Media
    val beforePhotoUrls: List<String> = emptyList(),
    val afterPhotoUrls: List<String> = emptyList(),
    
    val notes: String? = null,
    val performedBy: String? = null,
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Harvest records for tracking yields
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
    indices = [
        Index("plantId"),
        Index("harvestDate"),
        Index("harvestQuality")
    ]
)
@TypeConverters(Converters::class)
data class HarvestRecordEntity(
    @PrimaryKey
    val id: String,
    
    val plantId: String,
    val harvestDate: Long,
    
    // Harvest details
    val amount: Float,
    val unit: HarvestUnit,
    val quality: HarvestQuality,
    val ripeness: String? = null, // "underripe", "perfect", "overripe"
    
    // What was harvested
    val harvestedParts: List<String> = emptyList(), // "fruits", "leaves", "roots"
    val varietyNotes: String? = null, // Notes about this specific variety
    
    // Usage
    val primaryUsage: HarvestUsage,
    val secondaryUsages: List<HarvestUsage> = emptyList(),
    
    // Market/sharing info
    val sharedAmount: Float? = null,
    val sharedWith: String? = null,
    val soldAmount: Float? = null,
    val salePrice: Float? = null,
    
    // Comparison to expectations
    val expectedAmount: Float? = null,
    val comparedToExpected: String? = null, // "better", "as expected", "disappointing"
    
    // Conditions
    val weatherLastWeek: String? = null,
    val plantAge: Int? = null, // Days since planting
    val daysFromFlowering: Int? = null,
    
    // Quality assessments
    val flavorRating: Int? = null, // 1-5 scale
    val textureRating: Int? = null,
    val appearanceRating: Int? = null,
    val overallSatisfaction: Int? = null,
    
    // Seeds saved
    val seedsSaved: Boolean = false,
    val seedsSavedAmount: Int? = null,
    val seedQuality: String? = null,
    
    // Storage
    val storageMethod: String? = null, // "refrigerated", "frozen", "dried"
    val expectedStorageLife: String? = null,
    
    // Media
    val photoUrls: List<String> = emptyList(),
    
    val notes: String? = null,
    val harvestedBy: String? = null,
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Propagation records for tracking plant reproduction
 */
@Entity(
    tableName = "propagation_records",
    foreignKeys = [
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentPlantId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["resultingPlantId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("parentPlantId"),
        Index("resultingPlantId"),
        Index("propagationDate"),
        Index("status")
    ]
)
@TypeConverters(Converters::class)
data class PropagationRecordEntity(
    @PrimaryKey
    val id: String,
    
    val parentPlantId: String, // Plant being propagated from
    val resultingPlantId: String? = null, // New plant created (set when successful)
    val propagationDate: Long,
    val method: PropagationMethod,
    val status: PropagationStatus,
    
    // Method-specific details
    val materialTaken: String? = null, // "6-inch cutting", "2 bulb offsets"
    val preparationMethod: String? = null, // "rooting hormone applied"
    val rootingMedium: String? = null, // "potting soil", "water", "sand"
    
    // Timing and milestones
    val rootingStartDate: Long? = null,
    val rootingSuccessDate: Long? = null,
    val transplantDate: Long? = null,
    val establishmentDate: Long? = null,
    
    // Success tracking
    val successRate: Float? = null, // For multiple attempts (e.g., 3 of 5 cuttings rooted)
    val totalAttempts: Int = 1,
    val successfulCount: Int? = null,
    
    // Conditions
    val environmentalConditions: String? = null, // "humidity dome", "warm windowsill"
    val careGiven: String? = null,
    val challengesFaced: String? = null,
    
    // Final results
    val finalOutcome: String? = null, // "strong new plant", "failed to root", "transplanted successfully"
    val vigorRating: Int? = null, // 1-5 scale for resulting plant vigor
    
    // Learning notes
    val whatWorkedWell: String? = null,
    val whatToImproveNext: String? = null,
    val recommendedTiming: String? = null,
    
    // Media
    val photoUrls: List<String> = emptyList(), // Progress photos
    
    val notes: String? = null,
    val performedBy: String? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userId: String, // Duplikacja dla łatwiejszych zapytań
    val startDate: Long = propagationDate, // Alias lub osobne pole
)
