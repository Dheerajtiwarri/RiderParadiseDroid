package com.droid.riderparadise.domain.model

enum class RideStatus { PLANNED, LOBBY, LIVE, COMPLETED }

enum class RsvpResponse { NONE, GOING, MAYBE, CANT }

enum class CheckInStatus { HERE, ON_THE_WAY, NOT_CHECKED_IN }

/** A planned/active/finished group ride. */
data class Ride(
    val id: String,
    val groupId: String,
    val groupName: String,
    val title: String,
    val whenLabel: String,
    val difficulty: String,
    val distanceKm: Int,
    val climbM: Int,
    val durationLabel: String,
    val leaderName: String,
    val startLocation: String,
    val status: RideStatus,
    val goingCount: Int,
    val maybeCount: Int,
    val myRsvp: RsvpResponse,
)

/** A rider participating in a ride (roster / live map / lobby check-in). */
data class RideParticipant(
    val id: String,
    val name: String,
    val initials: String,
    val colorHex: String,
    val isLeader: Boolean,
    val isYou: Boolean,
    val speedKmh: Int,
    val offsetLabel: String,
    val checkIn: CheckInStatus,
    val stale: Boolean,
)

enum class ChatKind { NORMAL, MACRO, SYSTEM }

data class ChatMessage(
    val id: String,
    val rideId: String,
    val senderName: String,
    val senderInitials: String,
    val colorHex: String,
    val text: String,
    val mine: Boolean,
    val kind: ChatKind,
    val createdAt: Long,
)

data class RideRecap(
    val rideId: String,
    val title: String,
    val dateLabel: String,
    val ridersFinished: Int,
    val combinedKm: Int,
    val distanceKm: Double,
    val movingTime: String,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val climbM: Int,
    val kcal: Int,
)

/** Rider in the Riders/Suggestions tabs (from contacts / recommendations). */
data class RiderContact(
    val id: String,
    val name: String,
    val initials: String,
    val colorHex: String,
    val subtitle: String,
    val onApp: Boolean,
    val following: Boolean,
)

enum class FeedbackType { SUGGESTION, PROBLEM }

data class Feedback(
    val type: FeedbackType,
    val area: String,
    val body: String,
    val appVersion: String,
)
