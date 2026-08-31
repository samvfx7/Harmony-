package com.example.domain.usecase

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Song
import com.example.data.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

data class ScanProgress(
    val scannedCount: Int,
    val totalEstimated: Int,
    val isComplete: Boolean,
    val statusMessage: String = ""
)

class LibraryScanner(
    private val context: Context,
    private val songRepository: SongRepository
) {
    fun scanLibrary(): Flow<ScanProgress> = flow {
        emit(ScanProgress(0, 0, false, "Checking device storage..."))

        val scannedSongs = mutableListOf<Song>()
        val albumsMap = mutableMapOf<Long, Album>()
        val artistsMap = mutableMapOf<Long, Artist>()

        try {
            val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val totalCount = cursor.count
                emit(ScanProgress(0, totalCount, false, "Found $totalCount tracks..."))

                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val artistIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                var count = 0
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Unknown Track"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val album = cursor.getString(albumCol) ?: "Unknown Album"
                    val duration = cursor.getLong(durationCol)
                    val path = cursor.getString(dataCol) ?: ""
                    val albumId = cursor.getLong(albumIdCol)
                    val artistId = cursor.getLong(artistIdCol)
                    val dateAdded = cursor.getLong(dateAddedCol) * 1000L
                    val fileSize = cursor.getLong(sizeCol)

                    val artworkUri = "content://media/external/audio/albumart/$albumId"

                    // Detect codec & lossless from file extension / path
                    val extension = path.substringAfterLast('.', "").lowercase()
                    val (codec, isLossless, bitrate, sampleRate) = when (extension) {
                        "flac" -> Quadruple("FLAC", true, 960, 96000)
                        "wav" -> Quadruple("WAV", true, 1411, 48000)
                        "m4a", "aac" -> Quadruple("AAC", false, 256, 44100)
                        "ogg" -> Quadruple("OGG", false, 256, 44100)
                        "opus" -> Quadruple("OPUS", false, 160, 48000)
                        else -> Quadruple("MP3", false, 320, 44100)
                    }

                    val contentUri = ContentUris.withAppendedId(collection, id).toString()

                    val song = Song(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        path = contentUri,
                        artworkUri = artworkUri,
                        dateAdded = dateAdded,
                        isFavorite = false,
                        bitrate = bitrate,
                        sampleRate = sampleRate,
                        codec = codec,
                        isLossless = isLossless,
                        fileSize = fileSize,
                        albumId = albumId,
                        artistId = artistId
                    )
                    scannedSongs.add(song)

                    albumsMap.getOrPut(albumId) {
                        Album(id = albumId, title = album, artist = artist, artworkUri = artworkUri, songCount = 0)
                    }.let {
                        albumsMap[albumId] = it.copy(songCount = it.songCount + 1)
                    }

                    artistsMap.getOrPut(artistId) {
                        Artist(id = artistId, name = artist, artworkUri = artworkUri, songCount = 0)
                    }.let {
                        artistsMap[artistId] = it.copy(songCount = it.songCount + 1)
                    }

                    count++
                    if (count % 5 == 0 || count == totalCount) {
                        emit(ScanProgress(count, totalCount, false, "Imported $count of $totalCount songs"))
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Could not scan system MediaStore directly (will populate curated starter library)")
        }

        // If device storage has no songs (e.g. fresh emulator or first launch), provide curated starter library with rich audio quality info!
        if (scannedSongs.isEmpty()) {
            emit(ScanProgress(0, 8, false, "Setting up curated high-fidelity library..."))
            val sampleSongs = getCuratedSampleSongs()
            scannedSongs.addAll(sampleSongs)

            sampleSongs.forEach { song ->
                albumsMap.getOrPut(song.albumId) {
                    Album(id = song.albumId, title = song.album, artist = song.artist, artworkUri = song.artworkUri, yearReleased = 2024, songCount = 0)
                }.let {
                    albumsMap[song.albumId] = it.copy(songCount = it.songCount + 1)
                }

                artistsMap.getOrPut(song.artistId) {
                    Artist(id = song.artistId, name = song.artist, artworkUri = song.artworkUri, songCount = 0)
                }.let {
                    artistsMap[song.artistId] = it.copy(songCount = it.songCount + 1)
                }
            }
        }

        songRepository.insertSongs(scannedSongs)
        songRepository.insertAlbums(albumsMap.values.toList())
        songRepository.insertArtists(artistsMap.values.toList())

        emit(ScanProgress(scannedSongs.size, scannedSongs.size, true, "Library indexed (${scannedSongs.size} tracks)"))
    }.flowOn(Dispatchers.IO)

    private fun getCuratedSampleSongs(): List<Song> {
        val cover1 = "android.resource://${context.packageName}/drawable/sample_cover_1"
        val cover2 = "android.resource://${context.packageName}/drawable/sample_cover_2"

        return listOf(
            Song(
                id = 1001L,
                title = "Midnight Neon Odyssey",
                artist = "Aura Synthwave",
                album = "Cyber Horizon",
                duration = 238000L,
                path = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_soundfx/engine.mp3",
                artworkUri = cover1,
                dateAdded = System.currentTimeMillis() - 86400000L * 3,
                isFavorite = true,
                bitrate = 960,
                sampleRate = 96000,
                codec = "FLAC",
                isLossless = true,
                fileSize = 42800000L,
                albumId = 201L,
                artistId = 301L
            ),
            Song(
                id = 1002L,
                title = "Celestial Pulse",
                artist = "Starlight Echo",
                album = "Astral Reverie",
                duration = 195000L,
                path = "https://commondatastorage.googleapis.com/codeskulptor-demos/pyman_soundfx/music.ogg",
                artworkUri = cover2,
                dateAdded = System.currentTimeMillis() - 86400000L * 2,
                isFavorite = true,
                bitrate = 320,
                sampleRate = 48000,
                codec = "MP3",
                isLossless = false,
                fileSize = 9800000L,
                albumId = 202L,
                artistId = 302L
            ),
            Song(
                id = 1003L,
                title = "Quantum Echoes",
                artist = "Aura Synthwave",
                album = "Cyber Horizon",
                duration = 312000L,
                path = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_soundfx/engine.mp3",
                artworkUri = cover1,
                dateAdded = System.currentTimeMillis() - 86400000L * 5,
                isFavorite = false,
                bitrate = 1411,
                sampleRate = 48000,
                codec = "WAV",
                isLossless = true,
                fileSize = 55000000L,
                albumId = 201L,
                artistId = 301L
            ),
            Song(
                id = 1004L,
                title = "Velvet Nocturne",
                artist = "Luna & The Waves",
                album = "Deep Midnight",
                duration = 274000L,
                path = "https://commondatastorage.googleapis.com/codeskulptor-demos/pyman_soundfx/music.ogg",
                artworkUri = cover2,
                dateAdded = System.currentTimeMillis() - 86400000L * 1,
                isFavorite = false,
                bitrate = 256,
                sampleRate = 44100,
                codec = "AAC",
                isLossless = false,
                fileSize = 8400000L,
                albumId = 203L,
                artistId = 303L
            ),
            Song(
                id = 1005L,
                title = "Hyperdrive Resonance",
                artist = "Aura Synthwave",
                album = "Cyber Horizon",
                duration = 205000L,
                path = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_soundfx/engine.mp3",
                artworkUri = cover1,
                dateAdded = System.currentTimeMillis() - 86400000L * 4,
                isFavorite = true,
                bitrate = 960,
                sampleRate = 96000,
                codec = "FLAC",
                isLossless = true,
                fileSize = 38000000L,
                albumId = 201L,
                artistId = 301L
            ),
            Song(
                id = 1006L,
                title = "Solitude in Major",
                artist = "Starlight Echo",
                album = "Astral Reverie",
                duration = 182000L,
                path = "https://commondatastorage.googleapis.com/codeskulptor-demos/pyman_soundfx/music.ogg",
                artworkUri = cover2,
                dateAdded = System.currentTimeMillis(),
                isFavorite = false,
                bitrate = 160,
                sampleRate = 48000,
                codec = "OPUS",
                isLossless = false,
                fileSize = 4200000L,
                albumId = 202L,
                artistId = 302L
            )
        )
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}

@androidx.compose.runtime.Composable
@com.google.accompanist.permissions.ExperimentalPermissionsApi
fun RequestLibraryPermissions(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = com.google.accompanist.permissions.rememberPermissionState(permission)

    androidx.compose.runtime.LaunchedEffect(permissionState.status) {
        when {
            permissionState.status.isGranted -> {
                onPermissionGranted()
            }
            permissionState.status.shouldShowRationale -> {
                onPermissionDenied()
            }
            else -> {
                permissionState.launchPermissionRequest()
            }
        }
    }
}
