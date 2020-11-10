package com.jurajkusnier.common

import androidx.annotation.DrawableRes

data class Sound(
    val id: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    val fileName: String
)