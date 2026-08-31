package com.example.data.repository

import com.example.data.db.dao.PlaylistDao
import com.example.data.db.entity.PlaylistEntity
import com.example.data.db.entity.PlaylistSongCrossRef
import com.example.data.model.Playlist
import com.example.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String, description: String = "", artworkUri: String? = null): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long, position: Int = 0)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>>
    suspend fun getPlaylistById(playlistId: Long): Playlist?
}

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun createPlaylist(name: String, description: String, artworkUri: String?): Long {
        val entity = PlaylistEntity(
            name = name,
            description = description,
            artworkUri = artworkUri
        )
        return playlistDao.insertPlaylist(entity)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long, position: Int) {
        playlistDao.addSongToPlaylist(
            PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = songId,
                position = position
            )
        )
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    override fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsForPlaylist(playlistId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistDao.getPlaylistById(playlistId)?.toDomainModel()
    }
}
