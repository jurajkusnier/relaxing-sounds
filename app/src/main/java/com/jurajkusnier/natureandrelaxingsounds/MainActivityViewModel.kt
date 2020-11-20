package com.jurajkusnier.natureandrelaxingsounds

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.jurajkusnier.common.utils.id
import com.jurajkusnier.common.utils.title
import com.jurajkusnier.natureandrelaxingsounds.data.PlaybackState
import com.jurajkusnier.natureandrelaxingsounds.data.Sound
import com.jurajkusnier.natureandrelaxingsounds.ui.MediaServiceConnection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivityViewModel(private val mediaServiceConnection: MediaServiceConnection) :
    ViewModel() {

    private val _playlist = MutableLiveData<List<Sound>>()
    val playlist: LiveData<List<Sound>>
        get() = _playlist

    val playbackState = liveData {
        mediaServiceConnection.nowPlaying.combine(mediaServiceConnection.playbackState) { nowPlaying, playbackState ->
            PlaybackState(
                isPlaying = playbackState.state == PlaybackStateCompat.STATE_PLAYING,
                mediaTitle = nowPlaying.title ?: "",
                mediaId = nowPlaying.id ?: ""
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
                        item.description.iconUri,
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

    fun playPause(sound: Sound) {
        playbackState.value?.let { state ->
            if (state.isPlaying && sound.id == state.mediaId) {
                mediaServiceConnection.pause()
            } else {
                mediaServiceConnection.play(sound.id)
            }
        }
    }

    fun next() {
        mediaServiceConnection.next()
    }

    fun prev() {
        mediaServiceConnection.prev()
    }

}

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(private val mediaServiceConnection: MediaServiceConnection) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(mediaServiceConnection) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
