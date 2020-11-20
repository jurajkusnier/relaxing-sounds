package com.jurajkusnier.common.player

import android.content.res.AssetManager
import android.media.MediaPlayer
import com.jurajkusnier.common.data.Sound

fun MediaPlayer.loadSound(assetManager: AssetManager, sound: Sound) {
    val songFileDescriptor = assetManager.openFd(sound.fileName)
    stop()
    reset()
    setDataSource(
        songFileDescriptor.fileDescriptor,
        songFileDescriptor.startOffset,
        songFileDescriptor.length
    )
    songFileDescriptor.close()
    prepare()
}