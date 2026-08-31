package com.example.data.repository

import com.example.data.db.dao.AlbumDao
import com.example.data.db.dao.ArtistDao
import com.example.data.db.dao.SongDao
import com.example.data.db.entity.AlbumEntity
import com.example.data.db.entity.ArtistEntity
import com.example.data.db.entity.SongEntity
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getFavoriteSongs(): Flow<List<Song>>
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
    fun getSongsByArtist(artistId: Long): Flow<List<Song>>
    fun searchSongs(query: String): Flow<List<Song>>
    suspend fun getSongById(id: Long): Song?
    fun getSongFlowById(id: Long): Flow<Song?>
    suspend fun insertSongs(songs: List<Song>)
    suspend fun toggleFavorite(songId: Long, isFavorite: Boolean)
    suspend fun incrementPlayCount(songId: Long)
    suspend fun getSongCount(): Int

    fun getAllAlbums(): Flow<List<Album>>
    fun getAllArtists(): Flow<List<Artist>>
    suspend fun insertAlbums(albums: List<Album>)
    suspend fun insertArtists(artists: List<Artist>)
}

class SongRepositoryImpl(
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return songDao.getFavoriteSongs().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> {
        return songDao.getSongsByAlbum(albumId).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getSongsByArtist(artistId: Long): Flow<List<Song>> {
        return songDao.getSongsByArtist(artistId).map { list -> list.map { it.toDomainModel() } }
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getSongById(id: Long): Song? {
        return songDao.getSongById(id)?.toDomainModel()
    }

    override fun getSongFlowById(id: Long): Flow<Song?> {
        return songDao.getSongFlowById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertSongs(songs: List<Song>) {
        songDao.insertSongs(songs.map { SongEntity.fromDomainModel(it) })
    }

    override suspend fun toggleFavorite(songId: Long, isFavorite: Boolean) {
        songDao.updateFavoriteStatus(songId, isFavorite)
    }

    override suspend fun incrementPlayCount(songId: Long) {
        songDao.incrementPlayCount(songId)
    }

    override suspend fun getSongCount(): Int {
        return songDao.getSongCount()
    }

    override fun getAllAlbums(): Flow<List<Album>> {
        return albumDao.getAllAlbums().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getAllArtists(): Flow<List<Artist>> {
        return artistDao.getAllArtists().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun insertAlbums(albums: List<Album>) {
        albumDao.insertAlbums(albums.map {
            AlbumEntity(
                id = it.id,
                title = it.title,
                artist = it.artist,
                artworkUri = it.artworkUri,
                yearReleased = it.yearReleased,
                songCount = it.songCount
            )
        })
    }

    override suspend fun insertArtists(artists: List<Artist>) {
        artistDao.insertArtists(artists.map {
            ArtistEntity(
                id = it.id,
                name = it.name,
                artworkUri = it.artworkUri,
                songCount = it.songCount
            )
        })
    }
}
