package com.example.domain.audio

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.sqrt

class RealtimeAudioProcessor : BaseAudioProcessor() {

    private val bandCount = 28
    private val currentFFT = FloatArray(bandCount) { 0.05f }
    private val _fftData = MutableStateFlow(FloatArray(bandCount) { 0.05f })
    val fftData: StateFlow<FloatArray> = _fftData.asStateFlow()

    private var processCounter = 0

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        // Pass-through configuration for any format
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val position = inputBuffer.position()
        val limit = inputBuffer.limit()
        val remaining = limit - position

        if (remaining <= 0) return

        // 1. Mandatory pass-through to output buffer for audio playback
        val outputBuffer = replaceOutputBuffer(remaining)
        outputBuffer.put(inputBuffer.duplicate())
        outputBuffer.flip()

        // 2. Process PCM audio sample values for real-time visualization (decimated for performance)
        processCounter++
        if (processCounter % 2 == 0) {
            val duplicate = inputBuffer.duplicate()
            duplicate.order(ByteOrder.LITTLE_ENDIAN)

            val shortCount = remaining / 2
            if (shortCount >= bandCount) {
                val chunkSize = shortCount / bandCount
                val newValues = FloatArray(bandCount)

                for (b in 0 until bandCount) {
                    var sumSquare = 0.0
                    val start = b * chunkSize
                    val end = if (b == bandCount - 1) shortCount else (b + 1) * chunkSize
                    var count = 0

                    val step = (chunkSize / 16).coerceAtLeast(1)
                    var idx = start
                    while (idx < end && duplicate.hasRemaining()) {
                        val sample = duplicate.short / 32768.0
                        sumSquare += sample * sample
                        count++
                        idx += step
                        if (duplicate.remaining() >= step * 2) {
                            duplicate.position(duplicate.position() + (step - 1) * 2)
                        }
                    }

                    val rms = if (count > 0) sqrt(sumSquare / count).toFloat() else 0f
                    // Frequency weighting: boost higher bands slightly for visually balanced spectrum
                    val bandWeight = 1.0f + (b.toFloat() / bandCount) * 1.5f
                    val rawMag = (rms * bandWeight * 3.5f).coerceIn(0.04f, 1.0f)

                    // Temporal smoothing (fast attack, smooth decay)
                    val prev = currentFFT[b]
                    val smoothed = if (rawMag > prev) {
                        prev + (rawMag - prev) * 0.6f
                    } else {
                        prev - (prev - rawMag) * 0.25f
                    }
                    currentFFT[b] = smoothed
                    newValues[b] = smoothed
                }
                _fftData.value = newValues
            }
        }

        // Advance buffer position to signal completion to ExoPlayer
        inputBuffer.position(limit)
    }

    fun resetVisualizer() {
        val resetArray = FloatArray(bandCount) { 0.05f }
        System.arraycopy(resetArray, 0, currentFFT, 0, bandCount)
        _fftData.value = resetArray
    }
}
