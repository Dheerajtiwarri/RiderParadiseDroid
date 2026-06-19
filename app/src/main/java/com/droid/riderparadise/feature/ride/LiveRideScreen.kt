package com.droid.riderparadise.feature.ride

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.ui.components.InitialsAvatar
import com.droid.riderparadise.ui.components.PlaceholderMap
import com.droid.riderparadise.ui.components.RiderDot
import com.droid.riderparadise.ui.components.toColorOrDefault
import com.droid.riderparadise.ui.theme.AccentMint
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.NightSurface

private val Glass = Color(0xCC101814)

@Composable
fun LiveRideScreen(
    rideId: String,
    onBack: () -> Unit,
    onOpenChat: () -> Unit,
    onEndRide: (rideId: String) -> Unit,
    viewModel: LiveRideViewModel = hiltViewModel(),
) {
    val ride by viewModel.ride.collectAsStateWithLifecycle()
    val roster = viewModel.roster

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(NightSurface)) {
        val w = maxWidth
        val h = maxHeight
        PlaceholderMap(dark = true)

        // Labeled rider markers (color identity + name·speed pill), positioned over the map.
        val positions = listOf(0.74f to 0.30f, 0.42f to 0.55f, 0.60f to 0.42f, 0.50f to 0.50f, 0.36f to 0.66f)
        roster.forEachIndexed { i, r ->
            val (fx, fy) = positions.getOrElse(i) { 0.5f to 0.5f }
            LabeledMarker(r, Modifier.offset(x = w * fx - 23.dp, y = h * fy - 23.dp))
        }

        Column(modifier = Modifier.fillMaxSize().padding(top = 46.dp)) {
            // top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                GlassCircle { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(20.dp)) }
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(Glass).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)).padding(horizontal = 16.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5A5A)))
                    Spacer(Modifier.size(8.dp))
                    Text(ride?.title ?: "Live ride", color = Color.White, fontSize = 13.5f.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(Glass).border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape).clickable { onOpenChat() },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.Share, "Chat", tint = Color.White, modifier = Modifier.size(18.dp)) }
            }

            Spacer(Modifier.height(12.dp))
            // next-turn nav card
            Row(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Glass).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(BrandGreen), contentAlignment = Alignment.Center) {
                    Text("↰", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("200 m", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Turn right · Elm Street", color = Color(0xFF9FB3A6), fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("10:42", color = AccentMint, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    Text("${ride?.distanceKm ?: 38} km left", color = Color(0xFF9FB3A6), fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            // group spread alert
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x29F5B53D))
                    .border(1.dp, Color(0x66F5B53D), RoundedCornerShape(12.dp))
                    .padding(horizontal = 13.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Warning, null, tint = Color(0xFFF5B53D), modifier = Modifier.size(15.dp))
                Spacer(Modifier.size(8.dp))
                Text("Group spread 340 m · Lena is dropping", color = Color(0xFFF5D28A), fontSize = 12.5f.sp, fontWeight = FontWeight.Bold)
            }
        }

        // side FABs (recenter + layers)
        Column(
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            SideFab(Icons.Filled.GpsFixed, AccentMint)
            SideFab(Icons.Filled.Layers, Color.White)
        }

        // roster sheet
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color(0xEB0D1411))
                .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 24.dp),
        ) {
            Box(modifier = Modifier.size(width = 42.dp, height = 5.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.22f)).align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(buildString { append("On this ride · ${roster.size}") }, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Box(modifier = Modifier.clip(RoundedCornerShape(11.dp)).background(Color(0xFFC23434)).clickable { viewModel.endRide(onEndRide) }.padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Stop, null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.size(5.dp))
                        Text("End ride", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            roster.forEach { RosterRow(it) }
        }
    }
}

@Composable
private fun GlassCircle(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(42.dp).clip(CircleShape).background(Glass).border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun SideFab(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Box(
        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(Glass).border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(21.dp)) }
}

/** Map marker: rider's color-identity circle + a name·speed pill, per the design. */
@Composable
private fun LabeledMarker(p: RideParticipant, modifier: Modifier) {
    val color = p.colorHex.toColorOrDefault()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(if (p.isYou) 30.dp else 44.dp)
                .clip(CircleShape)
                .background(if (p.stale) Color(0xFF5A4242) else color)
                .border(2.dp, Color(0xFF0C1411), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (!p.isYou) Text(p.initials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (p.stale) Color(0xE6282E2A) else color.copy(alpha = 0.95f)).padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text(
                if (p.stale) "${p.name} · seen 1m" else "${p.name.substringBefore(' ').ifEmpty { p.name }} · ${p.speedKmh}",
                color = if (p.stale) Color(0xFFCCAAAA) else Color(0xFF14241A),
                fontSize = 10.5f.sp, fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun RosterRow(p: RideParticipant) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InitialsAvatar(p.initials, p.colorHex.toColorOrDefault(), size = 34)
        Spacer(Modifier.size(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(if (p.isYou) "You" else p.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(p.offsetLabel, color = if (p.stale) Color(0xFFFF6B6B) else Color(0xFF7D9484), fontSize = 11.sp)
        }
        Text("${p.speedKmh} km/h", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.size(8.dp))
        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(if (p.stale) Color(0xFFFF6B6B) else AccentMint))
    }
}
