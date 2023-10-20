package com.example.watchdl.objects

data class BusLine(
    val number: String,
    val color: String,
    val description: String,
    var enabled: Boolean = true,
){
    override fun toString(): String {
        return "$number:$color:$description:$enabled"
    }
}
