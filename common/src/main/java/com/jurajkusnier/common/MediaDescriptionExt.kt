package com.jurajkusnier.common

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.annotation.DrawableRes

fun MediaDescriptionCompat.Builder.setIcon(@DrawableRes icon: Int): MediaDescriptionCompat.Builder {
    return setExtras(Bundle().apply { putInt(ICON_KEY, icon) })
}

@DrawableRes
fun MediaBrowserCompat.MediaItem.getIcon(): Int? {
    return description.extras?.getInt(ICON_KEY)
}

private const val ICON_KEY = "ICON"