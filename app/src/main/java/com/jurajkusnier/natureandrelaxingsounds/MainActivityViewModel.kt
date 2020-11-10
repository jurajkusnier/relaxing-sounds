package com.jurajkusnier.natureandrelaxingsounds

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.jurajkusnier.common.MediaServiceConnection
import com.jurajkusnier.common.getIcon
import com.jurajkusnier.common.id
import com.jurajkusnier.common.title
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PlaybackState(val isPlaying: Boolean, val mediaTitle: String)

class MainActivityViewModel(private val mediaServiceConnection: MediaServiceConnection) :
    ViewModel() {

    private val _playlist = MutableLiveData<List<Sound>>()
    val playlist: LiveData<List<Sound>>
        get() = _playlist

    val playbackState = liveData {
        mediaServiceConnection.nowPlaying.combine(mediaServiceConnection.playbackState) { nowPlaying, playbackState ->
            PlaybackState(
                isPlaying = playbackState.state == PlaybackStateCompat.STATE_PLAYING,
                mediaTitle = nowPlaying.title ?: ""
            )
        }.collect { emit(it) }
    }

    init {
        viewModelScope.launch {
            mediaServiceConnection.playlist.combine(mediaServiceConnection.nowPlaying) { playlist, nowPlaying ->
                playlist.map { item ->
                    Sound(
                        item.mediaId ?: "",
                        item.description.title?.toString() ?: "EMPTY",
                        item.getIcon(),
                        item.mediaId == nowPlaying.id
                    )
                }
            }.collect {
                _playlist.value = it
            }
        }
    }

    fun playPause() {
        mediaServiceConnection.playPause()
    }

    fun play() {
        mediaServiceConnection.play()
    }

    fun pause() {
        mediaServiceConnection.pause()
    }

    fun next() {
        mediaServiceConnection.next()
    }

    fun prev() {
        mediaServiceConnection.prev()
    }

}

class MainActivityViewModelFactory(private val mediaServiceConnection: MediaServiceConnection) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(mediaServiceConnection) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
