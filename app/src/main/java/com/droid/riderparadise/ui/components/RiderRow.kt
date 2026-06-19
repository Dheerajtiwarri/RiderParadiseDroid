package com.droid.riderparadise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.riderparadise.domain.model.RiderContact
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkSoft

/** Rider list row with a contextual action (Follow / Following / Invite / Invited). */
@Composable
fun RiderRow(
    rider: RiderContact,
    invited: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite.copy(alpha = if (rider.onApp) 0.72f else 0.5f))
            .border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(16.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InitialsAvatar(rider.initials, rider.colorHex.toColorOrDefault(), size = 42)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(rider.name, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(rider.subtitle, color = InkSoft, fontSize = 11.sp)
        }
        when {
            !rider.onApp -> ActionPill(if (invited) "Invited" else "Invite", filled = false, onClick = onAction, enabled = !invited)
            rider.following -> ActionPill("Following", filled = false, leadingCheck = true, onClick = onAction)
            else -> ActionPill("Follow", filled = true, leadingPlus = true, onClick = onAction)
        }
    }
}

@Composable
private fun ActionPill(
    text: String,
    filled: Boolean,
    leadingCheck: Boolean = false,
    leadingPlus: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (filled) BrandGreen else BrandGreen.copy(alpha = 0.12f))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 13.dp, vertical = 7.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingCheck) Icon(Icons.Filled.Check, null, tint = BrandGreen, modifier = Modifier.size(13.dp))
            if (leadingPlus) Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(13.dp))
            if (leadingCheck || leadingPlus) Spacer(Modifier.size(5.dp))
            Text(text, color = if (filled) Color.White else BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
