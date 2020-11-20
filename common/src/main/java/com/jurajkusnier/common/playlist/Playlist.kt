package com.jurajkusnier.common.playlist

import android.content.Context
import android.support.v4.media.MediaBrowserCompat

interface Playlist<T> {
    fun getSong(): T
    fun <N> skipTo(id: N)
    fun skipToNext()
    fun skipToPrev()
    fun toMediaItemList(context: Context): List<MediaBrowserCompat.MediaItem>
    fun getRootItem(context: Context): MediaBrowserCompat.MediaItem
}