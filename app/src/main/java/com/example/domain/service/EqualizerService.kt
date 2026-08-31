package com.example.domain.service

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import com.example.data.model.EqualizerBand
import com.example.data.model.EqualizerPreset
import timber.log.Timber

class EqualizerService {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var currentAudioSessionId: Int = 0

    fun attachToAudioSession(audioSessionId: Int) {
        if (audioSessionId == 0 || audioSessionId == currentAudioSessionId) return
        currentAudioSessionId = audioSessionId

        release()

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = true
            }
            Timber.d("Attached Equalizer and BassBoost to audio session: $audioSessionId")
        } catch (e: Exception) {
            Timber.w(e, "AudioFX Equalizer not supported on this device/session (safe fallback active)")
            equalizer = null
            bassBoost = null
        }
    }

    fun applyPreset(preset: EqualizerPreset) {
        try {
            val eq = equalizer ?: return
            val numBands = eq.numberOfBands.toInt()
            val minLevel = eq.bandLevelRange[0]
            val maxLevel = eq.bandLevelRange[1]

            preset.bands.forEachIndexed { index, band ->
                if (index < numBands) {
                    // Gain is -12 to +12 dB. Range in mB is typically -1500 to +1500 or -1200 to +1200
                    val targetMillibels = (band.gain * 100).toInt().coerceIn(minLevel.toInt(), maxLevel.toInt()).toShort()
                    eq.setBandLevel(index.toShort(), targetMillibels)
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to apply hardware EQ preset")
        }
    }

    fun applyBandGain(bandIndex: Int, gainDb: Float) {
        try {
            val eq = equalizer ?: return
            if (bandIndex < eq.numberOfBands) {
                val minLevel = eq.bandLevelRange[0]
                val maxLevel = eq.bandLevelRange[1]
                val targetMillibels = (gainDb * 100).toInt().coerceIn(minLevel.toInt(), maxLevel.toInt()).toShort()
                eq.setBandLevel(bandIndex.toShort(), targetMillibels)
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to set band level")
        }
    }

    fun setBassBoost(strength: Float) { // 0.0 to 1.0
        try {
            val bb = bassBoost ?: return
            val strengthShort = (strength.coerceIn(0f, 1f) * 1000).toInt().toShort()
            if (bb.strengthSupported) {
                bb.setStrength(strengthShort)
            }
            bb.enabled = strength > 0.01f
        } catch (e: Exception) {
            Timber.w(e, "Failed to set Bass Boost")
        }
    }

    fun release() {
        try {
            equalizer?.release()
            equalizer = null
            bassBoost?.release()
            bassBoost = null
        } catch (e: Exception) {
            Timber.w(e, "Error releasing AudioFX")
        }
    }
}
