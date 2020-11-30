package com.jurajkusnier.common.player

import android.content.Intent
import android.content.res.AssetManager
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.jurajkusnier.common.data.Sound
import com.jurajkusnier.common.playlist.Playlist
import com.jurajkusnier.common.utils.MyNotificationManager
import com.jurajkusnier.common.utils.getKeyEvent

class SimpleMediaPlayerImpl(
    private val audioManager: AudioManager,
    private val player: MediaPlayer,
    private val assetManager: AssetManager,
    private val notificationManager: MyNotificationManager,
    private val mediaSession: MediaSessionCompat,
    private val playlist: Playlist<Sound>
) : SimpleMediaPlayer {

    var pausedByAudioFocus = false
    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                pausedByAudioFocus = player.isPlaying
                pause()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (pausedByAudioFocus) {
                    pausedByAudioFocus = false
                    play()
                }
            }
        }
    }

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
        requestFocus()
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
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI,  playlist.getSong().resourceUri)
                .build()
        )
    }

    private fun updateNotification() {
        notificationManager.showNotification(mediaSession, playlist.getSong(), player.isPlaying)
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

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
            if (playlist.getSong().id == mediaId) {
                if (!player.isPlaying) {
                    play()
                }
                return
            }
            playlist.skipTo(mediaId)
            loadCurrentSong()
            updateSessionMetadata()
            play()
        }
    }

    private fun requestFocus() {
        val focusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributesCompat.Builder().run {
                setUsage(AudioAttributesCompat.USAGE_MEDIA)
                setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                build()
            })
            setOnAudioFocusChangeListener(afChangeListener, Handler())
            build()
        }
        AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
    }

    companion object {
        const val RESOURCE_ROOT_URI = "android.resource://com.jurajkusnier.natureandrelaxingsounds/drawable/"
    }

}