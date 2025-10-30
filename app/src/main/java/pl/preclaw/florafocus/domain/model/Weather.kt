package pl.preclaw.florafocus.domain.model

/**
 * Domain model for Weather data
 */
data class Weather(
    val locationId: String,
    val date: Long,
    val temperature: Temperature,
    val precipitation: Float, // mm
    val humidity: Int, // percentage
    val windSpeed: Float, // km/h or mph
    val conditions: WeatherConditions,
    val uvIndex: Int?,
    val sunrise: Long?,
    val sunset: Long?
)

/**
 * Temperature data
 */
data class Temperature(
    val min: Float,
    val max: Float,
    val current: Float?,
    val unit: TemperatureUnit
)

/**
 * Weather conditions
 */
enum class WeatherConditions {
    SUNNY,
    PARTLY_CLOUDY,
    CLOUDY,
    RAINY,
    STORMY,
    SNOWY,
    FOGGY,
    WINDY
}

/**
 * Temperature unit
 */
enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

/**
 * Weather forecast
 */
data class WeatherForecast(
    val locationId: String,
    val dailyForecasts: List<DailyForecast>
)

/**
 * Daily forecast
 */
data class DailyForecast(
    val date: Long,
    val weather: Weather,
    val precipitationProbability: Int // percentage
)

/**
 * Weather alert
 */
data class WeatherAlert(
    val id: String,
    val alertType: AlertType,
    val severity: AlertSeverity,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val affectedPlants: List<String> // plant IDs
)

enum class AlertType {
    FROST,
    HEAT_WAVE,
    HEAVY_RAIN,
    DROUGHT,
    STORM,
    HAIL,
    STRONG_WIND
}

enum class AlertSeverity {
    INFO,
    WARNING,
    DANGER
}
