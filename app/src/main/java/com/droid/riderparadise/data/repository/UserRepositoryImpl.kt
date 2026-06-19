package com.droid.riderparadise.data.repository

import com.droid.riderparadise.core.network.RiderParadiseApi
import com.droid.riderparadise.core.network.UpdateProfileBody
import com.droid.riderparadise.core.network.UserDto
import com.droid.riderparadise.core.network.apiOrNull
import com.droid.riderparadise.core.network.pollFlow
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.data.datastore.SessionStore
import com.droid.riderparadise.domain.model.BikeType
import com.droid.riderparadise.domain.model.Rider
import com.droid.riderparadise.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: RiderParadiseApi,
    private val session: SessionStore,
) : UserRepository {

    override fun observeCurrentUser(): Flow<Rider?> = pollFlow {
        if (session.token() == null) null
        else apiOrNull { api.me() }?.toRider()
    }

    override suspend fun currentUserId(): String? = session.userId()

    override suspend fun isOnboardingComplete(): Boolean = session.isOnboarded()

    override suspend fun saveProfile(
        displayName: String,
        bikeType: BikeType,
        shareLocation: Boolean,
        allowContacts: Boolean,
    ): Resource<Rider> = try {
        val env = api.updateMe(
            UpdateProfileBody(
                displayName = displayName,
                bikeType = bikeType.name,
                shareLocation = shareLocation,
                allowContacts = allowContacts,
            )
        )
        val user = env.data
        if (user != null) {
            session.setOnboarded(true)
            Resource.Success(user.toRider())
        } else {
            Resource.Error(env.error?.message ?: "Could not save profile")
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Could not save profile", e)
    }

    override suspend fun signOut() {
        runCatching { api.logout() }
        session.clear()
    }
}

internal fun UserDto.toRider(): Rider = Rider(
    id = id ?: "",
    phone = phone ?: "",
    displayName = displayName ?: "",
    bikeType = BikeType.fromName(bikeType),
    avatarUrl = avatarUrl,
    colorHex = colorHex ?: "#3B9DFF",
    shareLocation = shareLocation ?: false,
    allowContacts = allowContacts ?: false,
    createdAt = createdAt ?: 0L,
)
