package com.example.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.Song
import com.example.di.AppContainer
import com.example.presentation.screens.AudioEffectsScreen
import com.example.presentation.screens.LibraryScreen
import com.example.presentation.screens.NowPlayingScreen
import com.example.presentation.screens.PlaylistDetailScreen
import com.example.presentation.screens.QueueScreen
import com.example.presentation.screens.SettingsScreen
import com.example.presentation.screens.TrackInfoDialog
import com.example.presentation.viewmodel.AudioEffectsViewModel
import com.example.presentation.viewmodel.LibraryViewModel
import com.example.presentation.viewmodel.PlayerViewModel
import com.example.presentation.viewmodel.PlaylistViewModel
import com.example.presentation.viewmodel.SettingsViewModel

object Destinations {
    const val LIBRARY = "library"
    const val NOW_PLAYING = "now_playing"
    const val QUEUE = "queue"
    const val AUDIO_EFFECTS = "audio_effects"
    const val SETTINGS = "settings"
    const val PLAYLIST_DETAIL = "playlist_detail/{playlistId}"
    fun playlistDetail(playlistId: Long) = "playlist_detail/$playlistId"
}

@Composable
fun HarmonyNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val factory = appContainer.provideViewModelFactory()

    val playerViewModel: PlayerViewModel = viewModel(factory = factory)
    val libraryViewModel: LibraryViewModel = viewModel(factory = factory)
    val effectsViewModel: AudioEffectsViewModel = viewModel(factory = factory)
    val playlistViewModel: PlaylistViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    val playerUIState by playerViewModel.uiState.collectAsState()
    val libraryUIState by libraryViewModel.uiState.collectAsState()
    val effectsUIState by effectsViewModel.uiState.collectAsState()

    var activeTrackInfoSong by remember { mutableStateOf<Song?>(null) }

    NavHost(
        navController = navController,
        startDestination = Destinations.LIBRARY,
        enterTransition = { fadeIn(animationSpec = tween(280)) },
        exitTransition = { fadeOut(animationSpec = tween(280)) },
        popEnterTransition = { fadeIn(animationSpec = tween(280)) },
        popExitTransition = { fadeOut(animationSpec = tween(280)) },
        modifier = modifier
    ) {
        composable(Destinations.LIBRARY) {
            LibraryScreen(
                libraryUIState = libraryUIState,
                playerUIState = playerUIState,
                libraryViewModel = libraryViewModel,
                playerViewModel = playerViewModel,
                onMiniPlayerClick = { navController.navigate(Destinations.NOW_PLAYING) },
                onSettingsClick = { navController.navigate(Destinations.SETTINGS) },
                onTrackInfoClick = { song -> activeTrackInfoSong = song },
                onPlaylistClick = { id -> navController.navigate(Destinations.playlistDetail(id)) }
            )
        }

        composable(
            route = Destinations.NOW_PLAYING,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(350)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(350)
                )
            }
        ) {
            NowPlayingScreen(
                playerUIState = playerUIState,
                effectsUIState = effectsUIState,
                playerViewModel = playerViewModel,
                effectsViewModel = effectsViewModel,
                onBackClick = { navController.popBackStack() },
                onQueueClick = { navController.navigate(Destinations.QUEUE) },
                onTrackInfoClick = { activeTrackInfoSong = playerUIState.currentSong }
            )
        }

        composable(Destinations.QUEUE) {
            QueueScreen(
                playerUIState = playerUIState,
                playerViewModel = playerViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Destinations.AUDIO_EFFECTS) {
            AudioEffectsScreen(
                effectsUIState = effectsUIState,
                viewModel = effectsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Destinations.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToEqualizer = { navController.navigate(Destinations.AUDIO_EFFECTS) }
            )
        }

        composable(
            route = Destinations.PLAYLIST_DETAIL,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            PlaylistDetailScreen(
                playlistId = playlistId,
                playlistViewModel = playlistViewModel,
                playerUIState = playerUIState,
                onBackClick = { navController.popBackStack() },
                onTrackInfoClick = { song -> activeTrackInfoSong = song }
            )
        }
    }

    if (activeTrackInfoSong != null) {
        TrackInfoDialog(
            song = activeTrackInfoSong!!,
            onDismiss = { activeTrackInfoSong = null }
        )
    }
}
