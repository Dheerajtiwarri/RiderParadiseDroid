package com.droid.riderparadise.feature.onboarding

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.core.mvi.CollectEffect
import com.droid.riderparadise.domain.model.BikeType
import com.droid.riderparadise.ui.components.RpButton
import com.droid.riderparadise.ui.components.RpChip
import com.droid.riderparadise.ui.components.StepProgress
import com.droid.riderparadise.ui.theme.AccentMint
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandHeaderBrushVertical
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onContinue: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effects) { effect ->
        when (effect) {
            ProfileEffect.NavigateToJoin -> onContinue()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        // Header band
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandHeaderBrushVertical)
                .padding(start = 26.dp, end = 26.dp, top = 56.dp, bottom = 28.dp),
        ) {
            Text("Step 2 of 3", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            StepProgress(current = 2, total = 3)
            Spacer(Modifier.height(18.dp))
            Text(
                "Set up your\nrider profile",
                color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp,
            )
        }

        Column(modifier = Modifier.padding(26.dp)) {
            // Avatar placeholder with + badge
            Box(modifier = Modifier.size(100.dp)) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(AccentMint.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Add\nphoto", color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(BrandGreen)
                        .border(3.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add photo", tint = Color.White, modifier = Modifier.size(15.dp))
                }
            }
            Spacer(Modifier.height(18.dp))

            Text("DISPLAY NAME *", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .border(1.dp, Ink.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 15.dp),
            ) {
                BasicTextField(
                    value = state.displayName,
                    onValueChange = { viewModel.onIntent(ProfileIntent.NameChanged(it)) },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Ink),
                    decorationBox = { inner ->
                        if (state.displayName.isEmpty()) {
                            Text("Alex Rivera", color = InkMuted, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        inner()
                    },
                )
            }
            Spacer(Modifier.height(16.dp))

            Text("BIKE TYPE *", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BikeType.entries.forEach { type ->
                    RpChip(
                        label = type.label,
                        selected = state.bikeType == type,
                        onClick = { viewModel.onIntent(ProfileIntent.BikeSelected(type)) },
                    )
                }
            }
            Spacer(Modifier.height(18.dp))

            ConsentRow(
                icon = Icons.Filled.LocationOn,
                title = "Live location sharing",
                subtitle = "Shared only with your group, only during a ride.",
                checked = state.shareLocation,
                onCheckedChange = { viewModel.onIntent(ProfileIntent.ShareLocationToggled(it)) },
            )
            Spacer(Modifier.height(10.dp))
            ConsentRow(
                icon = Icons.Filled.People,
                title = "Contacts access",
                subtitle = "Connect with riders you already know.",
                checked = state.allowContacts,
                onCheckedChange = { viewModel.onIntent(ProfileIntent.ContactsToggled(it)) },
            )

            state.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(22.dp))
            RpButton(
                text = "Continue",
                onClick = { viewModel.onIntent(ProfileIntent.Continue) },
                enabled = state.canContinue,
                loading = state.isSaving,
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ConsentRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AccentMint.copy(alpha = 0.12f))
            .border(1.dp, BrandGreen.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BrandGreen),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.size(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = InkMuted, fontSize = 12.sp, lineHeight = 16.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = BrandGreen, checkedThumbColor = Color.White),
        )
    }
}
