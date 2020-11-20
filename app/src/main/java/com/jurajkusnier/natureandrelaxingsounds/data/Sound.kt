package com.jurajkusnier.natureandrelaxingsounds.data

import android.net.Uri

data class Sound(
    val id: String,
    val title: String,
    val iconUri: Uri?,
    val isSelected: Boolean
)