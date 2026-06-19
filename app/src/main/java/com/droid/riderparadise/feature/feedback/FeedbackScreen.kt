package com.droid.riderparadise.feature.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.domain.model.FeedbackType
import com.droid.riderparadise.ui.components.RpButton
import com.droid.riderparadise.ui.components.RpChip
import com.droid.riderparadise.ui.components.ScreenTopBar
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted
import com.droid.riderparadise.ui.theme.InkSoft

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedbackScreen(
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 46.dp, bottom = 30.dp),
    ) {
        ScreenTopBar(title = "Send feedback", onBack = onBack)
        Column(modifier = Modifier.padding(horizontal = 22.dp)) {
            Spacer(Modifier.height(4.dp))
            Text("Help shape the ride", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Suggest a feature or tell us what's not working — every rider's note reaches the team.",
                color = InkSoft, fontSize = 13.5f.sp, lineHeight = 19.sp,
            )
            Spacer(Modifier.height(18.dp))

            Label("WHAT'S THIS ABOUT?")
            Spacer(Modifier.height(9.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                TypeCard(Modifier.weight(1f), Icons.Filled.Lightbulb, "Suggest a feature", state.type == FeedbackType.SUGGESTION) { viewModel.setType(FeedbackType.SUGGESTION) }
                TypeCard(Modifier.weight(1f), Icons.Filled.WarningAmber, "Report a problem", state.type == FeedbackType.PROBLEM) { viewModel.setType(FeedbackType.PROBLEM) }
            }
            Spacer(Modifier.height(18.dp))

            Label("AREA")
            Spacer(Modifier.height(9.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.areas.forEach { a ->
                    RpChip(label = a, selected = a == state.area, onClick = { viewModel.setArea(a) })
                }
            }
            Spacer(Modifier.height(18.dp))

            Label("TELL US MORE")
            Spacer(Modifier.height(9.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite.copy(alpha = 0.72f))
                    .border(1.dp, BrandGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                BasicTextField(
                    value = state.body,
                    onValueChange = viewModel::setBody,
                    textStyle = TextStyle(fontSize = 14.5f.sp, color = Ink, lineHeight = 21.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (state.body.isEmpty()) {
                            Text("It'd be great to drop a regroup pin on the live map that pings everyone…", color = InkMuted, fontSize = 14.5f.sp, lineHeight = 21.sp)
                        }
                        inner()
                    },
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(4.dp)).background(BrandGreen))
                Spacer(Modifier.size(8.dp))
                Text("App v1.0 · auto-attached", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(22.dp))
            if (state.sent) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(BrandGreen.copy(alpha = 0.12f)).padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Thanks! Sent to the team.", color = BrandGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            } else {
                RpButton(text = "Send to the team", onClick = { viewModel.submit(onBack) }, enabled = state.canSend, loading = state.sending)
                Spacer(Modifier.height(8.dp))
                Text(
                    "We read everything · usually reply within 2 days",
                    color = InkMuted, fontSize = 11.5f.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun TypeCard(modifier: Modifier, icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) BrandGreen else CardWhite.copy(alpha = 0.72f))
            .border(1.dp, if (selected) BrandGreen else Ink.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, tint = if (selected) Color.White else Color(0xFF33493B), modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(7.dp))
        Text(label, color = if (selected) Color.White else Color(0xFF33493B), fontSize = 12.5f.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Label(text: String) {
    Text(text, color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
}
