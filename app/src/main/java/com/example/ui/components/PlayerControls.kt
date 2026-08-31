package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FavoritePink
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PlayButtonGradient
import com.example.ui.theme.ProgressGradient
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MainPlaybackControls(
    isPlaying: Boolean,
    shuffleMode: Boolean,
    repeatMode: Int,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    // Pulsing glowing animation on play/pause button
    val infiniteTransition = rememberInfiniteTransition(label = "PlayGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.05f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScale"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle mode
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onShuffleClick()
            },
            modifier = Modifier.testTag("shuffle_button")
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (shuffleMode) CyanPrimary else TextMuted,
                modifier = Modifier.size(26.dp)
            )
        }

        // Center cluster: Prev, Big Gradient Play/Pause, Next
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onSkipPreviousClick()
                },
                modifier = Modifier
                    .size(52.dp)
                    .testTag("prev_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous Track",
                    tint = TextPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            // 80dp Multi-color Gradient Play/Pause Button with shadow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(glowScale)
                    .size(80.dp)
                    .shadow(
                        elevation = if (isPlaying) 24.dp else 12.dp,
                        shape = CircleShape,
                        ambientColor = PurpleAccent,
                        spotColor = PurpleAccent
                    )
                    .clip(CircleShape)
                    .background(PlayButtonGradient)
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onPlayPauseClick()
                    }
                    .testTag("play_pause_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onSkipNextClick()
                },
                modifier = Modifier
                    .size(52.dp)
                    .testTag("next_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next Track",
                    tint = TextPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Repeat mode
        IconButton(
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onRepeatClick()
            },
            modifier = Modifier.testTag("repeat_button")
        ) {
            Icon(
                imageVector = if (repeatMode == Player.REPEAT_MODE_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = "Repeat Mode",
                tint = if (repeatMode != Player.REPEAT_MODE_OFF) CyanPrimary else TextMuted,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun ImmersiveControlBar(
    playbackSpeed: Float,
    equalizerPresetName: String,
    pitchSemitones: Int,
    onSpeedClick: () -> Unit,
    onEqualizerClick: () -> Unit,
    onPitchClick: () -> Unit,
    onQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0x14FFFFFF), // border-white/5
                shape = RoundedCornerShape(16.dp)
            )
            .background(Color(0x0AFFFFFF), RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onSpeedClick()
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("quick_speed_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Playback Speed",
                    tint = if (playbackSpeed != 1.0f) CyanPrimary else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${playbackSpeed}x",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = if (playbackSpeed != 1.0f) CyanPrimary else TextSecondary
                )
            }

            // Equalizer preset
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onEqualizerClick()
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("quick_eq_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = "Equalizer Preset",
                    tint = CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = equalizerPresetName.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = CyanPrimary
                )
            }

            // Pitch / Tune
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onPitchClick()
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("quick_pitch_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Pitch Tuning",
                    tint = if (pitchSemitones != 0) CyanPrimary else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                val sign = if (pitchSemitones > 0) "+" else ""
                Text(
                    text = "$sign${pitchSemitones} ST",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = if (pitchSemitones != 0) CyanPrimary else TextSecondary
                )
            }

            // Queue
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onQueueClick()
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("quick_queue_button")
            ) {
                Icon(
                    imageVector = Icons.Default.QueueMusic,
                    contentDescription = "Queue",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "QUEUE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun SmoothProgressBar(
    currentPosition: Long,
    duration: Long,
    currentTimeDisplay: String,
    durationDisplay: String,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val progress = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = progress,
            onValueChange = { newProgress ->
                val targetMs = (newProgress * duration).toLong()
                onSeek(targetMs)
            },
            onValueChangeFinished = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = CyanPrimary,
                inactiveTrackColor = Color(0x26FFFFFF)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("progress_slider")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTimeDisplay,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = TextMuted
            )
            Text(
                text = durationDisplay,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = TextMuted
            )
        }
    }
}

