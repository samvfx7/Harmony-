package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EqualizerBand
import com.example.data.model.EqualizerPreset
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EqualizerPanel(
    currentPreset: EqualizerPreset,
    onPresetSelected: (EqualizerPreset) -> Unit,
    onBandGainChanged: (Int, Float) -> Unit,
    onSaveCustomPreset: (String) -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var customPresetName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Header with Save and Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "5-Band Equalizer",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Active Preset: ${currentPreset.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanPrimary
                )
            }

            Row {
                IconButton(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.testTag("save_eq_preset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Save Custom Preset",
                        tint = CyanPrimary
                    )
                }

                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onResetDefaults()
                    },
                    modifier = Modifier.testTag("reset_eq_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset EQ",
                        tint = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Real-time Frequency Response Curve Visualizer Canvas
        EqualizerFrequencyVisualizer(
            bands = currentPreset.bands,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 11 Preset Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EqualizerPreset.PRESETS.forEach { preset ->
                val isSelected = currentPreset.name.equals(preset.name, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onPresetSelected(preset)
                    },
                    label = {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPrimary.copy(alpha = 0.25f),
                        selectedLabelColor = CyanPrimary,
                        containerColor = SurfaceCard,
                        labelColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("eq_preset_${preset.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5 Vertical/Horizontal Sliders for Bands (60Hz, 250Hz, 1kHz, 4kHz, 15kHz)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            currentPreset.bands.forEachIndexed { index, band ->
                BandSliderColumn(
                    band = band,
                    index = index,
                    onGainChanged = { gain ->
                        onBandGainChanged(index, gain)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Custom Preset", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = customPresetName,
                    onValueChange = { customPresetName = it },
                    label = { Text("Preset Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customPresetName.isNotBlank()) {
                            onSaveCustomPreset(customPresetName.trim())
                            showSaveDialog = false
                            customPresetName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text("Save", color = Color(0xFF121212))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCardElevated
        )
    }
}

@Composable
fun BandSliderColumn(
    band: EqualizerBand,
    index: Int,
    onGainChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${if (band.gain > 0) "+" else ""}${band.gain.toInt()}dB",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (band.gain != 0f) CyanPrimary else TextMuted
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Slider (-12 to +12 dB)
        Slider(
            value = band.gain,
            onValueChange = { gain ->
                onGainChanged(gain)
            },
            valueRange = -12f..12f,
            onValueChangeFinished = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            },
            colors = SliderDefaults.colors(
                thumbColor = CyanPrimary,
                activeTrackColor = CyanPrimary,
                inactiveTrackColor = Color(0xFF2E323F)
            ),
            modifier = Modifier.testTag("band_slider_$index")
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = band.label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 10.sp
        )
    }
}

@Composable
fun EqualizerFrequencyVisualizer(
    bands: List<EqualizerBand>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = SurfaceCardElevated,
        shape = RoundedCornerShape(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            val width = size.width
            val height = size.height
            val midY = height / 2f

            // Center zero-dB baseline
            drawLine(
                color = Color(0x33FFFFFF),
                start = Offset(0f, midY),
                end = Offset(width, midY),
                strokeWidth = 1.dp.toPx()
            )

            if (bands.isEmpty()) return@Canvas

            val stepX = width / (bands.size - 1).coerceAtLeast(1)
            val points = bands.mapIndexed { index, band ->
                val x = index * stepX
                // Gain: +12dB -> top (0), -12dB -> bottom (height)
                val normalizedGain = (band.gain / 12f).coerceIn(-1f, 1f)
                val y = midY - (normalizedGain * (height * 0.4f))
                Offset(x, y)
            }

            // Smooth cubic bezier spline
            val strokePath = Path()
            val fillPath = Path()

            strokePath.moveTo(points[0].x, points[0].y)
            fillPath.moveTo(points[0].x, height)
            fillPath.lineTo(points[0].x, points[0].y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX = (p0.x + p1.x) / 2f

                strokePath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                fillPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Draw glowing gradient fill under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyanPrimary.copy(alpha = 0.35f),
                        PurpleAccent.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                )
            )

            // Draw glowing curve line
            drawPath(
                path = strokePath,
                brush = Brush.horizontalGradient(
                    colors = listOf(CyanPrimary, PinkAccent, CyanPrimary)
                ),
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw points at band nodes
            points.forEach { point ->
                drawCircle(
                    color = CyanPrimary,
                    radius = 4.dp.toPx(),
                    center = point
                )
            }
        }
    }
}
