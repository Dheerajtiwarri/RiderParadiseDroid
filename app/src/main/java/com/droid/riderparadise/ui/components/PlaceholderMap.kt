package com.droid.riderparadise.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Self-contained styled map placeholder (grid + route polyline + optional rider dots).
 * Used in lieu of a real map SDK for this POC — swappable behind this composable later.
 */
@Composable
fun PlaceholderMap(
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    gridColor: Color = if (dark) Color(0xFF1A2922) else Color(0xFFDFE9DC),
    background: Color = if (dark) Color(0xFF0E1714) else Color(0xFFEAF2E7),
    routeColor: Color = if (dark) Color(0xFF36E0A1) else Color(0xFF16893B),
    riders: List<RiderDot> = emptyList(),
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(background, size = size)
        val step = 34f
        var x = 0f
        while (x < size.width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 1f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
            y += step
        }
        // route polyline (normalized control points)
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(0.12f * w, 0.85f * h)
            cubicTo(0.30f * w, 0.65f * h, 0.28f * w, 0.50f * h, 0.45f * w, 0.46f * h)
            cubicTo(0.62f * w, 0.42f * h, 0.58f * w, 0.28f * h, 0.78f * w, 0.22f * h)
            cubicTo(0.86f * w, 0.18f * h, 0.90f * w, 0.16f * h, 0.92f * w, 0.10f * h)
        }
        drawPath(path, routeColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
        // rider dots
        riders.forEach { r ->
            drawCircle(Color.White, radius = 13f, center = Offset(r.x * w, r.y * h))
            drawCircle(r.color, radius = 10f, center = Offset(r.x * w, r.y * h))
        }
    }
}

data class RiderDot(val x: Float, val y: Float, val color: Color)
