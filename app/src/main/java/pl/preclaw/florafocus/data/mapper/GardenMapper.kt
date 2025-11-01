package pl.preclaw.florafocus.data.mapper

import pl.preclaw.florafocus.data.local.entities.*
import pl.preclaw.florafocus.domain.model.*

/**
 * Mapper for Garden-related entities - POPRAWIONA WERSJA
 */
object GardenMapper {

    // ==================== GARDEN ====================

    fun toDomain(entity: GardenEntity, areas: List<GardenArea> = emptyList()): Garden {
        return Garden(
            id = entity.id,
            userId = entity.userId,
            name = entity.name,
            location = entity.location,
            dimensions = Dimensions(
                width = entity.totalWidth,
                height = entity.totalHeight,
                unit = "m"
            ),
            areas = areas,
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toGardenEntity(domain: Garden): GardenEntity {
        return GardenEntity(
            id = domain.id,
            userId = domain.userId,
            name = domain.name,
            location = domain.location,
            totalWidth = domain.dimensions.width,
            totalHeight = domain.dimensions.height,
            notes = domain.notes,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== GARDEN AREA ====================

    fun toDomain(
        entity: GardenAreaEntity,
        objects: List<AreaObject> = emptyList()
    ): GardenArea {
        return GardenArea(
            id = entity.id,
            gardenId = entity.gardenId,
            name = entity.name,
            position = entity.position.toDomain(),
            size = entity.size.toDomain(),
            rotation = entity.rotation,
            areaType = entity.areaType.toDomain(),
            sunExposure = entity.sunExposure?.toDomain(),
            soilType = entity.soilType,
            soilPH = entity.soilPH,
            objects = objects,
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toAreaEntity(domain: GardenArea): GardenAreaEntity {
        return GardenAreaEntity(
            id = domain.id,
            gardenId = domain.gardenId,
            name = domain.name,
            position = domain.position.toEntity(),
            size = domain.size.toEntity(),
            rotation = domain.rotation,
            areaType = domain.areaType.toEntity(),
            sunExposure = domain.sunExposure?.toEntity(),
            soilType = domain.soilType,
            soilPH = domain.soilPH,
            notes = domain.notes,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== BED ====================

    fun bedToDomain(entity: BedEntity, cells: Map<Pair<Int, Int>, BedCell>): Bed {
        return Bed(
            id = entity.id,
            areaId = entity.areaId,
            name = entity.name,
            position = entity.position.toDomain(),
            size = entity.size.toDomain(),
            rotation = entity.rotation,
            bedType = entity.bedType.toDomain(),
            gridRows = entity.gridRows,
            gridColumns = entity.gridColumns,
            cells = cells,
            soilPH = entity.soilPH,
            sunExposure = entity.sunExposure?.toDomain(),
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toBedEntity(domain: Bed): BedEntity {
        return BedEntity(
            id = domain.id,
            areaId = domain.areaId,
            name = domain.name,
            position = domain.position.toEntity(),
            size = domain.size.toEntity(),
            rotation = domain.rotation,
            bedType = domain.bedType.toEntity(),
            gridRows = domain.gridRows,
            gridColumns = domain.gridColumns,
            soilPH = domain.soilPH,
            sunExposure = domain.sunExposure?.toEntity(),
            notes = domain.notes,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== BED CELL ====================

    fun toDomain(entity: BedCellEntity): BedCell {
        return BedCell(
            bedId = entity.bedId,
            currentPlantId = entity.currentPlantId,
            soilConditions = entity.soilConditions,
            sunExposure = entity.sunExposure?.toDomain(),
            plantingHistory = entity.plantingHistory.map { it.toDomain() },
            notes = entity.notes,
            updatedAt = entity.updatedAt
        )
    }

    fun toCellEntity(domain: BedCell, cellId: String, rowIndex: Int, columnIndex: Int): BedCellEntity {
        return BedCellEntity(
            id = cellId,
            bedId = domain.bedId,
            rowIndex = rowIndex,
            columnIndex = columnIndex,
            currentPlantId = domain.currentPlantId,
            soilConditions = domain.soilConditions,
            sunExposure = domain.sunExposure?.toEntity(),
            plantingHistory = domain.plantingHistory.map { it.toEntity() },
            notes = domain.notes,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== DECORATIONS ====================

    fun toAreaDecoration(entity: AreaDecorationEntity): AreaDecoration {
        return AreaDecoration(
            id = entity.id,
            areaId = entity.areaId,
            decorationType = entity.decorationType.toDomain(),
            name = entity.name,
            position = entity.position.toDomain(),
            size = entity.size.toDomain(),
            rotation = entity.rotation,
            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }

    fun toDecorationEntity(domain: AreaDecoration): AreaDecorationEntity {
        return AreaDecorationEntity(
            id = domain.id,
            areaId = domain.areaId,
            decorationType = domain.decorationType.toEntity(),
            name = domain.name,
            position = domain.position.toEntity(),
            size = domain.size.toEntity(),
            rotation = domain.rotation,
            notes = domain.notes,
            createdAt = domain.createdAt
        )
    }

    // ==================== ROTATION PLAN ====================

    fun toDomain(entity: RotationPlanEntity): RotationPlan {
        return RotationPlan(
            id = entity.id,
            gardenId = entity.gardenId,
            planName = entity.planName,
            seasonYear = entity.seasonYear,
            seasonType = entity.seasonType.toDomain(),
            rotationGroups = entity.rotationGroups.map { it.toDomain() },
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toRotationPlanEntity(domain: RotationPlan): RotationPlanEntity {
        return RotationPlanEntity(
            id = domain.id,
            gardenId = domain.gardenId,
            planName = domain.planName,
            seasonYear = domain.seasonYear,
            seasonType = domain.seasonType.toEntity(),
            rotationGroups = domain.rotationGroups.map { it.toEntity() },
            notes = domain.notes,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== VALUE OBJECTS ====================

    private fun pl.preclaw.florafocus.data.local.entities.Position2D.toDomain() =
        pl.preclaw.florafocus.domain.model.Position2D(x = this.x, y = this.y)

    private fun pl.preclaw.florafocus.domain.model.Position2D.toEntity() =
        pl.preclaw.florafocus.data.local.entities.Position2D(x = this.x, y = this.y)

    private fun pl.preclaw.florafocus.data.local.entities.Size2D.toDomain() =
        pl.preclaw.florafocus.domain.model.Size2D(width = this.width, height = this.height)

    private fun pl.preclaw.florafocus.domain.model.Size2D.toEntity() =
        pl.preclaw.florafocus.data.local.entities.Size2D(width = this.width, height = this.height)

    private fun PlantingHistoryData.toDomain() =
        PlantingHistoryEntry(
            plantId = this.plantId,
            plantFamily = this.plantFamily,
            plantedDate = this.plantedDate,
            harvestedDate = this.harvestedDate
        )

    private fun PlantingHistoryEntry.toEntity() =
        PlantingHistoryData(
            plantId = this.plantId,
            plantFamily = this.plantFamily,
            plantedDate = this.plantedDate,
            harvestedDate = this.harvestedDate
        )

    private fun RotationGroupData.toDomain() =
        RotationGroup(
            groupName = this.groupName,
            plantFamily = this.plantFamily,
            assignedBeds = this.assignedBeds,
            rotationOrder = this.rotationOrder
        )

    private fun RotationGroup.toEntity() =
        RotationGroupData(
            groupName = this.groupName,
            plantFamily = this.plantFamily,
            assignedBeds = this.assignedBeds,
            rotationOrder = this.rotationOrder
        )

    // ==================== ENUM CONVERSIONS ====================

    private fun pl.preclaw.florafocus.data.local.entities.AreaType.toDomain() =
        pl.preclaw.florafocus.domain.model.AreaType.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.AreaType.toEntity() =
        pl.preclaw.florafocus.data.local.entities.AreaType.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.BedType.toDomain() =
        pl.preclaw.florafocus.domain.model.BedType.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.BedType.toEntity() =
        pl.preclaw.florafocus.data.local.entities.BedType.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.SunExposure.toDomain() =
        pl.preclaw.florafocus.domain.model.SunExposure.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.SunExposure.toEntity() =
        pl.preclaw.florafocus.data.local.entities.SunExposure.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.DecorationType.toDomain() =
        pl.preclaw.florafocus.domain.model.DecorationType.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.DecorationType.toEntity() =
        pl.preclaw.florafocus.data.local.entities.DecorationType.valueOf(this.name)

    private fun pl.preclaw.florafocus.data.local.entities.SeasonType.toDomain() =
        pl.preclaw.florafocus.domain.model.SeasonType.valueOf(this.name)

    private fun pl.preclaw.florafocus.domain.model.SeasonType.toEntity() =
        pl.preclaw.florafocus.data.local.entities.SeasonType.valueOf(this.name)
}