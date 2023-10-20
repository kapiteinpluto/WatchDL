package com.example.watchdl.objects

import android.location.Location

data class BusStop(
    var id: Int,
    var name: String,
    var lines: List<BusLine>,
    var location: Location
){
    constructor(line: String) : this(0,"", listOf(), Location("")) {
        val pieces = line.split(";")
        val location = Location("")
        location.latitude = pieces[2].toDouble()
        location.longitude = pieces[3].toDouble()
        this.id = pieces[0].toInt()
        this.name = pieces[1]
        this.lines = stringToLines(pieces[4])
        this.location = location
    }
    override fun toString(): String {
        return ("" + id + ";" + name + ";" + location.latitude + ";" + location.longitude + ";" + lines.joinToString(","))
    }
    private fun stringToLines(lineString:String): List<BusLine> {
        val list = mutableListOf<BusLine>()
        if(lineString.isNotEmpty()) {
            val lines = lineString.split(',')

            for(line in lines){
                val pieces = line.split(":")
                list.add(BusLine(pieces[0], pieces[1], pieces[2], pieces[3].toBoolean()))
            }
        }
        return list

    }
}