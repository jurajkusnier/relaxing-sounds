package com.jurajkusnier.common

import android.support.v4.media.session.MediaSessionCompat

interface SimpleMediaPlayer {
    fun playPause()
    fun next()
    fun prev()
    fun release()
    val mediaSessionCallback: MediaSessionCompat.Callback
}