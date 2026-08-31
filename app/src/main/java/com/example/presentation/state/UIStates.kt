package com.example.presentation.state

import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.AudioEffectsState
import com.example.data.model.EqualizerPreset
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.model.TrackAudioInfo

data class PlayerUIState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isFavorite: Boolean = false,
    val repeatMode: Int = 0,
    val shuffleMode: Boolean = false,
    val queue: List<Song> = emptyList(),
    val currentTimeDisplay: String = "00:00",
    val durationDisplay: String = "00:00",
    val sleepTimeRemainingMs: Long? = null
)

data class LibraryUIState(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val favoriteSongs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val isScanning: Boolean = false,
    val scanProgressMessage: String = "",
    val selectedTab: Int = 0 // 0: Songs, 1: Albums, 2: Artists, 3: Playlists
)

data class AudioEffectsUIState(
    val playbackSpeed: Float = 1.0f,
    val pitch: Float = 0.0f,
    val equalizerPreset: EqualizerPreset = EqualizerPreset.NORMAL,
    val bassBoost: Float = 0.0f,
    val volume: Float = 1.0f,
    val crossfadeEnabled: Boolean = false,
    val crossfadeDuration: Int = 2,
    val gaplessEnabled: Boolean = true,
    val normalizeAudio: Boolean = false,
    val dynamicRangeCompression: Boolean = false,
    val customPresets: List<EqualizerPreset> = emptyList()
)

data class TrackInfoUIState(
    val song: Song? = null,
    val audioInfo: TrackAudioInfo? = null,
    val playCount: Int = 0,
    val lastPlayedDate: Long? = null
)
