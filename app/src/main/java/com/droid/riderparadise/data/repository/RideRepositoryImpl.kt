package com.droid.riderparadise.data.repository

import com.droid.riderparadise.core.network.ChatMessageDto
import com.droid.riderparadise.core.network.CreateRideBody
import com.droid.riderparadise.core.network.RideDto
import com.droid.riderparadise.core.network.RiderParadiseApi
import com.droid.riderparadise.core.network.RsvpBody
import com.droid.riderparadise.core.network.SendMessageBody
import com.droid.riderparadise.core.network.apiOrNull
import com.droid.riderparadise.core.network.pollFlow
import com.droid.riderparadise.domain.model.ChatKind
import com.droid.riderparadise.domain.model.ChatMessage
import com.droid.riderparadise.domain.model.CheckInStatus
import com.droid.riderparadise.domain.model.Ride
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.domain.model.RideRecap
import com.droid.riderparadise.domain.model.RideStatus
import com.droid.riderparadise.domain.model.RsvpResponse
import com.droid.riderparadise.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RideRepositoryImpl @Inject constructor(
    private val api: RiderParadiseApi,
) : RideRepository {

    override fun observeRides(): Flow<List<Ride>> = pollFlow {
        apiOrNull { api.rides() }?.map { it.toRide() } ?: emptyList()
    }

    override fun observeRide(rideId: String): Flow<Ride?> = pollFlow {
        apiOrNull { api.ride(rideId) }?.toRide()
    }

    /** Backend owns seeding. */
    override suspend fun ensureSeeded() = Unit

    override suspend fun setRsvp(rideId: String, response: RsvpResponse) {
        runCatching { api.rsvp(rideId, RsvpBody(response.name)) }
    }

    override suspend fun startRide(rideId: String) {
        runCatching { api.startRide(rideId) }
    }

    override suspend fun completeRide(rideId: String) {
        runCatching { api.completeRide(rideId) }
    }

    override suspend fun createRide(
        groupId: String,
        groupName: String,
        title: String,
        difficulty: String,
    ): String {
        val created = apiOrNull {
            api.createRide(CreateRideBody(groupId, groupName, title.ifBlank { "New ride" }, difficulty))
        }
        return created?.id ?: ""
    }

    override fun observeMessages(rideId: String): Flow<List<ChatMessage>> = pollFlow {
        apiOrNull { api.messages(rideId) }?.map { it.toChatMessage(rideId) } ?: emptyList()
    }

    override suspend fun sendMessage(rideId: String, text: String, kind: ChatKind) {
        if (text.isBlank()) return
        runCatching { api.sendMessage(rideId, SendMessageBody(text.trim(), kind.name)) }
    }

    // --- Simulated peer data (not backend-owned in this POC; see BACKEND.md §2/§7 roster/recap). ---

    override fun roster(rideId: String): List<RideParticipant> = listOf(
        RideParticipant("p_mv", "Mara V.", "MV", "#F5B53D", isLeader = true, isYou = false, speedKmh = 26, offsetLabel = "+180 m ahead", checkIn = CheckInStatus.HERE, stale = false),
        RideParticipant("p_you", "You", "AR", "#3B9DFF", isLeader = false, isYou = true, speedKmh = 24, offsetLabel = "on pace", checkIn = CheckInStatus.HERE, stale = false),
        RideParticipant("p_th", "Theo H.", "TH", "#25D98A", isLeader = false, isYou = false, speedKmh = 25, offsetLabel = "+90 m ahead", checkIn = CheckInStatus.ON_THE_WAY, stale = false),
        RideParticipant("p_sp", "Sam P.", "SP", "#A78BFA", isLeader = false, isYou = false, speedKmh = 23, offsetLabel = "−40 m", checkIn = CheckInStatus.NOT_CHECKED_IN, stale = false),
        RideParticipant("p_lp", "Lena P.", "LP", "#FF6B6B", isLeader = false, isYou = false, speedKmh = 0, offsetLabel = "−340 m · seen 1m ago", checkIn = CheckInStatus.NOT_CHECKED_IN, stale = true),
    )

    override fun recapFor(rideId: String): RideRecap = RideRecap(
        rideId = rideId,
        title = "Sunday Hills Loop",
        dateLabel = "COMPLETED · SUN OCT 5",
        ridersFinished = 6,
        combinedKm = 289,
        distanceKm = 48.2,
        movingTime = "2:14",
        avgSpeed = 21.4,
        maxSpeed = 54.7,
        climbM = 612,
        kcal = 1840,
    )
}

private fun RideDto.toRide(): Ride = Ride(
    id = id ?: "",
    groupId = groupId ?: "",
    groupName = groupName ?: "",
    title = title ?: "",
    whenLabel = whenLabel ?: "",
    difficulty = difficulty ?: "",
    distanceKm = distanceKm ?: 0,
    climbM = climbM ?: 0,
    durationLabel = durationLabel ?: "",
    leaderName = leaderName ?: "",
    startLocation = startLocation ?: "",
    status = runCatching { RideStatus.valueOf(status ?: "") }.getOrDefault(RideStatus.PLANNED),
    goingCount = goingCount ?: 0,
    maybeCount = maybeCount ?: 0,
    myRsvp = runCatching { RsvpResponse.valueOf(myRsvp ?: "") }.getOrDefault(RsvpResponse.NONE),
)

private fun ChatMessageDto.toChatMessage(rideId: String): ChatMessage = ChatMessage(
    id = id ?: "",
    rideId = rideId,
    senderName = senderName ?: "",
    senderInitials = senderInitials ?: "",
    colorHex = colorHex ?: "#6B8473",
    text = text ?: "",
    mine = mine ?: false,
    kind = runCatching { ChatKind.valueOf(kind ?: "") }.getOrDefault(ChatKind.NORMAL),
    createdAt = createdAt ?: 0L,
)
