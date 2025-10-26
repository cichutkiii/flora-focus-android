package pl.preclaw.florafocus.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Room TypeConverters for complex data types
 *
 * Add more converters as needed for your entities
 */
class Converters {

    private val gson = Gson()

    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // List<String> converters
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Map<String, String> converters
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    // Pair<Int, Int> converters (for BedCell position)
    @TypeConverter
    fun fromIntPair(value: Pair<Int, Int>?): String? {
        return value?.let { "${it.first},${it.second}" }
    }

    @TypeConverter
    fun toIntPair(value: String?): Pair<Int, Int>? {
        return value?.split(",")?.let {
            if (it.size == 2) Pair(it[0].toInt(), it[1].toInt()) else null
        }
    }

    // Add more converters as needed for enums and complex types
    // Example for Enums:
    /*
    @TypeConverter
    fun fromHealthStatus(value: HealthStatus?): String? {
        return value?.name
    }

    @TypeConverter
    fun toHealthStatus(value: String?): HealthStatus? {
        return value?.let { HealthStatus.valueOf(it) }
    }
    */
}