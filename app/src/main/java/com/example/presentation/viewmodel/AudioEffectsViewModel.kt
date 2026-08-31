package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AudioEffectsState
import com.example.data.model.EqualizerPreset
import com.example.data.repository.AudioEffectsRepository
import com.example.domain.service.PlayerController
import com.example.presentation.state.AudioEffectsUIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AudioEffectsViewModel(
    private val playerController: PlayerController,
    private val audioEffectsRepository: AudioEffectsRepository
) : ViewModel() {

    val uiState: StateFlow<AudioEffectsUIState> = combine(
        playerController.playbackSpeedFlow,
        playerController.pitchFlow,
        playerController.equalizerFlow,
        playerController.bassBoostFlow,
        playerController.volumeFlow,
        playerController.crossfadeFlow,
        playerController.crossfadeDurationFlow,
        playerController.gaplessFlow,
        playerController.normalizeAudioFlow,
        playerController.drcFlow,
        audioEffectsRepository.getCustomPresets()
    ) { params ->
        AudioEffectsUIState(
            playbackSpeed = params[0] as Float,
            pitch = params[1] as Float,
            equalizerPreset = params[2] as EqualizerPreset,
            bassBoost = params[3] as Float,
            volume = params[4] as Float,
            crossfadeEnabled = params[5] as Boolean,
            crossfadeDuration = params[6] as Int,
            gaplessEnabled = params[7] as Boolean,
            normalizeAudio = params[8] as Boolean,
            dynamicRangeCompression = params[9] as Boolean,
            customPresets = @Suppress("UNCHECKED_CAST") (params[10] as List<EqualizerPreset>)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AudioEffectsUIState()
    )

    fun setSpeed(speed: Float) = playerController.setPlaybackSpeed(speed)
    fun setPitch(semitones: Float) = playerController.setPitch(semitones)
    fun resetSpeedAndPitch() = playerController.resetSpeedAndPitch()

    fun setEqualizerPreset(preset: EqualizerPreset) = playerController.setEqualizerPreset(preset)
    fun setEqualizerBandGain(bandIndex: Int, gainDb: Float) =
        playerController.setEqualizerBandGain(bandIndex, gainDb)
    fun setBassBoost(boost: Float) = playerController.setBassBoost(boost)
    fun setVolume(volume: Float) = playerController.setVolume(volume)

    fun setCrossfadeEnabled(enabled: Boolean) = playerController.setCrossfadeEnabled(enabled)
    fun setCrossfadeDuration(durationSeconds: Int) = playerController.setCrossfadeDuration(durationSeconds)
    fun setGaplessPlaybackEnabled(enabled: Boolean) = playerController.setGaplessPlaybackEnabled(enabled)
    fun setNormalizeAudio(enabled: Boolean) = playerController.setNormalizeAudio(enabled)
    fun setDynamicRangeCompression(enabled: Boolean) = playerController.setDynamicRangeCompression(enabled)

    fun saveCustomPreset(name: String) {
        viewModelScope.launch {
            val current = uiState.value.equalizerPreset
            val preset = EqualizerPreset(name = name, bands = current.bands, isCustom = true)
            audioEffectsRepository.saveCustomPreset(preset)
        }
    }

    fun resetToDefaults() {
        playerController.resetSpeedAndPitch()
        playerController.setEqualizerPreset(EqualizerPreset.NORMAL)
        playerController.setBassBoost(0.0f)
        playerController.setVolume(1.0f)
        playerController.setNormalizeAudio(false)
        playerController.setDynamicRangeCompression(false)
    }
}
