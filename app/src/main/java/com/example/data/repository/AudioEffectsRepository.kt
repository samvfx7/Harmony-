package com.example.data.repository

import com.example.data.db.dao.AudioEffectsDao
import com.example.data.db.dao.PlaybackHistoryDao
import com.example.data.db.entity.PlaybackHistoryEntity
import com.example.data.db.entity.SavedEqualizerPresetEntity
import com.example.data.db.entity.UserAudioEffectsPreferenceEntity
import com.example.data.model.AudioEffectsState
import com.example.data.model.EqualizerPreset
import com.example.data.model.PlaybackHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AudioEffectsRepository {
    fun getAudioEffectsState(): Flow<AudioEffectsState>
    suspend fun saveAudioEffectsState(state: AudioEffectsState)
    fun getCustomPresets(): Flow<List<EqualizerPreset>>
    suspend fun saveCustomPreset(preset: EqualizerPreset): Long
    suspend fun deleteCustomPreset(presetId: Long)
}

class AudioEffectsRepositoryImpl(
    private val audioEffectsDao: AudioEffectsDao
) : AudioEffectsRepository {

    override fun getAudioEffectsState(): Flow<AudioEffectsState> {
        return audioEffectsDao.getPreferences().map { entity ->
            if (entity == null) {
                AudioEffectsState()
            } else {
                val preset = EqualizerPreset.PRESETS.find { it.name.equals(entity.equalizerPresetName, ignoreCase = true) }
                    ?: EqualizerPreset.NORMAL
                AudioEffectsState(
                    playbackSpeed = entity.playbackSpeed,
                    pitch = entity.pitch,
                    equalizerPreset = preset,
                    bassBoost = entity.bassBoost,
                    volume = entity.volume,
                    crossfadeEnabled = entity.crossfadeEnabled,
                    crossfadeDuration = entity.crossfadeDuration,
                    gaplessPlaybackEnabled = entity.gaplessPlaybackEnabled,
                    normalizeAudio = entity.normalizeAudio,
                    dynamicRangeCompression = entity.dynamicRangeCompression
                )
            }
        }
    }

    override suspend fun saveAudioEffectsState(state: AudioEffectsState) {
        val entity = UserAudioEffectsPreferenceEntity(
            id = 1,
            playbackSpeed = state.playbackSpeed,
            pitch = state.pitch,
            equalizerPresetName = state.equalizerPreset.name,
            bassBoost = state.bassBoost,
            volume = state.volume,
            crossfadeEnabled = state.crossfadeEnabled,
            crossfadeDuration = state.crossfadeDuration,
            gaplessPlaybackEnabled = state.gaplessPlaybackEnabled,
            normalizeAudio = state.normalizeAudio,
            dynamicRangeCompression = state.dynamicRangeCompression,
            savedTimestamp = System.currentTimeMillis()
        )
        audioEffectsDao.savePreferences(entity)
    }

    override fun getCustomPresets(): Flow<List<EqualizerPreset>> {
        return audioEffectsDao.getCustomPresets().map { list ->
            list.map { entity ->
                val gains = entity.bandGainsJson.split(",").mapNotNull { it.trim().toFloatOrNull() }
                EqualizerPreset(
                    name = entity.name,
                    bands = EqualizerPreset.createBands(gains),
                    isCustom = true
                )
            }
        }
    }

    override suspend fun saveCustomPreset(preset: EqualizerPreset): Long {
        val gainsStr = preset.bands.joinToString(",") { it.gain.toString() }
        val entity = SavedEqualizerPresetEntity(
            name = preset.name,
            bandGainsJson = gainsStr,
            isCustom = true
        )
        return audioEffectsDao.saveCustomPreset(entity)
    }

    override suspend fun deleteCustomPreset(presetId: Long) {
        audioEffectsDao.deleteCustomPreset(presetId)
    }
}

interface PlaybackHistoryRepository {
    suspend fun recordPlayback(songId: Long, duration: Long)
    fun getRecentHistory(limit: Int = 50): Flow<List<PlaybackHistory>>
    suspend fun clearHistory()
}

class PlaybackHistoryRepositoryImpl(
    private val playbackHistoryDao: PlaybackHistoryDao
) : PlaybackHistoryRepository {

    override suspend fun recordPlayback(songId: Long, duration: Long) {
        playbackHistoryDao.insertHistory(
            PlaybackHistoryEntity(
                songId = songId,
                playedAt = System.currentTimeMillis(),
                duration = duration
            )
        )
    }

    override fun getRecentHistory(limit: Int): Flow<List<PlaybackHistory>> {
        return playbackHistoryDao.getRecentHistory(limit).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun clearHistory() {
        playbackHistoryDao.clearHistory()
    }
}
