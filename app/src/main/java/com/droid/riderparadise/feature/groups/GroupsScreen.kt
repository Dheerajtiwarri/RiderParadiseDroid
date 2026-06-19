package com.droid.riderparadise.feature.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.domain.model.Group
import com.droid.riderparadise.domain.model.Ride
import com.droid.riderparadise.domain.model.RideStatus
import com.droid.riderparadise.domain.model.RsvpResponse
import com.droid.riderparadise.ui.components.InitialsAvatar
import com.droid.riderparadise.ui.components.toColorOrDefault
import com.droid.riderparadise.ui.theme.AccentMint
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandGreenCta
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted
import com.droid.riderparadise.ui.theme.InkSoft
import com.droid.riderparadise.ui.theme.WarnAmber

private data class GoingAvatar(val initials: String, val hex: String)
private val GOING = listOf(GoingAvatar("MV", "#F5B53D"), GoingAvatar("TH", "#25D98A"), GoingAvatar("LP", "#FF6B6B"))

@Composable
fun GroupsScreen(
    onPlanRide: (groupId: String, groupName: String) -> Unit,
    onOpenLobby: (rideId: String) -> Unit,
    onOpenRecap: (rideId: String) -> Unit,
    viewModel: GroupsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // soonest group with a ride is expanded; rest compact
    val expandedId = state.groups.firstOrNull { state.rideByGroup[it.id] != null }?.id

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 52.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(state.user?.displayName ?: "Rider", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Your groups", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
                CircleBtn(Icons.Filled.Search, CardWhite.copy(alpha = 0.72f), Ink) {}
                Spacer(Modifier.size(9.dp))
                val first = state.groups.firstOrNull()
                CircleBtn(Icons.Filled.Add, BrandGreenCta, Color.White, enabled = first != null) {
                    first?.let { onPlanRide(it.id, it.name) }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("YOUR GROUPS · ${state.groups.size}", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(CardWhite.copy(alpha = 0.7f)).border(1.dp, Ink.copy(alpha = 0.1f), RoundedCornerShape(9.dp)).padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.SwapVert, null, tint = BrandGreen, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.size(5.dp))
                    Text("By next ride", color = InkSoft, fontSize = 11.5f.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(2.dp))
        }

        if (state.groups.isEmpty()) {
            item { Text("You haven't joined any groups yet. Discover groups on the Home tab.", color = InkMuted, fontSize = 14.sp) }
        }

        items(state.groups, key = { it.id }) { group ->
            val ride = state.rideByGroup[group.id]
            if (group.id == expandedId && ride != null) {
                ExpandedGroupCard(group, ride, onRsvp = { viewModel.setRsvp(ride.id, it) }, onOpenRide = {
                    if (ride.status == RideStatus.COMPLETED) onOpenRecap(ride.id) else onOpenLobby(ride.id)
                })
            } else {
                CompactGroupCard(group, ride, onClick = {
                    ride?.let { if (it.status == RideStatus.COMPLETED) onOpenRecap(it.id) else onOpenLobby(it.id) }
                        ?: onPlanRide(group.id, group.name)
                })
            }
        }

        item { Spacer(Modifier.height(4.dp)); ContactsStrip() }
    }
}

@Composable
private fun CircleBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, bg: Color, tint: Color, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(44.dp).clip(CircleShape).background(if (enabled) bg else bg.copy(alpha = 0.5f)).clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
}

@Composable
private fun ExpandedGroupCard(group: Group, ride: Ride, onRsvp: (RsvpResponse) -> Unit, onOpenRide: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(AccentMint.copy(alpha = 0.22f), AccentMint.copy(alpha = 0.10f))))
            .border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(22.dp)).padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GroupTile(group, 48)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(group.name, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.size(6.dp))
                    Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(Color(0xFFFBEEC9)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                        Text("ADMIN", color = Color(0xFFB07D12), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Text("${group.privacy.name.lowercase().replaceFirstChar { it.uppercase() }} · ${group.riderCount} riders", color = InkSoft, fontSize = 12.sp)
            }
            Row(
                modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(BrandGreen).padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(11.dp))
                Spacer(Modifier.size(4.dp))
                Text(ride.whenLabel.substringBefore(" ·").take(10), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(Modifier.height(13.dp))
        // NEXT RIDE inner card
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(CardWhite.copy(alpha = 0.7f)).clickable { onOpenRide() }.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${ride.whenLabel} · NEXT RIDE", color = BrandGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(ride.title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Text("${ride.distanceKm} km · ${ride.difficulty} · led by ${ride.leaderName}", color = InkSoft, fontSize = 12.sp)
            }
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(BrandGreenCta), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarStack(GOING.map { it.initials to it.hex }, 26)
            Spacer(Modifier.size(8.dp))
            Text("${ride.goingCount} going · ${ride.maybeCount} maybe", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            RsvpChips(ride.myRsvp, onRsvp)
        }

        Spacer(Modifier.height(11.dp))
        // approval row
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(CardWhite.copy(alpha = 0.78f)).border(1.dp, WarnAmber.copy(alpha = 0.45f), RoundedCornerShape(15.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarStack(listOf("JD" to "#FF6B6B", "KR" to "#3B9DFF"), 28)
            Spacer(Modifier.size(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("2 riders to approve", color = Ink, fontSize = 12.5f.sp, fontWeight = FontWeight.ExtraBold)
                Text("Invited by Theo · admin approval needed", color = InkSoft, fontSize = 11.sp)
            }
            Box(modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(Ink.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Close, null, tint = InkMuted, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.size(6.dp))
            Box(modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(BrandGreen), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun CompactGroupCard(group: Group, ride: Ride?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(CardWhite.copy(alpha = 0.66f)).border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(18.dp)).clickable { onClick() }.padding(horizontal = 13.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GroupTile(group, 46)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(group.name, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            val meta = if (ride != null) "${ride.whenLabel} · ${ride.title} · ${group.riderCount} riders"
            else "${group.privacy.name.lowercase().replaceFirstChar { it.uppercase() }} · ${group.riderCount} riders"
            Text(meta, color = InkSoft, fontSize = 12.sp)
        }
        AvatarStack(listOf("TH" to "#25D98A", "+${group.riderCount}" to "#2C4A38"), 24)
    }
}

@Composable
private fun GroupTile(group: Group, sizeDp: Int) {
    Box(
        modifier = Modifier.size(sizeDp.dp).clip(RoundedCornerShape((sizeDp / 3.4f).dp))
            .background(Brush.linearGradient(listOf(group.gradientStartHex.toColorOrDefault(), group.gradientEndHex.toColorOrDefault()))),
        contentAlignment = Alignment.Center,
    ) { Text(group.initials, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = (sizeDp / 3.2f).sp) }
}

@Composable
private fun AvatarStack(items: List<Pair<String, String>>, sizeDp: Int) {
    Row {
        items.forEachIndexed { i, (label, hex) ->
            Box(modifier = Modifier.offset(x = (i * -7).dp)) {
                Box(
                    modifier = Modifier.size(sizeDp.dp).clip(CircleShape).background(hex.toColorOrDefault()).border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { Text(label, color = Color.White, fontSize = (sizeDp / 2.8f).sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun RsvpChips(current: RsvpResponse, onRsvp: (RsvpResponse) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf("Going" to RsvpResponse.GOING, "Maybe" to RsvpResponse.MAYBE, "Can't" to RsvpResponse.CANT).forEach { (label, resp) ->
            val sel = current == resp
            Box(
                modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(if (sel) BrandGreen else CardWhite).border(1.dp, if (sel) BrandGreen else Ink.copy(alpha = 0.12f), RoundedCornerShape(9.dp)).clickable { onRsvp(resp) }.padding(horizontal = 10.dp, vertical = 6.dp),
            ) { Text(label, color = if (sel) Color.White else InkSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ContactsStrip() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(AccentMint.copy(alpha = 0.14f)).border(1.dp, BrandGreen.copy(alpha = 0.18f), RoundedCornerShape(18.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(BrandGreen), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.People, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.size(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Invite riders from your contacts", color = Ink, fontSize = 13.5f.sp, fontWeight = FontWeight.Bold)
            Text("Anyone can invite — an admin approves before they join.", color = InkSoft, fontSize = 11.5f.sp)
        }
        Box(modifier = Modifier.clip(RoundedCornerShape(11.dp)).background(BrandGreen).padding(horizontal = 13.dp, vertical = 8.dp)) {
            Text("Allow", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
