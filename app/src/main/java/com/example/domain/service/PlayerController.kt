package com.example.domain.service

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.model.AudioEffectsState
import com.example.data.model.EqualizerPreset
import com.example.data.model.Song
import com.example.data.repository.AudioEffectsRepository
import com.example.data.repository.PlaybackHistoryRepository
import com.example.data.repository.SongRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.pow

class PlayerController(
    private val context: Context,
    private val songRepository: SongRepository,
    private val audioEffectsRepository: AudioEffectsRepository,
    private val playbackHistoryRepository: PlaybackHistoryRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val equalizerService = EqualizerService()

    private var exoPlayer: ExoPlayer? = null

    // State Flows
    private val _currentTrack = MutableStateFlow<Song?>(null)
    val currentTrackFlow: StateFlow<Song?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlayingFlow: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPositionFlow: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val durationFlow: StateFlow<Long> = _duration.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatModeFlow: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleModeFlow: StateFlow<Boolean> = _shuffleMode.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val playlistFlow: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val favoriteFlow: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeedFlow: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _pitch = MutableStateFlow(0.0f) // -12 to +12 semitones
    val pitchFlow: StateFlow<Float> = _pitch.asStateFlow()

    private val _equalizerPreset = MutableStateFlow(EqualizerPreset.NORMAL)
    val equalizerFlow: StateFlow<EqualizerPreset> = _equalizerPreset.asStateFlow()

    private val _bassBoost = MutableStateFlow(0.0f)
    val bassBoostFlow: StateFlow<Float> = _bassBoost.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    val volumeFlow: StateFlow<Float> = _volume.asStateFlow()

    private val _sleepTimerRemainingMs = MutableStateFlow<Long?>(null)
    val sleepTimerFlow: StateFlow<Long?> = _sleepTimerRemainingMs.asStateFlow()

    private val _crossfadeEnabled = MutableStateFlow(false)
    val crossfadeFlow: StateFlow<Boolean> = _crossfadeEnabled.asStateFlow()

    private val _crossfadeDuration = MutableStateFlow(2) // 0-5 sec
    val crossfadeDurationFlow: StateFlow<Int> = _crossfadeDuration.asStateFlow()

    private val _gaplessEnabled = MutableStateFlow(true)
    val gaplessFlow: StateFlow<Boolean> = _gaplessEnabled.asStateFlow()

    private val _normalizeAudio = MutableStateFlow(false)
    val normalizeAudioFlow: StateFlow<Boolean> = _normalizeAudio.asStateFlow()

    private val _drcEnabled = MutableStateFlow(false)
    val drcFlow: StateFlow<Boolean> = _drcEnabled.asStateFlow()

    val currentTimeDisplay: Flow<String> = currentPositionFlow.map { formatTime(it) }
    val durationDisplay: Flow<String> = durationFlow.map { formatTime(it) }

    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        initializePlayer()
        loadPersistedEffects()
        startProgressUpdater()
    }

    private fun initializePlayer() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build()

            exoPlayer = ExoPlayer.Builder(context)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build().apply {
                    addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            _isPlaying.value = isPlaying
                            if (isPlaying) {
                                _currentTrack.value?.let { song ->
                                    scope.launch {
                                        songRepository.incrementPlayCount(song.id)
                                        playbackHistoryRepository.recordPlayback(song.id, song.duration)
                                    }
                                }
                            }
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_READY) {
                                _duration.value = duration.coerceAtLeast(0L)
                                equalizerService.attachToAudioSession(audioSessionId)
                                applyCurrentPlaybackParameters()
                            } else if (playbackState == Player.STATE_ENDED) {
                                skipNext()
                            }
                        }

                        override fun onPositionDiscontinuity(
                            oldPosition: Player.PositionInfo,
                            newPosition: Player.PositionInfo,
                            reason: Int
                        ) {
                            updateCurrentTrackFromPlayer()
                        }
                    })
                }
        } catch (e: Exception) {
            Timber.e(e, "Error initializing ExoPlayer")
        }
    }

    fun getExoPlayer(): ExoPlayer? = exoPlayer

    private fun loadPersistedEffects() {
        scope.launch {
            audioEffectsRepository.getAudioEffectsState().collect { effects ->
                _playbackSpeed.value = effects.playbackSpeed
                _pitch.value = effects.pitch
                _equalizerPreset.value = effects.equalizerPreset
                _bassBoost.value = effects.bassBoost
                _volume.value = effects.volume
                _crossfadeEnabled.value = effects.crossfadeEnabled
                _crossfadeDuration.value = effects.crossfadeDuration
                _gaplessEnabled.value = effects.gaplessPlaybackEnabled
                _normalizeAudio.value = effects.normalizeAudio
                _drcEnabled.value = effects.dynamicRangeCompression

                applyCurrentPlaybackParameters()
                equalizerService.applyPreset(effects.equalizerPreset)
                equalizerService.setBassBoost(effects.bassBoost)
                exoPlayer?.volume = effects.volume
            }
        }
    }

    private fun saveCurrentEffectsState() {
        scope.launch {
            val state = AudioEffectsState(
                playbackSpeed = _playbackSpeed.value,
                pitch = _pitch.value,
                equalizerPreset = _equalizerPreset.value,
                bassBoost = _bassBoost.value,
                volume = _volume.value,
                crossfadeEnabled = _crossfadeEnabled.value,
                crossfadeDuration = _crossfadeDuration.value,
                gaplessPlaybackEnabled = _gaplessEnabled.value,
                normalizeAudio = _normalizeAudio.value,
                dynamicRangeCompression = _drcEnabled.value
            )
            audioEffectsRepository.saveAudioEffectsState(state)
        }
    }

    private fun startProgressUpdater() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPosition.value = player.currentPosition.coerceAtLeast(0L)
                        _duration.value = player.duration.coerceAtLeast(0L)
                    }
                }
                delay(100) // 100ms updates
            }
        }
    }

    // Playback Controls
    fun loadPlaylist(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        _queue.value = songs
        val validIndex = startIndex.coerceIn(0, songs.size - 1)
        val selectedSong = songs[validIndex]
        _currentTrack.value = selectedSong
        _isFavorite.value = selectedSong.isFavorite

        try {
            val player = exoPlayer ?: return
            player.clearMediaItems()
            val mediaItems = songs.map { song ->
                MediaItem.Builder()
                    .setUri(Uri.parse(song.path))
                    .setMediaId(song.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(song.title)
                            .setArtist(song.artist)
                            .setAlbumTitle(song.album)
                            .setArtworkUri(song.artworkUri?.let { Uri.parse(it) })
                            .build()
                    )
                    .build()
            }
            player.setMediaItems(mediaItems, validIndex, 0L)
            player.prepare()
            player.play()
            applyCurrentPlaybackParameters()
        } catch (e: Exception) {
            Timber.e(e, "Error loading playlist into ExoPlayer")
        }
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun seekTo(positionMs: Long) {
        _currentPosition.value = positionMs
        exoPlayer?.seekTo(positionMs)
    }

    fun skipNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val currentIndex = q.indexOfFirst { it.id == _currentTrack.value?.id }
        if (currentIndex != -1 && currentIndex + 1 < q.size) {
            val nextSong = q[currentIndex + 1]
            _currentTrack.value = nextSong
            _isFavorite.value = nextSong.isFavorite
            exoPlayer?.seekToNextMediaItem()
            exoPlayer?.play()
        } else if (_repeatMode.value == Player.REPEAT_MODE_ALL && q.isNotEmpty()) {
            val firstSong = q[0]
            _currentTrack.value = firstSong
            _isFavorite.value = firstSong.isFavorite
            exoPlayer?.seekToDefaultPosition(0)
            exoPlayer?.play()
        }
    }

    fun skipPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        if (_currentPosition.value > 3000) {
            seekTo(0)
            return
        }
        val currentIndex = q.indexOfFirst { it.id == _currentTrack.value?.id }
        if (currentIndex > 0) {
            val prevSong = q[currentIndex - 1]
            _currentTrack.value = prevSong
            _isFavorite.value = prevSong.isFavorite
            exoPlayer?.seekToPreviousMediaItem()
            exoPlayer?.play()
        } else {
            seekTo(0)
        }
    }

    fun setRepeatMode(mode: Int) {
        _repeatMode.value = mode
        exoPlayer?.repeatMode = mode
    }

    fun toggleRepeatMode() {
        val nextMode = when (_repeatMode.value) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        setRepeatMode(nextMode)
    }

    fun setShuffleMode(enabled: Boolean) {
        _shuffleMode.value = enabled
        exoPlayer?.shuffleModeEnabled = enabled
    }

    fun toggleShuffleMode() {
        setShuffleMode(!_shuffleMode.value)
    }

    fun removeFromQueue(index: Int) {
        val currentList = _queue.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _queue.value = currentList
            try {
                exoPlayer?.removeMediaItem(index)
            } catch (e: Exception) {
                Timber.w(e, "Could not remove media item at $index")
            }
        }
    }

    fun moveInQueue(fromIndex: Int, toIndex: Int) {
        val currentList = _queue.value.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            _queue.value = currentList
            try {
                exoPlayer?.moveMediaItem(fromIndex, toIndex)
            } catch (e: Exception) {
                Timber.w(e, "Could not move media item in ExoPlayer")
            }
        }
    }

    fun clearQueue() {
        _queue.value = emptyList()
        _currentTrack.value = null
        exoPlayer?.clearMediaItems()
        _isPlaying.value = false
    }

    fun toggleFavorite() {
        val current = _currentTrack.value ?: return
        val newFav = !current.isFavorite
        _isFavorite.value = newFav
        _currentTrack.value = current.copy(isFavorite = newFav)
        scope.launch {
            songRepository.toggleFavorite(current.id, newFav)
        }
    }

    // Speed & Pitch
    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.25f, 2.0f)
        _playbackSpeed.value = clamped
        applyCurrentPlaybackParameters()
        saveCurrentEffectsState()
    }

    fun setPitch(semitones: Float) {
        val clamped = semitones.coerceIn(-12.0f, 12.0f)
        _pitch.value = clamped
        applyCurrentPlaybackParameters()
        saveCurrentEffectsState()
    }

    fun resetSpeedAndPitch() {
        _playbackSpeed.value = 1.0f
        _pitch.value = 0.0f
        applyCurrentPlaybackParameters()
        saveCurrentEffectsState()
    }

    private fun applyCurrentPlaybackParameters() {
        try {
            val speed = _playbackSpeed.value
            val semitones = _pitch.value
            // Formula: pitchMultiplier = 2 ^ (semitones / 12.0)
            val pitchMultiplier = 2.0.pow(semitones.toDouble() / 12.0).toFloat()
            exoPlayer?.playbackParameters = PlaybackParameters(speed, pitchMultiplier)
        } catch (e: Exception) {
            Timber.e(e, "Error setting PlaybackParameters")
        }
    }

    // Audio Effects
    fun setEqualizerPreset(preset: EqualizerPreset) {
        _equalizerPreset.value = preset
        equalizerService.applyPreset(preset)
        saveCurrentEffectsState()
    }

    fun setEqualizerBandGain(bandIndex: Int, gainDb: Float) {
        val currentPreset = _equalizerPreset.value
        val updatedBands = currentPreset.bands.toMutableList()
        if (bandIndex in updatedBands.indices) {
            updatedBands[bandIndex] = updatedBands[bandIndex].copy(gain = gainDb)
            val customPreset = EqualizerPreset("Custom", updatedBands, isCustom = true)
            _equalizerPreset.value = customPreset
            equalizerService.applyBandGain(bandIndex, gainDb)
            saveCurrentEffectsState()
        }
    }

    fun setBassBoost(boost: Float) {
        val clamped = boost.coerceIn(0f, 1f)
        _bassBoost.value = clamped
        equalizerService.setBassBoost(clamped)
        saveCurrentEffectsState()
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _volume.value = clamped
        exoPlayer?.volume = clamped
        saveCurrentEffectsState()
    }

    fun setCrossfadeEnabled(enabled: Boolean) {
        _crossfadeEnabled.value = enabled
        saveCurrentEffectsState()
    }

    fun setCrossfadeDuration(durationSeconds: Int) {
        _crossfadeDuration.value = durationSeconds.coerceIn(0, 5)
        saveCurrentEffectsState()
    }

    fun setGaplessPlaybackEnabled(enabled: Boolean) {
        _gaplessEnabled.value = enabled
        saveCurrentEffectsState()
    }

    fun setNormalizeAudio(enabled: Boolean) {
        _normalizeAudio.value = enabled
        saveCurrentEffectsState()
    }

    fun setDynamicRangeCompression(enabled: Boolean) {
        _drcEnabled.value = enabled
        saveCurrentEffectsState()
    }

    // Sleep Timer
    fun setSleepTimer(durationMinutes: Int) {
        sleepTimerJob?.cancel()
        val totalMs = durationMinutes * 60 * 1000L
        _sleepTimerRemainingMs.value = totalMs

        sleepTimerJob = scope.launch {
            var remaining = totalMs
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining -= 1000
                _sleepTimerRemainingMs.value = remaining.coerceAtLeast(0L)
            }
            // Sleep timer reached zero: pause music gracefully
            pause()
            _sleepTimerRemainingMs.value = null
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRemainingMs.value = null
    }

    private fun updateCurrentTrackFromPlayer() {
        val player = exoPlayer ?: return
        val currentMediaItemIndex = player.currentMediaItemIndex
        val q = _queue.value
        if (currentMediaItemIndex in q.indices) {
            val song = q[currentMediaItemIndex]
            _currentTrack.value = song
            _isFavorite.value = song.isFavorite
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun release() {
        progressJob?.cancel()
        sleepTimerJob?.cancel()
        equalizerService.release()
        exoPlayer?.release()
        exoPlayer = null
    }
}
