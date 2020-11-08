package com.jurajkusnier.natureandrelaxingsounds

import android.content.ComponentName
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Button
import com.jurajkusnier.common.MediaPlaybackService

class MainActivity : AppCompatActivity() {

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {

            mediaBrowser.sessionToken.also { token ->

                // Create a MediaControllerCompat
                val mediaController = MediaControllerCompat(
                    this@MainActivity, // Context
                    token
                )

                // Save the controller
                MediaControllerCompat.setMediaController(this@MainActivity, mediaController)
            }

            // Finish building the UI
            buildTransportControls()
        }

        override fun onConnectionSuspended() {
            Log.e(TAG, "MediaBrowserCompat.ConnectionCallback: connection suspended")
        }

        override fun onConnectionFailed() {
            Log.e(TAG, "MediaBrowserCompat.ConnectionCallback: connection failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaBrowser = getMediaBrowser()
    }

    public override fun onStart() {
        super.onStart()
        startMediaBrowser()
    }

    public override fun onResume() {
        super.onResume()
        setupVolumeControlStream()
    }

    public override fun onStop() {
        super.onStop()
        stopMediaBrowser()
    }

    fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this@MainActivity)
        // Grab the view for the play/pause button
        findViewById<Button>(R.id.playPauseButton).apply {
            setOnClickListener {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                val pbState = mediaController.playbackState.state
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                } else {
                    mediaController.transportControls.play()
                }
            }
        }

        // Display the initial state
        val metadata = mediaController.metadata
        bindPlayPauseButton(mediaController.playbackState)

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback)
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {}

        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            bindPlayPauseButton(state)
        }
    }

    private fun getMediaBrowser() = MediaBrowserCompat(
        this,
        ComponentName(this, MediaPlaybackService::class.java),
        connectionCallbacks,
        null // optional Bundle
    )

    private fun startMediaBrowser() {
        mediaBrowser.connect()
    }

    private fun stopMediaBrowser() {
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()

    }

    private fun setupVolumeControlStream() {
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    private fun bindPlayPauseButton(state: PlaybackStateCompat) {
        findViewById<Button>(R.id.playPauseButton).text =
            if (state.state == PlaybackStateCompat.STATE_PLAYING) "pause" else "play"
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}