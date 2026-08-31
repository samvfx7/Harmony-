package com.example.data.model

data class EqualizerBand(
    val frequency: Float, // Frequency in Hz, e.g. 60f, 250f, 1000f, 4000f, 15000f
    val gain: Float, // Gain in dB, typically -12.0f to +12.0f
    val label: String = ""
)

data class EqualizerPreset(
    val name: String,
    val bands: List<EqualizerBand>,
    val isCustom: Boolean = false
) {
    companion object {
        // Standard 5 frequencies in Hz: 60Hz, 250Hz, 1kHz, 4kHz, 15kHz
        val DEFAULT_FREQUENCIES = listOf(60f, 250f, 1000f, 4000f, 15000f)
        val DEFAULT_LABELS = listOf("60Hz", "250Hz", "1kHz", "4kHz", "15kHz")

        fun createBands(gains: List<Float>): List<EqualizerBand> {
            return gains.mapIndexed { index, gain ->
                EqualizerBand(
                    frequency = DEFAULT_FREQUENCIES.getOrElse(index) { 1000f },
                    gain = gain,
                    label = DEFAULT_LABELS.getOrElse(index) { "Band ${index + 1}" }
                )
            }
        }

        // 11 Presets requested: NORMAL, BASS_BOOST, TREBLE_BOOST, VOCAL, ACOUSTIC, ELECTRONIC, ROCK, POP, HIP_HOP, JAZZ, CLASSICAL
        val NORMAL = EqualizerPreset("Normal", createBands(listOf(0f, 0f, 0f, 0f, 0f)))
        val BASS_BOOST = EqualizerPreset("Bass Boost", createBands(listOf(6f, 4f, 1f, 0f, 0f)))
        val TREBLE_BOOST = EqualizerPreset("Treble Boost", createBands(listOf(0f, 0f, 1f, 4f, 6f)))
        val VOCAL = EqualizerPreset("Vocal", createBands(listOf(-2f, 1f, 5f, 3f, -1f)))
        val ACOUSTIC = EqualizerPreset("Acoustic", createBands(listOf(3f, 2f, 1f, 2f, 3f)))
        val ELECTRONIC = EqualizerPreset("Electronic", createBands(listOf(5f, 3f, 0f, 2f, 5f)))
        val ROCK = EqualizerPreset("Rock", createBands(listOf(5f, 2f, -1f, 4f, 4f)))
        val POP = EqualizerPreset("Pop", createBands(listOf(2f, 3f, 4f, 2f, 1f)))
        val HIP_HOP = EqualizerPreset("Hip-Hop", createBands(listOf(6f, 4f, 0f, 2f, 3f)))
        val JAZZ = EqualizerPreset("Jazz", createBands(listOf(3f, 2f, 1f, 2f, 3f)))
        val CLASSICAL = EqualizerPreset("Classical", createBands(listOf(3f, 2f, 0f, 2f, 4f)))

        val PRESETS = listOf(
            NORMAL, BASS_BOOST, TREBLE_BOOST, VOCAL, ACOUSTIC,
            ELECTRONIC, ROCK, POP, HIP_HOP, JAZZ, CLASSICAL
        )
    }
}

data class AudioEffectsState(
    val playbackSpeed: Float = 1.0f, // 0.25x - 2.0x
    val pitch: Float = 0.0f, // -12 to +12 semitones
    val equalizerPreset: EqualizerPreset = EqualizerPreset.NORMAL,
    val bassBoost: Float = 0.0f, // 0.0 to 1.0
    val volume: Float = 1.0f, // 0.0 to 1.0
    val crossfadeEnabled: Boolean = false,
    val crossfadeDuration: Int = 2, // 0 to 5 seconds
    val gaplessPlaybackEnabled: Boolean = true,
    val normalizeAudio: Boolean = false,
    val dynamicRangeCompression: Boolean = false
)

data class TrackAudioInfo(
    val codec: String,
    val bitrate: Int, // kbps
    val sampleRate: Int, // Hz
    val channels: String, // Mono, Stereo, Surround
    val duration: Long,
    val fileSize: Long,
    val isLossless: Boolean = false,
    val qualityBadge: String = if (isLossless) "LOSSLESS" else if (bitrate >= 320) "HI-FI" else "STANDARD"
)

data class PlaybackHistory(
    val id: Long = 0L,
    val songId: Long,
    val playedAt: Long = System.currentTimeMillis(),
    val duration: Long = 0L
)
