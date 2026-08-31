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
    val speedPresets = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

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

        // Slider from 0.25x to 2.0x
        Slider(
            value = currentSpeed,
            onValueChange = { newSpeed ->
                onSpeedChanged(newSpeed)
            },
            valueRange = 0.25f..2.0f,
            steps = 6, // 0.25 increments: 0.25, 0.50, 0.75, 1.00, 1.25, 1.50, 1.75, 2.00
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

        Spacer(modifier = Modifier.height(6.dp))

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
