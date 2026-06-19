package com.droid.riderparadise.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Design uses Plus Jakarta Sans; falls back to system sans until the font is bundled.
private val Display = FontFamily.Default

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.ExtraBold,
        fontSize = 31.sp, lineHeight = 34.sp, letterSpacing = (-0.8).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp, lineHeight = 30.sp, letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 26.sp, letterSpacing = (-0.4).sp
    ),
    titleMedium = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.Bold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp
    ),
)
