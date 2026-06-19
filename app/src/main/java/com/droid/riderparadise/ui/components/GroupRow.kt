package com.droid.riderparadise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.riderparadise.domain.model.Group
import com.droid.riderparadise.domain.model.GroupPrivacy
import com.droid.riderparadise.domain.model.MembershipStatus
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted

/** Group list card with avatar tile, name/meta, and a contextual action chip. */
@Composable
fun GroupRow(
    group: Group,
    onJoin: () -> Unit,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite.copy(alpha = 0.7f))
            .border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(18.dp))
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            group.gradientStartHex.toColorOrDefault(),
                            group.gradientEndHex.toColorOrDefault(),
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(group.initials, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(group.name, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (group.trending) {
                    Icon(
                        Icons.Filled.LocalFireDepartment, contentDescription = null,
                        tint = Color(0xFFD9742C), modifier = Modifier.size(12.dp),
                    )
                    Text("Trending · ", color = Color(0xFFD9742C), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(metaLine(group), color = InkMuted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.width(8.dp))
        ActionChip(group, onJoin, onRequest)
    }
}

@Composable
private fun ActionChip(group: Group, onJoin: () -> Unit, onRequest: () -> Unit) {
    when (group.membership) {
        MembershipStatus.JOINED -> ChipLabel("Joined", filled = false)
        MembershipStatus.REQUESTED -> ChipLabel("Requested", filled = false)
        MembershipStatus.NONE -> if (group.privacy == GroupPrivacy.PRIVATE) {
            ChipLabel("Request", filled = false, onClick = onRequest)
        } else {
            ChipLabel("Join", filled = true, onClick = onJoin)
        }
    }
}

@Composable
private fun ChipLabel(text: String, filled: Boolean, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(11.dp))
            .background(if (filled) BrandGreen else BrandGreen.copy(alpha = 0.12f))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 15.dp, vertical = 8.dp),
    ) {
        Text(
            text,
            color = if (filled) Color.White else BrandGreen,
            fontSize = 12.5f.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun metaLine(group: Group): String {
    val parts = buildList {
        if (group.privacy == GroupPrivacy.PRIVATE) add("Private")
        add(group.category.label)
        add("${group.riderCount} riders")
        group.distanceKm?.let { add("${it.toInt()} km away") }
    }
    return parts.joinToString(" · ")
}
