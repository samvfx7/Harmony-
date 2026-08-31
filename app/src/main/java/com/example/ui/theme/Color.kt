package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Immersive UI Primary Palette
val BackgroundDark = Color(0xFF000000)
val BackgroundSecondary = Color(0xFF0A0A0A)
val BackgroundTertiary = Color(0xFF121212)
val SurfaceCard = Color(0xFF050505)
val SurfaceCardElevated = Color(0xFF111111)

// Glassmorphism tokens
val GlassBackground = Color(0x08FFFFFF) // rgba(255, 255, 255, 0.03)
val GlassSurface = Color(0x11FFFFFF)
val GlassBorder = Color(0x22FFFFFF) // rgba(255, 255, 255, 0.13)
val GlassBorderCyan = Color(0x66FFFFFF) // Glowing white border

// Immersive UI Accent Palette - Glowing White Theme
val CyanPrimary = Color(0xFFFFFFFF) // Pure glowing white
val PurpleAccent = Color(0xFFB0B0B0) // Silver highlight
val PinkAccent = Color(0xFFE8E8E8) // Off-white
val AquaAccent = Color(0xFFFFFFFF)

// Text Hierarchy
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9CA3AF) // gray-400
val TextMuted = Color(0xFF6B7280) // gray-500

// Quality Badges
val LosslessGold = Color(0xFFFFFFFF)
val HiFiCyan = Color(0xFFFFFFFF)

// State colors
val FavoritePink = Color(0xFFFFFFFF)
val ErrorRed = Color(0xFFFF4444)
val WarningAmber = Color(0xFFFFB703)

// Immersive UI Gradients
val PlayButtonGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFCCCCCC), Color(0xFFEEEEEE))
)

val ProgressGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFAAAAAA))
)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFE0E0E0), Color(0xFFFFFFFF))
)

val ArtworkCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF151515), Color(0xFF000000))
)

val ArtworkBacklight = Brush.radialGradient(
    colors = listOf(Color(0x44FFFFFF), Color(0x00000000))
)

val CardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF111111), Color(0xFF000000))
)

val VisualizerGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFCCCCCC), Color(0xFFFFFFFF))
)

