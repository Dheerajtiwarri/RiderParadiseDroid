package com.droid.riderparadise.core.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Backend REST surface (BACKEND.md §7). All paths relative to base `…/api/`. */
interface RiderParadiseApi {

    // ---- Auth (public) ----
    @POST("auth/request-otp")
    suspend fun requestOtp(@Body body: RequestOtpBody): ApiEnvelope<AckDto>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpBody): ApiEnvelope<AuthDto>

    @POST("auth/logout")
    suspend fun logout(): ApiEnvelope<AckDto>

    // ---- User ----
    @GET("me")
    suspend fun me(): ApiEnvelope<UserDto>

    @PATCH("me")
    suspend fun updateMe(@Body body: UpdateProfileBody): ApiEnvelope<UserDto>

    // ---- Groups ----
    @GET("groups")
    suspend fun groups(): ApiEnvelope<List<GroupDto>>

    @GET("groups/joined")
    suspend fun joinedGroups(): ApiEnvelope<List<GroupDto>>

    @POST("groups/{id}/join")
    suspend fun joinGroup(@Path("id") id: String): ApiEnvelope<AckDto>

    @POST("groups/{id}/request")
    suspend fun requestToJoin(@Path("id") id: String): ApiEnvelope<AckDto>

    @POST("groups/{id}/leave")
    suspend fun leaveGroup(@Path("id") id: String): ApiEnvelope<AckDto>

    // ---- Rides ----
    @GET("rides")
    suspend fun rides(
        @Query("groupId") groupId: String? = null,
        @Query("status") status: String? = null,
    ): ApiEnvelope<List<RideDto>>

    @GET("rides/{id}")
    suspend fun ride(@Path("id") id: String): ApiEnvelope<RideDto>

    @POST("rides")
    suspend fun createRide(@Body body: CreateRideBody): ApiEnvelope<RideDto>

    @POST("rides/{id}/rsvp")
    suspend fun rsvp(@Path("id") id: String, @Body body: RsvpBody): ApiEnvelope<AckDto>

    @POST("rides/{id}/start")
    suspend fun startRide(@Path("id") id: String): ApiEnvelope<AckDto>

    @POST("rides/{id}/complete")
    suspend fun completeRide(@Path("id") id: String): ApiEnvelope<AckDto>

    // ---- Chat ----
    @GET("rides/{id}/messages")
    suspend fun messages(@Path("id") id: String): ApiEnvelope<List<ChatMessageDto>>

    @POST("rides/{id}/messages")
    suspend fun sendMessage(@Path("id") id: String, @Body body: SendMessageBody): ApiEnvelope<AckDto>

    // ---- Feedback ----
    @POST("feedback")
    suspend fun feedback(@Body body: FeedbackBody): ApiEnvelope<AckDto>
}
