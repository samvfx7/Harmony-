package com.example.presentation.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.SettingsViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onNavigateToEqualizer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val sleepTimerRemaining by viewModel.sleepTimerRemaining.collectAsState()
    val crossfadeDuration by viewModel.crossfadeDuration.collectAsState()
    val crossfadeEnabled by viewModel.crossfadeEnabled.collectAsState()
    val gaplessEnabled by viewModel.gaplessEnabled.collectAsState()
    val normalizeAudio by viewModel.normalizeAudio.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    val sleepTimerOptions = listOf(0 to "Off", 15 to "15m", 30 to "30m", 45 to "45m", 60 to "60m")

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Audio Engine",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("settings_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
            Spacer(modifier = Modifier.height(12.dp))

            // Sleep Timer Section
            Text(
                text = "SLEEP TIMER",
                style = MaterialTheme.typography.labelSmall,
                color = CyanPrimary,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, tint = CyanPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Auto Turn-off Timer",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                        }

                        if (sleepTimerRemaining != null) {
                            val mins = (sleepTimerRemaining!! / (1000 * 60))
                            val secs = ((sleepTimerRemaining!! / 1000) % 60)
                            Text(
                                text = String.format("%02d:%02d left", mins, secs),
                                color = CyanPrimary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        sleepTimerOptions.forEach { (minutes, label) ->
                            FilterChip(
                                selected = if (minutes == 0) sleepTimerRemaining == null else false,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.setSleepTimer(minutes)
                                },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanPrimary.copy(alpha = 0.25f),
                                    selectedLabelColor = CyanPrimary,
                                    containerColor = SurfaceCardElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Audio Processing Section
            Text(
                text = "AUDIO PLAYBACK ENGINE",
                style = MaterialTheme.typography.labelSmall,
                color = CyanPrimary,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Gapless Playback Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gapless Playback", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                            Text("Continuous playback without audible gaps between tracks", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(
                            checked = gaplessEnabled,
                            onCheckedChange = { viewModel.setGaplessEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    Divider(color = SurfaceCardElevated, modifier = Modifier.padding(vertical = 12.dp))

                    // Crossfade Toggle & Duration Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Crossfade Tracks", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                            Text("Smoothly blend the end of one song into the next", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(
                            checked = crossfadeEnabled,
                            onCheckedChange = { viewModel.setCrossfadeEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }

                    if (crossfadeEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Crossfade Duration", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("${crossfadeDuration}s", style = MaterialTheme.typography.bodySmall, color = CyanPrimary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = crossfadeDuration.toFloat(),
                            onValueChange = { viewModel.setCrossfadeDuration(it.toInt()) },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(thumbColor = CyanPrimary, activeTrackColor = CyanPrimary)
                        )
                    }

                    Divider(color = SurfaceCardElevated, modifier = Modifier.padding(vertical = 12.dp))

                    // Audio Normalization Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Audio Normalization", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                            Text("Equalizes loudness across tracks to avoid sudden volume spikes", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(
                            checked = normalizeAudio,
                            onCheckedChange = { viewModel.setNormalizeAudio(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Equalizer Studio Shortcut
            Button(
                onClick = onNavigateToEqualizer,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Equalizer, contentDescription = null, tint = CyanPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Open 5-Band Equalizer Studio", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Library Rescan
            Button(
                onClick = { viewModel.rescanLibrary() },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = CyanPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (isScanning) "Indexing storage..." else "Rescan Device Storage", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
