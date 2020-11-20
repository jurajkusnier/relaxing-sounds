package com.jurajkusnier.common

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.jurajkusnier.common.data.Sound
import com.jurajkusnier.common.player.SimpleMediaPlayer
import com.jurajkusnier.common.player.SimpleMediaPlayerImpl
import com.jurajkusnier.common.player.SimpleMediaPlayerImpl.Companion.RESOURCE_ROOT_URI
import com.jurajkusnier.common.playlist.Playlist
import com.jurajkusnier.common.playlist.PlaylistImpl
import com.jurajkusnier.common.utils.MyNotificationManager

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private lateinit var myMediaSession: MediaSessionCompat
    private lateinit var simpleMediaPlayer: SimpleMediaPlayer
    private lateinit var playlist: Playlist<Sound>

    private var isForeground: Boolean = false

    override fun onCreate() {
        super.onCreate()
        playlist = PlaylistImpl(getSounds())
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
        val extras = Bundle().apply {
            putBoolean(CONTENT_STYLE_SUPPORTED, true)
            putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_GRID)
            putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID)
        }
        return BrowserRoot(MY_MEDIA_ROOT_ID, extras)
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentMediaId == MY_MEDIA_ROOT_ID) {
            result.sendResult(listOf(playlist.getRootItem(applicationContext)))
        } else {
            result.sendResult(playlist.toMediaItemList(applicationContext))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleMediaPlayer.release()
    }

    private fun getSounds() = listOf(
        Sound(
            "BEACH",
            getString(R.string.beach),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.beach),
            "beach.mp3"
        ),
        Sound(
            "BIRDS",
            getString(R.string.birds),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.birds),
            "birds.mp3"
        ),
        Sound(
            "BRAZILFOREST",
            getString(R.string.brazil_forest),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.brazil),
            "brazilforest.mp3"
        ),
        Sound(
            "GARDEN",
            getString(R.string.country_garden),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.garden),
            "countrygarden.mp3"
        ),
        Sound(
            "CREEK",
            getString(R.string.creek),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.creek),
            "creek.mp3"
        ),
        Sound(
            "FIRE",
            getString(R.string.fire),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.fire),
            "fire.mp3"
        ),
        Sound(
            "FOREST",
            getString(R.string.forest),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.forest),
            "forest.mp3"
        ),
        Sound(
            "FOUNTAIN",
            getString(R.string.fountain),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.fountain),
            "fountain.mp3"
        ),
        Sound(
            "HEARTBEAT",
            getString(R.string.heartbeat),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.heart),
            "heartbeat.mp3"
        ),
        Sound(
            "RAIN",
            getString(R.string.rain),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.rain),
            "rain.mp3"
        ),
        Sound(
            "RAINFOREST",
            getString(R.string.rainforest),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.rainforest),
            "rainforest.mp3"
        ),
        Sound(
            "THUNDERSTORM",
            getString(R.string.thunderstorm),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.thunder),
            "thunderstorm.mp3"
        ),
        Sound(
            "WHITENOISE",
            getString(R.string.white_noise),
            getString(R.string.relaxing_sounds),
            RESOURCE_ROOT_URI + resources.getResourceEntryName(R.drawable.whitenoise),
            "whitenoise.wav"
        ),
    )

    companion object {
        private const val MY_MEDIA_ROOT_ID = "media_root_id_x"
        private const val CONTENT_STYLE_BROWSABLE_HINT =
            "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
        private const val CONTENT_STYLE_PLAYABLE_HINT =
            "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
        private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
        private const val CONTENT_STYLE_LIST = 1
        private const val CONTENT_STYLE_GRID = 2
    }
}