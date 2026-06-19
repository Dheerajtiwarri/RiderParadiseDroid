package com.droid.riderparadise.ui.theme

import androidx.compose.ui.graphics.Brush

/** Diagonal brand gradient used for hero/auth backgrounds. */
val BrandHeaderBrush: Brush
    get() = Brush.linearGradient(listOf(BrandGreen, BrandGreenDeep))

/** Top-to-bottom brand gradient for header bands. */
val BrandHeaderBrushVertical: Brush
    get() = Brush.verticalGradient(listOf(BrandGreen, BrandGreenDeep))
