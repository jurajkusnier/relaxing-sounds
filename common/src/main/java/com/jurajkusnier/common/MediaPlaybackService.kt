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
            Sound("BEACH", "Beach", "Beach Subtitle", R.drawable.beach, "beach.mp3"),
            Sound("BIRDS", "Birds", "Birds Subtitle", R.drawable.birds, "birds.mp3"),
            Sound(
                "BRAZILFOREST",
                "Brazil Forest",
                "Brazil Forest Subtitle",
                R.drawable.brazil,
                "brazilforest.mp3"
            ),
            Sound(
                "GARDEN",
                "Country Garden",
                "Country Garden Subtitle",
                R.drawable.garden,
                "countrygarden.mp3"
            ),
            Sound("CREEK", "Creek", "Creek Subtitle", R.drawable.creek, "creek.mp3"),
            Sound("FIRE", "Fire", "Fire Subtitle", R.drawable.fire, "fire.mp3"),
            Sound("FOREST", "Forest", "Forest Subtitle", R.drawable.forest, "forest.mp3"),
            Sound("FOUNTAIN", "Fountain", "Fountain Subtitle", R.drawable.fountain, "fountain.mp3"),
            Sound("HEARTBEAT", "Hearbeat", "Heartbeat Subtitle", R.drawable.heart, "heartbeat.mp3"),
            Sound("RAIN", "Rain", "Rain Subtitle", R.drawable.rain, "rain.mp3"),
            Sound(
                "RAINFOREST",
                "Rainforest",
                "Rainforest Subtitle",
                R.drawable.rainforest,
                "rainforest.mp3"
            ),
            Sound(
                "THUNDERSTORM",
                "Thunderstorm",
                "Thunderstorm Subtitle",
                R.drawable.thunder,
                "thunderstorm.mp3"
            ),
            Sound(
                "WHITENOISE",
                "White noise",
                "White noise Subtitle",
                R.drawable.whitenoise,
                "whitenoise.wav"
            ),
        )
    }
}