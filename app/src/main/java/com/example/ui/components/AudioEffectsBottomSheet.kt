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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val currentTrack by viewModel.currentTrack.collectAsState(initial = null)

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

            // Current Track Custom Speed & Pitch Persistence Card
            currentTrack?.let { song ->
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = SurfaceCardElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Track Custom Presets",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Button(
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                        viewModel.saveSongCustomEffects(song.id, uiState.pitch, uiState.playbackSpeed)
                                    },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = CyanPrimary,
                                        contentColor = Color.Black
                                    ),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Save Defaults", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }

                                if (song.customPitch != null || song.customSpeed != null) {
                                    androidx.compose.material3.IconButton(
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            viewModel.saveSongCustomEffects(song.id, null, null)
                                            viewModel.setPitch(0.0f)
                                            viewModel.setSpeed(1.0f)
                                        },
                                        modifier = Modifier.height(32.dp).width(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Clear Saved Effects",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }

                        if (song.customPitch != null || song.customSpeed != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Saved Indicator",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Custom defaults: " + 
                                           (if (song.customSpeed != null) "${String.format(Locale.US, "%.2f", song.customSpeed)}x speed" else "1.00x speed") +
                                           " & " +
                                           (if (song.customPitch != null) "${String.format(Locale.US, "%+.2f", song.customPitch)} semitones" else "Natural pitch"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyanPrimary
                                )
                            }
                        }
                    }
                }
            }

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
