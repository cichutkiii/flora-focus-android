package pl.preclaw.florafocus.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.preclaw.florafocus.data.local.dao.*
import pl.preclaw.florafocus.data.local.entities.*

/**
 * Main Room database for Flora Focus
 * 
 * Version 1 - MVP Schema:
 * - PlantCatalog (reference plants)
 * - UserPlants + related tables (growth history, health, interventions, harvests, propagation)
 * - Tasks + recurring templates
 * - Garden mapping (gardens, areas, beds, cells, decorations)
 * - Rotation planning
 */
@Database(
    entities = [
        // Plant Catalog
        PlantCatalogEntity::class,
        
        // User Plants & Related
        UserPlantEntity::class,
        PlantGrowthHistoryEntity::class,
        HealthRecordEntity::class,
        InterventionEntity::class,
        HarvestRecordEntity::class,
        PropagationRecordEntity::class,
        
        // Tasks
        TaskEntity::class,
        RecurringTaskTemplateEntity::class,
        
        // Garden Mapping
        GardenEntity::class,
        GardenAreaEntity::class,
        BedEntity::class,
        BedCellEntity::class,
        AreaDecorationEntity::class,
        
        // Rotation Planning
        RotationPlanEntity::class
    ],
    version = 1,
    exportSchema = true // Set to true for production - generates schema files for migrations
)
@TypeConverters(Converters::class)
abstract class FloraFocusDatabase : RoomDatabase() {

    // ==================== DAOs ====================

    /**
     * DAO for plant catalog operations
     */
    abstract fun plantCatalogDao(): PlantCatalogDao

    /**
     * DAO for user plant operations
     */
    abstract fun userPlantDao(): UserPlantDao

    /**
     * DAO for task operations
     */
    abstract fun taskDao(): TaskDao

    /**
     * DAO for garden mapping operations
     */
    abstract fun gardenDao(): GardenDao

    companion object {
        const val DATABASE_NAME = "flora_focus_database"
        
        /**
         * Database version history:
         * 
         * Version 1 (MVP):
         * - Initial schema with all core entities
         * - PlantCatalog with growth phases
         * - UserPlant with health tracking
         * - Task management with auto-generation support
         * - Garden 2D mapping with grid system
         * - Rotation planning
         */
    }
}
