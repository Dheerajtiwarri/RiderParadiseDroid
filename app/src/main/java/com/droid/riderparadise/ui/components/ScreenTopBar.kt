package com.droid.riderparadise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink

/** Centered title with a circular back button, used on pushed detail screens. */
@Composable
fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Ink,
    backgroundCircle: Color = CardWhite.copy(alpha = 0.72f),
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(backgroundCircle)
                .border(1.dp, Ink.copy(alpha = 0.08f), CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = tint, modifier = Modifier.size(20.dp))
        }
        Text(
            title,
            color = tint, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f).padding(start = 12.dp),
        )
        if (trailing != null) trailing()
    }
}
