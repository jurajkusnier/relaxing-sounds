package com.jurajkusnier.common

import android.support.v4.media.MediaBrowserCompat

interface Playlist<T> {
    fun getSong(): T
    fun skipToNext()
    fun skipToPrev()
    fun toMediaItemList(): List<MediaBrowserCompat.MediaItem>
}