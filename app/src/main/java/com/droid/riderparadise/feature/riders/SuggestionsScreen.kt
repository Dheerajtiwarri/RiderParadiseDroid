package com.droid.riderparadise.feature.riders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import com.droid.riderparadise.ui.components.RiderRow
import com.droid.riderparadise.ui.components.ScreenTopBar
import com.droid.riderparadise.ui.theme.AccentMint
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandGreenDeep
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted
import com.droid.riderparadise.ui.theme.InkSoft

private data class SuggGroup(val initials: String, val name: String, val meta: String, val tag: String, val start: Color, val end: Color)

@Composable
fun SuggestionsScreen(
    onBack: () -> Unit,
    viewModel: RidersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val groups = listOf(
        SuggGroup("GG", "Golden Gate Grinders", "Adventure · 212 riders", "3 friends in", Color(0xFFFF8A5B), Color(0xFFE85D2C)),
        SuggGroup("EB", "East Bay Tourers", "Adventure · 148 riders", "4 km away", Color(0xFFA78BFA), Color(0xFF7C5FD6)),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 46.dp, bottom = 30.dp),
    ) {
        ScreenTopBar(title = "For you", onBack = onBack)
        Spacer(Modifier.height(8.dp))

        // why banner
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(BrandGreen, BrandGreenDeep)))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Star, null, tint = AccentMint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Picked for you — based on your Adventure Tourers rides near the Bay Area.",
                color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp,
            )
        }

        SectionLabel("GROUPS YOU MIGHT LIKE")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            groups.forEach { SuggGroupCard(it) }
        }

        SectionLabel("RIDERS YOU MAY KNOW")
        Column(modifier = Modifier.padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            state.suggested.forEach { rider ->
                RiderRow(rider = rider, invited = false, onAction = { viewModel.toggleFollow(rider.id) })
            }
        }

        SectionLabel("OPEN RIDES THIS WEEK")
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(AccentMint.copy(alpha = 0.14f))
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.clip(RoundedCornerShape(13.dp)).background(CardWhite).padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("SAT", color = Color(0xFFD64545), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("12", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Marin Headlands Tour", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text("62 km · open ride · 18 going", color = InkSoft, fontSize = 12.sp)
            }
            Box(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(BrandGreen.copy(alpha = 0.12f)).padding(horizontal = 13.dp, vertical = 7.dp)) {
                Text("View", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SuggGroupCard(g: SuggGroup) {
    Column(
        modifier = Modifier
            .width(172.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite.copy(alpha = 0.72f))
            .border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(20.dp))
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(Brush.linearGradient(listOf(g.start, g.end))),
            contentAlignment = Alignment.Center,
        ) { Text(g.initials, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp) }
        Spacer(Modifier.height(11.dp))
        Text(g.name, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 17.sp)
        Text(g.meta, color = InkSoft, fontSize = 11.sp)
        Spacer(Modifier.height(9.dp))
        Box(modifier = Modifier.clip(RoundedCornerShape(7.dp)).background(BrandGreen.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
            Text(g.tag, color = BrandGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(11.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp)).background(BrandGreen).padding(vertical = 9.dp),
            contentAlignment = Alignment.Center,
        ) { Text("Join", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text, color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 11.dp),
    )
}
