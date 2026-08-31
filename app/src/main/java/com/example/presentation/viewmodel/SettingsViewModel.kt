package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AudioEffectsRepository
import com.example.domain.service.PlayerController
import com.example.domain.usecase.LibraryScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val playerController: PlayerController,
    private val libraryScanner: LibraryScanner,
    private val audioEffectsRepository: AudioEffectsRepository
) : ViewModel() {

    val crossfadeDuration: StateFlow<Int> = playerController.crossfadeDurationFlow
    val crossfadeEnabled: StateFlow<Boolean> = playerController.crossfadeFlow
    val gaplessEnabled: StateFlow<Boolean> = playerController.gaplessFlow
    val normalizeAudio: StateFlow<Boolean> = playerController.normalizeAudioFlow
    val drcEnabled: StateFlow<Boolean> = playerController.drcFlow
    val sleepTimerRemaining: StateFlow<Long?> = playerController.sleepTimerFlow

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun setSleepTimer(minutes: Int) {
        if (minutes > 0) {
            playerController.setSleepTimer(minutes)
        } else {
            playerController.cancelSleepTimer()
        }
    }

    fun cancelSleepTimer() = playerController.cancelSleepTimer()

    fun setCrossfadeDuration(seconds: Int) = playerController.setCrossfadeDuration(seconds)
    fun setCrossfadeEnabled(enabled: Boolean) = playerController.setCrossfadeEnabled(enabled)
    fun setGaplessEnabled(enabled: Boolean) = playerController.setGaplessPlaybackEnabled(enabled)
    fun setNormalizeAudio(enabled: Boolean) = playerController.setNormalizeAudio(enabled)
    fun setDrcEnabled(enabled: Boolean) = playerController.setDynamicRangeCompression(enabled)

    fun rescanLibrary() {
        viewModelScope.launch {
            _isScanning.value = true
            libraryScanner.scanLibrary().collect { progress ->
                if (progress.isComplete) {
                    _isScanning.value = false
                }
            }
        }
    }
}
