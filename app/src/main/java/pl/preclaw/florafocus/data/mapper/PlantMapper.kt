package pl.preclaw.florafocus.data.mapper

import pl.preclaw.florafocus.data.local.entities.*
import pl.preclaw.florafocus.domain.model.*

/**
 * Mapper for Plant Catalog
 * Converts between Entity (Room) and Domain models
 */
object PlantMapper {

    // ==================== PLANT CATALOG ====================

    fun toDomain(entity: PlantCatalogEntity): Plant {
        return Plant(
            id = entity.id,
            commonName = entity.commonName,
            latinName = entity.latinName,
            family = entity.family,
            plantType = entity.plantType.toDomain(),
            lightRequirements = entity.lightRequirements.toDomain(),
            soilType = entity.soilType,
            soilPHRange = if (entity.soilPHMin != null && entity.soilPHMax != null) {
                Pair(entity.soilPHMin, entity.soilPHMax)
            } else null,
            wateringFrequency = entity.wateringFrequency.toDomain(),
            growthDifficulty = entity.growthDifficulty.toDomain(),
            toxicity = entity.toxicity,
            edible = entity.edible,
            hardiness = entity.hardiness,
            companionPlantIds = entity.companionPlantIds,
            incompatiblePlantIds = entity.incompatiblePlantIds,
            sowingPeriod = if (entity.sowingPeriodStart != null && entity.sowingPeriodEnd != null) {
                DateRange(entity.sowingPeriodStart, entity.sowingPeriodEnd)
            } else null,
            harvestPeriod = if (entity.harvestPeriodStart != null && entity.harvestPeriodEnd != null) {
                DateRange(entity.harvestPeriodStart, entity.harvestPeriodEnd)
            } else null,
            daysToHarvestRange = if (entity.daysToHarvestMin != null && entity.daysToHarvestMax != null) {
                Pair(entity.daysToHarvestMin, entity.daysToHarvestMax)
            } else null,
            averageYield = entity.averageYield,
            growthPhases = entity.growthPhases.map { it.toDomain() },
            imageUrls = entity.imageUrls,
            tags = entity.tags,
            description = entity.description,
            careInstructions = entity.careInstructions,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Plant): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = domain.id,
            commonName = domain.commonName,
            latinName = domain.latinName,
            family = domain.family,
            plantType = domain.plantType.toEntity(),
            lightRequirements = domain.lightRequirements.toEntity(),
            soilType = domain.soilType,
            soilPHMin = domain.soilPHRange?.first,
            soilPHMax = domain.soilPHRange?.second,
            wateringFrequency = domain.wateringFrequency.toEntity(),
            growthDifficulty = domain.growthDifficulty.toEntity(),
            toxicity = domain.toxicity,
            edible = domain.edible,
            hardiness = domain.hardiness,
            companionPlantIds = domain.companionPlantIds,
            incompatiblePlantIds = domain.incompatiblePlantIds,
            sowingPeriodStart = domain.sowingPeriod?.start,
            sowingPeriodEnd = domain.sowingPeriod?.end,
            harvestPeriodStart = domain.harvestPeriod?.start,
            harvestPeriodEnd = domain.harvestPeriod?.end,
            daysToHarvestMin = domain.daysToHarvestRange?.first,
            daysToHarvestMax = domain.daysToHarvestRange?.second,
            averageYield = domain.averageYield,
            growthPhases = domain.growthPhases.map { it.toEntity() },
            imageUrls = domain.imageUrls,
            tags = domain.tags,
            description = domain.description,
            careInstructions = domain.careInstructions,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== GROWTH PHASE ====================

    private fun GrowthPhaseData.toDomain(): GrowthPhase {
        return GrowthPhase(
            id = this.id,
            phaseName = this.phaseName.toDomain(),
            displayName = this.displayName,
            averageDurationRange = Pair(
                this.averageDurationDaysMin,
                this.averageDurationDaysMax
            ),
            description = this.description,
            careInstructions = this.careInstructions,
            visualIndicators = this.visualIndicators,
            autoTasks = this.autoTasks.map { it.toDomain() }
        )
    }

    private fun GrowthPhase.toEntity(): GrowthPhaseData {
        return GrowthPhaseData(
            id = this.id,
            phaseName = this.phaseName.toEntity(),
            displayName = this.displayName,
            averageDurationDaysMin = this.averageDurationRange.first,
            averageDurationDaysMax = this.averageDurationRange.second,
            description = this.description,
            careInstructions = this.careInstructions,
            visualIndicators = this.visualIndicators,
            autoTasks = this.autoTasks.map { it.toEntity() }
        )
    }

    // ==================== AUTO TASK ====================

    private fun AutoTaskData.toDomain(): AutoTask {
        return AutoTask(
            taskTitle = this.taskTitle,
            taskDescription = this.taskDescription,
            taskType = this.taskType.toDomain(),
            triggerDayOffset = this.triggerDayOffset,
            priority = this.priority.toDomain()
        )
    }

    private fun AutoTask.toEntity(): AutoTaskData {
        return AutoTaskData(
            taskTitle = this.taskTitle,
            taskDescription = this.taskDescription,
            taskType = this.taskType.toEntity(),
            triggerDayOffset = this.triggerDayOffset,
            priority = this.priority.toEntity()
        )
    }

    // ==================== ENUM CONVERSIONS ====================

    private fun pl.preclaw.florafocus.data.local.entities.PlantType.toDomain() =
        pl.preclaw.florafocus.domain.model.PlantType.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.PlantType.toEntity() =
        pl.preclaw.florafocus.data.local.entities.PlantType.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.LightRequirements.toDomain() =
        pl.preclaw.florafocus.domain.model.LightRequirements.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.LightRequirements.toEntity() =
        pl.preclaw.florafocus.data.local.entities.LightRequirements.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.WateringFrequency.toDomain() =
        pl.preclaw.florafocus.domain.model.WateringFrequency.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.WateringFrequency.toEntity() =
        pl.preclaw.florafocus.data.local.entities.WateringFrequency.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.GrowthDifficulty.toDomain() =
        pl.preclaw.florafocus.domain.model.GrowthDifficulty.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.GrowthDifficulty.toEntity() =
        pl.preclaw.florafocus.data.local.entities.GrowthDifficulty.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.GrowthPhaseName.toDomain() =
        pl.preclaw.florafocus.domain.model.GrowthPhaseName.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.GrowthPhaseName.toEntity() =
        pl.preclaw.florafocus.data.local.entities.GrowthPhaseName.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.TaskType.toDomain() =
        pl.preclaw.florafocus.domain.model.TaskType.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.TaskType.toEntity() =
        pl.preclaw.florafocus.data.local.entities.TaskType.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.TaskPriority.toDomain() =
        pl.preclaw.florafocus.domain.model.TaskPriority.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.TaskPriority.toEntity() =
        pl.preclaw.florafocus.data.local.entities.TaskPriority.valueOf(this.name)
}
