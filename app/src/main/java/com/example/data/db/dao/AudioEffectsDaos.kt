package com.example.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.db.entity.PlaybackHistoryEntity
import com.example.data.db.entity.SavedEqualizerPresetEntity
import com.example.data.db.entity.UserAudioEffectsPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioEffectsDao {
    @Query("SELECT * FROM user_audio_effects WHERE id = 1")
    fun getPreferences(): Flow<UserAudioEffectsPreferenceEntity?>

    @Query("SELECT * FROM user_audio_effects WHERE id = 1")
    suspend fun getPreferencesSync(): UserAudioEffectsPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: UserAudioEffectsPreferenceEntity)

    @Query("SELECT * FROM saved_equalizer_presets ORDER BY name ASC")
    fun getCustomPresets(): Flow<List<SavedEqualizerPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCustomPreset(preset: SavedEqualizerPresetEntity): Long

    @Query("DELETE FROM saved_equalizer_presets WHERE id = :presetId")
    suspend fun deleteCustomPreset(presetId: Long)
}

@Dao
interface PlaybackHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PlaybackHistoryEntity)

    @Query("SELECT * FROM playback_history ORDER BY playedAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 50): Flow<List<PlaybackHistoryEntity>>

    @Query("DELETE FROM playback_history")
    suspend fun clearHistory()
}
