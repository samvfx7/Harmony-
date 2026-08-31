package com.example.domain.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import com.example.MainActivity

class MediaSessionManager(
    private val context: Context,
    private val player: Player
) {
    var mediaSession: MediaSession? = null
        private set

    fun initializeSession(): MediaSession {
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val session = MediaSession.Builder(context, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()

        mediaSession = session
        return session
    }

    fun release() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
    }
}
