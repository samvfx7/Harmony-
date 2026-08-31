package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Song
import com.example.data.model.TrackAudioInfo
import com.example.domain.service.PlayerController
import com.example.presentation.state.TrackInfoUIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrackInfoViewModel(
    private val playerController: PlayerController
) : ViewModel() {

    val uiState: StateFlow<TrackInfoUIState> = playerController.currentTrackFlow.map { song ->
        if (song != null) {
            TrackAudioInfoUI(song)
        } else {
            TrackInfoUIState()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrackInfoUIState()
    )

    private fun TrackAudioInfoUI(song: Song): TrackInfoUIState {
        val audioInfo = TrackAudioInfo(
            codec = song.codec,
            bitrate = song.bitrate,
            sampleRate = song.sampleRate,
            channels = "Stereo (2.0 ch)",
            duration = song.duration,
            fileSize = song.fileSize,
            isLossless = song.isLossless,
            qualityBadge = if (song.isLossless) "LOSSLESS" else if (song.bitrate >= 320) "HI-FI" else "STANDARD"
        )
        return TrackInfoUIState(
            song = song,
            audioInfo = audioInfo,
            playCount = 12,
            lastPlayedDate = song.dateAdded
        )
    }
}
