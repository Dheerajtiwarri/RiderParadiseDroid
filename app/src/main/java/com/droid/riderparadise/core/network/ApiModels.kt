package com.droid.riderparadise.core.network

import com.squareup.moshi.JsonClass

/**
 * Standard backend response envelope: `{ "data": <payload>, "error": null }`
 * or `{ "data": null, "error": { "code", "message" } }`. See BACKEND.md §7.
 */
@JsonClass(generateAdapter = true)
data class ApiEnvelope<T>(
    val data: T? = null,
    val error: ApiError? = null,
)

@JsonClass(generateAdapter = true)
data class ApiError(
    val code: String? = null,
    val message: String? = null,
)

/** Generic acknowledgement payload for action endpoints (`{ ok }`, `{ id }`, or `{}`). */
@JsonClass(generateAdapter = true)
data class AckDto(
    val ok: Boolean? = null,
    val id: String? = null,
)

// ---- Auth ----

@JsonClass(generateAdapter = true)
data class RequestOtpBody(val phone: String)

@JsonClass(generateAdapter = true)
data class VerifyOtpBody(val phone: String, val code: String)

@JsonClass(generateAdapter = true)
data class AuthDto(
    val token: String,
    val user: UserDto? = null,
)

// ---- User ----

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String? = null,
    val phone: String? = null,
    val displayName: String? = null,
    val bikeType: String? = null,
    val avatarUrl: String? = null,
    val colorHex: String? = null,
    val shareLocation: Boolean? = null,
    val allowContacts: Boolean? = null,
    val createdAt: Long? = null,
)

@JsonClass(generateAdapter = true)
data class UpdateProfileBody(
    val displayName: String? = null,
    val bikeType: String? = null,
    val shareLocation: Boolean? = null,
    val allowContacts: Boolean? = null,
    val avatarUrl: String? = null,
)

// ---- Groups ----

@JsonClass(generateAdapter = true)
data class GroupDto(
    val id: String? = null,
    val name: String? = null,
    val initials: String? = null,
    val category: String? = null,
    val privacy: String? = null,
    val riderCount: Int? = null,
    val distanceKm: Double? = null,
    val trending: Boolean? = null,
    val gradientStartHex: String? = null,
    val gradientEndHex: String? = null,
    /** Caller's membership status relative to this group. */
    val membership: String? = null,
)

// ---- Rides ----

@JsonClass(generateAdapter = true)
data class RideDto(
    val id: String? = null,
    val groupId: String? = null,
    val groupName: String? = null,
    val title: String? = null,
    val whenLabel: String? = null,
    val difficulty: String? = null,
    val distanceKm: Int? = null,
    val climbM: Int? = null,
    val durationLabel: String? = null,
    val leaderName: String? = null,
    val startLocation: String? = null,
    val status: String? = null,
    val goingCount: Int? = null,
    val maybeCount: Int? = null,
    val myRsvp: String? = null,
)

@JsonClass(generateAdapter = true)
data class CreateRideBody(
    val groupId: String,
    val groupName: String,
    val title: String,
    val difficulty: String,
)

@JsonClass(generateAdapter = true)
data class RsvpBody(val response: String)

// ---- Chat ----

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    val id: String? = null,
    val senderName: String? = null,
    val senderInitials: String? = null,
    val colorHex: String? = null,
    val text: String? = null,
    val mine: Boolean? = null,
    val kind: String? = null,
    val createdAt: Long? = null,
)

@JsonClass(generateAdapter = true)
data class SendMessageBody(val text: String, val kind: String)

// ---- Feedback ----

@JsonClass(generateAdapter = true)
data class FeedbackBody(
    val type: String,
    val area: String,
    val body: String,
    val appVersion: String,
)
