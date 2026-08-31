package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Immersive UI Primary Palette
val BackgroundDark = Color(0xFF121212)
val BackgroundSecondary = Color(0xFF1E1E1E)
val BackgroundTertiary = Color(0xFF2A2A2A)
val SurfaceCard = Color(0xFF1C1D22)
val SurfaceCardElevated = Color(0xFF242731)

// Glassmorphism tokens
val GlassBackground = Color(0x0DFFFFFF) // rgba(255, 255, 255, 0.05)
val GlassSurface = Color(0x1AFFFFFF)
val GlassBorder = Color(0x1AFFFFFF) // rgba(255, 255, 255, 0.10)
val GlassBorderCyan = Color(0x3300D9FF) // rgba(0, 217, 255, 0.20)

// Immersive UI Accent Palette
val CyanPrimary = Color(0xFF00D9FF)
val PurpleAccent = Color(0xFF7B61FF)
val PinkAccent = Color(0xFFFF61D2)
val AquaAccent = Color(0xFF00F5D4)

// Text Hierarchy
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9CA3AF) // gray-400
val TextMuted = Color(0xFF6B7280) // gray-500

// Quality Badges
val LosslessGold = Color(0xFFFFD166)
val HiFiCyan = Color(0xFF00D9FF)

// State colors
val FavoritePink = Color(0xFFFF2E93)
val ErrorRed = Color(0xFFFF4444)
val WarningAmber = Color(0xFFFFB703)

// Immersive UI Gradients
val PlayButtonGradient = Brush.linearGradient(
    colors = listOf(CyanPrimary, PurpleAccent, PinkAccent)
)

val ProgressGradient = Brush.horizontalGradient(
    colors = listOf(CyanPrimary, PurpleAccent)
)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(CyanPrimary, PurpleAccent, PinkAccent)
)

val ArtworkCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF2A2A2A), Color(0xFF121212))
)

val ArtworkBacklight = Brush.radialGradient(
    colors = listOf(Color(0x3300D9FF), Color(0x00121212))
)

val CardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF22252E), Color(0xFF171920))
)

val VisualizerGradient = Brush.verticalGradient(
    colors = listOf(PinkAccent, PurpleAccent, CyanPrimary)
)

