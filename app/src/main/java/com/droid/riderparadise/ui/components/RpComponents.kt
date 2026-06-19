package com.droid.riderparadise.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.droid.riderparadise.ui.theme.AccentMint
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandGreenCta
import com.droid.riderparadise.ui.theme.BrandGreenDeep
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkLabel

/** Primary green CTA matching the design's pill buttons. */
@Composable
fun RpButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) BrandGreenCta else BrandGreenCta.copy(alpha = 0.4f))
            .clickable(enabled = enabled && !loading) { onClick() }
            .padding(vertical = 17.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp),
            )
        } else {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

/** Selectable pill chip (categories, bike types). */
@Composable
fun RpChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (selected) Modifier.background(BrandGreen)
                else Modifier
                    .background(CardWhite.copy(alpha = 0.7f))
                    .border(1.dp, Ink.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp),
    ) {
        Text(
            label,
            color = if (selected) Color.White else InkLabel,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

/** Step progress bar used across onboarding (filled vs remaining segments). */
@Composable
fun StepProgress(current: Int, total: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (i < current) AccentMint else Color.White.copy(alpha = 0.28f))
            )
        }
    }
}

@Composable
fun rememberInitials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

/** Circular colored avatar with initials. */
@Composable
fun InitialsAvatar(
    initials: String,
    color: Color,
    size: Int = 50,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size / 3.2f).sp,
        )
    }
}

@Composable
fun RpSpacerH(dp: Int) = Spacer(Modifier.height(dp.dp))

@Composable
fun RpSpacerW(dp: Int) = Spacer(Modifier.width(dp.dp))

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = modifier,
    )
}
