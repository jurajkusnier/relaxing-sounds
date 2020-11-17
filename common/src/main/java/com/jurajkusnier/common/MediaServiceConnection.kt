package com.jurajkusnier.common

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MediaServiceConnection(context: Context, serviceComponent: ComponentName) {

    private val _playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val playbackState: Flow<PlaybackStateCompat>
        get() = _playbackState

    private val _nowPlaying = MutableStateFlow(NOTHING_PLAYING)
    val nowPlaying: Flow<MediaMetadataCompat>
        get() = _nowPlaying

    private val _playlist = MutableStateFlow(listOf<MediaBrowserCompat.MediaItem>())
    val playlist: Flow<List<MediaBrowserCompat.MediaItem>>
        get() = _playlist

    private lateinit var mediaController: MediaControllerCompat
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    fun playPause() {
        val playbackState = mediaController.playbackState.state
        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            mediaController.transportControls.pause()
        } else {
            mediaController.transportControls.play()
        }
    }

    fun playPause(mediaId: String) {
        mediaController.transportControls.playFromMediaId(mediaId, null)
    }

    fun next() {
        mediaController.transportControls.skipToNext()
    }

    fun prev() {
        mediaController.transportControls.skipToPrevious()
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            mediaBrowser.subscribe(PlaylistImpl.ROOT_ITEM_ID, //TODO: remove PlaylistImpl dependency
                object : MediaBrowserCompat.SubscriptionCallback() {

                    override fun onChildrenLoaded(
                        parentId: String,
                        children: MutableList<MediaBrowserCompat.MediaItem>
                    ) {
                        _playlist.value = children
                    }
                })

            _playbackState.value = mediaController.playbackState
            _nowPlaying.value = mediaController.metadata
        }

        override fun onConnectionSuspended() {
            Log.e(TAG, "MediaBrowserConnectionCallback: connection suspended")
        }

        override fun onConnectionFailed() {
            Log.e(TAG, "MediaBrowserConnectionCallback: connection failed")
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.value = (state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _nowPlaying.value = if (metadata?.id == null) {
                NOTHING_PLAYING
            } else {
                metadata
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    companion object {
        private const val TAG = "MediaServiceConnection"

        private val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build()

        val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .build()
    }

}