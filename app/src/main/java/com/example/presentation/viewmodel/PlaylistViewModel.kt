package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.repository.PlaylistRepository
import com.example.domain.service.PlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _selectedPlaylistId = MutableStateFlow<Long?>(null)

    val currentPlaylistSongs: StateFlow<List<Song>> = _selectedPlaylistId.flatMapLatest { id ->
        if (id != null) playlistRepository.getSongsForPlaylist(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectPlaylist(id: Long) {
        _selectedPlaylistId.value = id
    }

    fun playPlaylistSong(song: Song) {
        val songs = currentPlaylistSongs.value
        val index = songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playerController.loadPlaylist(songs, index)
    }

    fun playAll() {
        val songs = currentPlaylistSongs.value
        if (songs.isNotEmpty()) {
            playerController.loadPlaylist(songs, 0)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun removeSong(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }
}
