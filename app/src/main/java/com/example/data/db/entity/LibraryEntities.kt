package com.example.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist

@Entity(tableName = "albums", indices = [Index("title"), Index("artist")])
data class AlbumEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: String?,
    val yearReleased: Int = 2024,
    val songCount: Int = 0
) {
    fun toDomainModel(): Album = Album(
        id = id,
        title = title,
        artist = artist,
        artworkUri = artworkUri,
        yearReleased = yearReleased,
        songCount = songCount
    )
}

@Entity(tableName = "artists", indices = [Index("name")])
data class ArtistEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val artworkUri: String?,
    val songCount: Int = 0
) {
    fun toDomainModel(): Artist = Artist(
        id = id,
        name = name,
        artworkUri = artworkUri,
        songCount = songCount
    )
}

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val artworkUri: String? = null
) {
    fun toDomainModel(songCount: Int = 0): Playlist = Playlist(
        id = id,
        name = name,
        description = description,
        createdDate = createdDate,
        artworkUri = artworkUri,
        songCount = songCount
    )
}

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playlistId"), Index("songId")]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long,
    val position: Int = 0
)
