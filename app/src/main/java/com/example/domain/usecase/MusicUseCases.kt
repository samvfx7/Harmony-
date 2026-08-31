package com.example.domain.usecase

import com.example.data.model.EqualizerPreset
import com.example.data.model.Playlist
import com.example.data.model.Song
import com.example.data.repository.PlaylistRepository
import com.example.data.repository.SongRepository
import com.example.domain.service.PlayerController
import kotlinx.coroutines.flow.Flow

class GetSongsUseCase(private val songRepository: SongRepository) {
    fun getAllSongs(): Flow<List<Song>> = songRepository.getAllSongs()
    fun getFavorites(): Flow<List<Song>> = songRepository.getFavoriteSongs()
    fun search(query: String): Flow<List<Song>> = songRepository.searchSongs(query)
}

class GetPlaylistsUseCase(private val playlistRepository: PlaylistRepository) {
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistRepository.getAllPlaylists()
    suspend fun createPlaylist(name: String, description: String = ""): Long =
        playlistRepository.createPlaylist(name, description)
}

class ApplyEqualizerUseCase(private val playerController: PlayerController) {
    fun applyPreset(preset: EqualizerPreset) = playerController.setEqualizerPreset(preset)
    fun applyBand(bandIndex: Int, gainDb: Float) = playerController.setEqualizerBandGain(bandIndex, gainDb)
    fun setBassBoost(boost: Float) = playerController.setBassBoost(boost)
}

class SetPlaybackSpeedUseCase(private val playerController: PlayerController) {
    operator fun invoke(speed: Float) = playerController.setPlaybackSpeed(speed)
}

class SetPitchUseCase(private val playerController: PlayerController) {
    operator fun invoke(semitones: Float) = playerController.setPitch(semitones)
}
