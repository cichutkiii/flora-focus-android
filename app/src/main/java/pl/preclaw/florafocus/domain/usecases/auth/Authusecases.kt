package pl.preclaw.florafocus.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.AuthResult
import pl.preclaw.florafocus.domain.model.User
import pl.preclaw.florafocus.domain.model.UserSettings
import pl.preclaw.florafocus.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Sign in with email and password
 */
class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return authRepository.signInWithEmail(email, password)
    }
}

/**
 * Sign up with email and password
 */
class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String?
    ): AuthResult {
        return authRepository.signUpWithEmail(email, password, displayName)
    }
}

/**
 * Sign in with Google
 */
class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult {
        return authRepository.signInWithGoogle(idToken)
    }
}

/**
 * Sign out
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}

/**
 * Send password reset email
 */
class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.sendPasswordResetEmail(email)
    }
}

/**
 * Check if user is authenticated
 */
class IsAuthenticatedUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return authRepository.isAuthenticated()
    }
}

/**
 * Get current user
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}

/**
 * Get current user as Flow
 */
class ObserveCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUserFlow()
    }
}

/**
 * Update user profile
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(displayName: String?, photoUrl: String?): Result<Unit> {
        return authRepository.updateUserProfile(displayName, photoUrl)
    }
}

/**
 * Get user settings
 */
class GetUserSettingsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String): UserSettings? {
        return authRepository.getUserSettings(userId)
    }
}

/**
 * Observe user settings as Flow
 */
class ObserveUserSettingsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(userId: String): Flow<UserSettings?> {
        return authRepository.getUserSettingsFlow(userId)
    }
}

/**
 * Update user settings
 */
class UpdateUserSettingsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String, settings: UserSettings): Result<Unit> {
        return authRepository.updateUserSettings(userId, settings)
    }
}

/**
 * Update dark mode setting
 */
class UpdateDarkModeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String, enabled: Boolean): Result<Unit> {
        return authRepository.updateDarkMode(userId, enabled)
    }
}

/**
 * Update notifications setting
 */
class UpdateNotificationsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String, enabled: Boolean): Result<Unit> {
        return authRepository.updateNotificationsEnabled(userId, enabled)
    }
}