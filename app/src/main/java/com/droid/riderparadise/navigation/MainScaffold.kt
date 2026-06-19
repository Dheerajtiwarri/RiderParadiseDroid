package com.droid.riderparadise.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droid.riderparadise.feature.groups.GroupsScreen
import com.droid.riderparadise.feature.home.HomeScreen
import com.droid.riderparadise.feature.profile.ProfileTabScreen
import com.droid.riderparadise.feature.riders.RidersScreen
import com.droid.riderparadise.ui.components.BottomTabBar
import com.droid.riderparadise.ui.components.RpTab

/** Hosts the four bottom tabs and forwards ride-flow navigation to the parent NavHost. */
@Composable
fun MainScaffold(
    onPlanRide: (groupId: String, groupName: String) -> Unit,
    onOpenLobby: (rideId: String) -> Unit,
    onOpenLive: (rideId: String) -> Unit,
    onOpenRecap: (rideId: String) -> Unit,
    onOpenSuggestions: () -> Unit,
    onOpenFeedback: () -> Unit,
    onSignedOut: () -> Unit,
) {
    var tab by remember { mutableStateOf(RpTab.HOME) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (tab) {
            RpTab.HOME -> HomeScreen(onOpenSuggestions = onOpenSuggestions)
            RpTab.GROUPS -> GroupsScreen(
                onPlanRide = onPlanRide,
                onOpenLobby = onOpenLobby,
                onOpenRecap = onOpenRecap,
            )
            RpTab.RIDERS -> RidersScreen()
            RpTab.PROFILE -> ProfileTabScreen(
                onOpenFeedback = onOpenFeedback,
                onSignedOut = onSignedOut,
            )
        }

        BottomTabBar(
            selected = tab,
            onSelect = { tab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 28.dp, vertical = 26.dp),
        )
    }
}
