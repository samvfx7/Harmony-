package com.example.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.Song

@Entity(
    tableName = "songs",
    indices = [
        Index("title"),
        Index("artist"),
        Index("albumId"),
        Index("isFavorite")
    ]
)
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val artworkUri: String?,
    val dateAdded: Long,
    val isFavorite: Boolean = false,
    val bitrate: Int = 320,
    val sampleRate: Int = 44100,
    val codec: String = "MP3",
    val isLossless: Boolean = false,
    val fileSize: Long = 0L,
    val albumId: Long = 0L,
    val artistId: Long = 0L,
    val playCount: Int = 0,
    val lastPlayedDate: Long? = null,
    val customPitch: Float? = null,
    val customSpeed: Float? = null
) {
    fun toDomainModel(): Song = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        path = path,
        artworkUri = artworkUri,
        dateAdded = dateAdded,
        isFavorite = isFavorite,
        bitrate = bitrate,
        sampleRate = sampleRate,
        codec = codec,
        isLossless = isLossless,
        fileSize = fileSize,
        albumId = albumId,
        artistId = artistId,
        customPitch = customPitch,
        customSpeed = customSpeed
    )

    companion object {
        fun fromDomainModel(song: Song, playCount: Int = 0, lastPlayedDate: Long? = null): SongEntity = SongEntity(
            id = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            duration = song.duration,
            path = song.path,
            artworkUri = song.artworkUri,
            dateAdded = song.dateAdded,
            isFavorite = song.isFavorite,
            bitrate = song.bitrate,
            sampleRate = song.sampleRate,
            codec = song.codec,
            isLossless = song.isLossless,
            fileSize = song.fileSize,
            albumId = song.albumId,
            artistId = song.artistId,
            playCount = playCount,
            lastPlayedDate = lastPlayedDate,
            customPitch = song.customPitch,
            customSpeed = song.customSpeed
        )
    }
}
