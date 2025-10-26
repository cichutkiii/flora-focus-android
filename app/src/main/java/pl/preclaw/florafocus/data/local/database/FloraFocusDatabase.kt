package pl.preclaw.florafocus.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.preclaw.florafocus.data.local.entities.DummyEntity

/**
 * Main Room database for Flora Focus
 *
 * This is the skeleton - add entities and DAOs as you implement them
 *
 * Example of what will be added:
 * @Database(
 *     entities = [
 *         UserPlantEntity::class,
 *         TaskEntity::class,
 *         GardenEntity::class,
 *         GardenAreaEntity::class,
 *         BedEntity::class,
 *         PlantNoteEntity::class,
 *         // ... other entities
 *     ],
 *     version = 1,
 *     exportSchema = true
 * )
 */
@Database(
    entities = [
        DummyEntity::class
        // Add your entities here as you create them
    ],
    version = 1,
    exportSchema = false // Set to true in production
)
@TypeConverters(Converters::class)
abstract class FloraFocusDatabase : RoomDatabase() {

    // Add DAO abstract methods here as you create them
    // Example:
    // abstract fun userPlantDao(): UserPlantDao
    // abstract fun taskDao(): TaskDao
    // abstract fun gardenDao(): GardenDao
    // abstract fun gardenAreaDao(): GardenAreaDao
    // abstract fun bedDao(): BedDao
    // abstract fun inventoryDao(): InventoryDao
    // abstract fun harvestDao(): HarvestDao
    // abstract fun rotationDao(): RotationDao
    // abstract fun weatherDao(): WeatherDao
}