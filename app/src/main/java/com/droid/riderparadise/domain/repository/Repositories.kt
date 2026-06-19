package com.droid.riderparadise.domain.repository

import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.model.BikeType
import com.droid.riderparadise.domain.model.Group
import com.droid.riderparadise.domain.model.Rider
import kotlinx.coroutines.flow.Flow

/** Result of verifying an OTP code. */
sealed interface OtpResult {
    /** Verified. [isNewUser] true when no profile exists yet (route to onboarding). */
    data class Verified(val userId: String, val isNewUser: Boolean) : OtpResult
    data class Invalid(val attemptsLeft: Int) : OtpResult
    data object Expired : OtpResult
}

/**
 * Outcome of requesting a code. [code] is exposed only so the UI can fall back to showing it
 * when [deliveredAsNotification] is false (notifications blocked).
 */
data class OtpDelivery(val code: String, val deliveredAsNotification: Boolean)

interface OtpRepository {
    /** Generates a code, persists its hash locally, and delivers it via notification. */
    suspend fun requestOtp(phone: String): Resource<OtpDelivery>
    suspend fun verifyOtp(phone: String, code: String): Resource<OtpResult>
}

interface UserRepository {
    fun observeCurrentUser(): Flow<Rider?>
    suspend fun currentUserId(): String?
    suspend fun isOnboardingComplete(): Boolean
    suspend fun saveProfile(
        displayName: String,
        bikeType: BikeType,
        shareLocation: Boolean,
        allowContacts: Boolean,
    ): Resource<Rider>
    suspend fun signOut()
}

interface GroupRepository {
    /** Streams groups (from Room), kept fresh by a Firestore listener. */
    fun observeGroups(): Flow<List<Group>>
    fun observeJoinedGroups(): Flow<List<Group>>
    suspend fun refreshGroups(): Resource<Unit>
    suspend fun joinGroup(groupId: String): Resource<Unit>
    suspend fun requestToJoin(groupId: String): Resource<Unit>
}
