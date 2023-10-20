package com.example.watchdl.helperClasses

import android.location.Location
import android.util.Log
import androidx.core.graphics.toColorInt
import com.example.watchdl.objects.BusItem
import com.example.watchdl.objects.BusStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.jvm.Throws
import kotlin.math.min

class DataFetcher {
    private var nowEpoch: Long = 0

    private fun mockBusses(count: Int): List<BusItem>{
        val busses = mutableListOf<BusItem>()
        for(i in 1..count){
            busses.add(BusItem(0, i.toString(), i*2, "string" + i, 0xFFFFFF))
        }
        return busses
    }

    suspend fun fetchStopBusses(busStop : BusStop): BusStop {
        val json = getUrl("https://www.delijn.be/api/trips/stops/${busStop.id}/?type=MERGE")

        nowEpoch = json.getLong("serverDateTime")

        val bussesJson = json.getJSONArray("trips")
        val busses = mutableListOf<BusItem>()
        for (i :Int in 0 until bussesJson.length()){
            busses.add(jsonToBus(bussesJson[i] as JSONObject))
        }
        busStop.busses = busses

        val json2 = getUrl("https://www.delijn.be/api/stops/${busStop.id}")

        busStop.name = json2.getString("longDescription")
        busStop.location.latitude = json2.getJSONObject("coordinate").getDouble("latitude")
        busStop.location.longitude = json2.getJSONObject("coordinate").getDouble("longitude")

        return busStop
    }

    private suspend fun getUrl(url: String): JSONObject {
        val connection =
            withContext(Dispatchers.IO) {
                URL(url).openConnection()
            } as HttpURLConnection
        val response = connection.inputStream.bufferedReader().readText()
        withContext(Dispatchers.IO) {
            connection.inputStream.close()
        }
        return JSONTokener(response).nextValue() as JSONObject
    }

    private fun jsonToBus(json: JSONObject): BusItem {
        val number = json.getJSONObject("lineDirection").getJSONObject("line").getString("publicLineNr")
        val color = json.getJSONObject("lineDirection").getJSONObject("line").getJSONObject("lineColor").getString("background").toColorInt()
        val (arriveInterval, liveData) = calcArriveInterval(json)
        val line = json.getJSONObject("lineDirection").getString("destination") //description too long
        return BusItem(null, number, arriveInterval, line, color, liveData)
    }

    private fun calcArriveInterval(json: JSONObject): Pair<Int, Boolean>{
        var realEpoch: Long
        var liveData: Boolean = true
        try {
            realEpoch = (json.getJSONArray("passages")[0] as JSONObject).getJSONObject("realtimePassage").getLong("arrivalEpoch")
        }catch (e: JSONException){
            liveData = false
            realEpoch = (json.getJSONArray("passages")[0] as JSONObject).getJSONObject("plannedPassage").getLong("arrivalEpoch")
        }

        val realTime = LocalDateTime.ofEpochSecond(realEpoch/1000, 0, ZoneOffset.ofHours(1))
        val now = LocalDateTime.ofEpochSecond(nowEpoch/1000, 0, ZoneOffset.ofHours(1))
        val minutesLeft = Duration.between(now, realTime).plusSeconds(30).toMinutes().toInt()
        return Pair(minutesLeft, liveData)
    }
}