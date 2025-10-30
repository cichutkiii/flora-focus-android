package pl.preclaw.florafocus.data.repository

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.AuthResult
import pl.preclaw.florafocus.domain.model.User
import pl.preclaw.florafocus.domain.model.UserSettings

/**
 * Repository interface for Authentication operations
 */
interface AuthRepository {

    // ==================== AUTHENTICATION ====================

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResult

    /**
     * Sign up with email and password
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): AuthResult

    /**
     * Sign in with Google
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult

    /**
     * Sign out
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Get current user ID
     */
    suspend fun getCurrentUserId(): String?

    // ==================== USER PROFILE ====================

    /**
     * Get current user
     */
    suspend fun getCurrentUser(): User?

    /**
     * Get current user as Flow
     */
    fun getCurrentUserFlow(): Flow<User?>

    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: String): User?

    /**
     * Update user profile
     */
    suspend fun updateUserProfile(
        displayName: String?,
        photoUrl: String?
    ): Result<Unit>

    /**
     * Update user email
     */
    suspend fun updateUserEmail(newEmail: String): Result<Unit>

    /**
     * Update user password
     */
    suspend fun updateUserPassword(newPassword: String): Result<Unit>

    /**
     * Delete user account
     */
    suspend fun deleteUserAccount(): Result<Unit>

    // ==================== USER SETTINGS ====================

    /**
     * Get user settings
     */
    suspend fun getUserSettings(userId: String): UserSettings?

    /**
     * Get user settings as Flow
     */
    fun getUserSettingsFlow(userId: String): Flow<UserSettings?>

    /**
     * Update user settings
     */
    suspend fun updateUserSettings(userId: String, settings: UserSettings): Result<Unit>

    /**
     * Update specific setting
     */
    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean): Result<Unit>
    suspend fun updateReminderTime(userId: String, time: String): Result<Unit>
    suspend fun updateDarkMode(userId: String, enabled: Boolean): Result<Unit>
    suspend fun updatePreferredUnits(userId: String, units: String): Result<Unit>
    suspend fun updateWeatherAlerts(userId: String, enabled: Boolean): Result<Unit>
}
