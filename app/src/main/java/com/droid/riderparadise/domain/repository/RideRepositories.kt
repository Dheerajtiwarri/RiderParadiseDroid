package com.droid.riderparadise.domain.repository

import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.model.ChatKind
import com.droid.riderparadise.domain.model.Feedback
import com.droid.riderparadise.domain.model.Ride
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.domain.model.RideRecap
import com.droid.riderparadise.domain.model.RiderContact
import com.droid.riderparadise.domain.model.RsvpResponse
import kotlinx.coroutines.flow.Flow

interface RideRepository {
    fun observeRides(): Flow<List<Ride>>
    fun observeRide(rideId: String): Flow<Ride?>
    suspend fun ensureSeeded()
    suspend fun setRsvp(rideId: String, response: RsvpResponse)
    suspend fun startRide(rideId: String)
    suspend fun completeRide(rideId: String)
    suspend fun createRide(groupId: String, groupName: String, title: String, difficulty: String): String
    /** Static roster for a ride (simulated peers in this POC). */
    fun roster(rideId: String): List<RideParticipant>
    fun recapFor(rideId: String): RideRecap
    fun observeMessages(rideId: String): Flow<List<com.droid.riderparadise.domain.model.ChatMessage>>
    suspend fun sendMessage(rideId: String, text: String, kind: ChatKind)
}

interface RiderRepository {
    fun ridersOnApp(): List<RiderContact>
    fun ridersToInvite(): List<RiderContact>
    fun suggestedRiders(): List<RiderContact>
    fun colorLegend(): List<RideParticipant>
}

interface FeedbackRepository {
    suspend fun submit(feedback: Feedback): Resource<Unit>
}
