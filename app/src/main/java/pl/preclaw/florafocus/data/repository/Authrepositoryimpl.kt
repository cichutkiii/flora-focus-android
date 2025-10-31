package pl.preclaw.florafocus.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pl.preclaw.florafocus.domain.model.AuthResult
import pl.preclaw.florafocus.domain.model.User
import pl.preclaw.florafocus.domain.model.UserSettings
import pl.preclaw.florafocus.domain.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository using Firebase Authentication
 *
 * Handles user authentication, profile management, and settings
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_USER_SETTINGS = "user_settings"
        private const val TAG = "AuthRepository"
    }

    // ==================== AUTHENTICATION ====================

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                Timber.tag(TAG).d("Sign in successful: ${user.uid}")
                AuthResult.Success(mapFirebaseUser(user))
            } else {
                Timber.tag(TAG).w("Sign in failed: User is null")
                AuthResult.Error("Authentication failed")
            }
        } catch (e: FirebaseAuthException) {
            Timber.tag(TAG).e(e, "Sign in error")
            AuthResult.Error(getAuthErrorMessage(e))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected sign in error")
            AuthResult.Error("An unexpected error occurred")
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): AuthResult {
        return try {
            // Create user account
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Update profile with display name
                if (displayName != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    user.updateProfile(profileUpdates).await()
                }

                // Create user document in Firestore
                val userData = mapOf(
                    "uid" to user.uid,
                    "email" to user.email,
                    "displayName" to (displayName ?: ""),
                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection(COLLECTION_USERS)
                    .document(user.uid)
                    .set(userData)
                    .await()

                // Create default user settings
                val defaultSettings = UserSettings(
                    userId = user.uid,
                    notificationsEnabled = true,
                    reminderTime = "08:00",
                    darkModeEnabled = false,
                    preferredUnits = "METRIC",
                    moonCalendarEnabled = false,
                    weatherAlertsEnabled = true
                )

                firestore.collection(COLLECTION_USER_SETTINGS)
                    .document(user.uid)
                    .set(defaultSettings)
                    .await()

                Timber.tag(TAG).d("Sign up successful: ${user.uid}")
                AuthResult.Success(mapFirebaseUser(user))
            } else {
                Timber.tag(TAG).w("Sign up failed: User is null")
                AuthResult.Error("Registration failed")
            }
        } catch (e: FirebaseAuthException) {
            Timber.tag(TAG).e(e, "Sign up error")
            AuthResult.Error(getAuthErrorMessage(e))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected sign up error")
            AuthResult.Error("An unexpected error occurred")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                // Check if this is a new user
                if (result.additionalUserInfo?.isNewUser == true) {
                    // Create user document
                    val userData = mapOf(
                        "uid" to user.uid,
                        "email" to user.email,
                        "displayName" to (user.displayName ?: ""),
                        "photoUrl" to (user.photoUrl?.toString() ?: ""),
                        "createdAt" to System.currentTimeMillis(),
                        "updatedAt" to System.currentTimeMillis()
                    )

                    firestore.collection(COLLECTION_USERS)
                        .document(user.uid)
                        .set(userData)
                        .await()

                    // Create default settings
                    val defaultSettings = UserSettings(
                        userId = user.uid,
                        notificationsEnabled = true,
                        reminderTime = "08:00",
                        darkModeEnabled = false,
                        preferredUnits = "METRIC",
                        moonCalendarEnabled = false,
                        weatherAlertsEnabled = true
                    )

                    firestore.collection(COLLECTION_USER_SETTINGS)
                        .document(user.uid)
                        .set(defaultSettings)
                        .await()
                }

                Timber.tag(TAG).d("Google sign in successful: ${user.uid}")
                AuthResult.Success(mapFirebaseUser(user))
            } else {
                Timber.tag(TAG).w("Google sign in failed: User is null")
                AuthResult.Error("Google authentication failed")
            }
        } catch (e: FirebaseAuthException) {
            Timber.tag(TAG).e(e, "Google sign in error")
            AuthResult.Error(getAuthErrorMessage(e))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected Google sign in error")
            AuthResult.Error("An unexpected error occurred")
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Timber.tag(TAG).d("Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Sign out error")
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Timber.tag(TAG).d("Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Timber.tag(TAG).e(e, "Password reset error")
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected password reset error")
            Result.failure(e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    // ==================== USER PROFILE ====================

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null

        return try {
            val doc = firestore.collection(COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            if (doc.exists()) {
                User(
                    uid = doc.getString("uid") ?: firebaseUser.uid,
                    email = doc.getString("email") ?: firebaseUser.email ?: "",
                    displayName = doc.getString("displayName") ?: firebaseUser.displayName,
                    photoUrl = doc.getString("photoUrl") ?: firebaseUser.photoUrl?.toString(),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            } else {
                mapFirebaseUser(firebaseUser)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting current user")
            mapFirebaseUser(firebaseUser)
        }
    }

    override fun getCurrentUserFlow(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(user?.let { mapFirebaseUser(it) })
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val doc = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            if (doc.exists()) {
                User(
                    uid = doc.getString("uid") ?: userId,
                    email = doc.getString("email") ?: "",
                    displayName = doc.getString("displayName"),
                    photoUrl = doc.getString("photoUrl"),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting user by ID")
            null
        }
    }

    override suspend fun updateUserProfile(displayName: String?, photoUrl: String?): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No user logged in"))

            // Update Firebase Auth profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .apply {
                    displayName?.let { setDisplayName(it) }
                    photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
                }
                .build()

            user.updateProfile(profileUpdates).await()

            // Update Firestore document
            val updates = mutableMapOf<String, Any>(
                "updatedAt" to System.currentTimeMillis()
            )
            displayName?.let { updates["displayName"] = it }
            photoUrl?.let { updates["photoUrl"] = it }

            firestore.collection(COLLECTION_USERS)
                .document(user.uid)
                .update(updates)
                .await()

            Timber.tag(TAG).d("Profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating profile")
            Result.failure(e)
        }
    }

    override suspend fun updateUserEmail(newEmail: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No user logged in"))

            user.updateEmail(newEmail).await()

            // Update Firestore
            firestore.collection(COLLECTION_USERS)
                .document(user.uid)
                .update(
                    mapOf(
                        "email" to newEmail,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()

            Timber.tag(TAG).d("Email updated successfully")
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Timber.tag(TAG).e(e, "Error updating email")
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected error updating email")
            Result.failure(e)
        }
    }

    override suspend fun updateUserPassword(newPassword: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No user logged in"))

            user.updatePassword(newPassword).await()

            Timber.tag(TAG).d("Password updated successfully")
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Timber.tag(TAG).e(e, "Error updating password")
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected error updating password")
            Result.failure(e)
        }
    }

    override suspend fun deleteUserAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No user logged in"))
            val userId = user.uid

            // Delete Firestore data
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .delete()
                .await()

            firestore.collection(COLLECTION_USER_SETTINGS)
                .document(userId)
                .delete()
                .await()

            // Delete Firebase Auth account
            user.delete().await()

            Timber.tag(TAG).d("User account deleted successfully")
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Timber.tag(TAG).e(e, "Error deleting account")
            Result.failure(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected error deleting account")
            Result.failure(e)
        }
    }

    // ==================== USER SETTINGS ====================

    override suspend fun getUserSettings(userId: String): UserSettings? {
        return try {
            val doc = firestore.collection(COLLECTION_USER_SETTINGS)
                .document(userId)
                .get()
                .await()

            if (doc.exists()) {
                UserSettings(
                    userId = userId,
                    notificationsEnabled = doc.getBoolean("notificationsEnabled") ?: true,
                    reminderTime = doc.getString("reminderTime") ?: "08:00",
                    darkModeEnabled = doc.getBoolean("darkModeEnabled") ?: false,
                    preferredUnits = doc.getString("preferredUnits") ?: "METRIC",
                    moonCalendarEnabled = doc.getBoolean("moonCalendarEnabled") ?: false,
                    weatherAlertsEnabled = doc.getBoolean("weatherAlertsEnabled") ?: true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting user settings")
            null
        }
    }

    override fun getUserSettingsFlow(userId: String): Flow<UserSettings?> = callbackFlow {
        val listener = firestore.collection(COLLECTION_USER_SETTINGS)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.tag(TAG).e(error, "Error listening to settings")
                    trySend(null)
                    return@addSnapshotListener
                }

                val settings = snapshot?.takeIf { it.exists() }?.let { doc ->
                    UserSettings(
                        userId = userId,
                        notificationsEnabled = doc.getBoolean("notificationsEnabled") ?: true,
                        reminderTime = doc.getString("reminderTime") ?: "08:00",
                        darkModeEnabled = doc.getBoolean("darkModeEnabled") ?: false,
                        preferredUnits = doc.getString("preferredUnits") ?: "METRIC",
                        moonCalendarEnabled = doc.getBoolean("moonCalendarEnabled") ?: false,
                        weatherAlertsEnabled = doc.getBoolean("weatherAlertsEnabled") ?: true
                    )
                }

                trySend(settings)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun updateUserSettings(userId: String, settings: UserSettings): Result<Unit> {
        return try {
            val settingsMap = mapOf(
                "notificationsEnabled" to settings.notificationsEnabled,
                "reminderTime" to settings.reminderTime,
                "darkModeEnabled" to settings.darkModeEnabled,
                "preferredUnits" to settings.preferredUnits,
                "moonCalendarEnabled" to settings.moonCalendarEnabled,
                "weatherAlertsEnabled" to settings.weatherAlertsEnabled
            )

            firestore.collection(COLLECTION_USER_SETTINGS)
                .document(userId)
                .set(settingsMap)
                .await()

            Timber.tag(TAG).d("Settings updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating settings")
            Result.failure(e)
        }
    }

    override suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean): Result<Unit> {
        return updateSingleSetting(userId, "notificationsEnabled", enabled)
    }

    override suspend fun updateReminderTime(userId: String, time: String): Result<Unit> {
        return updateSingleSetting(userId, "reminderTime", time)
    }

    override suspend fun updateDarkMode(userId: String, enabled: Boolean): Result<Unit> {
        return updateSingleSetting(userId, "darkModeEnabled", enabled)
    }

    override suspend fun updatePreferredUnits(userId: String, units: String): Result<Unit> {
        return updateSingleSetting(userId, "preferredUnits", units)
    }

    override suspend fun updateWeatherAlerts(userId: String, enabled: Boolean): Result<Unit> {
        return updateSingleSetting(userId, "weatherAlertsEnabled", enabled)
    }

    // ==================== HELPERS ====================

    private suspend fun updateSingleSetting(userId: String, field: String, value: Any): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_USER_SETTINGS)
                .document(userId)
                .update(field, value)
                .await()

            Timber.tag(TAG).d("Setting $field updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error updating setting $field")
            Result.failure(e)
        }
    }

    private fun mapFirebaseUser(firebaseUser: com.google.firebase.auth.FirebaseUser): User {
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl?.toString(),
            createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
            updatedAt = firebaseUser.metadata?.lastSignInTimestamp ?: System.currentTimeMillis()
        )
    }

    private fun getAuthErrorMessage(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address"
            "ERROR_WRONG_PASSWORD" -> "Wrong password"
            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again later"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak"
            "ERROR_REQUIRES_RECENT_LOGIN" -> "Please sign in again to continue"
            else -> "Authentication error: ${exception.message}"
        }
    }
}