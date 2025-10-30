package pl.preclaw.florafocus.domain.model

/**
 * Domain model for User
 */
data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val settings: UserSettings,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * User settings
 */
data class UserSettings(
    val notificationsEnabled: Boolean = true,
    val reminderTime: String = "09:00", // HH:mm format
    val darkModeEnabled: Boolean = false,
    val preferredUnits: UnitSystem = UnitSystem.METRIC,
    val moonCalendarEnabled: Boolean = false,
    val weatherAlertsEnabled: Boolean = true,
    val language: String = "pl"
)

/**
 * Authentication result
 */
sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String, val code: String? = null) : AuthResult()
}

// ==================== ENUMS ====================

enum class UnitSystem {
    METRIC,
    IMPERIAL
}
