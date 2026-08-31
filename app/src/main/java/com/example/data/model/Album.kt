package com.example.data.model

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: String? = null,
    val yearReleased: Int = 2024,
    val songCount: Int = 0
)

data class Artist(
    val id: Long,
    val name: String,
    val artworkUri: String? = null,
    val songCount: Int = 0
)

data class Playlist(
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val artworkUri: String? = null,
    val songCount: Int = 0
)

data class PlaylistSong(
    val playlistId: Long,
    val songId: Long,
    val position: Int
)

data class QueueItem(
    val songId: Long,
    val position: Int
)
