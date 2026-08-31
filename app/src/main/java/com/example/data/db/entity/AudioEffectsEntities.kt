package com.example.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.AudioEffectsState
import com.example.data.model.EqualizerPreset
import com.example.data.model.PlaybackHistory

@Entity(tableName = "user_audio_effects")
data class UserAudioEffectsPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val playbackSpeed: Float = 1.0f,
    val pitch: Float = 0.0f,
    val equalizerPresetName: String = "Normal",
    val bassBoost: Float = 0.0f,
    val volume: Float = 1.0f,
    val crossfadeEnabled: Boolean = false,
    val crossfadeDuration: Int = 2,
    val gaplessPlaybackEnabled: Boolean = true,
    val normalizeAudio: Boolean = false,
    val dynamicRangeCompression: Boolean = false,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_equalizer_presets")
data class SavedEqualizerPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val bandGainsJson: String, // comma-separated or json float gains
    val isCustom: Boolean = true,
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "playback_history",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("songId"), Index("playedAt")]
)
data class PlaybackHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val songId: Long,
    val playedAt: Long = System.currentTimeMillis(),
    val duration: Long = 0L
) {
    fun toDomainModel(): PlaybackHistory = PlaybackHistory(
        id = id,
        songId = songId,
        playedAt = playedAt,
        duration = duration
    )
}
