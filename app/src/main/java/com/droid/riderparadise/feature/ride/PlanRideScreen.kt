package com.droid.riderparadise.feature.ride

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.ui.components.PlaceholderMap
import com.droid.riderparadise.ui.components.RpButton
import com.droid.riderparadise.ui.components.RpChip
import com.droid.riderparadise.ui.components.ScreenTopBar
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted

@Composable
fun PlanRideScreen(
    groupId: String,
    groupName: String,
    onBack: () -> Unit,
    onPublished: (rideId: String) -> Unit,
    viewModel: PlanRideViewModel = hiltViewModel(),
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val difficulty by viewModel.difficulty.collectAsStateWithLifecycle()
    val publishing by viewModel.publishing.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 46.dp, bottom = 30.dp),
    ) {
        ScreenTopBar(title = "Plan a ride", onBack = onBack)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(22.dp)),
        ) { PlaceholderMap() }

        Column(modifier = Modifier.padding(22.dp)) {
            Text("For ${viewModel.groupName.ifBlank { groupName }}", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(11.dp))

            FieldCard("RIDE TITLE") {
                BasicTextField(
                    value = title,
                    onValueChange = viewModel::onTitleChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink),
                )
            }
            Spacer(Modifier.height(11.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FieldCard("WHEN") { Text("Sun · 8:00 AM", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                }
                Box(modifier = Modifier.weight(1f)) {
                    FieldCard("DIFFICULTY") { Text(difficulty, color = BrandGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Easy", "Moderate", "Hard").forEach { d ->
                    RpChip(label = d, selected = d == difficulty, onClick = { viewModel.onDifficulty(d) })
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandGreen.copy(alpha = 0.10f))
                    .padding(16.dp),
            ) {
                Stat("48", "km distance"); Divider(); Stat("612", "m climb"); Divider(); Stat("~2:15", "duration")
            }
            Spacer(Modifier.height(16.dp))
            Text("Invite riders", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite.copy(alpha = 0.66f))
                    .border(1.dp, Ink.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(14.dp),
            ) {
                Text("Whole group will be notified when you publish.", color = InkMuted, fontSize = 13.sp)
            }
            Spacer(Modifier.height(22.dp))
            RpButton(
                text = "Publish ride to group",
                onClick = { viewModel.publish(onPublished) },
                loading = publishing,
            )
        }
    }
}

@Composable
private fun FieldCard(label: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite.copy(alpha = 0.72f))
            .border(1.dp, Ink.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 13.dp),
    ) {
        Text(label, color = InkMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(3.dp))
        content()
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.Stat(value: String, label: String) {
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = InkMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Divider() {
    Box(modifier = Modifier.width(1.dp).height(34.dp).background(Ink.copy(alpha = 0.12f)))
}
