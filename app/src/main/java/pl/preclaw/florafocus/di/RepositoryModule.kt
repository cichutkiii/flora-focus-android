package pl.preclaw.florafocus.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.preclaw.florafocus.data.repository.*
import pl.preclaw.florafocus.domain.repository.*
import javax.inject.Singleton

/**
 * Hilt module for providing repository implementations
 *
 * Binds repository interfaces to their implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlantRepository(
        plantRepositoryImpl: PlantRepositoryImpl
    ): PlantRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserPlantRepository(
        userPlantRepositoryImpl: UserPlantRepositoryImpl
    ): UserPlantRepository

    @Binds
    @Singleton
    abstract fun bindGardenRepository(
        gardenRepositoryImpl: GardenRepositoryImpl
    ): GardenRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    // Future Phase II repositories:

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