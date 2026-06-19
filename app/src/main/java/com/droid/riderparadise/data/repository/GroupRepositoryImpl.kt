package com.droid.riderparadise.data.repository

import com.droid.riderparadise.core.network.GroupDto
import com.droid.riderparadise.core.network.RiderParadiseApi
import com.droid.riderparadise.core.network.apiOrNull
import com.droid.riderparadise.core.network.apiResource
import com.droid.riderparadise.core.network.pollFlow
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.model.Group
import com.droid.riderparadise.domain.model.GroupCategory
import com.droid.riderparadise.domain.model.GroupPrivacy
import com.droid.riderparadise.domain.model.MembershipStatus
import com.droid.riderparadise.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: RiderParadiseApi,
) : GroupRepository {

    override fun observeGroups(): Flow<List<Group>> = pollFlow {
        apiOrNull { api.groups() }?.map { it.toGroup() } ?: emptyList()
    }

    override fun observeJoinedGroups(): Flow<List<Group>> = pollFlow {
        apiOrNull { api.joinedGroups() }?.map { it.toGroup() } ?: emptyList()
    }

    /** Backend owns seeding/refresh; a fetch validates connectivity. */
    override suspend fun refreshGroups(): Resource<Unit> =
        apiResource { api.groups() }.let {
            when (it) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> it
            }
        }

    override suspend fun joinGroup(groupId: String): Resource<Unit> = try {
        val env = api.joinGroup(groupId)
        if (env.error == null) Resource.Success(Unit)
        else Resource.Error(env.error.message ?: "Could not join group")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Could not join group", e)
    }

    override suspend fun requestToJoin(groupId: String): Resource<Unit> = try {
        val env = api.requestToJoin(groupId)
        if (env.error == null) Resource.Success(Unit)
        else Resource.Error(env.error.message ?: "Could not request to join")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Could not request to join", e)
    }
}

private fun GroupDto.toGroup(): Group {
    val groupName = name ?: ""
    return Group(
        id = id ?: "",
        name = groupName,
        initials = initials ?: groupName.take(2).uppercase(),
        category = GroupCategory.fromName(category),
        privacy = runCatching { GroupPrivacy.valueOf(privacy ?: "PUBLIC") }
            .getOrDefault(GroupPrivacy.PUBLIC),
        riderCount = riderCount ?: 0,
        distanceKm = distanceKm,
        trending = trending ?: false,
        gradientStartHex = gradientStartHex ?: "#1F9E4A",
        gradientEndHex = gradientEndHex ?: "#0C5C25",
        membership = runCatching { MembershipStatus.valueOf(membership ?: "NONE") }
            .getOrDefault(MembershipStatus.NONE),
    )
}
