package com.jurajkusnier.common

import android.content.Intent
import android.content.res.AssetManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import com.jurajkusnier.common.utils.MyNotificationManager
import com.jurajkusnier.common.utils.getKeyEvent

class SimpleMediaPlayerImpl(
    private val player: MediaPlayer,
    private val assetManager: AssetManager,
    private val notificationManager: MyNotificationManager,
    private val mediaSession: MediaSessionCompat,
    private val playlist: Playlist<Sound>
) : SimpleMediaPlayer {

    init {
        loadCurrentSong()
        updateSessionMetadata()
        updatePlaybackState()
        updateNotification()
    }

    private fun loadCurrentSong() {
        player.apply {
            loadSound(assetManager, playlist.getSong())
            player.isLooping = true
        }
    }

    override fun playPause() {
        if (player.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    override fun next() {
        val wasPlaying = player.isPlaying
        playlist.skipToNext()
        loadCurrentSong()
        updateSessionMetadata()
        if (wasPlaying) {
            play()
        } else {
            updateNotification()
        }
    }

    override fun prev() {
        val wasPlaying = player.isPlaying
        playlist.skipToPrev()
        loadCurrentSong()
        updateSessionMetadata()
        if (wasPlaying) {
            play()
        } else {
            updateNotification()
        }
    }

    override fun release() {
        mediaSession.isActive = false
        mediaSession.release()
        player.stop()
        player.release()
    }

    private fun play() {
        player.start()
        updatePlaybackState()
        updateNotification()
    }

    private fun pause() {
        player.pause()
        updatePlaybackState()
        updateNotification()
    }

    private fun updateSessionMetadata() {
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, playlist.getSong().id)
                .putString(MediaMetadata.METADATA_KEY_TITLE, playlist.getSong().title)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, playlist.getSong().subtitle)
                .build()
        )
    }

    private fun updateNotification() {
        notificationManager.showNotification(mediaSession, player.isPlaying)
    }

    private fun updatePlaybackState() {
        val availableActions = if (player.isPlaying) {
            PlaybackStateCompat.ACTION_PAUSE
        } else {
            PlaybackStateCompat.ACTION_PLAY
        } or PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS

        val playerState = if (player.isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(availableActions)
                .setState(
                    playerState,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f
                )
                .build()
        )
    }

    override val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            val keyEvent = mediaButtonEvent?.getKeyEvent()
            if (keyEvent?.action == KeyEvent.ACTION_DOWN) {
                when (keyEvent.keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        play()
                        return true
                    }
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        pause()
                        return true
                    }
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                        playPause()
                        return true
                    }
                    KeyEvent.KEYCODE_MEDIA_NEXT -> {
                        next()
                        return true
                    }
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                        prev()
                        return true
                    }
                }
            }
            return super.onMediaButtonEvent(mediaButtonEvent)
        }

        override fun onPlay() {
            play()
        }

        override fun onPause() {
            pause()
        }

        override fun onSkipToNext() {
            next()
        }

        override fun onSkipToPrevious() {
            prev()
        }
    }

}