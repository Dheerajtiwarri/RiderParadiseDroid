package com.droid.riderparadise.feature.onboarding

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.core.mvi.CollectEffect
import com.droid.riderparadise.ui.components.GroupRow
import com.droid.riderparadise.ui.components.RpButton
import com.droid.riderparadise.ui.components.StepProgress
import com.droid.riderparadise.ui.theme.AccentMint
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandHeaderBrushVertical
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted

@Composable
fun JoinGroupScreen(
    onEnter: () -> Unit,
    viewModel: JoinGroupViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effects) { effect ->
        when (effect) {
            JoinGroupEffect.NavigateToHome -> onEnter()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandHeaderBrushVertical)
                .padding(start = 26.dp, end = 26.dp, top = 56.dp, bottom = 28.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Step 3 of 3", color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Skip", color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .padding(2.dp))
            }
            Spacer(Modifier.height(12.dp))
            StepProgress(current = 3, total = 3)
            Spacer(Modifier.height(18.dp))
            Text("Join your\nfirst group", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { InviteCard() }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("DISCOVER GROUPS NEAR YOU", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Text("See all", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            items(state.groups, key = { it.id }) { group ->
                GroupRow(
                    group = group,
                    onJoin = { viewModel.onIntent(JoinGroupIntent.Join(group.id)) },
                    onRequest = { viewModel.onIntent(JoinGroupIntent.Request(group.id)) },
                )
            }
            item { CreateGroupButton() }
        }

        Column(modifier = Modifier.padding(22.dp)) {
            RpButton(
                text = if (state.hasJoinedAny) "Enter RidersParadise" else "Skip for now",
                onClick = { viewModel.onIntent(JoinGroupIntent.Continue) },
            )
        }
    }
}

@Composable
private fun InviteCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AccentMint.copy(alpha = 0.12f))
            .border(1.dp, BrandGreen.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Text("Have an invite link or QR?", color = Ink, fontSize = 13.5f.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(11.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(13.dp))
                    .background(CardWhite)
                    .border(1.dp, Ink.copy(alpha = 0.1f), RoundedCornerShape(13.dp))
                    .padding(horizontal = 14.dp, vertical = 13.dp),
            ) {
                Text("Paste invite link…", color = Color(0xFF9BB3A3), fontSize = 13.5f.sp)
            }
            Spacer(Modifier.size(10.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(BrandGreen),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan QR", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun CreateGroupButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, SolidColor(BrandGreen.copy(alpha = 0.45f)), RoundedCornerShape(16.dp))
            .padding(15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(9.dp))
        Text("Create a new group", color = BrandGreen, fontSize = 14.5f.sp, fontWeight = FontWeight.Bold)
    }
}
