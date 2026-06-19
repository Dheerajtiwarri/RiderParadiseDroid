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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.droid.riderparadise.domain.model.RideRecap
import com.droid.riderparadise.ui.components.PlaceholderMap
import com.droid.riderparadise.ui.components.RpButton
import com.droid.riderparadise.ui.components.ScreenTopBar
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkMuted
import com.droid.riderparadise.ui.theme.InkSoft

@Composable
fun RecapScreen(
    rideId: String,
    onBack: () -> Unit,
    viewModel: RecapViewModel = hiltViewModel(),
) {
    val r: RideRecap = viewModel.recap

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 46.dp, bottom = 30.dp),
    ) {
        ScreenTopBar(title = "Ride recap", onBack = onBack)
        Column(modifier = Modifier.padding(horizontal = 22.dp)) {
            Spacer(Modifier.height(6.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(BrandGreen).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(r.dateLabel, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(r.title, color = Ink, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text("${r.ridersFinished} riders finished · combined ${r.combinedKm} km", color = InkSoft, fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))

            Box(modifier = Modifier.fillMaxWidth().height(172.dp).clip(RoundedCornerShape(22.dp))) {
                PlaceholderMap(dark = true)
                Box(modifier = Modifier.padding(12.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xCC101814)).padding(horizontal = 9.dp, vertical = 4.dp)) {
                    Text("${r.ridersFinished} tracks combined", color = Color(0xFF36E0A1), fontSize = 10.5f.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(14.dp))

            // stats grid 3x2
            val stats = listOf(
                r.distanceKm.toString() to "km distance",
                r.movingTime to "moving time",
                r.avgSpeed.toString() to "avg km/h",
                r.maxSpeed.toString() to "max km/h",
                r.climbM.toString() to "m climb",
                r.kcal.toString() to "kcal est.",
            )
            stats.chunked(3).forEach { rowStats ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    rowStats.forEach { (value, label) ->
                        Column(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(15.dp)).background(CardWhite.copy(alpha = 0.66f)).border(1.dp, Ink.copy(alpha = 0.07f), RoundedCornerShape(15.dp)).padding(12.dp),
                        ) {
                            Text(value, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Text(label, color = InkMuted, fontSize = 10.5f.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            RpButton(text = "Share to group feed", onClick = onBack)
        }
    }
}
