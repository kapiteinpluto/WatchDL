package com.example.watchdl.helperClasses

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.watchdl.objects.BusStop
import java.io.File

class FileEditor(context: Context) {
    private val file = File(context.filesDir, "stopSettings")
    private val settingsFile = File(context.filesDir, "appSettings")

    init {
        file.appendText("")
        settingsFile.appendText("")
    }

    fun getFile(): File{
        return file
    }

    fun getSettingsFile(): File{
        return settingsFile
    }

    fun getStop(id: Int): BusStop{
        if(file.isFile){
            for(line in file.readLines()){
                val pieces = line.split(";")
                if(id == pieces[0].toInt()) return BusStop(line)
            }
        }
        return BusStop(0, "Error: stop not found in file", mutableListOf(), Location(""))
    }

    fun fileToStops(): List<BusStop>{
        val stops = mutableListOf<BusStop>()
        if(file.isFile){
            println(file.readText())
            if(file.readText().isNotEmpty()) {
                for (line in file.readLines()) {
                    stops.add(BusStop(line))
                }
            }
        }
        return stops
    }

    fun stopsToFile(stops:List<BusStop>){
        var string = ""
        for(stop in stops){
            string += stop.toString()
        }
        file.writeText(string)
    }

    fun updateStop(stop: BusStop){
        if(file.isFile){
            val lines = file.readLines() as MutableList<String>
            for((i,line) in lines.withIndex()){
                val pieces = line.split(";")
                if(stop.id == pieces[0].toInt()) {
                    lines[i] = stop.toString()
                    file.writeText(lines.joinToString("\n") + "\n")
                }
            }
        }
    }

    fun deleteStop(stop: BusStop){
        if(file.isFile){
            var string = ""
            for(line in file.readLines()){
                val pieces = line.split(";")
                if(stop.id.toString() != pieces[0]) {
                    string += line + "\n"
                }
            }
            file.writeText(string)
        }
    }

    fun addStop(stop: BusStop){
        val string =  stop.toString() + "\n"
        file.appendText(string)
    }

    fun editSetting(name:String, value:String){
        if(settingsFile.isFile){
            var settingInFile = false
            val lines = settingsFile.readLines() as MutableList<String>
            for((i,line) in lines.withIndex()){
                val pieces = line.split(":")
                if(name == pieces[0]) {
                    settingInFile = true
                    lines[i] = name + ":" + value
                    settingsFile.writeText(lines.joinToString("\n") + "\n")
                }
            }
            if(!settingInFile){
                settingsFile.writeText(name + ":" + value + "\n")
            }
            Log.d("editSettings",settingsFile.readText())
        }
    }
    fun getSetting(name:String): String{
        var settingsValue = ""
        if(settingsFile.isFile){
            val lines = settingsFile.readLines() as MutableList<String>
            for(line in lines){
                val pieces = line.split(":")
                if(name == pieces[0]) {
                    settingsValue = pieces[1]
                }
            }
        }
        return settingsValue
    }
}