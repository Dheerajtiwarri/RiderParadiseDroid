package com.droid.riderparadise.feature.auth

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droid.riderparadise.core.mvi.CollectEffect
import com.droid.riderparadise.ui.components.OtpInput
import com.droid.riderparadise.ui.components.RpButton
import com.droid.riderparadise.ui.components.StepProgress
import com.droid.riderparadise.ui.theme.AccentMint
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.BrandGreenDeep
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink
import com.droid.riderparadise.ui.theme.InkLabel
import com.droid.riderparadise.ui.theme.InkMuted

@Composable
fun AuthScreen(
    onAuthenticatedNew: () -> Unit,
    onAuthenticatedExisting: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* denial handled by OtpNotifier's in-UI fallback */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    CollectEffect(viewModel.effects) { effect ->
        when (effect) {
            AuthEffect.NavigateToOnboarding -> onAuthenticatedNew()
            AuthEffect.NavigateToHome -> onAuthenticatedExisting()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Hero()
        AuthSheet(state, viewModel)
    }
}

@Composable
private fun Hero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
            .background(Brush.linearGradient(listOf(BrandGreen, BrandGreenDeep))),
    ) {
        // decorative translucent circles
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-40).dp)
                .size(210.dp)
                .clip(CircleShape)
                .background(AccentMint.copy(alpha = 0.16f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 30.dp)
                .size(170.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f)),
        )
        Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 56.dp)) {
            Text("Step 1 of 3", color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            StepProgress(current = 1, total = 3)
            Spacer(Modifier.height(28.dp))
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(19.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.PedalBike, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Welcome to\nRidersParadise",
                color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Find your group, plan the route, and ride together — everyone on one live map.",
                color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp, lineHeight = 22.sp,
            )
        }
    }
}

@Composable
private fun AuthSheet(state: AuthState, vm: AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-24).dp)
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 26.dp, end = 26.dp, top = 26.dp, bottom = 30.dp),
    ) {
        Text("SIGN IN WITH MOBILE NUMBER", color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(7.dp))
        Text(
            "We'll send you a one-time code as a notification to verify it's you.",
            color = InkMuted, fontSize = 13.sp, lineHeight = 19.sp,
        )
        Spacer(Modifier.height(16.dp))

        // phone row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .border(1.dp, Ink.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🇺🇸 +1", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = InkMuted, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardWhite)
                    .border(1.dp, BrandGreen.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                BasicTextField(
                    value = state.phone,
                    onValueChange = { v -> vm.onIntent(AuthIntent.PhoneChanged(v.filter { it.isDigit() || it == ' ' })) },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    decorationBox = { inner ->
                        if (state.phone.isEmpty()) {
                            Text("415 820 4407", color = InkMuted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        inner()
                    },
                )
            }
        }
        Spacer(Modifier.height(18.dp))

        // OTP section (active once a code has been sent)
        val otpActive = state.step == AuthStep.OTP
        Text(
            "ENTER THE 6-DIGIT CODE",
            color = if (otpActive) InkMuted else InkMuted.copy(alpha = 0.5f),
            fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(9.dp))
        if (otpActive) {
            OtpInput(value = state.otp, onValueChange = { vm.onIntent(AuthIntent.OtpChanged(it)) })
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Sent to +1 ••• ${state.phone.filter { it.isDigit() }.takeLast(2)}",
                    color = InkMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                )
                Text(
                    if (state.canResend) "Resend code" else "Resend in 0:${state.resendSeconds.toString().padStart(2, '0')}",
                    color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    modifier = if (state.canResend) Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { vm.onIntent(AuthIntent.Resend) } else Modifier,
                )
            }
            state.devCodeHint?.let {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandGreen.copy(alpha = 0.1f))
                        .padding(12.dp),
                ) {
                    Text("Notifications are off — your code is $it", color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // placeholder boxes hinting where the code will go
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardWhite.copy(alpha = 0.5f))
                            .border(1.dp, Ink.copy(alpha = 0.08f), RoundedCornerShape(14.dp)),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
        }

        // primary CTA with trailing arrow
        Box {
            RpButton(
                text = if (otpActive) "Verify & continue" else "Send code",
                onClick = {
                    if (otpActive) vm.onIntent(AuthIntent.VerifyOtp) else vm.onIntent(AuthIntent.SubmitPhone)
                },
                enabled = if (otpActive) state.otpComplete else state.phoneValid,
                loading = state.isSubmitting,
            )
            if ((otpActive && state.otpComplete) || (!otpActive && state.phoneValid)) {
                Icon(
                    Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp).size(18.dp),
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        // guest option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Visibility, contentDescription = null, tint = InkLabel, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Browse a public ride first", color = InkLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        if (otpActive) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Change number",
                color = InkMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { vm.onIntent(AuthIntent.BackToPhone) }
                    .padding(6.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "By continuing you agree to our Terms & Privacy Policy.",
            color = InkMuted, fontSize = 11.sp, lineHeight = 17.sp, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
