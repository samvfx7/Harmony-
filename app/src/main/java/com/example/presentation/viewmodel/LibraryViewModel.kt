package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.repository.PlaylistRepository
import com.example.data.repository.SongRepository
import com.example.domain.service.PlayerController
import com.example.domain.usecase.LibraryScanner
import com.example.presentation.state.LibraryUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val libraryScanner: LibraryScanner,
    private val playerController: PlayerController
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _scanProgressMessage = MutableStateFlow("")
    val scanProgressMessage: StateFlow<String> = _scanProgressMessage

    private var scanJob: Job? = null

    init {
        // Initial scan on startup if library is empty
        viewModelScope.launch {
            val count = songRepository.getSongCount()
            if (count == 0) {
                rescanLibrary()
            }
        }
    }

    val uiState: StateFlow<LibraryUIState> = combine(
        _searchQuery.debounce(150).distinctUntilChanged().flatMapLatest { query ->
            if (query.isBlank()) songRepository.getAllSongs() else songRepository.searchSongs(query)
        },
        songRepository.getAllAlbums(),
        songRepository.getAllArtists(),
        playlistRepository.getAllPlaylists(),
        songRepository.getFavoriteSongs(),
        _searchQuery,
        _isScanning,
        _scanProgressMessage,
        _selectedTab
    ) { params: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        LibraryUIState(
            songs = params[0] as List<Song>,
            albums = params[1] as List<Album>,
            artists = params[2] as List<Artist>,
            playlists = params[3] as List<Playlist>,
            favoriteSongs = params[4] as List<Song>,
            searchQuery = params[5] as String,
            isScanning = params[6] as Boolean,
            scanProgressMessage = params[7] as String,
            selectedTab = params[8] as Int
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUIState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun rescanLibrary() {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _isScanning.value = true
            libraryScanner.scanLibrary().collect { progress ->
                _scanProgressMessage.value = progress.statusMessage
                if (progress.isComplete) {
                    _isScanning.value = false
                }
            }
        }
    }

    fun playSong(song: Song, playlistContext: List<Song>? = null) {
        val list = playlistContext ?: uiState.value.songs
        val index = list.indexOfFirst { it.id == song.id }.takeIf { it >= 0 } ?: 0
        playerController.loadPlaylist(list, index)
    }

    fun playAll(songs: List<Song>) {
        if (songs.isNotEmpty()) {
            playerController.loadPlaylist(songs, 0)
        }
    }

    fun shuffleAll(songs: List<Song>) {
        if (songs.isNotEmpty()) {
            val shuffled = songs.shuffled()
            playerController.setShuffleMode(true)
            playerController.loadPlaylist(shuffled, 0)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            songRepository.toggleFavorite(song.id, !song.isFavorite)
        }
    }

    fun createPlaylist(name: String, description: String = "") {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, description)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(playlistId, songId)
        }
    }
}
