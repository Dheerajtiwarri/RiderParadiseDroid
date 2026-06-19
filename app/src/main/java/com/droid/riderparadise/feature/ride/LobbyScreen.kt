package com.droid.riderparadise.feature.ride

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
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
import com.droid.riderparadise.domain.model.CheckInStatus
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.ui.components.InitialsAvatar
import com.droid.riderparadise.ui.components.PlaceholderMap
import com.droid.riderparadise.ui.components.RiderDot
import com.droid.riderparadise.ui.components.RpButton
import com.droid.riderparadise.ui.components.ScreenTopBar
import com.droid.riderparadise.ui.components.toColorOrDefault
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandGreenDeep
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted
import com.droid.riderparadise.ui.theme.WarnAmber

@Composable
fun LobbyScreen(
    rideId: String,
    onBack: () -> Unit,
    onStart: (rideId: String) -> Unit,
    viewModel: LobbyViewModel = hiltViewModel(),
) {
    val ride by viewModel.ride.collectAsStateWithLifecycle()
    val roster = viewModel.roster
    val checkedIn = roster.count { it.checkIn == CheckInStatus.HERE }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 46.dp, bottom = 30.dp),
    ) {
        ScreenTopBar(title = "Pre-ride lobby", onBack = onBack)
        Spacer(Modifier.height(8.dp))

        // countdown
        Column(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(BrandGreen, BrandGreenDeep)))
                .padding(22.dp),
        ) {
            Text("${ride?.title ?: "Ride"} · rolls out in", color = Color.White.copy(alpha = 0.78f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("11:42", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, null, tint = Color(0xFF36E0A1), modifier = Modifier.size(15.dp))
                Spacer(Modifier.size(6.dp))
                Text(ride?.startLocation ?: "Trailhead", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .height(108.dp)
                .clip(RoundedCornerShape(18.dp)),
        ) {
            PlaceholderMap(
                riders = roster.filterNot { it.isYou }.take(3).mapIndexed { i, r ->
                    RiderDot(0.3f + i * 0.2f, 0.4f + (i % 2) * 0.2f, r.colorHex.toColorOrDefault())
                },
            )
        }

        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("CHECKED IN", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text("$checkedIn of ${roster.size}", color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(10.dp))
        Column(modifier = Modifier.padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            roster.forEach { CheckInRow(it) }
        }

        Spacer(Modifier.height(22.dp))
        Column(modifier = Modifier.padding(horizontal = 22.dp)) {
            RpButton(text = "▶  Start ride", onClick = { viewModel.start(onStart) })
            Spacer(Modifier.height(8.dp))
            Text(
                "Start opens live chat & tracking for everyone.",
                color = InkMuted, fontSize = 11.5f.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CheckInRow(p: RideParticipant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(CardWhite.copy(alpha = if (p.checkIn == CheckInStatus.NOT_CHECKED_IN) 0.4f else 0.66f))
            .border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(15.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InitialsAvatar(p.initials, p.colorHex.toColorOrDefault(), size = 36)
        Spacer(Modifier.size(12.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(if (p.isYou) "You" else p.name, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            if (p.isLeader) {
                Spacer(Modifier.size(6.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(WarnAmber.copy(alpha = 0.25f)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                    Text("LEADER", color = Color(0xFFB07D12), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        when (p.checkIn) {
            CheckInStatus.HERE -> Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(18.dp).clip(RoundedCornerShape(9.dp)).background(BrandGreen), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(11.dp))
                }
                Spacer(Modifier.size(5.dp))
                Text("Here", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            CheckInStatus.ON_THE_WAY -> Box(modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(Color(0xFFFBF2D6)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text("On the way", color = Color(0xFF9A7B1E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            CheckInStatus.NOT_CHECKED_IN -> Text("Not checked in", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
