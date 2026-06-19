package com.droid.riderparadise.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Campaign
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
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.ui.components.InitialsAvatar
import com.droid.riderparadise.ui.components.rememberInitials
import com.droid.riderparadise.ui.components.toColorOrDefault
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandGreenDeep
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted
import com.droid.riderparadise.ui.theme.InkSoft
import com.droid.riderparadise.ui.theme.WarnAmber

@Composable
fun ProfileTabScreen(
    onOpenFeedback: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileTabViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user = state.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(start = 22.dp, end = 22.dp, top = 56.dp, bottom = 120.dp),
    ) {
        // header card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(BrandGreen, BrandGreenDeep)))
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InitialsAvatar(
                initials = rememberInitials(user?.displayName ?: "Rider"),
                color = (user?.colorHex ?: "#3B9DFF").toColorOrDefault(),
                size = 64,
            )
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user?.displayName ?: "Rider", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    user?.bikeType?.label ?: "Set up your profile",
                    color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp,
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        // rider color identity
        Text("RIDER COLOR IDENTITY", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardWhite.copy(alpha = 0.6f))
                .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                .padding(18.dp),
        ) {
            Text(
                "Every rider gets one stable color used everywhere they appear — map marker, track, roster, and chat — so the group stays identifiable at a glance.",
                color = InkSoft, fontSize = 13.sp, lineHeight = 19.sp,
            )
            Spacer(Modifier.height(14.dp))
            state.legend.forEach { LegendRow(it) }
        }

        Spacer(Modifier.height(18.dp))
        // actions
        ActionRow(Icons.Filled.Campaign, "Send feedback", "Suggest a feature or report a problem", onOpenFeedback)
        Spacer(Modifier.height(10.dp))
        ActionRow(Icons.AutoMirrored.Filled.Logout, "Sign out", "End your session on this device") {
            viewModel.signOut(onSignedOut)
        }
    }
}

@Composable
private fun LegendRow(p: RideParticipant) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InitialsAvatar(p.initials, p.colorHex.toColorOrDefault(), size = 30)
        Spacer(Modifier.size(12.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(p.name, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            if (p.isLeader) {
                Spacer(Modifier.size(6.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(WarnAmber.copy(alpha = 0.25f)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                    Text("LEADER", color = Color(0xFFB07D12), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(p.offsetLabel, color = if (p.stale) Color(0xFFFF6B6B) else InkMuted, fontSize = 12.sp)
    }
}

@Composable
private fun ActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite.copy(alpha = 0.7f))
            .border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(BrandGreen),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(19.dp)) }
        Spacer(Modifier.size(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = InkMuted, fontSize = 12.sp)
        }
    }
}
