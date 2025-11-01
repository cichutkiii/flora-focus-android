package pl.preclaw.florafocus.data.mapper

import pl.preclaw.florafocus.data.local.entities.*
import pl.preclaw.florafocus.domain.model.*
import pl.preclaw.florafocus.data.local.entities.HealthStatus as EntityHealthStatus
import pl.preclaw.florafocus.data.local.entities.PropagationStatus as EntityPropagationStatus
import pl.preclaw.florafocus.domain.model.PropagationStatus as DomainPropagationStatus
/**
 * Mapper for UserPlant and related entities
 */
object UserPlantMapper {

    // ==================== USER PLANT ====================

    fun toDomain(entity: UserPlantEntity): UserPlant {
        return UserPlant(
            id = entity.id,
            userId = entity.userId,
            catalogPlantId = entity.catalogPlantId,
            customName = entity.customName,
            variety = entity.variety,
            acquisitionDate = entity.acquisitionDate,
            plantingDate = entity.plantingDate,
            location = if (entity.areaObjectBedId != null && 
                          entity.cellRow != null && 
                          entity.cellColumn != null) {
                PlantLocation(
                    bedId = entity.areaObjectBedId,
                    cellRow = entity.cellRow,
                    cellColumn = entity.cellColumn
                )
            } else null,
            currentPhase = if (entity.currentPhaseId != null && 
                              entity.currentPhaseName != null && 
                              entity.phaseStartDate != null) {
                PhaseInfo(
                    phaseId = entity.currentPhaseId,
                    phaseName = entity.currentPhaseName.toDomain(),
                    startDate = entity.phaseStartDate
                )
            } else null,
            lastWateredDate = entity.lastWateredDate,
            lastFertilizedDate = entity.lastFertilizedDate,
            healthStatus = entity.healthStatus.toDomain(),
            lastHealthCheckDate = entity.lastHealthCheckDate,
            healthScore = entity.healthScore,
            imageUrls = entity.imageUrls,
            notes = entity.notes,
            isActive = entity.isActive,
            harvestedDate = entity.harvestedDate,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: UserPlant): UserPlantEntity {
        return UserPlantEntity(
            id = domain.id,
            userId = domain.userId,
            catalogPlantId = domain.catalogPlantId,
            customName = domain.customName,
            variety = domain.variety,
            acquisitionDate = domain.acquisitionDate,
            plantingDate = domain.plantingDate,
            expectedHarvestDate = null, // ✅ Lub oblicz na podstawie plantingDate + days to harvest

            // Location
            areaObjectBedId = domain.location?.bedId,
            cellRow = domain.location?.cellRow,
            cellColumn = domain.location?.cellColumn,

            // Growth phase
            currentPhaseId = domain.currentPhase?.phaseId,
            currentPhaseName = domain.currentPhase?.phaseName?.toEntity(),
            phaseStartDate = domain.currentPhase?.startDate,
            phaseHistory = emptyList(), // ✅ Domyślna wartość

            // Care tracking
            lastWateredDate = domain.lastWateredDate,
            lastFertilizedDate = domain.lastFertilizedDate,
            lastPrunedDate = null,      // ✅ Jeśli Domain nie ma
            lastInspectionDate = null,  // ✅ Jeśli Domain nie ma

            // Health
            healthStatus = domain.healthStatus.toEntity(),
            lastHealthCheckDate = domain.lastHealthCheckDate,
            healthScore = domain.healthScore,
            currentSymptoms = emptyList(), // ✅ Lub domain.symptoms jeśli masz

            // Environmental
            actualSunExposure = null,    // ✅ Jeśli Domain nie ma
            actualSoilType = null,       // ✅ Jeśli Domain nie ma
            microclimate = null,         // ✅ Jeśli Domain nie ma

            // Measurements
            heightCm = null,             // ✅ Jeśli Domain nie ma
            spreadCm = null,             // ✅ Jeśli Domain nie ma
            trunkDiameterCm = null,      // ✅ Jeśli Domain nie ma

            // Yield tracking
            totalHarvestedAmount = 0f,   // ✅ Domyślna wartość
            totalHarvestSessions = 0,    // ✅ Domyślna wartość
            bestHarvestDate = null,      // ✅ Jeśli Domain nie ma
            bestHarvestAmount = null,    // ✅ Jeśli Domain nie ma

            // Care preferences
            preferredWateringFrequency = null, // ✅ Jeśli Domain nie ma
            customCareNotes = null,      // ✅ Jeśli Domain nie ma
            fertilizerSchedule = null,   // ✅ Jeśli Domain nie ma

            // Media
            imageUrls = domain.imageUrls,
            thumbnailUrl = domain.imageUrls.firstOrNull(), // ✅ Pierwszy obraz jako thumbnail
            notes = domain.notes,
            privateNotes = null,         // ✅ Jeśli Domain nie ma

            // Propagation
            isFromPropagation = false,   // ✅ Lub domain.isFromPropagation jeśli masz
            parentPlantId = null,        // ✅ Jeśli Domain nie ma
            propagationMethod = null,    // ✅ Jeśli Domain nie ma
            propagationDate = null,      // ✅ Jeśli Domain nie ma

            // Status
            isActive = domain.isActive,
            isArchived = false,          // ✅ Domyślna wartość
            deactivatedDate = domain.harvestedDate, // ✅ Lub null jeśli isActive
            deactivationReason = if (domain.harvestedDate != null) "harvested" else null,
            harvestedDate = domain.harvestedDate,
            // Reminders
            wateringReminderEnabled = true,     // ✅ Domyślna wartość
            fertilizingReminderEnabled = true,  // ✅ Domyślna wartość
            customReminderInterval = null,      // ✅ Jeśli Domain nie ma

            // Metadata
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            lastSyncedAt = null          // ✅ Domyślna wartość
        )
    }
    // ==================== GROWTH HISTORY ====================
    fun toDomain(entity: PlantGrowthHistoryEntity): GrowthHistory {
        return GrowthHistory(
            id = entity.id,
            plantId = entity.plantId,
            phaseId = entity.phaseId,
            phaseName = entity.phaseName.toDomain(),
            startDate = entity.phaseStartDate,      // ✅ phaseStartDate
            endDate = entity.phaseEndDate,          // ✅ phaseEndDate
            userConfirmed = !entity.autoTransitioned, // ✅ Odwrotność autoTransitioned
            autoDetected = entity.autoTransitioned, // ✅ autoTransitioned
            notes = entity.observations,            // ✅ observations
            imageUrls = entity.photoUrls,           // ✅ photoUrls
            createdAt = entity.createdAt
        )
    }
    fun toEntity(domain: GrowthHistory): PlantGrowthHistoryEntity {
        return PlantGrowthHistoryEntity(
            id = domain.id,
            plantId = domain.plantId,
            phaseId = domain.phaseId,
            phaseName = domain.phaseName.toEntity(),
            phaseStartDate = domain.startDate,      // ✅ startDate → phaseStartDate
            phaseEndDate = domain.endDate,          // ✅ endDate → phaseEndDate
            actualDurationDays = if (domain.endDate != null) {
                ((domain.endDate - domain.startDate) / (24 * 60 * 60 * 1000)).toInt()
            } else null,
            observations = domain.notes,            // ✅ notes → observations
            photoUrls = domain.imageUrls,           // ✅ imageUrls → photoUrls
            autoTransitioned = domain.autoDetected, // ✅ autoDetected → autoTransitioned
            createdAt = domain.createdAt,
            updatedAt = System.currentTimeMillis()
        )
    }

    // ==================== HEALTH RECORD ====================

    fun toDomain(entity: HealthRecordEntity): HealthRecord {
        return HealthRecord(
            id = entity.id,
            plantId = entity.plantId,
            recordDate = entity.checkDate,          // ✅ checkDate
            healthScore = entity.healthScore,
            symptoms = entity.symptoms.map { it.toDomain() },

            // Pola których brak w Entity - ustaw wartości domyślne:
            diagnosis = null,                       // ✅ Brak w Entity
            possibleCauses = emptyList(),          // ✅ Brak w Entity
            treatment = null,                      // ✅ Brak w Entity
            treatmentSteps = emptyList(),          // ✅ Brak w Entity

            imageUrls = entity.photoUrls,          // ✅ photoUrls

            // Pola których brak w Entity:
            resolved = false,                      // ✅ Brak w Entity - domyślnie false
            resolvedDate = null,                   // ✅ Brak w Entity

            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: HealthRecord): HealthRecordEntity {
        return HealthRecordEntity(
            id = domain.id,
            plantId = domain.plantId,
            checkDate = domain.recordDate,          // ✅ recordDate → checkDate
            healthStatus = deriveHealthStatus(domain.healthScore), // ✅ Wydedukuj z score
            healthScore = domain.healthScore,
            previousHealthScore = null,             // ✅ Domyślna wartość
            symptoms = domain.symptoms.map { it.toEntity() },
            newSymptoms = emptyList(),              // ✅ Domyślna wartość
            resolvedSymptoms = emptyList(),         // ✅ Domyślna wartość

            // Szczegółowe observacje z diagnosis/treatment:
            leafCondition = null,                   // ✅ Domyślna wartość
            stemCondition = null,
            rootCondition = null,
            overallVigor = domain.diagnosis,        // ✅ Użyj diagnosis jako vigor

            // Suspected issues z Domain:
            suspectedPests = emptyList(),           // ✅ Lub parse z possibleCauses
            suspectedDiseases = emptyList(),
            suspectedDeficiencies = emptyList(),

            photoUrls = domain.imageUrls,           // ✅ imageUrls → photoUrls
            notes = domain.notes,
            checkedBy = null,                       // ✅ Domyślna wartość
            createdAt = domain.createdAt
        )
    }

    // Pomocnicza funkcja:
    private fun deriveHealthStatus(score: Int): EntityHealthStatus {
        return when (score) {
            in 9..10 -> EntityHealthStatus.EXCELLENT
            in 7..8 -> EntityHealthStatus.HEALTHY
            in 5..6 -> EntityHealthStatus.GOOD
            in 3..4 -> EntityHealthStatus.POOR
            in 1..2 -> EntityHealthStatus.CRITICAL
            else -> EntityHealthStatus.DEAD
        }
    }
    // ==================== INTERVENTION ====================

    fun toDomain(entity: InterventionEntity): Intervention {
        return Intervention(
            id = entity.id,
            plantId = entity.plantId,
            interventionDate = entity.interventionDate,
            interventionType = entity.interventionType.toDomain(),

            details = entity.description ?: entity.title, // ✅ description lub title

            // Konwertuj productsUsed + quantities na ProductUsed obiekty:
            products = entity.productsUsed.map { productName ->
                ProductUsed(
                    productName = productName,
                    amount = entity.quantities[productName] ?: "unknown",
                    unit = "unknown" // ✅ Entity nie ma unit - ustaw domyślne
                )
            },

            // Pola których brak w Entity:
            nextScheduledDate = null,        // ✅ Brak w Entity
            completed = true,                // ✅ Brak w Entity - załóż że completed

            notes = entity.notes,

            // Połącz before i after photos:
            imageUrls = entity.beforePhotoUrls + entity.afterPhotoUrls, // ✅ Połącz listy

            createdAt = entity.createdAt
        )
    }
    fun toEntity(domain: Intervention): InterventionEntity {
        return InterventionEntity(
            id = domain.id,
            plantId = domain.plantId,
            interventionDate = domain.interventionDate,
            interventionType = domain.interventionType.toEntity(),
            title = domain.interventionType.toString(), // ✅ Lub pierwsze słowa z details
            description = domain.details,

            // Rozdziel ProductUsed na listy:
            productsUsed = domain.products.map { it.productName },
            quantities = domain.products.associate {
                it.productName to "${it.amount} ${it.unit}"
            },
            applicationMethod = null,        // ✅ Domyślna wartość

            // Warunki:
            weatherConditions = null,        // ✅ Domyślna wartość
            temperature = null,
            humidity = null,
            timeOfDay = null,
            durationMinutes = null,

            // Rezultaty:
            immediateResults = null,         // ✅ Domyślne wartości
            followUpDate = domain.nextScheduledDate,
            followUpNotes = null,
            effectiveness = null,

            // Koszt:
            cost = null,
            currency = "PLN",

            // Bezpieczeństwo:
            safetyPrecautions = emptyList(),
            harvestDelay = null,

            // Zdjęcia - podziel na before/after (lub ustaw wszystkie jako after):
            beforePhotoUrls = emptyList(),   // ✅ Lub część z imageUrls
            afterPhotoUrls = domain.imageUrls,

            notes = domain.notes,
            performedBy = null,              // ✅ Domyślna wartość
            createdAt = domain.createdAt
        )
    }

    // ==================== HARVEST ====================
    fun toDomain(entity: HarvestRecordEntity): HarvestRecord {
        return HarvestRecord(
            id = entity.id,
            plantId = entity.plantId,
            harvestDate = entity.harvestDate,
            amount = entity.amount,
            unit = entity.unit.toDomain(),
            quality = entity.quality.toDomain(),
            usage = entity.primaryUsage.toDomain(),      // ✅ primaryUsage → usage
            marketValue = entity.salePrice,              // ✅ salePrice → marketValue
            imageUrls = entity.photoUrls,                // ✅ photoUrls → imageUrls
            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: HarvestRecord): HarvestRecordEntity {
        return HarvestRecordEntity(
            id = domain.id,
            plantId = domain.plantId,
            harvestDate = domain.harvestDate,
            amount = domain.amount,
            unit = domain.unit.toEntity(),
            quality = domain.quality.toEntity(),
            ripeness = null,                     // ✅ Domyślna wartość

            // Harvest parts:
            harvestedParts = emptyList(),        // ✅ Domyślna wartość
            varietyNotes = null,

            // Usage:
            primaryUsage = domain.usage.toEntity(), // ✅ usage → primaryUsage
            secondaryUsages = emptyList(),       // ✅ Domyślna wartość

            // Market/sharing (jeśli Domain ma marketValue):
            sharedAmount = null,                 // ✅ Domyślne wartości
            sharedWith = null,
            soldAmount = null,
            salePrice = domain.marketValue,      // ✅ marketValue → salePrice

            // Expectations:
            expectedAmount = null,               // ✅ Domyślne wartości
            comparedToExpected = null,

            // Conditions:
            weatherLastWeek = null,              // ✅ Domyślne wartości
            plantAge = null,
            daysFromFlowering = null,

            // Quality ratings:
            flavorRating = null,                 // ✅ Domyślne wartości
            textureRating = null,
            appearanceRating = null,
            overallSatisfaction = null,

            // Seeds:
            seedsSaved = false,                  // ✅ Domyślne wartości
            seedsSavedAmount = null,
            seedQuality = null,

            // Storage:
            storageMethod = null,                // ✅ Domyślne wartości
            expectedStorageLife = null,

            // Media:
            photoUrls = domain.imageUrls,        // ✅ imageUrls → photoUrls

            notes = domain.notes,
            harvestedBy = null,                  // ✅ Domyślna wartość
            createdAt = domain.createdAt
        )
    }

    // ==================== PROPAGATION ====================

    fun toDomain(entity: PropagationRecordEntity): PropagationRecord {
        return PropagationRecord(
            id = entity.id,
            parentPlantId = entity.parentPlantId,
            method = entity.method.toDomain(),
            startDate = entity.startDate,
            status = entity.status.toDomain(),          // ✅ Użyje extension function
            successDate = entity.rootingSuccessDate,
            failureDate = if (entity.status == EntityPropagationStatus.FAILED) {
                entity.updatedAt
            } else null,
            resultingPlantId = entity.resultingPlantId,
            notes = entity.notes,
            imageUrls = entity.photoUrls,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: PropagationRecord, userId: String): PropagationRecordEntity {
        return PropagationRecordEntity(
            id = domain.id,
            parentPlantId = domain.parentPlantId,
            propagationDate = domain.startDate,
            method = domain.method.toEntity(),
            status = domain.status.toEntity(),          // ✅ Użyje extension function

            // ... reszta pól ...

            finalOutcome = when (domain.status) {
                DomainPropagationStatus.IN_PROGRESS -> "In progress"
                DomainPropagationStatus.ROOTING -> "Rooting"
                DomainPropagationStatus.TRANSPLANTED -> "Transplanted successfully"
                DomainPropagationStatus.ESTABLISHED -> "Strong new plant"
                DomainPropagationStatus.SUCCESS -> "Plant transplanted and growing"
                DomainPropagationStatus.FAILED -> "Failed"
            },

            userId = userId,
            startDate = domain.startDate
        )
    }

    // ==================== ENUM CONVERSIONS ====================

    private fun pl.preclaw.florafocus.data.local.entities.HealthStatus.toDomain() =
        pl.preclaw.florafocus.domain.model.HealthStatus.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.HealthStatus.toEntity() =
        pl.preclaw.florafocus.data.local.entities.HealthStatus.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.GrowthPhaseName.toDomain() =
        pl.preclaw.florafocus.domain.model.GrowthPhaseName.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.GrowthPhaseName.toEntity() =
        pl.preclaw.florafocus.data.local.entities.GrowthPhaseName.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.Symptom.toDomain() =
        pl.preclaw.florafocus.domain.model.Symptom.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.Symptom.toEntity() =
        pl.preclaw.florafocus.data.local.entities.Symptom.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.InterventionType.toDomain() =
        pl.preclaw.florafocus.domain.model.InterventionType.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.InterventionType.toEntity() =
        pl.preclaw.florafocus.data.local.entities.InterventionType.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.HarvestUnit.toDomain() =
        pl.preclaw.florafocus.domain.model.HarvestUnit.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.HarvestUnit.toEntity() =
        pl.preclaw.florafocus.data.local.entities.HarvestUnit.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.HarvestQuality.toDomain() =
        pl.preclaw.florafocus.domain.model.HarvestQuality.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.HarvestQuality.toEntity() =
        pl.preclaw.florafocus.data.local.entities.HarvestQuality.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.HarvestUsage.toDomain() =
        pl.preclaw.florafocus.domain.model.HarvestUsage.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.HarvestUsage.toEntity() =
        pl.preclaw.florafocus.data.local.entities.HarvestUsage.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.PropagationMethod.toDomain() =
        pl.preclaw.florafocus.domain.model.PropagationMethod.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.PropagationMethod.toEntity() =
        pl.preclaw.florafocus.data.local.entities.PropagationMethod.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.PropagationStatus.toDomain() =
        pl.preclaw.florafocus.domain.model.PropagationStatus.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.PropagationStatus.toEntity() =
        pl.preclaw.florafocus.data.local.entities.PropagationStatus.valueOf(this.name)
}
