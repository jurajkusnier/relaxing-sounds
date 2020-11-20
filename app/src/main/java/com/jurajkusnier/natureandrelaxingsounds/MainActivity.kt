package com.jurajkusnier.natureandrelaxingsounds

import android.content.ComponentName
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jurajkusnier.common.MediaPlaybackService
import com.jurajkusnier.common.MediaServiceConnection
import com.jurajkusnier.natureandrelaxingsounds.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val space = resources.getDimensionPixelSize(R.dimen.item_spacing)
        val spanCount = resources.getInteger(R.integer.span_count)
        val playlistAdapter = PlaylistAdapter {sound ->
            viewModel.playPause(sound.id)
        }
        binding.playlistRecyclerView.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(GridItemDecoration(space))
            adapter = playlistAdapter
        }
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
        binding.playPauseButton.setOnClickListener {
            viewModel.playPause()
        }
        binding.nextButton.setOnClickListener {
            viewModel.next()
        }
        binding.prevButton.setOnClickListener {
            viewModel.prev()
        }
    }

    private fun setupVolumeControlStream() {
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    private fun bindMediaInfo(mediaTitle: String) {
        binding.titleTextView.text = mediaTitle
    }

    private fun bindPlayPauseButton(isPlaying: Boolean) {
        binding.playPauseButton.setIconResource(
            if (isPlaying)
                R.drawable.ic_baseline_pause_24
            else
                R.drawable.ic_baseline_play_arrow_24
        )
    }
}