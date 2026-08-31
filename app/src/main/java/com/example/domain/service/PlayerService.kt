package com.example.domain.service

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.HarmonyApp
import timber.log.Timber

@UnstableApi
class PlayerService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        try {
            val appContainer = (applicationContext as? HarmonyApp)?.appContainer
            val playerController = appContainer?.playerController
            val exoPlayer = playerController?.getExoPlayer()

            if (exoPlayer != null) {
                val mediaSessionManager = MediaSessionManager(this, exoPlayer)
                mediaSession = mediaSessionManager.initializeSession()
                Timber.d("PlayerService MediaSession initialized successfully")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error initializing MediaSession in PlayerService")
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
