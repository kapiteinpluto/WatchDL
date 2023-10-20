package com.example.watchdl.objects

import android.location.Location

data class BusStop(
    var name: String,
    val id: Int,
    var busses: List<BusItem>,
    val location: Location,
    var visibleBusses: List<String>
)

