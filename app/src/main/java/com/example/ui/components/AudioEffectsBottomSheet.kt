package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.presentation.state.AudioEffectsUIState
import com.example.presentation.viewmodel.AudioEffectsViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BottomSheetShape
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEffectsBottomSheet(
    uiState: AudioEffectsUIState,
    viewModel: AudioEffectsViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val view = LocalView.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        shape = BottomSheetShape,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Audio Studio & Effects",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )

                Row {
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            viewModel.resetToDefaults()
                        },
                        modifier = Modifier.testTag("reset_all_effects_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset All Effects",
                            tint = TextMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Speed Control
            SpeedControl(
                currentSpeed = uiState.playbackSpeed,
                onSpeedChanged = { viewModel.setSpeed(it) }
            )

            Divider(color = SurfaceCard, modifier = Modifier.padding(vertical = 12.dp))

            // Pitch Control
            PitchControl(
                currentPitch = uiState.pitch,
                onPitchChanged = { viewModel.setPitch(it) }
            )

            Divider(color = SurfaceCard, modifier = Modifier.padding(vertical = 12.dp))

            // Bass Boost Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bass Boost",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                val bassLabel = when {
                    uiState.bassBoost > 0.65f -> "High"
                    uiState.bassBoost > 0.25f -> "Medium"
                    uiState.bassBoost > 0.05f -> "Low"
                    else -> "Off"
                }
                Text(
                    text = bassLabel,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CyanPrimary
                )
            }

            Slider(
                value = uiState.bassBoost,
                onValueChange = { viewModel.setBassBoost(it) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = CyanPrimary,
                    activeTrackColor = CyanPrimary,
                    inactiveTrackColor = Color(0xFF2E323F)
                ),
                modifier = Modifier.fillMaxWidth().testTag("bass_boost_slider")
            )

            Divider(color = SurfaceCard, modifier = Modifier.padding(vertical = 12.dp))

            // Equalizer
            EqualizerPanel(
                currentPreset = uiState.equalizerPreset,
                onPresetSelected = { viewModel.setEqualizerPreset(it) },
                onBandGainChanged = { band, gain -> viewModel.setEqualizerBandGain(band, gain) },
                onSaveCustomPreset = { viewModel.saveCustomPreset(it) },
                onResetDefaults = { viewModel.setEqualizerPreset(com.example.data.model.EqualizerPreset.NORMAL) }
            )

            Divider(color = SurfaceCard, modifier = Modifier.padding(vertical = 12.dp))

            // Crossfade & Gapless Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Crossfade",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "${uiState.crossfadeDuration}s transition between tracks",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = uiState.crossfadeEnabled,
                    onCheckedChange = { viewModel.setCrossfadeEnabled(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Gapless Playback",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Eliminates silence between album tracks",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = uiState.gaplessEnabled,
                    onCheckedChange = { viewModel.setGaplessPlaybackEnabled(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Audio Normalization",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Balances volume levels across tracks",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = uiState.normalizeAudio,
                    onCheckedChange = { viewModel.setNormalizeAudio(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
