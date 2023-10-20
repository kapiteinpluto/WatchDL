package com.example.watchdl.helperClasses

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.core.graphics.toColorInt
import com.example.watchdl.objects.BusLine
import com.example.watchdl.objects.BusStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import kotlin.coroutines.coroutineContext
import kotlin.jvm.Throws

class DataFetcher {
    @Throws(FileNotFoundException::class)
    suspend fun fetchStop(stopId : Int): BusStop {
        val json = getUrl("https://www.delijn.be/api/stops/$stopId")

        val name = json.getString("longDescription")

        val lines: MutableList<BusLine> = mutableListOf()
        for(i :Int in 0 until json.getJSONArray("lineDirections").length()){
            val lineJson = (json.getJSONArray("lineDirections")[i] as JSONObject).getJSONObject("line")
            if(lineJson.getString("serviceType") != "BELBUS"){
                lines.add(BusLine(
                    lineJson.getString("publicLineNr"),
                    lineJson.getJSONObject("lineColor").getString("background"),
                    lineJson.getString("description")
                ))
            }
        }
        val location = Location("")
        location.latitude = json.getJSONObject("coordinate").getDouble("latitude")
        location.longitude = json.getJSONObject("coordinate").getDouble("longitude")

        return BusStop(stopId, name, lines, location)
    }

    @Throws(FileNotFoundException::class)
    private suspend fun getUrl(url: String): JSONObject {
        val connection =
            withContext(Dispatchers.IO) {
                URL(url).openConnection()
            } as HttpURLConnection
        val response = connection.inputStream.bufferedReader().readText()
        return JSONTokener(response).nextValue() as JSONObject
    }
}