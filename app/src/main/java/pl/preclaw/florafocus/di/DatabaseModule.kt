package pl.preclaw.florafocus.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.preclaw.florafocus.data.local.dao.*
import pl.preclaw.florafocus.data.local.database.FloraFocusDatabase
import pl.preclaw.florafocus.data.local.database.InitialPlantData
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Provides Room database instance with async seed callback
     *
     * IMPORTANT: Uses coroutine launch (not runBlocking) to avoid ANR
     * Seeding happens in background, app startup is not blocked
     */
    @Provides
    @Singleton
    fun provideFloraFocusDatabase(
        @ApplicationContext context: Context
    ): FloraFocusDatabase {
        return Room.databaseBuilder(
            context,
            FloraFocusDatabase::class.java,
            FloraFocusDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()

            // Async callback - does NOT block main thread
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    Timber.d("🌱 Database onCreate callback triggered")

                    // Launch coroutine - ASYNC, doesn't block
                    applicationScope.launch(Dispatchers.IO) {
                        try {
                            Timber.d("🌱 Starting seed process (async)...")

                            // Create temporary database instance for seeding
                            val tempDb = Room.databaseBuilder(
                                context,
                                FloraFocusDatabase::class.java,
                                FloraFocusDatabase.DATABASE_NAME
                            ).build()

                            val dao = tempDb.plantCatalogDao()

                            // Check if already seeded
                            val existingCount = dao.getPlantCount()
                            if (existingCount > 0) {
                                Timber.d("⏭️  Database already contains $existingCount plants - skipping seed")
                                markSeedingComplete(context)
                                return@launch
                            }

                            Timber.d("📦 Database is empty - inserting seed data")

                            // Get and insert plants
                            val plants = InitialPlantData.getInitialPlants()
                            Timber.d("📦 Inserting ${plants.size} plants into catalog...")

                            var successCount = 0
                            plants.forEachIndexed { index, plant ->
                                try {
                                    dao.insertPlant(plant)
                                    successCount++
                                    Timber.v("✓ Inserted plant ${index + 1}/${plants.size}: ${plant.commonName}")
                                } catch (e: Exception) {
                                    Timber.e(e, "✗ Failed to insert plant: ${plant.commonName}")
                                }
                            }

                            val finalCount = dao.getPlantCount()
                            Timber.i("✅ Seed completed: $successCount/$finalCount plants in catalog")

                            // Mark seeding as complete
                            markSeedingComplete(context)

                            // Log summary
                            logSeedSummary(plants)

                            // Close temp database
                            tempDb.close()

                        } catch (e: Exception) {
                            Timber.e(e, "❌ Error seeding database")
                        }

                        Timber.d("🌱 Database seeding finished")
                    }
                }

                /**
                 * Mark seeding as complete in SharedPreferences
                 */
                private fun markSeedingComplete(context: Context) {
                    val prefs = context.getSharedPreferences("flora_focus_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("database_seeded", true).apply()
                    Timber.d("✅ Seeding marked as complete")
                }

                /**
                 * Log summary of seeded data
                 */
                private fun logSeedSummary(plants: List<pl.preclaw.florafocus.data.local.entities.PlantCatalogEntity>) {
                    Timber.d("═══════════════════════════════════")
                    Timber.d("📊 SEED DATA SUMMARY")
                    Timber.d("═══════════════════════════════════")
                    Timber.d("Total plants: ${plants.size}")

                    val byType = plants.groupBy { it.plantType }
                    byType.forEach { (type, plantsOfType) ->
                        Timber.d("  $type: ${plantsOfType.size}")
                        plantsOfType.forEach { plant ->
                            Timber.d("    • ${plant.commonName} (${plant.latinName})")
                        }
                    }

                    val companionCount = plants.sumOf { it.companionPlantIds.size }
                    val phaseCount = plants.sumOf { it.growthPhases.size }
                    val taskCount = plants.sumOf { plant ->
                        plant.growthPhases.sumOf { phase -> phase.autoTasks.size }
                    }

                    Timber.d("Companion relationships: $companionCount")
                    Timber.d("Growth phases: $phaseCount")
                    Timber.d("Auto-tasks: $taskCount")
                    Timber.d("═══════════════════════════════════")
                }
            })

            .build()
    }

    // ==================== DAOs ====================

    @Provides
    @Singleton
    fun providePlantCatalogDao(database: FloraFocusDatabase): PlantCatalogDao {
        return database.plantCatalogDao()
    }

    @Provides
    @Singleton
    fun provideUserPlantDao(database: FloraFocusDatabase): UserPlantDao {
        return database.userPlantDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: FloraFocusDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideGardenDao(database: FloraFocusDatabase): GardenDao {
        return database.gardenDao()
    }
}