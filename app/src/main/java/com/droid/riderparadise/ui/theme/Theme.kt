package com.droid.riderparadise.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    primaryContainer = BrandGreenDeep,
    onPrimaryContainer = Color.White,
    secondary = AccentMint,
    onSecondary = Ink,
    background = LightBg,
    onBackground = Ink,
    surface = CardWhite,
    onSurface = Ink,
    surfaceVariant = LightBgAlt,
    onSurfaceVariant = InkMuted,
    error = Danger,
    outline = InkMuted,
)

private val DarkColors = darkColorScheme(
    primary = AccentMint,
    onPrimary = Ink,
    primaryContainer = BrandGreenDeep,
    onPrimaryContainer = Color.White,
    secondary = BrandGreen,
    onSecondary = Color.White,
    background = NightSurface,
    onBackground = Color.White,
    surface = NightSurfaceAlt,
    onSurface = Color.White,
    surfaceVariant = NightLine,
    onSurfaceVariant = Color(0xFF9FB4A6),
    error = Danger,
    outline = Color(0xFF3A5246),
)

// Brand uses a fixed identity — dynamic color is intentionally not applied.
@Composable
fun RiderParadiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = RpShapes,
        content = content
    )
}
