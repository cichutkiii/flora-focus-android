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

    /**
     * Plant Catalog Repository
     * Handles read-only plant database (reference plants)
     */
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

// ==================== UWAGI DOTYCZĄCE AKTYWACJI ====================

/*
 * AKTYWOWANE REPOSITORIES (PHASE I):
 * ✅ PlantCatalogRepository - katalog roślin
 * ✅ UserPlantRepository - rośliny użytkownika
 * ✅ GardenRepository - zarządzanie ogrodem
 * ✅ TaskRepository - zadania i przypomnienia
 * ✅ AuthRepository - autentykacja użytkowników
 *
 * WYMAGANE IMPLEMENTACJE:
 * - PlantCatalogRepositoryImpl ✅ (istnieje)
 * - UserPlantRepositoryImpl ✅ (zrobione - naprawione błędy)
 * - GardenRepositoryImpl ❌ (do zrobienia)
 * - TaskRepositoryImpl ❌ (do zrobienia)
 * - AuthRepositoryImpl ❌ (do zrobienia)
 *
 * NASTĘPNE KROKI:
 * 1. Sprawdź czy wszystkie implementacje istnieją
 * 2. Jeśli nie - tymczasowo zakomentuj brakujące @Binds
 * 3. Aktywuj moduł stopniowo
 * 4. Dodaj DatabaseModule.kt dla Room
 */