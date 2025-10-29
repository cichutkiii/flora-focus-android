package pl.preclaw.florafocus.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.preclaw.florafocus.data.local.dao.*
import pl.preclaw.florafocus.data.local.database.FloraFocusDatabase
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 *
 * @InstallIn(SingletonComponent::class) - makes these dependencies available application-wide
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides Room database instance
     *
     * @Singleton ensures only one instance exists throughout the app lifecycle
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
            // TODO: Remove fallbackToDestructiveMigration in production
            // Implement proper migrations instead
            .fallbackToDestructiveMigration()
            
            // TODO: Add prepopulated database callback for seed data
            // .addCallback(seedDatabaseCallback)
            
            .build()
    }

    // ==================== DAOs ====================

    /**
     * Provides PlantCatalogDao for accessing plant catalog data
     */
    @Provides
    @Singleton
    fun providePlantCatalogDao(database: FloraFocusDatabase): PlantCatalogDao {
        return database.plantCatalogDao()
    }

    /**
     * Provides UserPlantDao for accessing user plant data
     * Includes: plants, growth history, health records, interventions, harvests, propagation
     */
    @Provides
    @Singleton
    fun provideUserPlantDao(database: FloraFocusDatabase): UserPlantDao {
        return database.userPlantDao()
    }

    /**
     * Provides TaskDao for accessing task data
     * Includes: tasks, recurring templates
     */
    @Provides
    @Singleton
    fun provideTaskDao(database: FloraFocusDatabase): TaskDao {
        return database.taskDao()
    }

    /**
     * Provides GardenDao for accessing garden mapping data
     * Includes: gardens, areas, beds, cells, decorations, rotation plans
     */
    @Provides
    @Singleton
    fun provideGardenDao(database: FloraFocusDatabase): GardenDao {
        return database.gardenDao()
    }

    // ==================== FUTURE DAOs ====================
    // Add more DAO providers here as you implement additional features:
    
    /*
    @Provides
    @Singleton
    fun provideInventoryDao(database: FloraFocusDatabase): InventoryDao {
        return database.inventoryDao()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(database: FloraFocusDatabase): WeatherDao {
        return database.weatherDao()
    }
    */
}
