package com.jurajkusnier.common

import android.content.res.AssetManager
import android.media.MediaPlayer

fun MediaPlayer.loadSound(assetManager: AssetManager, sound: Sound) {
    val songFileDescriptor = assetManager.openFd(sound.fileName)
    setDataSource(
        songFileDescriptor.fileDescriptor,
        songFileDescriptor.startOffset,
        songFileDescriptor.length
    )
    prepare()
}