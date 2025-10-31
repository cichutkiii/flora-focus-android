package pl.preclaw.florafocus.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import pl.preclaw.florafocus.data.local.dao.UserPlantDao
import pl.preclaw.florafocus.data.mapper.UserPlantMapper
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.domain.repository.UserPlantRepository
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserPlantRepository
 *
 * Manages user's plant instances with full tracking:
 * - Growth phases
 * - Health records
 * - Interventions
 * - Harvests
 * - Propagation
 */
@Singleton
class UserPlantRepositoryImpl @Inject constructor(
    private val userPlantDao: UserPlantDao,
    private val firestore: FirebaseFirestore,
    private val userPlantMapper: UserPlantMapper
) : UserPlantRepository {

    companion object {
        private const val COLLECTION_USER_PLANTS = "user_plants"
        private const val TAG = "UserPlantRepository"
    }

    // ==================== USER PLANT - CRUD ====================

    override fun getUserPlants(userId: String): Flow<List<UserPlant>> {
        return userPlantDao.getUserPlants(userId)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override suspend fun getUserPlantById(plantId: String): UserPlant? {
        return userPlantDao.getUserPlantById(plantId)?.let {
            userPlantMapper.toDomain(it)
        }
    }

    override fun getUserPlantByIdFlow(plantId: String): Flow<UserPlant?> {
        return userPlantDao.getUserPlantByIdFlow(plantId)
            .map { entity -> entity?.let { userPlantMapper.toDomain(it) } }
    }

    override suspend fun createUserPlant(plant: UserPlant): Result<String> {
        return try {
            val entity = userPlantMapper.toEntity(plant)
            userPlantDao.insertUserPlant(entity)

            Timber.tag(TAG).d("Created user plant: ${plant.customName}")
            Result.success(plant.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error creating user plant")
            Result.failure(e)
        }
    }

    override suspend fun updateUserPlant(plant: UserPlant): Result<Unit> {
        return try {
            val entity = userPlantMapper.toEntity(plant)
            userPlantDao.updateUserPlant(entity)

            Timber.tag(TAG).d("Updated user plant: ${plant.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating user plant")
            Result.failure(e)
        }
    }

    override suspend fun deleteUserPlant(plantId: String): Result<Unit> {
        return try {
            userPlantDao.deleteUserPlantById(plantId)

            Timber.tag(TAG).d("Deleted user plant: $plantId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting user plant")
            Result.failure(e)
        }
    }

    // ==================== QUERIES & FILTERS ====================

    override fun getUserPlantsByLocation(areaObjectBedId: String): Flow<List<UserPlant>> {
        return userPlantDao.getUserPlantsByLocation(areaObjectBedId)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override fun getUserPlantsByHealthStatus(
        userId: String,
        status: HealthStatus
    ): Flow<List<UserPlant>> {
        return userPlantDao.getUserPlantsByHealthStatus(userId, status)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override fun getUserPlantsByPhase(
        userId: String,
        phase: GrowthPhaseName
    ): Flow<List<UserPlant>> {
        return userPlantDao.getUserPlantsByPhase(userId, phase)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override fun searchUserPlants(userId: String, query: String): Flow<List<UserPlant>> {
        return userPlantDao.searchUserPlants(userId, query)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override suspend fun getUserPlantCount(userId: String): Int {
        return userPlantDao.getUserPlantCount(userId)
    }

    override suspend fun getHealthyPlantsCount(userId: String): Int {
        return userPlantDao.getHealthyPlantsCount(userId)
    }

    override suspend fun getPlantsNeedingAttention(userId: String): List<UserPlant> {
        return userPlantDao.getPlantsNeedingAttention(userId)
            .map { userPlantMapper.toDomain(it) }
    }

    // ==================== GROWTH PHASE TRACKING ====================

    override suspend fun updatePlantPhase(
        plantId: String,
        newPhaseId: String,
        newPhaseName: GrowthPhaseName,
        confirmedByUser: Boolean
    ): Result<Unit> {
        return try {
            val plant = userPlantDao.getUserPlantById(plantId)
                ?: return Result.failure(Exception("Plant not found"))

            // Create growth history entry
            val historyEntry = userPlantMapper.toEntity(
                PlantGrowthHistory(
                    id = UUID.randomUUID().toString(),
                    plantId = plantId,
                    phaseId = newPhaseId,
                    phaseName = newPhaseName,
                    startDate = System.currentTimeMillis(),
                    endDate = null,
                    confirmedByUser = confirmedByUser,
                    autoDetected = !confirmedByUser,
                    notes = null
                )
            )

            userPlantDao.insertGrowthHistory(historyEntry)

            // Update current phase in plant
            userPlantDao.updateUserPlant(
                plant.copy(
                    currentPhaseId = newPhaseId,
                    currentPhaseName = newPhaseName,
                    phaseStartDate = System.currentTimeMillis()
                )
            )

            Timber.tag(TAG).d("Updated plant $plantId phase to $newPhaseName")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating plant phase")
            Result.failure(e)
        }
    }

    override fun getPlantGrowthHistory(plantId: String): Flow<List<PlantGrowthHistory>> {
        return userPlantDao.getGrowthHistory(plantId)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override suspend fun confirmPhaseTransition(historyId: String): Result<Unit> {
        return try {
            val history = userPlantDao.getGrowthHistoryById(historyId)
                ?: return Result.failure(Exception("History entry not found"))

            userPlantDao.updateGrowthHistory(
                history.copy(
                    confirmedByUser = true,
                    autoDetected = false
                )
            )

            Timber.tag(TAG).d("Confirmed phase transition: $historyId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error confirming phase transition")
            Result.failure(e)
        }
    }

    // ==================== HEALTH RECORDS ====================

    override suspend fun addHealthRecord(record: HealthRecord): Result<String> {
        return try {
            val entity = userPlantMapper.toEntity(record)
            userPlantDao.insertHealthRecord(entity)

            // Update plant health status if needed
            if (record.healthScore < 7) {
                val plant = userPlantDao.getUserPlantById(record.plantId)
                plant?.let {
                    val newStatus = when {
                        record.healthScore >= 7 -> HealthStatus.HEALTHY
                        record.healthScore >= 4 -> HealthStatus.NEEDS_ATTENTION
                        else -> HealthStatus.SICK
                    }

                    userPlantDao.updateUserPlant(
                        it.copy(
                            healthStatus = newStatus,
                            healthScore = record.healthScore,
                            lastHealthCheckDate = record.recordDate
                        )
                    )
                }
            }

            Timber.tag(TAG).d("Added health record for plant ${record.plantId}")
            Result.success(record.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error adding health record")
            Result.failure(e)
        }
    }

    override fun getHealthRecords(plantId: String): Flow<List<HealthRecord>> {
        return userPlantDao.getHealthRecords(plantId)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override suspend fun updateHealthRecord(record: HealthRecord): Result<Unit> {
        return try {
            val entity = userPlantMapper.toEntity(record)
            userPlantDao.updateHealthRecord(entity)

            Timber.tag(TAG).d("Updated health record: ${record.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating health record")
            Result.failure(e)
        }
    }

    override suspend fun markHealthIssueResolved(recordId: String): Result<Unit> {
        return try {
            val record = userPlantDao.getHealthRecordById(recordId)
                ?: return Result.failure(Exception("Health record not found"))

            userPlantDao.updateHealthRecord(
                record.copy(
                    resolved = true,
                    resolvedDate = System.currentTimeMillis()
                )
            )

            Timber.tag(TAG).d("Marked health issue resolved: $recordId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error marking health issue resolved")
            Result.failure(e)
        }
    }

    // ==================== INTERVENTIONS ====================

    override suspend fun addIntervention(intervention: Intervention): Result<String> {
        return try {
            val entity = userPlantMapper.toEntity(intervention)
            userPlantDao.insertIntervention(entity)

            // Update last watered/fertilized dates
            when (intervention.interventionType) {
                InterventionType.WATERING -> {
                    val plant = userPlantDao.getUserPlantById(intervention.plantId)
                    plant?.let {
                        userPlantDao.updateUserPlant(
                            it.copy(lastWateredDate = intervention.interventionDate)
                        )
                    }
                }
                InterventionType.FERTILIZING -> {
                    val plant = userPlantDao.getUserPlantById(intervention.plantId)
                    plant?.let {
                        userPlantDao.updateUserPlant(
                            it.copy(lastFertilizedDate = intervention.interventionDate)
                        )
                    }
                }
                else -> {} // Other types don't update plant directly
            }

            Timber.tag(TAG).d("Added intervention for plant ${intervention.plantId}")
            Result.success(intervention.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error adding intervention")
            Result.failure(e)
        }
    }

    override fun getInterventions(plantId: String): Flow<List<Intervention>> {
        return userPlantDao.getInterventions(plantId)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override fun getInterventionsByType(
        plantId: String,
        type: InterventionType
    ): Flow<List<Intervention>> {
        return userPlantDao.getInterventionsByType(plantId, type)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override suspend fun updateIntervention(intervention: Intervention): Result<Unit> {
        return try {
            val entity = userPlantMapper.toEntity(intervention)
            userPlantDao.updateIntervention(entity)

            Timber.tag(TAG).d("Updated intervention: ${intervention.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating intervention")
            Result.failure(e)
        }
    }

    override suspend fun deleteIntervention(interventionId: String): Result<Unit> {
        return try {
            userPlantDao.deleteInterventionById(interventionId)

            Timber.tag(TAG).d("Deleted intervention: $interventionId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting intervention")
            Result.failure(e)
        }
    }

    // ==================== HARVESTS ====================

    override suspend fun addHarvestRecord(harvest: HarvestRecord): Result<String> {
        return try {
            val entity = userPlantMapper.toEntity(harvest)
            userPlantDao.insertHarvestRecord(entity)

            Timber.tag(TAG).d("Added harvest record for plant ${harvest.plantId}")
            Result.success(harvest.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error adding harvest record")
            Result.failure(e)
        }
    }

    override fun getHarvestRecords(plantId: String): Flow<List<HarvestRecord>> {
        return userPlantDao.getHarvestRecords(plantId)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override suspend fun updateHarvestRecord(harvest: HarvestRecord): Result<Unit> {
        return try {
            val entity = userPlantMapper.toEntity(harvest)
            userPlantDao.updateHarvestRecord(entity)

            Timber.tag(TAG).d("Updated harvest record: ${harvest.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating harvest record")
            Result.failure(e)
        }
    }

    override suspend fun deleteHarvestRecord(harvestId: String): Result<Unit> {
        return try {
            userPlantDao.deleteHarvestRecordById(harvestId)

            Timber.tag(TAG).d("Deleted harvest record: $harvestId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting harvest record")
            Result.failure(e)
        }
    }

    override suspend fun getTotalHarvestForUser(userId: String): Map<String, Float> {
        // Returns map of plant names to total harvest amounts
        return try {
            val plants = userPlantDao.getAllUserPlants(userId)
            val harvestMap = mutableMapOf<String, Float>()

            plants.forEach { plant ->
                val harvests = userPlantDao.getHarvestRecordsList(plant.id)
                val total = harvests.sumOf { it.amount.toDouble() }.toFloat()
                if (total > 0) {
                    harvestMap[plant.customName] = total
                }
            }

            harvestMap
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error calculating total harvest")
            emptyMap()
        }
    }

    // ==================== PROPAGATION ====================

    override suspend fun addPropagationRecord(record: PropagationRecord): Result<String> {
        return try {
            val entity = userPlantMapper.toEntity(record)
            userPlantDao.insertPropagationRecord(entity)

            Timber.tag(TAG).d("Added propagation record for plant ${record.parentPlantId}")
            Result.success(record.id)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error adding propagation record")
            Result.failure(e)
        }
    }

    override fun getPropagationRecords(parentPlantId: String): Flow<List<PropagationRecord>> {
        return userPlantDao.getPropagationRecords(parentPlantId)
            .map { entities ->
                entities.map { userPlantMapper.toDomain(it) }
            }
    }

    override suspend fun updatePropagationRecord(record: PropagationRecord): Result<Unit> {
        return try {
            val entity = userPlantMapper.toEntity(record)
            userPlantDao.updatePropagationRecord(entity)

            Timber.tag(TAG).d("Updated propagation record: ${record.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating propagation record")
            Result.failure(e)
        }
    }

    override suspend fun deletePropagationRecord(recordId: String): Result<Unit> {
        return try {
            userPlantDao.deletePropagationRecordById(recordId)

            Timber.tag(TAG).d("Deleted propagation record: $recordId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error deleting propagation record")
            Result.failure(e)
        }
    }

    // ==================== FIREBASE SYNC (Future) ====================

    override suspend fun syncUserPlantsWithFirebase(userId: String): Result<Unit> {
        return try {
            // TODO: Implement Firebase sync in Phase II
            Timber.tag(TAG).d("Firebase sync not yet implemented")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error syncing with Firebase")
            Result.failure(e)
        }
    }
}