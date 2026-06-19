package com.droid.riderparadise.data.repository

import com.droid.riderparadise.domain.model.CheckInStatus
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.domain.model.RiderContact
import com.droid.riderparadise.domain.repository.RiderRepository
import javax.inject.Inject

/** Seeded riders/contacts/suggestions for the Riders & Suggestions tabs (POC, no real contacts API). */
class RiderRepositoryImpl @Inject constructor() : RiderRepository {

    override fun ridersOnApp(): List<RiderContact> = listOf(
        RiderContact("r_mv", "Mara Velez", "MV", "#F5B53D", "Road · 2 mutual groups", onApp = true, following = true),
        RiderContact("r_th", "Theo Hayes", "TH", "#25D98A", "Gravel · 1 mutual group", onApp = true, following = false),
        RiderContact("r_sp", "Sam Park", "SP", "#A78BFA", "MTB · 1 mutual group", onApp = true, following = false),
    )

    override fun ridersToInvite(): List<RiderContact> = listOf(
        RiderContact("r_jd", "Jordan Diaz", "JD", "#9AAFA2", "In your contacts · not on app", onApp = false, following = false),
        RiderContact("r_kr", "Kim Reyes", "KR", "#9AAFA2", "In your contacts · not on app", onApp = false, following = false),
    )

    override fun suggestedRiders(): List<RiderContact> = listOf(
        RiderContact("r_lp", "Lena Park", "LP", "#FF6B6B", "Rode with you · Sunday Hills Loop", onApp = true, following = false),
        RiderContact("r_nk", "Noah Kim", "NK", "#3B9DFF", "2 mutual riders · Adventure", onApp = true, following = false),
    )

    override fun colorLegend(): List<RideParticipant> = listOf(
        RideParticipant("l_you", "You", "AR", "#3B9DFF", isLeader = false, isYou = true, speedKmh = 0, offsetLabel = "blue puck", checkIn = CheckInStatus.HERE, stale = false),
        RideParticipant("l_mv", "Mara V.", "MV", "#F5B53D", isLeader = true, isYou = false, speedKmh = 0, offsetLabel = "gold + crown", checkIn = CheckInStatus.HERE, stale = false),
        RideParticipant("l_th", "Theo H.", "TH", "#25D98A", isLeader = false, isYou = false, speedKmh = 0, offsetLabel = "live · full color", checkIn = CheckInStatus.HERE, stale = false),
        RideParticipant("l_sp", "Sam P.", "SP", "#A78BFA", isLeader = false, isYou = false, speedKmh = 0, offsetLabel = "live · full color", checkIn = CheckInStatus.HERE, stale = false),
        RideParticipant("l_lp", "Lena P.", "LP", "#FF6B6B", isLeader = false, isYou = false, speedKmh = 0, offsetLabel = "stale · dimmed", checkIn = CheckInStatus.NOT_CHECKED_IN, stale = true),
    )
}
