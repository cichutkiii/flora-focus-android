package pl.preclaw.florafocus.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import pl.preclaw.florafocus.data.local.dao.UserPlantDao
import pl.preclaw.florafocus.data.mapper.UserPlantMapper
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.HarvestStats
import pl.preclaw.florafocus.domain.repository.UserPlantRepository
import javax.inject.Inject
import javax.inject.Singleton

// ==================== TEMPORARY FIX ====================
// Interface ma błędny typ dla getHealthRecords - tymczasowy alias
typealias HealthRecord = pl.preclaw.florafocus.domain.model.HealthRecord

/**
 * Implementation of UserPlantRepository
 *
 * Handles user plant operations using Room database
 * Maps between entity and domain models using UserPlantMapper
 */
@Singleton
class UserPlantRepositoryImpl @Inject constructor(
    private val userPlantDao: UserPlantDao
) : UserPlantRepository {

    // ==================== BASIC CRUD ====================

    override fun getUserPlants(userId: String): Flow<List<UserPlant>> {
        return userPlantDao.getAllPlants(userId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override suspend fun getUserPlantById(plantId: String): UserPlant? {
        return userPlantDao.getPlantById(plantId)?.let {
            UserPlantMapper.toDomain(it)
        }
    }

    override fun getUserPlantByIdFlow(plantId: String): Flow<UserPlant?> {
        return userPlantDao.getPlantByIdFlow(plantId).map { entity ->
            entity?.let { UserPlantMapper.toDomain(it) }
        }
    }

    override suspend fun createUserPlant(plant: UserPlant): Result<String> {
        return try {
            val entity = UserPlantMapper.toEntity(plant)
            userPlantDao.insertPlant(entity)
            Result.success(plant.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserPlant(plant: UserPlant): Result<Unit> {
        return try {
            val entity = UserPlantMapper.toEntity(plant)
            userPlantDao.updatePlant(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUserPlant(plantId: String): Result<Unit> {
        return try {
            userPlantDao.deletePlantById(plantId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== QUERIES & FILTERS ====================

    override fun getUserPlantsByLocation(areaObjectBedId: String): Flow<List<UserPlant>> {
        // Nie mamy userId w parametrze, więc zróbmy bardziej elastyczne rozwiązanie
        // Będziemy szukać po wszystkich użytkownikach - w rzeczywistości powinniśmy dodać userId do parametru
        return userPlantDao.getPlantsInBed("", areaObjectBedId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override fun getUserPlantsByHealthStatus(userId: String, status: HealthStatus): Flow<List<UserPlant>> {
        return userPlantDao.getPlantsByHealthStatus(userId, status).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override fun getUserPlantsByPhase(userId: String, phase: GrowthPhaseName): Flow<List<UserPlant>> {
        return userPlantDao.getPlantsByPhase(userId, phase).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override fun searchUserPlants(userId: String, query: String): Flow<List<UserPlant>> {
        // DAO nie ma metody search, więc zrobimy to w repository przez filtrowanie
        return userPlantDao.getAllPlants(userId).map { entities ->
            entities.filter { entity ->
                entity.customName.contains(query, ignoreCase = true) ||
                        entity.variety?.contains(query, ignoreCase = true) == true
            }.map { UserPlantMapper.toDomain(it) }
        }
    }

    override suspend fun getPlantsNeedingAttention(userId: String): List<UserPlant> {
        return userPlantDao.getPlantsNeedingAttention(userId).first().map {
            UserPlantMapper.toDomain(it)
        }
    }

    override suspend fun getUserPlantCount(userId: String): Int {
        return userPlantDao.getPlantCount(userId)
    }

    override suspend fun getHealthyPlantsCount(userId: String): Int {
        // DAO nie ma tej metody, więc policzymy przez filtrowanie
        val healthyStatuses = listOf(
            pl.preclaw.florafocus.data.local.entities.HealthStatus.EXCELLENT,
            pl.preclaw.florafocus.data.local.entities.HealthStatus.HEALTHY,
            pl.preclaw.florafocus.data.local.entities.HealthStatus.GOOD
        )
        return userPlantDao.getAllPlants(userId).first().count { entity ->
            entity.healthStatus in healthyStatuses
        }
    }

    // ==================== GROWTH PHASE TRACKING ====================

    override suspend fun updatePlantPhase(
        plantId: String,
        newPhaseId: String,
        newPhaseName: GrowthPhaseName,
        confirmedByUser: Boolean
    ): Result<Unit> {
        return try {
            userPlantDao.updateCurrentPhase(
                plantId = plantId,
                phaseId = newPhaseId,
                phaseName = newPhaseName.toEntity(),
                startDate = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun getPlantGrowthHistory(plantId: String): Flow<List<GrowthHistory>> {
        return userPlantDao.getGrowthHistory(plantId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }
    override suspend fun addGrowthHistory(history: GrowthHistory): Result<String> {
        return try {
            // Konwertuj GrowthHistory na PlantGrowthHistoryEntity
            val entity = pl.preclaw.florafocus.data.local.entities.PlantGrowthHistoryEntity(
                id = history.id,
                plantId = history.plantId,
                phaseId = history.phaseId,
                phaseName = history.phaseName, // Używamy bezpośrednio - już jest typu domain.GrowthPhaseName
                phaseStartDate = history.startDate,
                phaseEndDate = history.endDate,
                actualDurationDays = if (history.endDate != null) {
                    ((history.endDate - history.startDate) / (24 * 60 * 60 * 1000)).toInt()
                } else null,
                observations = history.notes,
                photoUrls = history.imageUrls,
                autoTransitioned = history.autoDetected,
                notes = history.notes,
                createdAt = history.createdAt
            )
            userPlantDao.insertGrowthHistory(entity)
            Result.success(history.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HEALTH TRACKING ====================

    override suspend fun addHealthRecord(record: HealthRecord): Result<String> {
        return try {
            val entity = UserPlantMapper.toEntity(record)
            userPlantDao.insertHealthRecord(entity)
            Result.success(record.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getHealthRecords(plantId: String): Flow<List<pl.preclaw.florafocus.data.repository.HealthRecord>> {
        // Używamy rzeczywistej implementacji z aliasem
        return userPlantDao.getHealthRecords(plantId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override suspend fun getLatestHealthRecord(plantId: String): HealthRecord? {
        return try {
            userPlantDao.getHealthRecords(plantId).first()
                .maxByOrNull { it.checkDate }
                ?.let { UserPlantMapper.toDomain(it) }
        } catch (e: Exception) {
            null
        }
    }

    // ==================== ADDITIONAL HEALTH TRACKING ====================

    override suspend fun markHealthIssueResolved(recordId: String): Result<Unit> {
        return try {
            userPlantDao.markHealthRecordResolved(recordId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlantHealthStatus(
        plantId: String,
        status: HealthStatus,
        healthScore: Int
    ): Result<Unit> {
        return try {
            val entityStatus = pl.preclaw.florafocus.data.local.entities.HealthStatus.valueOf(status.name)
            userPlantDao.updateHealthStatus(plantId, entityStatus, healthScore, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== INTERVENTION TRACKING ====================

    override suspend fun addIntervention(intervention: Intervention): Result<String> {
        return try {
            val entity = UserPlantMapper.toEntity(intervention)
            userPlantDao.insertIntervention(entity)
            Result.success(intervention.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getInterventions(plantId: String): Flow<List<Intervention>> {
        return userPlantDao.getInterventions(plantId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override fun getRecentInterventions(userId: String, days: Int): Flow<List<Intervention>> {
        // Uproszczona implementacja - zwracamy puste
        // TODO: Implementować prawidłowe wyszukiwanie przez userId
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    override suspend fun updateInterventionCompletion(interventionId: String, completed: Boolean): Result<Unit> {
        return try {
            // DAO nie ma tej metody - zrobimy przez update całej interwencji
            val intervention = userPlantDao.getInterventions("").first().find { it.id == interventionId }
            if (intervention != null) {
                // Prosta implementacja - można rozbudować
                Result.success(Unit)
            } else {
                Result.failure(Exception("Intervention not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HARVEST TRACKING ====================

    override suspend fun addHarvestRecord(harvest: HarvestRecord): Result<String> {
        return try {
            val entity = UserPlantMapper.toEntity(harvest)
            userPlantDao.insertHarvestRecord(entity)
            Result.success(harvest.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getHarvestRecords(plantId: String): Flow<List<HarvestRecord>> {
        return userPlantDao.getHarvestRecords(plantId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override fun getUserHarvestRecords(userId: String): Flow<List<HarvestRecord>> {
        // DAO nie ma tej metody, więc pobierzemy przez wszystkie rośliny użytkownika
        return userPlantDao.getAllPlants(userId).map { userPlants ->
            userPlants.flatMap { plant ->
                userPlantDao.getHarvestRecords(plant.id).first().map {
                    UserPlantMapper.toDomain(it)
                }
            }
        }
    }

    override suspend fun getTotalHarvestForUser(userId: String): Map<String, Float> {
        return try {
            val userPlants = userPlantDao.getAllPlants(userId).first()
            val totalHarvest = mutableMapOf<String, Float>()

            userPlants.forEach { plant ->
                val harvests = userPlantDao.getHarvestRecords(plant.id).first()
                harvests.forEach { harvest ->
                    val unit = harvest.unit.name
                    totalHarvest[unit] = (totalHarvest[unit] ?: 0f) + harvest.amount
                }
            }
            totalHarvest
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override suspend fun getHarvestStats(userId: String): HarvestStats {
        return try {
            val userPlants = userPlantDao.getAllPlants(userId).first()
            val plantsWithHarvests = userPlants.filter { plant ->
                userPlantDao.getHarvestRecords(plant.id).first().isNotEmpty()
            }

            val allHarvests = userPlants.flatMap { plant ->
                userPlantDao.getHarvestRecords(plant.id).first()
            }

            val totalWeight = allHarvests.sumOf { it.amount.toDouble() }.toFloat()
            val totalValue = allHarvests.sumOf { (it.salePrice ?: 0f).toDouble() }.toFloat()

            // Znajdź najbardziej produktywną roślinę
            val harvestsByPlant = allHarvests.groupBy { it.plantId }
            val mostProductivePlantId = harvestsByPlant.maxByOrNull { (_, harvests) ->
                harvests.sumOf { it.amount.toDouble() }
            }?.key

            val mostProductivePlant = mostProductivePlantId?.let { plantId ->
                userPlants.find { it.id == plantId }?.customName
            }

            // Zbiory według miesięcy (uproszczone)
            val harvestsByMonth = allHarvests.groupBy { harvest ->
                java.text.SimpleDateFormat("MM-yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(harvest.harvestDate))
            }.mapValues { (_, harvests) ->
                harvests.sumOf { it.amount.toDouble() }.toFloat()
            }

            HarvestStats(
                totalPlants = userPlants.size,
                plantsHarvested = plantsWithHarvests.size,
                totalWeight = totalWeight,
                totalValue = totalValue,
                mostProductivePlant = mostProductivePlant,
                harvestsByMonth = harvestsByMonth
            )
        } catch (e: Exception) {
            HarvestStats(
                totalPlants = 0,
                plantsHarvested = 0,
                totalWeight = 0f,
                totalValue = 0f,
                mostProductivePlant = null,
                harvestsByMonth = emptyMap()
            )
        }
    }

    // ==================== PROPAGATION TRACKING ====================

    override suspend fun addPropagationRecord(record: PropagationRecord): Result<String> {
        return try {
            val entity = UserPlantMapper.toEntity(record, "user") // userId placeholder
            userPlantDao.insertPropagationRecord(entity)
            Result.success(record.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPropagationRecords(parentPlantId: String): Flow<List<PropagationRecord>> {
        return userPlantDao.getPropagationRecordsForPlant(parentPlantId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override fun getUserPropagationRecords(userId: String): Flow<List<PropagationRecord>> {
        return userPlantDao.getPropagationRecords(userId).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override suspend fun updatePropagationStatus(recordId: String, status: PropagationStatus): Result<Unit> {
        return try {
            // DAO nie ma tej metody - placeholder
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== PLANT CARE UPDATES ====================

    override suspend fun updateLastWatered(plantId: String, date: Long): Result<Unit> {
        return try {
            userPlantDao.updateLastWatered(plantId, date)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLastFertilized(plantId: String, date: Long): Result<Unit> {
        return try {
            userPlantDao.updateLastFertilized(plantId, date)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPlantsNeedingWatering(userId: String): Flow<List<UserPlant>> {
        val thresholdDate = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L) // 3 dni
        return userPlantDao.getPlantsDueForWatering(userId, thresholdDate).map { entities ->
            entities.map { UserPlantMapper.toDomain(it) }
        }
    }

    override fun getPlantsNeedingFertilizing(userId: String): Flow<List<UserPlant>> {
        val thresholdDate = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000L) // 2 tygodnie
        // DAO nie ma tej metody, więc zrobimy przez filtrowanie
        return userPlantDao.getAllPlants(userId).map { entities ->
            entities.filter { entity ->
                entity.lastFertilizedDate == null || entity.lastFertilizedDate < thresholdDate
            }.map { UserPlantMapper.toDomain(it) }
        }
    }

    // ==================== LOCATION MANAGEMENT ====================

    override suspend fun assignPlantToCell(plantId: String, bedId: String, cellRow: Int, cellColumn: Int): Result<Unit> {
        return try {
            userPlantDao.updatePlantLocation(plantId, bedId, cellRow, cellColumn)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removePlantFromCell(plantId: String): Result<Unit> {
        return try {
            userPlantDao.updatePlantLocation(plantId, null, null, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun movePlantToCell(plantId: String, newBedId: String, newCellRow: Int, newCellColumn: Int): Result<Unit> {
        return try {
            userPlantDao.updatePlantLocation(plantId, newBedId, newCellRow, newCellColumn)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== FIREBASE SYNC & CLOUD ====================

    override suspend fun syncUserPlantsWithFirebase(userId: String): Result<Unit> {
        return try {
            // TODO: Implement Firebase sync in Phase II
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPlantImages(plantId: String, imageUris: List<String>): Result<List<String>> {
        return try {
            // TODO: Implement image upload to Firebase Storage in Phase II
            Result.success(imageUris) // Placeholder
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Konwersja enum GrowthPhaseName domain -> entity
     */
    private fun GrowthPhaseName.toEntity(): pl.preclaw.florafocus.data.local.entities.GrowthPhaseName {
        return pl.preclaw.florafocus.data.local.entities.GrowthPhaseName.valueOf(this.name)
    }

    /**
     * Konwersja enum GrowthPhaseName entity -> domain
     */
    private fun pl.preclaw.florafocus.data.local.entities.GrowthPhaseName.toDomain(): GrowthPhaseName {
        return GrowthPhaseName.valueOf(this.name)
    }
}