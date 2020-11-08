package com.jurajkusnier.common

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.jurajkusnier.common.utils.MyNotificationManager

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private lateinit var myMediaSession: MediaSessionCompat
    private lateinit var simpleMediaPlayer: SimpleMediaPlayer
    private var isForeground: Boolean = false

    private val playlist: Playlist<Sound> = PlaylistImpl(SOUNDS)

    override fun onCreate() {
        super.onCreate()
        myMediaSession = getMediaSession()
        sessionToken = myMediaSession.sessionToken
        simpleMediaPlayer = SimpleMediaPlayerImpl(
            player = MediaPlayer(),
            assetManager = resources.assets,
            notificationManager = MyNotificationManager(this),
            mediaSession = myMediaSession,
            playlist = playlist
        )
        myMediaSession.setCallback(simpleMediaPlayer.mediaSessionCallback)
        setupForegroundService()
    }

    private fun setupForegroundService() {
        if (!isForeground) {
            ContextCompat.startForegroundService(
                applicationContext,
                Intent(applicationContext, this@MediaPlaybackService.javaClass)
            )
            isForeground = true
        }
    }

    private fun getMediaSession(): MediaSessionCompat {
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        return MediaSessionCompat(this, "MusicService")
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(playlist.toMediaItemList())
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleMediaPlayer.release()
    }

    companion object {
        private const val MY_MEDIA_ROOT_ID = "media_root_id"
        private val SOUNDS = listOf(
            Sound("RAIN", "Rain", "Rain Subtitle", "rain.mp3"),
            Sound("FOREST", "Forest", "Forest Subtitle", "forest.mp3"),
            Sound("FIRE", "Fire", "Fire Subtitle", "fire.mp3")
        )
    }
}