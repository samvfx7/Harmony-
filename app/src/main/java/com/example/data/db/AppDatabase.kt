package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.db.dao.AlbumDao
import com.example.data.db.dao.ArtistDao
import com.example.data.db.dao.AudioEffectsDao
import com.example.data.db.dao.PlaybackHistoryDao
import com.example.data.db.dao.PlaylistDao
import com.example.data.db.dao.SongDao
import com.example.data.db.entity.AlbumEntity
import com.example.data.db.entity.ArtistEntity
import com.example.data.db.entity.PlaybackHistoryEntity
import com.example.data.db.entity.PlaylistEntity
import com.example.data.db.entity.PlaylistSongCrossRef
import com.example.data.db.entity.SavedEqualizerPresetEntity
import com.example.data.db.entity.SongEntity
import com.example.data.db.entity.UserAudioEffectsPreferenceEntity

@Database(
    entities = [
        SongEntity::class,
        AlbumEntity::class,
        ArtistEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        UserAudioEffectsPreferenceEntity::class,
        SavedEqualizerPresetEntity::class,
        PlaybackHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun audioEffectsDao(): AudioEffectsDao
    abstract fun playbackHistoryDao(): PlaybackHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "harmony_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
