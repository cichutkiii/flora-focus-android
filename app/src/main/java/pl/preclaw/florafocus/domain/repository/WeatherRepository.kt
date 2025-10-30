package pl.preclaw.florafocus.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.*

/**
 * Repository interface for Weather operations
 */
interface WeatherRepository {

    // ==================== CURRENT WEATHER ====================

    /**
     * Get current weather for location
     */
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Result<Weather>

    /**
     * Get current weather as Flow (reactive updates)
     */
    fun getCurrentWeatherFlow(
        latitude: Double,
        longitude: Double
    ): Flow<Weather?>

    // ==================== FORECAST ====================

    /**
     * Get weather forecast
     * @param days Number of days to forecast (1-14)
     */
    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        days: Int = 7
    ): Result<WeatherForecast>

    /**
     * Get hourly forecast for today
     */
    suspend fun getHourlyForecast(
        latitude: Double,
        longitude: Double
    ): Result<List<Weather>>

    // ==================== ALERTS ====================

    /**
     * Get weather alerts for location
     */
    suspend fun getWeatherAlerts(
        latitude: Double,
        longitude: Double,
        userId: String
    ): Result<List<WeatherAlert>>

    /**
     * Get weather alerts as Flow
     */
    fun getWeatherAlertsFlow(
        latitude: Double,
        longitude: Double,
        userId: String
    ): Flow<List<WeatherAlert>>

    /**
     * Dismiss alert
     */
    suspend fun dismissAlert(alertId: String): Result<Unit>

    // ==================== HISTORICAL DATA ====================

    /**
     * Get historical weather data
     */
    suspend fun getHistoricalWeather(
        latitude: Double,
        longitude: Double,
        startDate: Long,
        endDate: Long
    ): Result<List<Weather>>

    // ==================== LOCATION ====================

    /**
     * Get weather for saved location
     */
    suspend fun getWeatherForSavedLocation(locationId: String): Result<Weather>

    /**
     * Save location for weather tracking
     */
    suspend fun saveLocation(
        locationId: String,
        name: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit>

    /**
     * Get saved locations
     */
    suspend fun getSavedLocations(userId: String): List<SavedLocation>

    /**
     * Delete saved location
     */
    suspend fun deleteLocation(locationId: String): Result<Unit>

    // ==================== ANALYSIS ====================

    /**
     * Check if it's a good day for specific task
     */
    suspend fun isGoodDayForTask(
        latitude: Double,
        longitude: Double,
        taskType: TaskType
    ): Result<Boolean>

    /**
     * Get best days for task in next N days
     */
    suspend fun getBestDaysForTask(
        latitude: Double,
        longitude: Double,
        taskType: TaskType,
        days: Int = 7
    ): Result<List<Long>>

    /**
     * Calculate frost risk
     */
    suspend fun calculateFrostRisk(
        latitude: Double,
        longitude: Double
    ): Result<FrostRisk>
}

/**
 * Saved location
 */
data class SavedLocation(
    val id: String,
    val userId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean
)

/**
 * Frost risk assessment
 */
data class FrostRisk(
    val riskLevel: RiskLevel,
    val expectedDate: Long?,
    val minimumTemperature: Float,
    val recommendations: List<String>
)

enum class RiskLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH
}
