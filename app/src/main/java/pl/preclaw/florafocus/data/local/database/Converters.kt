package pl.preclaw.florafocus.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.preclaw.florafocus.data.local.entities.AutoTaskData
import pl.preclaw.florafocus.data.local.entities.BedType
import pl.preclaw.florafocus.data.local.entities.CellHistoryRecord
import pl.preclaw.florafocus.data.local.entities.DecorationType
import pl.preclaw.florafocus.data.local.entities.GardenObject2D
import pl.preclaw.florafocus.data.local.entities.GardenObjectType
import pl.preclaw.florafocus.data.local.entities.GrowthDifficulty
import pl.preclaw.florafocus.data.local.entities.GrowthPhaseData
import pl.preclaw.florafocus.data.local.entities.GrowthPhaseName
import pl.preclaw.florafocus.data.local.entities.HarvestQuality
import pl.preclaw.florafocus.data.local.entities.HarvestUnit
import pl.preclaw.florafocus.data.local.entities.HarvestUsage
import pl.preclaw.florafocus.data.local.entities.HealthStatus
import pl.preclaw.florafocus.data.local.entities.InterventionType
import pl.preclaw.florafocus.data.local.entities.LightRequirements
import pl.preclaw.florafocus.data.local.entities.PlantType
import pl.preclaw.florafocus.data.local.entities.PropagationMethod
import pl.preclaw.florafocus.data.local.entities.PropagationStatus
import pl.preclaw.florafocus.data.local.entities.RecurrencePattern
import pl.preclaw.florafocus.data.local.entities.RotationWarning
import pl.preclaw.florafocus.data.local.entities.RotationWarningType
import pl.preclaw.florafocus.data.local.entities.Season
import pl.preclaw.florafocus.data.local.entities.SunExposure
import pl.preclaw.florafocus.data.local.entities.Symptom
import pl.preclaw.florafocus.data.local.entities.TaskPriority
import pl.preclaw.florafocus.data.local.entities.TaskType
import pl.preclaw.florafocus.data.local.entities.WateringFrequency
import java.util.Date

/**
 * Room TypeConverters for complex data types
 *
 * Add more converters as needed for your entities
 */

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

    // ==================== SYMPTOM LIST ====================

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

    // ==================== GARDEN OBJECTS ====================

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

    // ==================== ENUMS - PlantCatalog ====================

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
    fun fromGrowthPhaseName(value: GrowthPhaseName?): String? = value?.name

    @TypeConverter
    fun toGrowthPhaseName(value: String?): GrowthPhaseName? = value?.let { GrowthPhaseName.valueOf(it) }

    @TypeConverter
    fun fromTaskType(value: TaskType?): String? = value?.name

    @TypeConverter
    fun toTaskType(value: String?): TaskType? = value?.let { TaskType.valueOf(it) }

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority?): String? = value?.name

    @TypeConverter
    fun toTaskPriority(value: String?): TaskPriority? = value?.let { TaskPriority.valueOf(it) }

    // ==================== ENUMS - UserPlant ====================

    @TypeConverter
    fun fromHealthStatus(value: HealthStatus?): String? = value?.name

    @TypeConverter
    fun toHealthStatus(value: String?): HealthStatus? = value?.let { HealthStatus.valueOf(it) }

    @TypeConverter
    fun fromInterventionType(value: InterventionType?): String? = value?.name

    @TypeConverter
    fun toInterventionType(value: String?): InterventionType? = value?.let { InterventionType.valueOf(it) }

    @TypeConverter
    fun fromHarvestUnit(value: HarvestUnit?): String? = value?.name

    @TypeConverter
    fun toHarvestUnit(value: String?): HarvestUnit? = value?.let { HarvestUnit.valueOf(it) }

    @TypeConverter
    fun fromHarvestQuality(value: HarvestQuality?): String? = value?.name

    @TypeConverter
    fun toHarvestQuality(value: String?): HarvestQuality? = value?.let { HarvestQuality.valueOf(it) }

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

    // ==================== ENUMS - Task ====================

    @TypeConverter
    fun fromRecurrencePattern(value: RecurrencePattern?): String? = value?.name

    @TypeConverter
    fun toRecurrencePattern(value: String?): RecurrencePattern? = value?.let { RecurrencePattern.valueOf(it) }

    // ==================== ENUMS - Garden ====================

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

    @TypeConverter
    fun fromRotationWarningType(value: RotationWarningType?): String? = value?.name

    @TypeConverter
    fun toRotationWarningType(value: String?): RotationWarningType? = value?.let { RotationWarningType.valueOf(it) }
}