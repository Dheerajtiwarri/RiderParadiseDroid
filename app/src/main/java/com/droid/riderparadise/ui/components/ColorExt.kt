package com.droid.riderparadise.ui.components

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

/** Parses a "#RRGGBB" hex string into a Compose [Color], falling back to brand green. */
fun String.toColorOrDefault(default: Color = Color(0xFF16893B)): Color = try {
    Color(this.toColorInt())
} catch (_: Exception) {
    default
}
