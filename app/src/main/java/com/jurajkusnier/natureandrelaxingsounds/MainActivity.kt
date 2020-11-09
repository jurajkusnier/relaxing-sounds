package com.jurajkusnier.natureandrelaxingsounds

import android.content.ComponentName
import android.media.AudioManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.jurajkusnier.common.MediaPlaybackService
import com.jurajkusnier.common.MediaServiceConnection

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private val playlistAdapter = PlaylistAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mediaServiceConnection = MediaServiceConnection(
            applicationContext,
            ComponentName(applicationContext, MediaPlaybackService::class.java)
        )
        val factory = MainActivityViewModelFactory(mediaServiceConnection)
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)

        setupUI()
    }

    public override fun onResume() {
        super.onResume()
        setupVolumeControlStream()
    }

    private fun setupUI() {
        findViewById<RecyclerView>(R.id.playlistRecyclerView).adapter = playlistAdapter
        setupControls()

        viewModel.playlist.observe(this) {
            playlistAdapter.submitList(it)
        }

        viewModel.playbackState.observe(this) {
            bindMediaInfo(it.mediaTitle)
            bindPlayPauseButton(it.isPlaying)
        }
    }

    private fun setupControls() {
        findViewById<Button>(R.id.playPauseButton).setOnClickListener {
            viewModel.playPause()
        }
        findViewById<Button>(R.id.nextButton).setOnClickListener {
            viewModel.next()
        }
        findViewById<Button>(R.id.prevButton).setOnClickListener {
            viewModel.prev()
        }
    }

    private fun setupVolumeControlStream() {
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    private fun bindMediaInfo(mediaTitle:String) {
        findViewById<TextView>(R.id.titleTextView).text = mediaTitle
    }

    private fun bindPlayPauseButton(isPlaying: Boolean) {
        findViewById<MaterialButton>(R.id.playPauseButton).setIconResource(
            if (isPlaying)
                R.drawable.ic_baseline_pause_24
            else
                R.drawable.ic_baseline_play_arrow_24
        )
    }
}