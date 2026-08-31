package com.example.presentation.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.presentation.state.LibraryUIState
import com.example.presentation.state.PlayerUIState
import com.example.presentation.viewmodel.LibraryViewModel
import com.example.presentation.viewmodel.PlayerViewModel
import com.example.ui.components.AlbumGridCard
import com.example.ui.components.LiquidBackgroundAura
import com.example.ui.components.LiquidGlassSpecularBorder
import com.example.ui.components.MiniPlayer
import com.example.ui.components.PlaylistItem
import com.example.ui.components.SongCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    libraryUIState: LibraryUIState,
    playerUIState: PlayerUIState,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    onMiniPlayerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTrackInfoClick: (Song) -> Unit,
    onPlaylistClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var selectedSongForPlaylist by remember { mutableStateOf<Song?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val tabs = listOf("Tracks", "Albums", "Artists", "Playlists", "Favorites")

    LiquidBackgroundAura(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        if (isSearchExpanded) {
                            OutlinedTextField(
                                value = libraryUIState.searchQuery,
                                onValueChange = { libraryViewModel.onSearchQueryChanged(it) },
                                placeholder = { Text("Search songs, artists, albums...", color = TextSecondary) },
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        libraryViewModel.onSearchQueryChanged("")
                                        isSearchExpanded = false
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Close Search", tint = TextPrimary)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedContainerColor = Color(0x1AFFFFFF),
                                    unfocusedContainerColor = Color(0x0DFFFFFF),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = CircleShape,
                                modifier = Modifier.fillMaxWidth().testTag("search_text_field")
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(CyanPrimary, PurpleAccent)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LibraryMusic,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Harmony",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = TextPrimary
                                )
                            }
                        }
                    },
                    actions = {
                        if (!isSearchExpanded) {
                            IconButton(
                                onClick = { isSearchExpanded = true },
                                modifier = Modifier.testTag("search_button")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                            }

                            IconButton(
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    libraryViewModel.rescanLibrary()
                                },
                                modifier = Modifier.testTag("rescan_library_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Rescan Storage", tint = CyanPrimary)
                            }

                            IconButton(
                                onClick = onSettingsClick,
                                modifier = Modifier.testTag("settings_button")
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                // Liquid Glass Pill Tabs Row
                ScrollableTabRow(
                    selectedTabIndex = libraryUIState.selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = CyanPrimary,
                    edgePadding = 16.dp,
                    divider = {},
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[libraryUIState.selectedTab])
                                .height(38.dp)
                                .padding(horizontal = 4.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0x3300D9FF), Color(0x337B61FF))
                                    )
                                )
                                .border(1.dp, GlassBorderCyan, CircleShape)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        val isSelected = libraryUIState.selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                libraryViewModel.setSelectedTab(index)
                            },
                            text = {
                                Text(
                                    text = tabTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CyanPrimary else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            },
                            modifier = Modifier.testTag("tab_$index")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        floatingActionButton = {
            if (libraryUIState.selectedTab == 3) {
                FloatingActionButton(
                    onClick = { showCreatePlaylistDialog = true },
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF121212),
                    modifier = Modifier.testTag("create_playlist_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Playlist")
                }
            }
        },
        bottomBar = {
            if (playerUIState.currentSong != null) {
                MiniPlayer(
                    uiState = playerUIState,
                    onMiniPlayerClick = onMiniPlayerClick,
                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                    onSkipNextClick = { playerViewModel.skipNext() }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scanning progress banner if active
            AnimatedVisibility(visible = libraryUIState.isScanning) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCardElevated)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = CyanPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = libraryUIState.scanProgressMessage.ifBlank { "Indexing music library..." },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }

            // Tab Content
            when (libraryUIState.selectedTab) {
                0 -> SongsTabContent(
                    songs = libraryUIState.songs,
                    currentSongId = playerUIState.currentSong?.id,
                    isPlaying = playerUIState.isPlaying,
                    onSongClick = { song -> libraryViewModel.playSong(song, libraryUIState.songs) },
                    onPlayAll = { libraryViewModel.playAll(libraryUIState.songs) },
                    onShuffleAll = { libraryViewModel.shuffleAll(libraryUIState.songs) },
                    onFavoriteToggle = { libraryViewModel.toggleFavorite(it) },
                    onAddToPlaylist = { selectedSongForPlaylist = it },
                    onShowInfo = onTrackInfoClick
                )
                1 -> AlbumsTabContent(
                    albums = libraryUIState.albums,
                    onAlbumClick = { album ->
                        // Play all songs in album
                        val albumSongs = libraryUIState.songs.filter { it.albumId == album.id }
                        libraryViewModel.playAll(albumSongs)
                    }
                )
                2 -> ArtistsTabContent(
                    artists = libraryUIState.artists,
                    onArtistClick = { artist ->
                        val artistSongs = libraryUIState.songs.filter { it.artistId == artist.id }
                        libraryViewModel.playAll(artistSongs)
                    }
                )
                3 -> PlaylistsTabContent(
                    playlists = libraryUIState.playlists,
                    onPlaylistClick = onPlaylistClick
                )
                4 -> FavoritesTabContent(
                    favorites = libraryUIState.favoriteSongs,
                    currentSongId = playerUIState.currentSong?.id,
                    isPlaying = playerUIState.isPlaying,
                    onSongClick = { song -> libraryViewModel.playSong(song, libraryUIState.favoriteSongs) },
                    onPlayAll = { libraryViewModel.playAll(libraryUIState.favoriteSongs) },
                    onShuffleAll = { libraryViewModel.shuffleAll(libraryUIState.favoriteSongs) },
                    onFavoriteToggle = { libraryViewModel.toggleFavorite(it) },
                    onShowInfo = onTrackInfoClick
                )
            }
        }
    }

    // Dialog: Create Playlist
    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePlaylistDialog = false },
            title = { Text("New Playlist", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            libraryViewModel.createPlaylist(newPlaylistName.trim())
                            newPlaylistName = ""
                            showCreatePlaylistDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text("Create", color = Color(0xFF121212))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlaylistDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCardElevated
        )
    }

    // Dialog: Select Playlist to Add Song
    if (selectedSongForPlaylist != null) {
        val songToAdd = selectedSongForPlaylist!!
        AlertDialog(
            onDismissRequest = { selectedSongForPlaylist = null },
            title = { Text("Add to Playlist", color = TextPrimary) },
            text = {
                Column {
                    if (libraryUIState.playlists.isEmpty()) {
                        Text("No playlists created yet. Create one first!", color = TextSecondary)
                    } else {
                        libraryUIState.playlists.forEach { playlist ->
                            TextButton(
                                onClick = {
                                    libraryViewModel.addSongToPlaylist(playlist.id, songToAdd.id)
                                    selectedSongForPlaylist = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(playlist.name, color = CyanPrimary, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedSongForPlaylist = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceCardElevated
        )
    }
}
}

@Composable
fun SongsTabContent(
    songs: List<Song>,
    currentSongId: Long?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onShowInfo: (Song) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onPlayAll,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    modifier = Modifier.weight(1f).testTag("play_all_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF121212))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play All (${songs.size})", color = Color(0xFF121212), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onShuffleAll,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardElevated),
                    modifier = Modifier.weight(1f).testTag("shuffle_all_button")
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = null, tint = CyanPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Shuffle", color = TextPrimary)
                }
            }
        }

        items(songs, key = { it.id }) { song ->
            SongCard(
                song = song,
                isPlaying = isPlaying && currentSongId == song.id,
                isCurrent = currentSongId == song.id,
                onSongClick = { onSongClick(song) },
                onFavoriteToggle = { onFavoriteToggle(song) },
                onAddToPlaylistClick = { onAddToPlaylist(song) },
                onShowInfoClick = { onShowInfo(song) }
            )
        }
    }
}

@Composable
fun AlbumsTabContent(
    albums: List<com.example.data.model.Album>,
    onAlbumClick: (com.example.data.model.Album) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums, key = { it.id }) { album ->
            AlbumGridCard(album = album, onClick = { onAlbumClick(album) })
        }
    }
}

@Composable
fun ArtistsTabContent(
    artists: List<com.example.data.model.Artist>,
    onArtistClick: (com.example.data.model.Artist) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        items(artists, key = { it.id }) { artist ->
            PlaylistItem(
                playlist = com.example.data.model.Playlist(
                    id = artist.id,
                    name = artist.name,
                    description = "${artist.songCount} tracks"
                ),
                onClick = { onArtistClick(artist) }
            )
        }
    }
}

@Composable
fun PlaylistsTabContent(
    playlists: List<com.example.data.model.Playlist>,
    onPlaylistClick: (Long) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        items(playlists, key = { it.id }) { playlist ->
            PlaylistItem(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
        }
    }
}

@Composable
fun FavoritesTabContent(
    favorites: List<Song>,
    currentSongId: Long?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onPlayAll: () -> Unit,
    onShuffleAll: () -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onShowInfo: (Song) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorite songs yet. Tap heart on tracks to save them!", color = TextSecondary)
        }
    } else {
        SongsTabContent(
            songs = favorites,
            currentSongId = currentSongId,
            isPlaying = isPlaying,
            onSongClick = onSongClick,
            onPlayAll = onPlayAll,
            onShuffleAll = onShuffleAll,
            onFavoriteToggle = onFavoriteToggle,
            onAddToPlaylist = {},
            onShowInfo = onShowInfo
        )
    }
}
