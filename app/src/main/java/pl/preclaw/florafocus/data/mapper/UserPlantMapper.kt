package pl.preclaw.florafocus.data.mapper

import pl.preclaw.florafocus.data.local.entities.*
import pl.preclaw.florafocus.domain.model.*

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
            areaObjectBedId = domain.location?.bedId,
            cellRow = domain.location?.cellRow,
            cellColumn = domain.location?.cellColumn,
            currentPhaseId = domain.currentPhase?.phaseId,
            currentPhaseName = domain.currentPhase?.phaseName?.toEntity(),
            phaseStartDate = domain.currentPhase?.startDate,
            lastWateredDate = domain.lastWateredDate,
            lastFertilizedDate = domain.lastFertilizedDate,
            healthStatus = domain.healthStatus.toEntity(),
            lastHealthCheckDate = domain.lastHealthCheckDate,
            healthScore = domain.healthScore,
            imageUrls = domain.imageUrls,
            notes = domain.notes,
            isActive = domain.isActive,
            harvestedDate = domain.harvestedDate,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== GROWTH HISTORY ====================

    fun toDomain(entity: PlantGrowthHistoryEntity): GrowthHistory {
        return GrowthHistory(
            id = entity.id,
            plantId = entity.plantId,
            phaseId = entity.phaseId,
            phaseName = entity.phaseName.toDomain(),
            startDate = entity.startDate,
            endDate = entity.endDate,
            userConfirmed = entity.userConfirmed,
            autoDetected = entity.autoDetected,
            notes = entity.notes,
            imageUrls = entity.imageUrls,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: GrowthHistory): PlantGrowthHistoryEntity {
        return PlantGrowthHistoryEntity(
            id = domain.id,
            plantId = domain.plantId,
            phaseId = domain.phaseId,
            phaseName = domain.phaseName.toEntity(),
            startDate = domain.startDate,
            endDate = domain.endDate,
            userConfirmed = domain.userConfirmed,
            autoDetected = domain.autoDetected,
            notes = domain.notes,
            imageUrls = domain.imageUrls,
            createdAt = domain.createdAt
        )
    }

    // ==================== HEALTH RECORD ====================

    fun toDomain(entity: HealthRecordEntity): HealthRecord {
        return HealthRecord(
            id = entity.id,
            plantId = entity.plantId,
            recordDate = entity.recordDate,
            healthScore = entity.healthScore,
            symptoms = entity.symptoms.map { it.toDomain() },
            diagnosis = entity.diagnosis,
            possibleCauses = entity.possibleCauses,
            treatment = entity.treatment,
            treatmentSteps = entity.treatmentSteps,
            imageUrls = entity.imageUrls,
            resolved = entity.resolved,
            resolvedDate = entity.resolvedDate,
            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: HealthRecord): HealthRecordEntity {
        return HealthRecordEntity(
            id = domain.id,
            plantId = domain.plantId,
            recordDate = domain.recordDate,
            healthScore = domain.healthScore,
            symptoms = domain.symptoms.map { it.toEntity() },
            diagnosis = domain.diagnosis,
            possibleCauses = domain.possibleCauses,
            treatment = domain.treatment,
            treatmentSteps = domain.treatmentSteps,
            imageUrls = domain.imageUrls,
            resolved = domain.resolved,
            resolvedDate = domain.resolvedDate,
            notes = domain.notes,
            createdAt = domain.createdAt
        )
    }

    // ==================== INTERVENTION ====================

    fun toDomain(entity: InterventionEntity): Intervention {
        return Intervention(
            id = entity.id,
            plantId = entity.plantId,
            interventionDate = entity.interventionDate,
            interventionType = entity.interventionType.toDomain(),
            details = entity.details,
            products = entity.products.map { 
                ProductUsed(it.productName, it.amount, it.unit) 
            },
            nextScheduledDate = entity.nextScheduledDate,
            completed = entity.completed,
            notes = entity.notes,
            imageUrls = entity.imageUrls,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: Intervention): InterventionEntity {
        return InterventionEntity(
            id = domain.id,
            plantId = domain.plantId,
            interventionDate = domain.interventionDate,
            interventionType = domain.interventionType.toEntity(),
            details = domain.details,
            products = domain.products.map { 
                ProductUsedData(it.productName, it.amount, it.unit) 
            },
            nextScheduledDate = domain.nextScheduledDate,
            completed = domain.completed,
            notes = domain.notes,
            imageUrls = domain.imageUrls,
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
            usage = entity.usage.toDomain(),
            marketValue = entity.marketValue,
            notes = entity.notes,
            imageUrls = entity.imageUrls,
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
            usage = domain.usage.toEntity(),
            marketValue = domain.marketValue,
            notes = domain.notes,
            imageUrls = domain.imageUrls,
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
            status = entity.status.toDomain(),
            successDate = entity.successDate,
            failureDate = entity.failureDate,
            resultingPlantId = entity.resultingPlantId,
            notes = entity.notes,
            imageUrls = entity.imageUrls,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: PropagationRecord): PropagationRecordEntity {
        return PropagationRecordEntity(
            id = domain.id,
            parentPlantId = domain.parentPlantId,
            method = domain.method.toEntity(),
            startDate = domain.startDate,
            status = domain.status.toEntity(),
            successDate = domain.successDate,
            failureDate = domain.failureDate,
            resultingPlantId = domain.resultingPlantId,
            notes = domain.notes,
            imageUrls = domain.imageUrls,
            createdAt = domain.createdAt
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
