package pl.preclaw.florafocus.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.preclaw.florafocus.data.local.entities.*
import java.util.Date

/**
 * Room TypeConverters for complex data types
 *
 * Handles conversion between custom types and Room-compatible types
 */
class Converters {

    private val gson = Gson()

    // ==================== BASIC TYPES ====================

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // ==================== LISTS ====================

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ==================== MAPS ====================

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    // ==================== PLANT CATALOG TYPES ====================

    @TypeConverter
    fun fromPlantType(value: PlantType?): String? = value?.name

    @TypeConverter
    fun toPlantType(value: String?): PlantType? = value?.let { PlantType.valueOf(it) }

    @TypeConverter
    fun fromLightRequirements(value: LightRequirements?): String? = value?.name

    @TypeConverter
    fun toLightRequirements(value: String?): LightRequirements? = value?.let { LightRequirements.valueOf(it) }

    @TypeConverter
    fun fromWateringFrequency(value: WateringFrequency?): String? = value?.name

    @TypeConverter
    fun toWateringFrequency(value: String?): WateringFrequency? = value?.let { WateringFrequency.valueOf(it) }

    @TypeConverter
    fun fromGrowthDifficulty(value: GrowthDifficulty?): String? = value?.name

    @TypeConverter
    fun toGrowthDifficulty(value: String?): GrowthDifficulty? = value?.let { GrowthDifficulty.valueOf(it) }

    @TypeConverter
    fun fromGrowthPhaseDataList(value: List<GrowthPhaseData>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGrowthPhaseDataList(value: String?): List<GrowthPhaseData>? {
        if (value == null) return null
        val listType = object : TypeToken<List<GrowthPhaseData>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromAutoTaskDataList(value: List<AutoTaskData>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAutoTaskDataList(value: String?): List<AutoTaskData>? {
        if (value == null) return null
        val listType = object : TypeToken<List<AutoTaskData>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ==================== HEALTH & STATUS ====================

    @TypeConverter
    fun fromHealthStatus(value: HealthStatus?): String? = value?.name

    @TypeConverter
    fun toHealthStatus(value: String?): HealthStatus? = value?.let { HealthStatus.valueOf(it) }

    @TypeConverter
    fun fromGrowthPhaseName(value: GrowthPhaseName?): String? = value?.name

    @TypeConverter
    fun toGrowthPhaseName(value: String?): GrowthPhaseName? = value?.let { GrowthPhaseName.valueOf(it) }

    @TypeConverter
    fun fromSymptomList(value: List<Symptom>?): String? {
        return value?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toSymptomList(value: String?): List<Symptom>? {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",").mapNotNull {
            try {
                Symptom.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    // ==================== TASKS ====================

    @TypeConverter
    fun fromTaskType(value: TaskType?): String? = value?.name

    @TypeConverter
    fun toTaskType(value: String?): TaskType? = value?.let { TaskType.valueOf(it) }

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority?): String? = value?.name

    @TypeConverter
    fun toTaskPriority(value: String?): TaskPriority? = value?.let { TaskPriority.valueOf(it) }

    @TypeConverter
    fun fromRecurrencePattern(value: RecurrencePattern?): String? = value?.name

    @TypeConverter
    fun toRecurrencePattern(value: String?): RecurrencePattern? = value?.let { RecurrencePattern.valueOf(it) }

    // ==================== INTERVENTIONS ====================

    @TypeConverter
    fun fromInterventionType(value: InterventionType?): String? = value?.name

    @TypeConverter
    fun toInterventionType(value: String?): InterventionType? = value?.let { InterventionType.valueOf(it) }

    @TypeConverter
    fun fromHarvestQuality(value: HarvestQuality?): String? = value?.name

    @TypeConverter
    fun toHarvestQuality(value: String?): HarvestQuality? = value?.let { HarvestQuality.valueOf(it) }

    @TypeConverter
    fun fromHarvestUnit(value: HarvestUnit?): String? = value?.name

    @TypeConverter
    fun toHarvestUnit(value: String?): HarvestUnit? = value?.let { HarvestUnit.valueOf(it) }

    @TypeConverter
    fun fromHarvestUsage(value: HarvestUsage?): String? = value?.name

    @TypeConverter
    fun toHarvestUsage(value: String?): HarvestUsage? = value?.let { HarvestUsage.valueOf(it) }

    @TypeConverter
    fun fromPropagationMethod(value: PropagationMethod?): String? = value?.name

    @TypeConverter
    fun toPropagationMethod(value: String?): PropagationMethod? = value?.let { PropagationMethod.valueOf(it) }

    @TypeConverter
    fun fromPropagationStatus(value: PropagationStatus?): String? = value?.name

    @TypeConverter
    fun toPropagationStatus(value: String?): PropagationStatus? = value?.let { PropagationStatus.valueOf(it) }

    // ==================== GARDEN MAPPING ====================

    @TypeConverter
    fun fromPosition2D(value: Position2D?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPosition2D(value: String?): Position2D? {
        if (value == null) return null
        return gson.fromJson(value, Position2D::class.java)
    }

    @TypeConverter
    fun fromSize2D(value: Size2D?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSize2D(value: String?): Size2D? {
        if (value == null) return null
        return gson.fromJson(value, Size2D::class.java)
    }

    @TypeConverter
    fun fromGardenObject2DList(value: List<GardenObject2D>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGardenObject2DList(value: String?): List<GardenObject2D>? {
        if (value == null) return null
        val listType = object : TypeToken<List<GardenObject2D>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromGardenObjectType(value: GardenObjectType?): String? = value?.name

    @TypeConverter
    fun toGardenObjectType(value: String?): GardenObjectType? = value?.let { GardenObjectType.valueOf(it) }

    @TypeConverter
    fun fromBedType(value: BedType?): String? = value?.name

    @TypeConverter
    fun toBedType(value: String?): BedType? = value?.let { BedType.valueOf(it) }

    @TypeConverter
    fun fromSunExposure(value: SunExposure?): String? = value?.name

    @TypeConverter
    fun toSunExposure(value: String?): SunExposure? = value?.let { SunExposure.valueOf(it) }

    @TypeConverter
    fun fromDecorationType(value: DecorationType?): String? = value?.name

    @TypeConverter
    fun toDecorationType(value: String?): DecorationType? = value?.let { DecorationType.valueOf(it) }

    @TypeConverter
    fun fromSeason(value: Season?): String? = value?.name

    @TypeConverter
    fun toSeason(value: String?): Season? = value?.let { Season.valueOf(it) }

    // ==================== ROTATION PLANNING ====================

    @TypeConverter
    fun fromRotationWarningType(value: RotationWarningType?): String? = value?.name

    @TypeConverter
    fun toRotationWarningType(value: String?): RotationWarningType? = value?.let { RotationWarningType.valueOf(it) }

    @TypeConverter
    fun fromCellHistoryRecordList(value: List<CellHistoryRecord>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCellHistoryRecordList(value: String?): List<CellHistoryRecord>? {
        if (value == null) return null
        val listType = object : TypeToken<List<CellHistoryRecord>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromRotationWarningList(value: List<RotationWarning>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toRotationWarningList(value: String?): List<RotationWarning>? {
        if (value == null) return null
        val listType = object : TypeToken<List<RotationWarning>>() {}.type
        return gson.fromJson(value, listType)
    }

    // ==================== RANGES ====================

    @TypeConverter
    fun fromIntRange(value: IntRange?): String? {
        return value?.let { "${it.first},${it.last}" }
    }

    @TypeConverter
    fun toIntRange(value: String?): IntRange? {
        if (value == null) return null
        val parts = value.split(",")
        return if (parts.size == 2) {
            try {
                IntRange(parts[0].toInt(), parts[1].toInt())
            } catch (e: NumberFormatException) {
                null
            }
        } else null
    }
}
