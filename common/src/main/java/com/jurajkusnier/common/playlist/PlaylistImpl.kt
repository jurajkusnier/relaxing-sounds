package com.jurajkusnier.common.playlist

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.core.net.toUri
import com.jurajkusnier.common.R
import com.jurajkusnier.common.player.SimpleMediaPlayerImpl.Companion.RESOURCE_ROOT_URI
import com.jurajkusnier.common.data.Sound

class PlaylistImpl(private val sounds: List<Sound>) : Playlist<Sound> {

    private var currentIndex = 0

    override fun getSong(): Sound = sounds[currentIndex]

    override fun skipToNext() {
        currentIndex = (currentIndex + 1) % sounds.size
    }

    override fun skipToPrev() {
        currentIndex = (currentIndex + sounds.size - 1) % sounds.size
    }

    override fun toMediaItemList(context: Context): List<MediaBrowserCompat.MediaItem> {
        return sounds.map { sound ->
            MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder()
                    .setMediaId(sound.id)
                    .setTitle(sound.title)
                    .setSubtitle(sound.subtitle)
                    .setIconUri(sound.resourceUri.toUri())
                    .setExtras(
                        Bundle().apply {
                            putBoolean(CONTENT_STYLE_SUPPORTED, true)
                            putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_GRID)
                        })
                    .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
        }
    }

    override fun getRootItem(context: Context): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setMediaId(ROOT_ITEM_ID)
                .setTitle(context.getString(R.string.sound_library))
                .setIconUri((RESOURCE_ROOT_URI + context.resources.getResourceEntryName(R.drawable.ic_baseline_library_music_24)).toUri())
                .setExtras(
                    Bundle().apply {
                        putBoolean(CONTENT_STYLE_SUPPORTED, true)
                        putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_LIST)
                    })
                .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }

    override fun <String> skipTo(id: String) {
        val index = sounds.indexOfFirst { it.id == id }
        if (index >= -1) {
            currentIndex = index
        }
    }

    companion object {
        private const val CONTENT_STYLE_BROWSABLE_HINT =
            "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
        private const val CONTENT_STYLE_PLAYABLE_HINT =
            "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
        private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
        private const val CONTENT_STYLE_LIST = 1
        private const val CONTENT_STYLE_GRID = 2
        const val ROOT_ITEM_ID = "ROOT_ID"
    }
}