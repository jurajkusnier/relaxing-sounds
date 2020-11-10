package com.jurajkusnier.common

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat

class PlaylistImpl(private val sounds: List<Sound>) : Playlist<Sound> {

    private var currentIndex = 0

    override fun getSong(): Sound = sounds[currentIndex]

    override fun skipToNext() {
        currentIndex = (currentIndex + 1) % sounds.size
    }

    override fun skipToPrev() {
        currentIndex = (currentIndex + sounds.size - 1) % sounds.size
    }

    override fun toMediaItemList(): List<MediaBrowserCompat.MediaItem> {
        return sounds.map { sound ->
            MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder()
                    .setMediaId(sound.id)
                    .setTitle(sound.title)
                    .setSubtitle(sound.subtitle)
                    .setIcon(sound.icon)
                    .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
        }
    }

    override fun <String> skipTo(id: String) {
        val index = sounds.indexOfFirst { it.id == id }
        if (index >= -1) {
            currentIndex = index
        }
    }
}