package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.PurpleAccent
import kotlin.math.sin

@Composable
fun RealtimeAudioVisualizer(
    fftData: FloatArray,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 64.dp,
    barColorStart: Color = CyanPrimary,
    barColorEnd: Color = PurpleAccent
) {
    val infiniteTransition = rememberInfiniteTransition(label = "idle_visualizer")
    val idlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "idle_phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val canvasHeight = size.height
            val barCount = fftData.size.coerceAtLeast(1)
            val gap = 6.dp.toPx()
            val totalGapWidth = gap * (barCount - 1)
            val barWidth = ((width - totalGapWidth) / barCount).coerceAtLeast(2.dp.toPx())

            val gradient = Brush.verticalGradient(
                colors = listOf(barColorStart, barColorEnd, barColorStart.copy(alpha = 0.3f))
            )

            for (i in 0 until barCount) {
                val rawValue = if (i < fftData.size) fftData[i] else 0.05f

                // If playing, use live audio FFT data; if paused, generate gentle sine wave idle movement
                val magnitude = if (isPlaying) {
                    rawValue.coerceIn(0.05f, 1.0f)
                } else {
                    val sine = sin(idlePhase + i * 0.4f)
                    (0.08f + 0.04f * sine).toFloat()
                }

                val barHeight = (canvasHeight * magnitude).coerceAtLeast(4.dp.toPx())
                val x = i * (barWidth + gap)
                val y = canvasHeight - barHeight

                // Primary Spectrum Bar
                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )

                // Subdued reflection glow below
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColorStart.copy(alpha = 0.25f), Color.Transparent)
                    ),
                    topLeft = Offset(x, canvasHeight),
                    size = Size(barWidth, barHeight * 0.25f),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }
    }
}
