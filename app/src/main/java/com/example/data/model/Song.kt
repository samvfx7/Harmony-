package com.example.data.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long, // in milliseconds
    val path: String,
    val artworkUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val bitrate: Int = 320, // kbps
    val sampleRate: Int = 44100, // Hz
    val codec: String = "MP3",
    val isLossless: Boolean = false,
    val fileSize: Long = 0L,
    val albumId: Long = 0L,
    val artistId: Long = 0L,
    val customPitch: Float? = null,
    val customSpeed: Float? = null
) {
    val formattedDuration: String
        get() = formatDuration(duration)

    companion object {
        fun formatDuration(durationMs: Long): String {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}
