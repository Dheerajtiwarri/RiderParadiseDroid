package com.droid.riderparadise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.riderparadise.ui.theme.BrandGreenCta
import com.droid.riderparadise.ui.theme.CardWhite

enum class RpTab(val label: String, val outlined: ImageVector, val filled: ImageVector) {
    HOME("Home", Icons.Outlined.Home, Icons.Filled.Home),
    GROUPS("Groups", Icons.Outlined.Groups, Icons.Outlined.Groups),
    RIDERS("Riders", Icons.Outlined.PersonSearch, Icons.Outlined.PersonSearch),
    PROFILE("Profile", Icons.Outlined.Person, Icons.Filled.Person),
}

/** Floating glass-style bottom tab bar from the design. */
@Composable
fun BottomTabBar(
    selected: RpTab,
    onSelect: (RpTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(CardWhite.copy(alpha = 0.92f))
            .border(1.dp, Color.White, RoundedCornerShape(34.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        RpTab.entries.forEach { tab ->
            val active = tab == selected
            val tint = if (active) BrandGreenCta else Color(0xFF7D9484)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelect(tab) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Icon(
                    imageVector = if (active) tab.filled else tab.outlined,
                    contentDescription = tab.label,
                    tint = tint,
                    modifier = Modifier.size(23.dp),
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    tab.label,
                    color = tint,
                    fontSize = 10.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                )
            }
        }
    }
}
