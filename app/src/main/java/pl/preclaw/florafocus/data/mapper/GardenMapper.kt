package pl.preclaw.florafocus.data.mapper

import pl.preclaw.florafocus.data.local.entities.*
import pl.preclaw.florafocus.domain.model.*

/**
 * Mapper for Garden-related entities
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

    fun toEntity(domain: Garden): GardenEntity {
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
        objects: List<AreaObject> = emptyList() // POPRAWKA
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
            objects = objects, // POPRAWKA: Zamiast beds i decorations osobno
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: GardenArea): GardenAreaEntity {
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

    fun toDomain(entity: BedEntity, cells: List<BedCell> = emptyList()): Bed {
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
            soilPH = entity.soilPH,
            sunExposure = entity.sunExposure?.toDomain(),
            cells = cells,
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Bed): BedEntity {
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
            id = entity.id,
            bedId = entity.bedId,
            rowIndex = entity.rowIndex,
            columnIndex = entity.columnIndex,
            currentPlantId = entity.currentPlantId,
            soilConditions = entity.soilConditions,
            sunExposure = entity.sunExposure?.toDomain(),
            plantingHistory = entity.plantingHistory.map { it.toDomain() },
            notes = entity.notes,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: BedCell): BedCellEntity {
        return BedCellEntity(
            id = domain.id,
            bedId = domain.bedId,
            rowIndex = domain.rowIndex,
            columnIndex = domain.columnIndex,
            currentPlantId = domain.currentPlantId,
            soilConditions = domain.soilConditions,
            sunExposure = domain.sunExposure?.toEntity(),
            plantingHistory = domain.plantingHistory.map { it.toEntity() },
            notes = domain.notes,
            updatedAt = domain.updatedAt
        )
    }

    // ==================== DECORATION ====================
    fun toDomain(entity: BedEntity, cells: List<BedCellEntity>): AreaObject.Bed {
        return AreaObject.Bed(
            id = entity.id,
            areaId = entity.areaId,
            name = entity.name,
            position = entity.position.toDomain(),
            size = entity.size.toDomain(),
            rotation = entity.rotation,
            bedType = entity.bedType.toDomain(),
            gridRows = entity.gridRows,
            gridColumns = entity.gridColumns,
            cells = cells.associate { // POPRAWKA: Konwersja do mapy
                Pair(it.rowIndex, it.columnIndex) to BedCell(
                    bedId = it.bedId,
                    currentPlantId = it.currentPlantId,
                    soilConditions = it.soilConditions,
                    sunExposure = it.sunExposure?.toDomain(),
                    plantingHistory = it.plantingHistory.map { history ->
                        PlantingHistoryEntry(
                            plantId = history.plantId,
                            plantFamily = history.plantFamily,
                            plantedDate = history.plantedDate,
                            harvestedDate = history.harvestedDate
                        )
                    },
                    notes = it.notes,
                    updatedAt = it.updatedAt
                )
            },
            soilPH = entity.soilPH,
            sunExposure = entity.sunExposure?.toDomain(),
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Convert AreaDecorationEntity to domain AreaObject.Decoration
     */
    fun toDomain(entity: AreaDecorationEntity): AreaObject.Decoration {
        return AreaObject.Decoration(
            id = entity.id,
            areaId = entity.areaId,
            position = entity.position.toDomain(),
            size = entity.size.toDomain(),
            rotation = entity.rotation,
            decorationType = entity.decorationType.toDomain(),
            name = null, // Jeśli nie ma w entity, dodaj pole
            metadata = entity.metadata,
            notes = null, // Dodaj jeśli potrzeba
            createdAt = entity.createdAt
        )
    }
    fun toDomain(entity: AreaDecorationEntity): AreaDecoration {
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

    fun toEntity(domain: AreaDecoration): AreaDecorationEntity {
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
    fun toEntity(domain: AreaObject.Bed): BedEntity {
        return BedEntity(
            id = domain.id,
            areaId = domain.areaId,
            name = domain.name,
            position = Position2D(domain.position.x, domain.position.y),
            size = Size2D(domain.size.width, domain.size.height),
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

    fun toCellEntities(bed: AreaObject.Bed): List<BedCellEntity> {
        return bed.cells.map { (position, cell) ->
            BedCellEntity(
                id = "${bed.id}_${position.first}_${position.second}", // Generate ID
                bedId = cell.bedId,
                rowIndex = position.first,
                columnIndex = position.second,
                currentPlantId = cell.currentPlantId,
                soilConditions = cell.soilConditions,
                sunExposure = cell.sunExposure?.toEntity(),
                plantingHistory = cell.plantingHistory.map { entry ->
                    CellHistoryRecord(
                        plantId = entry.plantId,
                        plantCatalogId = "", // Potrzebne z repository
                        plantFamily = entry.plantFamily,
                        plantedDate = entry.plantedDate,
                        harvestedDate = entry.harvestedDate,
                        season = 0, // Potrzebne obliczenie
                        yieldKg = null
                    )
                },
                notes = cell.notes,
                updatedAt = cell.updatedAt
            )
        }
    }
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

    fun toEntity(domain: RotationPlan): RotationPlanEntity {
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
