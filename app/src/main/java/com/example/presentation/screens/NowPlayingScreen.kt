package com.example.presentation.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Song
import com.example.presentation.state.AudioEffectsUIState
import com.example.presentation.state.PlayerUIState
import com.example.presentation.viewmodel.AudioEffectsViewModel
import com.example.presentation.viewmodel.PlayerViewModel
import com.example.ui.components.AudioEffectsBottomSheet
import com.example.ui.components.ImmersiveControlBar
import com.example.ui.components.LiquidBackgroundAura
import com.example.ui.components.LiquidGlassSpecularBorder
import com.example.ui.components.MainPlaybackControls
import com.example.ui.components.RealtimeAudioVisualizer
import com.example.ui.components.SmoothProgressBar
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FavoritePink
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.LosslessGold
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    playerUIState: PlayerUIState,
    effectsUIState: AudioEffectsUIState,
    playerViewModel: PlayerViewModel,
    effectsViewModel: AudioEffectsViewModel,
    onBackClick: () -> Unit,
    onQueueClick: () -> Unit,
    onTrackInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    var showEffectsSheet by remember { mutableStateOf(false) }

    val song = playerUIState.currentSong

    // Ambient pulsing glow transition
    val transition = rememberInfiniteTransition(label = "AmbientGlow")
    val ambientPulse by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = if (playerUIState.isPlaying) 0.42f else 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientPulse"
    )

    LiquidBackgroundAura(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "NOW PLAYING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.5.sp,
                                    fontSize = 10.sp
                                ),
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = song?.album ?: "Harmony Collection",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onBackClick()
                            },
                            modifier = Modifier.testTag("now_playing_back")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Collapse Player",
                                tint = TextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onTrackInfoClick()
                            },
                            modifier = Modifier.testTag("track_info_action")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Track Info",
                                tint = TextSecondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Liquid Glass Artwork Container with specular border & cyan backlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Ambient backlight dynamic orb
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.90f)
                            .blur(60.dp)
                            .background(CyanPrimary.copy(alpha = ambientPulse), CircleShape)
                    )

                    // Liquid glass framed artwork container
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                elevation = 28.dp,
                                shape = RoundedCornerShape(44.dp),
                                ambientColor = Color.Black,
                                spotColor = CyanPrimary
                            )
                            .clip(RoundedCornerShape(44.dp))
                            .background(Color(0x1AFFFFFF))
                            .border(1.5.dp, LiquidGlassSpecularBorder, RoundedCornerShape(44.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!song?.artworkUri.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(song?.artworkUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Album Artwork",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Concentric pulsing rings with music note
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .border(1.dp, Color(0x1AFFFFFF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .border(1.dp, LiquidGlassSpecularBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = CyanPrimary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }
                        }

                        // Bottom liquid glass overlay badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xB3000000))
                                    )
                                )
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(CyanPrimary, CircleShape)
                                        .shadow(6.dp, CircleShape, spotColor = CyanPrimary)
                                )
                                Text(
                                    text = if (song?.isLossless == true) "MASTER QUALITY • LOSSLESS" else "HIGH-FIDELITY AUDIO",
                                    color = CyanPrimary,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.8.sp,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real-time Media3 AudioProcessor Frequency Visualizer
                RealtimeAudioVisualizer(
                    fftData = playerUIState.audioFFT,
                    isPlaying = playerUIState.isPlaying,
                    height = 54.dp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title, Favorite, and Serif Italic Artist
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = song?.title ?: "No Track Playing",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp,
                                fontSize = 26.sp
                            ),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .basicMarquee()
                        )

                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                playerViewModel.toggleFavorite()
                            },
                            modifier = Modifier.testTag("now_playing_favorite")
                        ) {
                            Icon(
                                imageVector = if (playerUIState.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (playerUIState.isFavorite) FavoritePink else CyanPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = song?.artist ?: "Select a song to start listening",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 18.sp
                        ),
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Glassmorphic Technical Badges Row (FLAC, Bitrate/Sample Rate, Lossless)
                if (song != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Codec Badge
                        ImmersiveBadge(text = song.codec.uppercase())

                        // Bitrate & Sample Rate
                        val sampleRateKhz = song.sampleRate / 1000
                        val bitDepth = if (song.isLossless) "24-BIT" else "16-BIT"
                        ImmersiveBadge(text = "$bitDepth / ${sampleRateKhz}KHZ")

                        // Lossless / Hi-Fi Badge
                        Surface(
                            color = GlassBackground,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .border(1.dp, GlassBorderCyan, RoundedCornerShape(8.dp))
                                .clickable { onTrackInfoClick() }
                        ) {
                            Text(
                                text = if (song.isLossless) "LOSSLESS" else "HI-RES",
                                color = CyanPrimary,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Smooth Progress Bar
                SmoothProgressBar(
                    currentPosition = playerUIState.currentPosition,
                    duration = playerUIState.duration,
                    currentTimeDisplay = playerUIState.currentTimeDisplay,
                    durationDisplay = playerUIState.durationDisplay,
                    onSeek = { playerViewModel.seekTo(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Main Playback Controls (Shuffle, Prev, 80dp Glow Play/Pause, Next, Repeat)
                MainPlaybackControls(
                    isPlaying = playerUIState.isPlaying,
                    shuffleMode = playerUIState.shuffleMode,
                    repeatMode = playerUIState.repeatMode,
                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                    onSkipNextClick = { playerViewModel.skipNext() },
                    onSkipPreviousClick = { playerViewModel.skipPrevious() },
                    onShuffleClick = { playerViewModel.toggleShuffleMode() },
                    onRepeatClick = { playerViewModel.toggleRepeatMode() }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Immersive Bottom Quick Audio Studio Control Bar
                ImmersiveControlBar(
                    playbackSpeed = effectsUIState.playbackSpeed,
                    equalizerPresetName = effectsUIState.equalizerPreset.name,
                    pitchSemitones = effectsUIState.pitch.toInt(),
                    onSpeedClick = { showEffectsSheet = true },
                    onEqualizerClick = { showEffectsSheet = true },
                    onPitchClick = { showEffectsSheet = true },
                    onQueueClick = onQueueClick
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showEffectsSheet) {
        AudioEffectsBottomSheet(
            uiState = effectsUIState,
            viewModel = effectsViewModel,
            onDismiss = { showEffectsSheet = false }
        )
    }
}

@Composable
fun ImmersiveBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = GlassBackground,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
    ) {
        Text(
            text = text,
            color = TextSecondary,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

