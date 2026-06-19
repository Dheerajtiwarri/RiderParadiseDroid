package com.droid.riderparadise.data.local

import com.droid.riderparadise.domain.model.BikeType
import com.droid.riderparadise.domain.model.Group
import com.droid.riderparadise.domain.model.GroupCategory
import com.droid.riderparadise.domain.model.GroupPrivacy
import com.droid.riderparadise.domain.model.MembershipStatus
import com.droid.riderparadise.domain.model.Rider

fun UserEntity.toDomain(): Rider = Rider(
    id = id,
    phone = phone,
    displayName = displayName,
    bikeType = BikeType.fromName(bikeType),
    avatarUrl = avatarUrl,
    colorHex = colorHex,
    shareLocation = shareLocation,
    allowContacts = allowContacts,
    createdAt = createdAt,
)

fun Rider.toEntity(): UserEntity = UserEntity(
    id = id,
    phone = phone,
    displayName = displayName,
    bikeType = bikeType?.name,
    avatarUrl = avatarUrl,
    colorHex = colorHex,
    shareLocation = shareLocation,
    allowContacts = allowContacts,
    createdAt = createdAt,
)

fun GroupEntity.toDomain(membership: MembershipStatus = MembershipStatus.NONE): Group = Group(
    id = id,
    name = name,
    initials = initials,
    category = GroupCategory.fromName(category),
    privacy = if (privacy == GroupPrivacy.PRIVATE.name) GroupPrivacy.PRIVATE else GroupPrivacy.PUBLIC,
    riderCount = riderCount,
    distanceKm = distanceKm,
    trending = trending,
    gradientStartHex = gradientStartHex,
    gradientEndHex = gradientEndHex,
    membership = membership,
)
