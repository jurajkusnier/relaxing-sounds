package com.jurajkusnier.common

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private val mediaPlayer = MediaPlayer()
    private lateinit var myMediaSession: MediaSessionCompat
    private var isForeground: Boolean = false

    override fun onCreate() {
        super.onCreate()
        Log.d("=TEST=", "MediaPlaybackService.onCreate()")
        val rainFd = resources.assets.openFd("rain.mp3")
        mediaPlayer.setDataSource(rainFd.fileDescriptor, rainFd.startOffset, rainFd.length)
        mediaPlayer.prepare()
        mediaPlayer.isLooping = true

        myMediaSession = getMediaSession()

        sessionToken = myMediaSession.sessionToken

        MyNotificationManager(this).showNotification(myMediaSession, false)
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
                val stateBuilder = PlaybackStateCompat.Builder()
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY
                                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                setPlaybackState(stateBuilder.build())
                setCallback(MediaSessionCallback(mediaPlayer))
                setMetadata(
                    MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, "Meta title")
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, "Meta artist")
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.duration.toLong())
                        .build()
                )
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
        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>()
        val mediaItem = MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setMediaId("song_id")
                .setTitle("song title")
                .setSubtitle("song subtitle")
                .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
        val mediaItems2 = listOf(mediaItem)
        result.sendResult(mediaItems2)
    }

    inner class MediaSessionCallback(private val mediaPlayer: MediaPlayer) :
        MediaSessionCompat.Callback() {

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            val keyEvent: KeyEvent? =
                mediaButtonEvent?.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent?
            if (keyEvent?.action == KeyEvent.ACTION_DOWN) {
                return when (keyEvent.keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        onPlay(); true
                    }
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        onPause(); true
                    }
                    else -> super.onMediaButtonEvent(mediaButtonEvent)
                }
            }
            return super.onMediaButtonEvent(mediaButtonEvent)
        }

        override fun onPrepare() {}

        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {}

        override fun onPrepareFromSearch(query: String?, extras: Bundle?) {}

        override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) {}

        override fun onPlay() {
            mediaPlayer.start()
            myMediaSession.setPlaybackState(
                PlaybackStateCompat.Builder().setActions(ACTION_PLAY or ACTION_PLAY_PAUSE).setState(
                    STATE_PLAYING, PLAYBACK_POSITION_UNKNOWN, 0f
                ).build()
            )
            MyNotificationManager(this@MediaPlaybackService).showNotification(myMediaSession, true)
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {}

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {}

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {}

        override fun onSkipToQueueItem(id: Long) {}

        override fun onPause() {
            mediaPlayer.pause()
            myMediaSession.setPlaybackState(
                PlaybackStateCompat.Builder().setActions(ACTION_PAUSE or ACTION_PLAY_PAUSE)
                    .setState(
                        STATE_PAUSED, PLAYBACK_POSITION_UNKNOWN, 0f
                    ).build()
            )
            MyNotificationManager(this@MediaPlaybackService).showNotification(myMediaSession, false)
        }

        override fun onSkipToNext() {}

        override fun onSkipToPrevious() {}

        override fun onFastForward() {}

        override fun onRewind() {}

        override fun onStop() {}

        override fun onSeekTo(pos: Long) {}

        override fun onSetRating(rating: RatingCompat?) {}

        override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {}

        override fun onSetPlaybackSpeed(speed: Float) {}

        override fun onSetCaptioningEnabled(enabled: Boolean) {}

        override fun onSetRepeatMode(repeatMode: Int) {}

        override fun onSetShuffleMode(shuffleMode: Int) {}

        override fun onCustomAction(action: String?, extras: Bundle?) {}

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {}

        override fun onAddQueueItem(description: MediaDescriptionCompat?, index: Int) {}

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {}

    }

    override fun onDestroy() {
        super.onDestroy()
        myMediaSession.run {
            isActive = false
            release()
        }
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    companion object {
        private const val MY_MEDIA_ROOT_ID = "media_root_id"
    }
}