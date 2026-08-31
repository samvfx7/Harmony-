package com.example.presentation.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ui.components.EqualizerPanel
import com.example.ui.components.PitchControl
import com.example.ui.components.SpeedControl
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioEffectsScreen(
    effectsUIState: AudioEffectsUIState,
    viewModel: AudioEffectsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Audio Studio & Equalizer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            viewModel.resetToDefaults()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Effects",
                            tint = CyanPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Speed Control
            SpeedControl(
                currentSpeed = effectsUIState.playbackSpeed,
                onSpeedChanged = { viewModel.setSpeed(it) }
            )

            Divider(color = SurfaceCard, modifier = Modifier.padding(vertical = 12.dp))

            // Pitch Control
            PitchControl(
                currentPitch = effectsUIState.pitch,
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
                    effectsUIState.bassBoost > 0.65f -> "High"
                    effectsUIState.bassBoost > 0.25f -> "Medium"
                    effectsUIState.bassBoost > 0.05f -> "Low"
                    else -> "Off"
                }
                Text(
                    text = bassLabel,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CyanPrimary
                )
            }

            Slider(
                value = effectsUIState.bassBoost,
                onValueChange = { viewModel.setBassBoost(it) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = CyanPrimary,
                    activeTrackColor = CyanPrimary,
                    inactiveTrackColor = Color(0xFF2E323F)
                ),
                modifier = Modifier.fillMaxWidth().testTag("bass_boost_slider_full")
            )

            Divider(color = SurfaceCard, modifier = Modifier.padding(vertical = 12.dp))

            // 5-Band Equalizer Panel
            EqualizerPanel(
                currentPreset = effectsUIState.equalizerPreset,
                onPresetSelected = { viewModel.setEqualizerPreset(it) },
                onBandGainChanged = { band, gain -> viewModel.setEqualizerBandGain(band, gain) },
                onSaveCustomPreset = { viewModel.saveCustomPreset(it) },
                onResetDefaults = { viewModel.setEqualizerPreset(com.example.data.model.EqualizerPreset.NORMAL) }
            )

            Divider(color = SurfaceCard, modifier = Modifier.padding(vertical = 12.dp))

            // Audio Normalization & DRC
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
                        text = "Prevent harsh loudness differences between songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Switch(
                    checked = effectsUIState.normalizeAudio,
                    onCheckedChange = { viewModel.setNormalizeAudio(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
