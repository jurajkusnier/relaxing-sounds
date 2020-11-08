package com.jurajkusnier.common.utils

import android.content.Intent
import android.view.KeyEvent

fun Intent.getKeyEvent(): KeyEvent? = extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent?