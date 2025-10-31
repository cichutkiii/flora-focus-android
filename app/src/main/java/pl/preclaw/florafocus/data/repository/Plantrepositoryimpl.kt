package pl.preclaw.florafocus.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import pl.preclaw.florafocus.data.local.dao.PlantCatalogDao
import pl.preclaw.florafocus.data.mapper.PlantMapper
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.PlantRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PlantRepository
 *
 * Handles plant catalog data from both local (Room) and remote (Firebase) sources
 * Local database is the source of truth, Firebase is used for syncing catalog updates
 */
@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val plantCatalogDao: PlantCatalogDao,
    private val firestore: FirebaseFirestore,
    private val plantMapper: PlantMapper
) : PlantRepository {

    companion object {
        private const val COLLECTION_PLANTS = "plant_catalog"
        private const val TAG = "PlantRepository"
    }

    // ==================== QUERIES ====================

    override fun getAllPlants(): Flow<List<Plant>> {
        return plantCatalogDao.getAllPlants()
            .map { entities ->
                entities.map { plantMapper.toDomain(it) }
            }
    }

    override suspend fun getPlantById(plantId: String): Plant? {
        return plantCatalogDao.getPlantById(plantId)?.let {
            plantMapper.toDomain(it)
        }
    }

    override fun getPlantByIdFlow(plantId: String): Flow<Plant?> {
        return plantCatalogDao.getPlantByIdFlow(plantId)
            .map { entity -> entity?.let { plantMapper.toDomain(it) } }
    }

    override fun searchPlants(query: String): Flow<List<Plant>> {
        return plantCatalogDao.searchPlants(query)
            .map { entities ->
                entities.map { plantMapper.toDomain(it) }
            }
    }

    override fun getPlantsByType(type: PlantType): Flow<List<Plant>> {
        return plantCatalogDao.getPlantsByType(type)
            .map { entities ->
                entities.map { plantMapper.toDomain(it) }
            }
    }

    override fun getPlantsByDifficulty(difficulty: GrowthDifficulty): Flow<List<Plant>> {
        return plantCatalogDao.getPlantsByDifficulty(difficulty)
            .map { entities ->
                entities.map { plantMapper.toDomain(it) }
            }
    }

    override fun getPlantsByLightRequirements(light: LightRequirements): Flow<List<Plant>> {
        return plantCatalogDao.getPlantsByLightRequirements(light)
            .map { entities ->
                entities.map { plantMapper.toDomain(it) }
            }
    }

    override fun getEdiblePlants(): Flow<List<Plant>> {
        return plantCatalogDao.getEdiblePlants()
            .map { entities ->
                entities.map { plantMapper.toDomain(it) }
            }
    }

    override fun getPlantsByFamily(family: String): Flow<List<Plant>> {
        return plantCatalogDao.getPlantsByFamily(family)
            .map { entities ->
                entities.map { plantMapper.toDomain(it) }
            }
    }

    override suspend fun getCompanionPlants(plantId: String): List<Plant> {
        val plant = plantCatalogDao.getPlantById(plantId) ?: return emptyList()

        return plant.companionPlantIds.mapNotNull { companionId ->
            plantCatalogDao.getPlantById(companionId)
        }.map { plantMapper.toDomain(it) }
    }

    override suspend fun getIncompatiblePlants(plantId: String): List<Plant> {
        val plant = plantCatalogDao.getPlantById(plantId) ?: return emptyList()

        return plant.incompatiblePlantIds.mapNotNull { incompatibleId ->
            plantCatalogDao.getPlantById(incompatibleId)
        }.map { plantMapper.toDomain(it) }
    }

    // ==================== ADVANCED FILTERS ====================

    override fun filterPlants(
        types: List<PlantType>?,
        difficulties: List<GrowthDifficulty>?,
        lightRequirements: List<LightRequirements>?,
        edibleOnly: Boolean,
        searchQuery: String?
    ): Flow<List<Plant>> {
        // If no filters applied, return all or search
        if (types == null && difficulties == null && lightRequirements == null && !edibleOnly && searchQuery == null) {
            return getAllPlants()
        }

        // For complex filtering, we'll get all plants and filter in memory
        // For production, consider creating a custom DAO query with FTS
        return getAllPlants().map { plants ->
            plants.filter { plant ->
                val matchesType = types?.contains(plant.plantType) ?: true
                val matchesDifficulty = difficulties?.contains(plant.growthDifficulty) ?: true
                val matchesLight = lightRequirements?.contains(plant.lightRequirements) ?: true
                val matchesEdible = !edibleOnly || plant.edible
                val matchesSearch = searchQuery?.let { query ->
                    plant.commonName.contains(query, ignoreCase = true) ||
                            plant.latinName.contains(query, ignoreCase = true) ||
                            plant.tags.any { it.contains(query, ignoreCase = true) }
                } ?: true

                matchesType && matchesDifficulty && matchesLight && matchesEdible && matchesSearch
            }
        }
    }

    // ==================== FIREBASE SYNC ====================

    override suspend fun syncCatalogFromFirebase(): Result<Unit> {
        return try {
            Timber.tag(TAG).d("Starting plant catalog sync from Firebase...")

            val snapshot = firestore.collection(COLLECTION_PLANTS)
                .get()
                .await()

            val plants = snapshot.documents.mapNotNull { doc ->
                try {
                    // Map Firestore document to PlantCatalogEntity
                    // Note: Adjust field names based on your Firestore schema
                    plantMapper.toEntity(doc.data as Map<String, Any>)
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error parsing plant document: ${doc.id}")
                    null
                }
            }

            if (plants.isNotEmpty()) {
                plantCatalogDao.insertPlants(plants)
                Timber.tag(TAG).d("Synced ${plants.size} plants from Firebase")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error syncing catalog from Firebase")
            Result.failure(e)
        }
    }

    override suspend fun uploadPlantToFirebase(plant: Plant): Result<Unit> {
        return try {
            val entity = plantMapper.toEntity(plant)
            val data = plantMapper.toFirestoreMap(entity)

            firestore.collection(COLLECTION_PLANTS)
                .document(plant.id)
                .set(data)
                .await()

            Timber.tag(TAG).d("Uploaded plant ${plant.commonName} to Firebase")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error uploading plant to Firebase")
            Result.failure(e)
        }
    }

    // ==================== LOCAL OPERATIONS (Admin/Debug) ====================

    override suspend fun insertPlant(plant: Plant): Result<String> {
        return try {
            val entity = plantMapper.toEntity(plant)
            plantCatalogDao.insertPlant(entity)
            Result.success(plant.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error inserting plant")
            Result.failure(e)
        }
    }

    override suspend fun updatePlant(plant: Plant): Result<Unit> {
        return try {
            val entity = plantMapper.toEntity(plant)
            plantCatalogDao.updatePlant(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating plant")
            Result.failure(e)
        }
    }

    override suspend fun deletePlant(plantId: String): Result<Unit> {
        return try {
            plantCatalogDao.deletePlantById(plantId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting plant")
            Result.failure(e)
        }
    }

    // ==================== STATISTICS ====================

    override suspend fun getPlantCount(): Int {
        return plantCatalogDao.getPlantCount()
    }

    override suspend fun getPlantsByFamilyCount(): Map<String, Int> {
        // Get all plants and group by family
        val plants = plantCatalogDao.getAllPlants()
        return try {
            kotlinx.coroutines.flow.first(plants)
                .groupBy { it.family }
                .mapValues { it.value.size }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting family counts")
            emptyMap()
        }
    }
}