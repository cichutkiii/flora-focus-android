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
    val expectedHarvestDate: Long?, // Calculated based on planting date + days to harvest
    
    // Location in garden (NEW structure from spec)
    val areaObjectBedId: String?, // ID of AreaObject.Bed where planted
    val cellRow: Int?, // Row in bed grid
    val cellColumn: Int?, // Column in bed grid
    
    // Growth tracking
    val currentPhaseId: String?, // Current GrowthPhaseData.id
    val currentPhaseName: GrowthPhaseName?, // Cached for quick access
    val phaseStartDate: Long?, // When current phase started
    val phaseHistory: List<String> = emptyList(), // List of completed phase IDs
    
    // Care tracking
    val lastWateredDate: Long?,
    val lastFertilizedDate: Long?,
    val lastPrunedDate: Long?,
    val lastInspectionDate: Long?,
    
    // Health
    val healthStatus: HealthStatus = HealthStatus.HEALTHY,
    val lastHealthCheckDate: Long?,
    val healthScore: Int = 10, // 1-10 scale
    val currentSymptoms: List<Symptom> = emptyList(),
    
    // Environmental conditions
    val actualSunExposure: SunExposure?,
    val actualSoilType: String?,
    val microclimate: String?, // User notes about specific location conditions
    val harvestedDate: Long?,

    // Growth measurements
    val heightCm: Float?, // Current height in centimeters
    val spreadCm: Float?, // Current spread/width in centimeters
    val trunkDiameterCm: Float?, // For trees/shrubs

    // Yield tracking
    val totalHarvestedAmount: Float = 0f,
    val totalHarvestSessions: Int = 0,
    val bestHarvestDate: Long?,
    val bestHarvestAmount: Float?,
    
    // Care preferences (overrides from catalog)
    val preferredWateringFrequency: WateringFrequency?,
    val customCareNotes: String?,
    val fertilizerSchedule: String?,
    
    // Media & notes
    val imageUrls: List<String> = emptyList(),
    val thumbnailUrl: String?, // Latest/best photo
    val notes: String?,
    val privateNotes: String?, // Notes not synced to cloud
    
    // Propagation tracking
    val isFromPropagation: Boolean = false,
    val parentPlantId: String?, // If this plant came from propagating another plant
    val propagationMethod: PropagationMethod?,
    val propagationDate: Long?,
    
    // Status flags
    val isActive: Boolean = true, // False if plant died or was removed
    val isArchived: Boolean = false,
    val deactivatedDate: Long?,
    val deactivationReason: String?, // "died", "harvested", "gave away", etc.
    
    // Reminder settings
    val wateringReminderEnabled: Boolean = true,
    val fertilizingReminderEnabled: Boolean = true,
    val customReminderInterval: Int?, // Days between custom reminders
    
    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long? = null // For Firebase sync tracking
)
