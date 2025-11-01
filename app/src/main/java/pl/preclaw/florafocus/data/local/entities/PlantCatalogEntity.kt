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
    val harvestPeriodStart: String?, // Format: "MM-DD" e.g., "07-01"
    val harvestPeriodEnd: String?, // Format: "MM-DD" e.g., "09-30"
    
    // Growth Information
    val daysToHarvestMin: Int?, // Minimum days from planting to harvest
    val daysToHarvestMax: Int?, // Maximum days from planting to harvest
    val averageYield: String?, // "5-8 kg z rośliny", "20-30 sztuk"
    
    // Weather Dependencies
    val minTemperature: Float?, // Minimum temperature (°C)
    val maxTemperature: Float?, // Maximum temperature (°C)
    val frostTolerant: Boolean = false,
    
    // Growth Phases - detailed phase information
    val growthPhases: List<GrowthPhaseData> = emptyList(),
    
    // Disease & Pest Information
    val commonDiseases: List<String> = emptyList(), // Common disease names
    val commonPests: List<String> = emptyList(), // Common pest names
    val diseaseResistance: List<String> = emptyList(), // Diseases this plant resists
    
    // Additional Care Information
    val wateringTips: String?,
    val fertilizingTips: String?,
    val pruningNotes: String?,
    val specialCareInstructions: String?,
    
    // Media
    val imageUrls: List<String> = emptyList(),
    val thumbnailUrl: String?,
    
    // Tags for filtering/searching
    val tags: List<String> = emptyList(), // "winter-hardy", "drought-resistant", "beginner-friendly"
    
    // Metadata
    val source: String?, // Where this data came from
    val verified: Boolean = false, // Is this data verified by experts
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
