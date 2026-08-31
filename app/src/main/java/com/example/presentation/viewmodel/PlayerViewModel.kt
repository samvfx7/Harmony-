package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Song
import com.example.data.repository.SongRepository
import com.example.domain.service.PlayerController
import com.example.presentation.state.PlayerUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerController: PlayerController,
    private val songRepository: SongRepository
) : ViewModel() {

    val uiState: StateFlow<PlayerUIState> = combine(
        playerController.currentTrackFlow,
        playerController.isPlayingFlow,
        playerController.currentPositionFlow,
        playerController.durationFlow,
        playerController.favoriteFlow,
        playerController.repeatModeFlow,
        playerController.shuffleModeFlow,
        playerController.playlistFlow,
        playerController.currentTimeDisplay,
        playerController.durationDisplay,
        playerController.sleepTimerFlow,
        playerController.audioFFTFlow
    ) { params: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        PlayerUIState(
            currentSong = params[0] as? Song,
            isPlaying = params[1] as? Boolean ?: false,
            currentPosition = params[2] as? Long ?: 0L,
            duration = params[3] as? Long ?: 0L,
            isFavorite = params[4] as? Boolean ?: false,
            repeatMode = params[5] as? Int ?: 0,
            shuffleMode = params[6] as? Boolean ?: false,
            queue = params[7] as? List<Song> ?: emptyList(),
            currentTimeDisplay = params[8] as? String ?: "00:00",
            durationDisplay = params[9] as? String ?: "00:00",
            sleepTimeRemainingMs = params[10] as? Long,
            audioFFT = params[11] as? FloatArray ?: FloatArray(28) { 0.05f }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerUIState()
    )

    fun play() = playerController.play()
    fun pause() = playerController.pause()
    fun togglePlayPause() = playerController.togglePlayPause()
    fun seekTo(positionMs: Long) = playerController.seekTo(positionMs)
    fun skipNext() = playerController.skipNext()
    fun skipPrevious() = playerController.skipPrevious()
    fun toggleRepeatMode() = playerController.toggleRepeatMode()
    fun toggleShuffleMode() = playerController.toggleShuffleMode()
    fun toggleFavorite() = playerController.toggleFavorite()
    fun removeFromQueue(index: Int) = playerController.removeFromQueue(index)
    fun moveInQueue(from: Int, to: Int) = playerController.moveInQueue(from, to)
    fun clearQueue() = playerController.clearQueue()
    fun playSongFromQueue(index: Int) {
        val q = uiState.value.queue
        if (index in q.indices) {
            playerController.loadPlaylist(q, index)
        }
    }
}
