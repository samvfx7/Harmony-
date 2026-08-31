package com.example.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.db.AppDatabase
import com.example.data.repository.AudioEffectsRepository
import com.example.data.repository.AudioEffectsRepositoryImpl
import com.example.data.repository.PlaybackHistoryRepository
import com.example.data.repository.PlaybackHistoryRepositoryImpl
import com.example.data.repository.PlaylistRepository
import com.example.data.repository.PlaylistRepositoryImpl
import com.example.data.repository.SongRepository
import com.example.data.repository.SongRepositoryImpl
import com.example.domain.service.PlayerController
import com.example.domain.usecase.ApplyEqualizerUseCase
import com.example.domain.usecase.GetPlaylistsUseCase
import com.example.domain.usecase.GetSongsUseCase
import com.example.domain.usecase.LibraryScanner
import com.example.domain.usecase.SetPitchUseCase
import com.example.domain.usecase.SetPlaybackSpeedUseCase
import com.example.presentation.viewmodel.AudioEffectsViewModel
import com.example.presentation.viewmodel.LibraryViewModel
import com.example.presentation.viewmodel.PlayerViewModel
import com.example.presentation.viewmodel.PlaylistViewModel
import com.example.presentation.viewmodel.SettingsViewModel
import com.example.presentation.viewmodel.TrackInfoViewModel

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val songRepository: SongRepository by lazy {
        SongRepositoryImpl(
            database.songDao(),
            database.albumDao(),
            database.artistDao()
        )
    }

    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepositoryImpl(database.playlistDao())
    }

    val audioEffectsRepository: AudioEffectsRepository by lazy {
        AudioEffectsRepositoryImpl(database.audioEffectsDao())
    }

    val playbackHistoryRepository: PlaybackHistoryRepository by lazy {
        PlaybackHistoryRepositoryImpl(database.playbackHistoryDao())
    }

    val playerController: PlayerController by lazy {
        PlayerController(
            context = context,
            songRepository = songRepository,
            audioEffectsRepository = audioEffectsRepository,
            playbackHistoryRepository = playbackHistoryRepository
        )
    }

    val libraryScanner: LibraryScanner by lazy {
        LibraryScanner(context, songRepository)
    }

    val getSongsUseCase: GetSongsUseCase by lazy { GetSongsUseCase(songRepository) }
    val getPlaylistsUseCase: GetPlaylistsUseCase by lazy { GetPlaylistsUseCase(playlistRepository) }
    val applyEqualizerUseCase: ApplyEqualizerUseCase by lazy { ApplyEqualizerUseCase(playerController) }
    val setPlaybackSpeedUseCase: SetPlaybackSpeedUseCase by lazy { SetPlaybackSpeedUseCase(playerController) }
    val setPitchUseCase: SetPitchUseCase by lazy { SetPitchUseCase(playerController) }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(PlayerViewModel::class.java) -> {
                        PlayerViewModel(playerController, songRepository) as T
                    }
                    modelClass.isAssignableFrom(LibraryViewModel::class.java) -> {
                        LibraryViewModel(songRepository, playlistRepository, libraryScanner, playerController) as T
                    }
                    modelClass.isAssignableFrom(AudioEffectsViewModel::class.java) -> {
                        AudioEffectsViewModel(playerController, audioEffectsRepository, songRepository) as T
                    }
                    modelClass.isAssignableFrom(TrackInfoViewModel::class.java) -> {
                        TrackInfoViewModel(playerController) as T
                    }
                    modelClass.isAssignableFrom(PlaylistViewModel::class.java) -> {
                        PlaylistViewModel(playlistRepository, playerController) as T
                    }
                    modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                        SettingsViewModel(playerController, libraryScanner, audioEffectsRepository) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}
