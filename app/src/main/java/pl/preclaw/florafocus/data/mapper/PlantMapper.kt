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
            description = entity.pruningNotes,
            careInstructions = entity.specialCareInstructions,
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

            // ===== BRAKUJĄCE POLA - DODAJ: =====
            minTemperature = null, // lub domain.minTemperature jeśli masz
            maxTemperature = null, // lub domain.maxTemperature jeśli masz
            frostTolerant = false, // lub domain.frostTolerant jeśli masz

            growthPhases = domain.growthPhases.map { it.toEntity() },

            // Disease & pest info
            commonDiseases = emptyList(), // lub domain.commonDiseases jeśli masz
            commonPests = emptyList(), // lub domain.commonPests jeśli masz
            diseaseResistance = emptyList(), // lub domain.diseaseResistance jeśli masz

            // Care tips - mapowanie z domain.careInstructions
            wateringTips = extractWateringTips(domain.careInstructions),
            fertilizingTips = extractFertilizingTips(domain.careInstructions),
            pruningNotes = extractPruningNotes(domain.careInstructions),
            specialCareInstructions = domain.description, // lub cały careInstructions

            // Media
            imageUrls = domain.imageUrls,
            thumbnailUrl = domain.imageUrls.firstOrNull(), // Pierwszy obraz jako thumbnail

            tags = domain.tags,

            // Metadata
            source = "user_input", // lub domain.source jeśli masz
            verified = false, // lub domain.verified jeśli masz
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // Pomocnicze funkcje do parsowania careInstructions:
    private fun extractWateringTips(careInstructions: String?): String? {
        return careInstructions?.lines()
            ?.find { it.contains("podlew", ignoreCase = true) || it.contains("water", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
    }

    private fun extractFertilizingTips(careInstructions: String?): String? {
        return careInstructions?.lines()
            ?.find { it.contains("nawóz", ignoreCase = true) || it.contains("fertil", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
    }

    private fun extractPruningNotes(careInstructions: String?): String? {
        return careInstructions?.lines()
            ?.find { it.contains("przycina", ignoreCase = true) || it.contains("prun", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
    }

    // ==================== GROWTH PHASE ====================

    private fun GrowthPhaseData.toDomain(): GrowthPhase {
        return GrowthPhase(
            id = this.id,
            phaseName = this.phaseName.toDomain(),
            displayName = this.displayName,
            averageDurationRange = Pair(
                this.averageDurationDays.first,
                this.averageDurationDays.last
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
            averageDurationDays = IntRange(               // ← Tworzysz IntRange
                this.averageDurationRange.first,          // ← start zakresu
                this.averageDurationRange.second          // ← koniec zakresu
            ),
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
            triggerDayOffset = this.triggerDay,
            priority = this.priority.toDomain()
        )
    }

    private fun AutoTask.toEntity(): AutoTaskData {
        return AutoTaskData(
            taskTitle = this.taskTitle,
            taskDescription = this.taskDescription,
            taskType = this.taskType.toEntity(),
            triggerDay = this.triggerDayOffset,
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
