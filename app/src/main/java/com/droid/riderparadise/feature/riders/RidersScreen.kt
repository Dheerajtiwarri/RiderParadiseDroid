package com.droid.riderparadise.feature.riders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.droid.riderparadise.ui.components.RiderRow
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted

@Composable
fun RidersScreen(viewModel: RidersViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 52.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("From your contacts", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Riders", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandGreen.copy(alpha = 0.12f))
                        .border(1.dp, BrandGreen.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.People, null, tint = BrandGreen, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Contacts on", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .border(1.dp, Ink.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 15.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Search, null, tint = Color(0xFF9BB3A3), modifier = Modifier.size(18.dp))
                Text("Search riders…", color = Color(0xFF9BB3A3), fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp))
            }
            Spacer(Modifier.height(16.dp))
            Header("ON RIDERSPARADISE · ${state.onApp.size}")
        }
        items(state.onApp, key = { it.id }) { rider ->
            RiderRow(rider = rider, invited = false, onAction = { viewModel.toggleFollow(rider.id) })
        }
        item { Spacer(Modifier.height(8.dp)); Header("INVITE TO RIDE · ${state.toInvite.size}") }
        items(state.toInvite, key = { it.id }) { rider ->
            RiderRow(rider = rider, invited = state.invited.contains(rider.id), onAction = { viewModel.invite(rider.id) })
        }
    }
}

@Composable
private fun Header(text: String) {
    Box(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text, color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}
