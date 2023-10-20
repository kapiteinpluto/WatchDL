package com.example.watchdl.objects

import androidx.annotation.ColorInt

data class BusItem(
    val id:Int?,
    val number: String, //numbers can be: N4 => no Int but String
    var arriveInterval: Int,
    val line: String,
    @ColorInt val color: Int,
    val liveData: Boolean = true
)
