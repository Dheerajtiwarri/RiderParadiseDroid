package com.droid.riderparadise.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.domain.model.MembershipStatus
import com.droid.riderparadise.ui.components.GroupRow
import com.droid.riderparadise.ui.components.RpChip
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted

private val CATEGORIES = listOf("All", "Adventure", "Sports", "MTB", "Road", "Cruiser")

@Composable
fun HomeScreen(
    onOpenSuggestions: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") }

    val joined = state.groups.filter { it.membership == MembershipStatus.JOINED }
    val discover = state.groups.filter { it.membership != MembershipStatus.JOINED }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 52.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Good morning, ${state.user?.displayName?.substringBefore(' ') ?: "rider"}",
                        color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    )
                    Text("Discover groups", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CardWhite.copy(alpha = 0.7f))
                        .border(1.dp, Ink.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Ink, modifier = Modifier.size(20.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 12.dp)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF6B6B))
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                    )
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
                Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF9BB3A3), modifier = Modifier.size(18.dp))
                Text("Search groups, routes, riders…", color = Color(0xFF9BB3A3), fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp))
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CATEGORIES.forEach { cat ->
                    RpChip(label = cat, selected = cat == selectedCategory, onClick = { selectedCategory = cat })
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (joined.isNotEmpty()) {
            item { SectionHeader("YOUR GROUPS", onSeeAll = onOpenSuggestions) }
            items(joined, key = { "joined_${it.id}" }) { group ->
                GroupRow(group = group, onJoin = {}, onRequest = {})
            }
            item { Spacer(Modifier.height(6.dp)) }
        }

        item { SectionHeader("POPULAR NEAR YOU", onSeeAll = onOpenSuggestions) }
        items(discover, key = { "disc_${it.id}" }) { group ->
            GroupRow(
                group = group,
                onJoin = { viewModel.onIntent(HomeIntent.Join(group.id)) },
                onRequest = { viewModel.onIntent(HomeIntent.Request(group.id)) },
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String, onSeeAll: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text, color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Text(
            "See all",
            color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable { onSeeAll() }.padding(2.dp),
        )
    }
}
