package pl.preclaw.florafocus.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.preclaw.florafocus.data.repository.*
import pl.preclaw.florafocus.domain.repository.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

@Binds
@Singleton
abstract fun bindPlantCatalogRepository(
plantCatalogRepositoryImpl: PlantCatalogRepositoryImpl
): PlantCatalogRepository

/**
 * User Plant Repository
 * Handles user's actual plant instances with tracking
*/
@Binds
@Singleton
abstract fun bindUserPlantRepository(
userPlantRepositoryImpl: UserPlantRepositoryImpl
): UserPlantRepository

/**
 * Garden Repository
 * Handles garden mapping (Garden → Areas → Beds → Cells)
*/
@Binds
@Singleton
abstract fun bindGardenRepository(
gardenRepositoryImpl: GardenRepositoryImpl
): GardenRepository

/**
 * Task Repository
 * Handles task management and auto-generation
*/
@Binds
@Singleton
abstract fun bindTaskRepository(
taskRepositoryImpl: TaskRepositoryImpl
): TaskRepository

/**
 * Auth Repository
 * Handles authentication and user management
*/
@Binds
@Singleton
abstract fun bindAuthRepository(
authRepositoryImpl: AuthRepositoryImpl
): AuthRepository

// ==================== PHASE II REPOSITORIES ====================
// Uncomment when implementing Phase II features

/*
@Binds
@Singleton
abstract fun bindWeatherRepository(
weatherRepositoryImpl: WeatherRepositoryImpl
): WeatherRepository

@Binds
@Singleton
abstract fun bindInventoryRepository(
inventoryRepositoryImpl: InventoryRepositoryImpl
): InventoryRepository

@Binds
@Singleton
abstract fun bindRotationRepository(
rotationRepositoryImpl: RotationRepositoryImpl
): RotationRepository
*/
}