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
fun PitchControl(
    currentPitch: Float, // -12 to +12 semitones
    onPitchChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val intervalPresets = listOf(
        -12f to "-1 Octave",
        -5f to "-5 Semitones",
        0f to "Natural (0)",
        5f to "+5 Semitones",
        12f to "+1 Octave"
    )

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
                text = "Pitch Shift (Key)",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            val pitchInt = currentPitch.toInt()
            val displayText = when {
                pitchInt > 0 -> "+$pitchInt semitones"
                pitchInt < 0 -> "$pitchInt semitones"
                else -> "Natural (0)"
            }

            Text(
                text = displayText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Slider from -12 to +12 semitones
        Slider(
            value = currentPitch,
            onValueChange = { newPitch ->
                onPitchChanged(newPitch)
            },
            valueRange = -12f..12f,
            steps = 23, // integer steps between -12 and +12
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
                .testTag("pitch_slider")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Interval Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            intervalPresets.forEach { (semitones, label) ->
                val isSelected = Math.abs(currentPitch - semitones) < 0.5f
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onPitchChanged(semitones)
                    },
                    label = {
                        Text(
                            text = label,
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
                    modifier = Modifier.testTag("pitch_chip_${semitones.toInt()}")
                )
            }
        }
    }
}
