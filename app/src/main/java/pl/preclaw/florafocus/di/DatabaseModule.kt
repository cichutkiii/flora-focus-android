package pl.preclaw.florafocus.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
            "flora_focus_database"
        )
            .fallbackToDestructiveMigration() // Only for development - remove in production
            .build()
    }

    // DAOs will be provided here as the database is built
    // Example:
    /*
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
    */
}