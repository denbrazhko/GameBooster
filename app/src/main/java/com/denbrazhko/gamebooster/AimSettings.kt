package com.denbrazhko.gamebooster

import android.graphics.Color

data class AimSettings(
    var color: Int = Color.parseColor("#605D58"),
    val defaultSize: Int = 100,
    var size: Int = defaultSize //random default size,
)
