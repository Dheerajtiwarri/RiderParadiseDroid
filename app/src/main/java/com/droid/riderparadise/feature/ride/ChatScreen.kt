package com.droid.riderparadise.feature.ride

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.droid.riderparadise.domain.model.ChatKind
import com.droid.riderparadise.domain.model.ChatMessage
import com.droid.riderparadise.ui.components.InitialsAvatar
import com.droid.riderparadise.ui.components.ScreenTopBar
import com.droid.riderparadise.ui.components.toColorOrDefault
import com.droid.riderparadise.ui.theme.BrandGreenCta
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted

private val MACROS = listOf("Flat tire", "Regrouping", "Stopping ahead", "I'm behind")

@Composable
fun ChatScreen(
    rideId: String,
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val draft by viewModel.draft.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(top = 46.dp)) {
        ScreenTopBar(title = "Live ride chat", onBack = onBack)
        Box(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp).fillMaxWidth()
                .clip(RoundedCornerShape(13.dp)).background(BrandGreenCta.copy(alpha = 0.12f)).padding(10.dp),
        ) {
            Text("Chat is open only during the ride — it closes when the ride ends.", color = Color(0xFF2F5A3C), fontSize = 11.5f.sp, fontWeight = FontWeight.SemiBold)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(messages, key = { it.id }) { msg -> MessageRow(msg) }
        }

        // macros
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MACROS.forEach { m ->
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(13.dp)).background(CardWhite)
                        .border(1.dp, Ink.copy(alpha = 0.15f), RoundedCornerShape(13.dp))
                        .clickable { viewModel.sendMacro(m) }.padding(horizontal = 13.dp, vertical = 8.dp),
                ) { Text(m, color = Color(0xFF33493B), fontSize = 12.5f.sp, fontWeight = FontWeight.Bold) }
            }
        }
        // input
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(22.dp)).background(CardWhite)
                    .border(1.dp, Ink.copy(alpha = 0.1f), RoundedCornerShape(22.dp)).padding(horizontal = 18.dp, vertical = 13.dp),
            ) {
                BasicTextField(
                    value = draft,
                    onValueChange = viewModel::onDraft,
                    textStyle = TextStyle(fontSize = 14.sp, color = Ink),
                    decorationBox = { inner -> if (draft.isEmpty()) Text("Message the group…", color = Color(0xFF9BB3A3), fontSize = 14.sp); inner() },
                )
            }
            Spacer(Modifier.size(10.dp))
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(22.dp)).background(BrandGreenCta).clickable { viewModel.send() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White, modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
private fun MessageRow(msg: ChatMessage) {
    when {
        msg.kind == ChatKind.SYSTEM -> Text(
            msg.text, color = InkMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        msg.mine -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier.widthIn(max = 250.dp).clip(RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)).background(BrandGreenCta).padding(horizontal = 14.dp, vertical = 11.dp),
            ) { Text(msg.text, color = Color.White, fontSize = 14.5f.sp) }
        }
        else -> Row(verticalAlignment = Alignment.Bottom) {
            InitialsAvatar(msg.senderInitials, msg.colorHex.toColorOrDefault(), size = 30)
            Spacer(Modifier.size(9.dp))
            Column {
                Text(msg.senderName, color = msg.colorHex.toColorOrDefault(), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 3.dp))
                Box(
                    modifier = Modifier.widthIn(max = 250.dp)
                        .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                        .background(if (msg.kind == ChatKind.MACRO) Color(0xFFFFE3E3) else CardWhite)
                        .border(1.dp, if (msg.kind == ChatKind.MACRO) Color(0x59FF6B6B) else Ink.copy(alpha = 0.07f), RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                ) {
                    Text(
                        msg.text,
                        color = if (msg.kind == ChatKind.MACRO) Color(0xFFC23434) else Ink,
                        fontSize = 14.5f.sp,
                        fontWeight = if (msg.kind == ChatKind.MACRO) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}
