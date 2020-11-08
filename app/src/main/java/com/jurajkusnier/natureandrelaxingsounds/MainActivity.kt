package com.jurajkusnier.natureandrelaxingsounds

import android.content.ComponentName
import android.media.AudioManager
import android.media.MediaMetadata.METADATA_KEY_TITLE
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.material.button.MaterialButton
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

        findViewById<Button>(R.id.playPauseButton).setOnClickListener {
            val pbState = mediaController.playbackState.state
            if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.transportControls.pause()
            } else {
                mediaController.transportControls.play()
            }
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            mediaController.transportControls.skipToNext()
        }

        findViewById<Button>(R.id.prevButton).setOnClickListener {
            mediaController.transportControls.skipToPrevious()
        }

        // Display the initial state
        bindPlayPauseButton(mediaController.playbackState)
        bindMediaInfo(mediaController.metadata)

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback)
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            bindMediaInfo(metadata)
        }

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

    private fun bindMediaInfo(metadata: MediaMetadataCompat) {
        findViewById<TextView>(R.id.titleTextView).text = metadata.getString(METADATA_KEY_TITLE)
    }

    private fun bindPlayPauseButton(state: PlaybackStateCompat) {
        findViewById<MaterialButton>(R.id.playPauseButton).setIconResource(
            if (state.state == PlaybackStateCompat.STATE_PLAYING)
                R.drawable.ic_baseline_pause_24
            else
                R.drawable.ic_baseline_play_arrow_24
        )
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}