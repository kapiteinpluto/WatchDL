package com.example.watchdl.helperClasses

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.watchdl.objects.BusStop
import java.io.File

class FileEditor(context: Context) {
    private val file = File(context.filesDir, "settings")
    private val file2 = File(context.filesDir, "settings2")

    @JvmOverloads
    fun getStops(attemptCount: Int = 0): List<BusStop>{
        var stops = mutableListOf<BusStop>()
        if(file.isFile){
            println(file.readText())
            for(line in file.readLines()){
                val pieces = line.split(";")
                if(pieces.size > 1){
                    val visibleBusLines = mutableListOf<String>()
                    if(pieces[4].isNotEmpty()){
                        for(busline in pieces[4].split(",")){
                            val linePieces = busline.split(':')
                            if(linePieces[3].toBoolean()){
                                visibleBusLines.add(linePieces[0])
                            }
                        }
                    }
                    stops.add(BusStop(pieces[1], pieces[0].toInt(), listOf(), Location(""), visibleBusLines))
                }
            }
        }
        if(stops.isEmpty() && attemptCount < 100){ //stops can be empty when file is in use, try until not empty with max attempts
            Log.d("fileEditor", "File in use, trying again")
            stops = getStops(attemptCount + 1) as MutableList<BusStop>
        }
        return stops
    }

    fun getLocations(): Map<Int, Location>{
        val locations = mutableMapOf<Int, Location>()
        if(file.isFile){
            for(line in file.readLines()){
                val pieces = line.split(";")
                if(pieces.size > 1){
                    val location = Location("")
                    location.latitude = pieces[2].toDouble()
                    location.longitude = pieces[3].toDouble()
                    locations[pieces[0].toInt()] = location
                }
            }
        }
        return locations
    }

    fun getAutoRefresh(): Int{
        var value = 10
        if(file2.isFile){
            for(line in file2.readLines()){
                val pieces = line.split(":")
                if(pieces.size > 1 && pieces[0] == "AutoRefresh"){
                    value = if(pieces[1] == "Never"){
                        0
                    } else{
                        pieces[1].toInt()
                    }
                }
            }
        }
        return value
    }

    fun fillFile(){
        file.writeText(
            "201550;Gent Einde Were;51.05296868371606;3.6992490376526046;9:#444411:Gentbrugge Groeningewijk - Mariakerke Post:true,14:#BB0022:Gent - Drongen - Deinze:true,15:#444411:Gent - Drongen - Nevele - Tielt:true,16:#FF88AA:Gent - Drongen - Nevele - Deinze:true,65:#0044BB:Gent Sint-Pietersstation - Zomergem - Ursel Kerk:true\n" +
                "202774;Deinze Hulhage;50.98692726828835;3.538880439967756;14:#BB0022:Deinze - Drongen - Gent:true,86:#AACCEE:Deinze - Nevele - Bellem Moerstraat:true"
        )
    }
}