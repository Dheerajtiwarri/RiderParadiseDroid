package com.droid.riderparadise.domain.model

/** Bike categories offered during profile setup (from the design's chip list). */
enum class BikeType(val label: String) {
    ADVENTURE("Adventure Tourers"),
    SPORTS("Sports Naked / Street"),
    MTB("Mountain Bikes (MTB)"),
    ROAD("Road Bikes"),
    CRUISER("Cruisers");

    companion object {
        fun fromName(name: String?): BikeType? = entries.firstOrNull { it.name == name }
    }
}

/** A rider / app user. [colorHex] is the rider's identity color for avatars and live pucks. */
data class Rider(
    val id: String,
    val phone: String,
    val displayName: String,
    val bikeType: BikeType?,
    val avatarUrl: String?,
    val colorHex: String,
    val shareLocation: Boolean,
    val allowContacts: Boolean,
    val createdAt: Long,
)

enum class GroupCategory(val label: String) {
    ADVENTURE("Adventure"),
    SPORTS("Sports"),
    MTB("MTB"),
    ROAD("Road"),
    GRAVEL("Gravel"),
    EBIKE("E-bike"),
    CRUISER("Cruiser");

    companion object {
        fun fromName(name: String?): GroupCategory = entries.firstOrNull { it.name == name } ?: ROAD
    }
}

enum class GroupPrivacy { PUBLIC, PRIVATE }

/** Membership state of the current user relative to a group. */
enum class MembershipStatus { NONE, REQUESTED, JOINED }

data class Group(
    val id: String,
    val name: String,
    val initials: String,
    val category: GroupCategory,
    val privacy: GroupPrivacy,
    val riderCount: Int,
    val distanceKm: Double?,
    val trending: Boolean,
    val gradientStartHex: String,
    val gradientEndHex: String,
    val membership: MembershipStatus = MembershipStatus.NONE,
)
