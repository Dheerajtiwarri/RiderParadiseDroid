package com.droid.riderparadise.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local-only OTP record. Stores the salted hash of the code — never the plaintext. */
@Entity(tableName = "otp")
data class OtpEntity(
    @PrimaryKey val phone: String,
    val codeHash: String,
    val salt: String,
    val expiresAt: Long,
    val attempts: Int,
    val createdAt: Long,
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val displayName: String,
    val bikeType: String?,
    val avatarUrl: String?,
    val colorHex: String,
    val shareLocation: Boolean,
    val allowContacts: Boolean,
    val createdAt: Long,
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val initials: String,
    val category: String,
    val privacy: String,
    val riderCount: Int,
    val distanceKm: Double?,
    val trending: Boolean,
    val gradientStartHex: String,
    val gradientEndHex: String,
)

/** Current user's membership of a group (joined/requested). */
@Entity(tableName = "memberships", primaryKeys = ["userId", "groupId"])
data class MembershipEntity(
    val userId: String,
    val groupId: String,
    val role: String,
    val status: String,
    val joinedAt: Long,
)
