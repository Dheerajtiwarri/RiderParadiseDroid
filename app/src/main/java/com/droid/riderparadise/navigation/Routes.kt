package com.droid.riderparadise.navigation

/** Navigation destinations. Simple string routes for this POC. */
object Routes {
    const val AUTH = "auth"
    const val ONBOARDING_PROFILE = "onboarding/profile"
    const val ONBOARDING_JOIN = "onboarding/join"

    /** Bottom-tab host (Home / Groups / Riders / Profile). */
    const val MAIN = "main"

    // Ride flow (pushed over the tab host)
    const val PLAN = "plan/{groupId}/{groupName}"
    const val LOBBY = "lobby/{rideId}"
    const val LIVE = "live/{rideId}"
    const val CHAT = "chat/{rideId}"
    const val RECAP = "recap/{rideId}"
    const val FEEDBACK = "feedback"
    const val SUGGESTIONS = "suggestions"

    fun plan(groupId: String, groupName: String) = "plan/$groupId/$groupName"
    fun lobby(rideId: String) = "lobby/$rideId"
    fun live(rideId: String) = "live/$rideId"
    fun chat(rideId: String) = "chat/$rideId"
    fun recap(rideId: String) = "recap/$rideId"
}
