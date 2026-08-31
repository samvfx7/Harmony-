package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassCardShape
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent

// Specular Glass Border Gradient (Bright top, subtle bottom for liquid glass refraction)
val LiquidGlassSpecularBorder = Brush.verticalGradient(
    colors = listOf(
        Color(0x66FFFFFF), // Top specular reflection highlight
        Color(0x1AFFFFFF), // Mid opacity
        Color(0x08FFFFFF)  // Bottom edge shadow
    )
)

val LiquidGlassActiveBorder = Brush.linearGradient(
    colors = listOf(
        Color(0x9900D9FF), // Bright Cyan Top-Left
        Color(0x667B61FF), // Purple Mid
        Color(0x33FF61D2)  // Pink Bottom
    )
)

val LiquidGlassSurface = Color(0x14FFFFFF) // 8% White base
val LiquidGlassSurfaceElevated = Color(0x24FFFFFF) // 14% White base

fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = LiquidGlassSurface,
    borderBrush: Brush = LiquidGlassSpecularBorder,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(borderWidth, borderBrush, shape)

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = LiquidGlassSurface,
    borderBrush: Brush = LiquidGlassSpecularBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .border(borderWidth, borderBrush, shape),
        color = backgroundColor,
        shape = shape
    ) {
        content()
    }
}

@Composable
fun LiquidGlassCapsule(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x1AFFFFFF),
    borderBrush: Brush = LiquidGlassSpecularBorder,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, borderBrush, CircleShape),
        color = backgroundColor,
        shape = CircleShape
    ) {
        content()
    }
}

/**
 * Ambient Liquid Aura background layer with floating glowing orbs
 * to create authentic liquid glass refraction and depth behind semi-transparent panels.
 */
@Composable
fun LiquidBackgroundAura(
    modifier: Modifier = Modifier,
    isAnimated: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "LiquidAura")
    
    val orb1Scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb1"
    )

    val orb2Scale by transition.animateFloat(
        initialValue = 1.1f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        // Fluid ambient light dynamic orbs
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-40).dp, y = (-20).dp)
                .size((280 * if (isAnimated) orb1Scale else 1f).dp)
                .blur(80.dp)
                .background(Color(0xFFFFFFFF).copy(alpha = 0.15f), CircleShape)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 60.dp, y = (-50).dp)
                .size((320 * if (isAnimated) orb2Scale else 1f).dp)
                .blur(90.dp)
                .background(Color(0xFFFFFFFF).copy(alpha = 0.10f), CircleShape)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 80.dp)
                .size(340.dp)
                .blur(100.dp)
                .background(Color(0xFFFFFFFF).copy(alpha = 0.05f), CircleShape)
        )

        // Main content layer placed on top of liquid light background
        content()
    }
}

