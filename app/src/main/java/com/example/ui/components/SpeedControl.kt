package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun SpeedControl(
    currentSpeed: Float,
    onSpeedChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val speedPresets = listOf(0.5f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.25f, 1.5f, 1.75f, 2.0f, 2.25f, 2.5f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playback Speed",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Text(
                text = String.format(Locale.US, "%.2fx", currentSpeed),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Slider from 0.25x to 2.5x
        Slider(
            value = currentSpeed,
            onValueChange = { newSpeed ->
                onSpeedChanged(newSpeed)
            },
            valueRange = 0.25f..2.5f,
            onValueChangeFinished = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            },
            colors = SliderDefaults.colors(
                thumbColor = CyanPrimary,
                activeTrackColor = CyanPrimary,
                inactiveTrackColor = Color(0xFF2E323F)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("speed_slider")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Fine Adjustment Micro-Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onSpeedChanged((currentSpeed - 0.1f).coerceIn(0.25f, 2.5f))
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("-0.1x", style = MaterialTheme.typography.labelSmall)
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onSpeedChanged((currentSpeed - 0.01f).coerceIn(0.25f, 2.5f))
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("-0.01x", style = MaterialTheme.typography.labelSmall)
                }
            }

            androidx.compose.material3.TextButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onSpeedChanged(1.0f)
                }
            ) {
                Text("Reset (1.0x)", style = MaterialTheme.typography.labelSmall, color = CyanPrimary, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onSpeedChanged((currentSpeed + 0.01f).coerceIn(0.25f, 2.5f))
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("+0.01x", style = MaterialTheme.typography.labelSmall)
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onSpeedChanged((currentSpeed + 0.1f).coerceIn(0.25f, 2.5f))
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("+0.1x", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            speedPresets.forEach { preset ->
                val isSelected = Math.abs(currentSpeed - preset) < 0.05f
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onSpeedChanged(preset)
                    },
                    label = {
                        Text(
                            text = "${preset}x",
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
                    modifier = Modifier.testTag("speed_chip_${preset}")
                )
            }
        }
    }
}
