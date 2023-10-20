package com.example.watchdl.helperClasses

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.work.*
import com.example.watchdl.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Timestamp


class LocationWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {
    private var interval: Long = 10000 // 10 seconds by default, can be changed later
    private var currentLocation: Location = Location("")
    private var fileEditor: FileEditor = FileEditor(applicationContext)
    private lateinit var locations: Map<Int, Location>
    private var atStop: Boolean = false

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            1, createNotification()
        )
    }

    override suspend fun doWork(): Result {
        try {
            atStop = this.inputData.getBoolean("atStop", false)
            interval = this.inputData.getLong("interval", 10000)
            Log.d("LocationWorker", atStop.toString() + " " + interval.toString())
            getLocation()
            withContext(Dispatchers.IO) {
                Thread.sleep(interval)//cycle determined by interval
            }
            repeat()
        } catch (e: InterruptedException) {
            Log.d("LocationWorker", "Thread sleep failed...")
            e.printStackTrace()
        }
        return Result.success()


    }

    private fun repeat(){
        val refreshWork = OneTimeWorkRequest.Builder(LocationWorker::class.java).setInputData(Data.Builder().putBoolean("atStop", atStop).putLong("interval", interval).build())
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "InitialLocationWorkRequest",
            ExistingWorkPolicy.REPLACE,
            refreshWork.build()
        )
    }

    private fun createNotification() : Notification {
        return Notification()
    }

    private fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "permission denied", Toast.LENGTH_LONG).show()
        }
        LocationServices.getFusedLocationProviderClient(applicationContext).getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnSuccessListener { location ->
            if(location != null) {
                currentLocation = location
                Log.d("BackgroundLocation", location.toString())
                Log.d("BackgroundLocation", Timestamp(location.time).toString())
            }
            else Log.e("BackgroundLocation", "null")
            calcDistances()
            Log.w("LocationWorker", "----------------------------------------------------")
        }
    }

    private fun calcDistances(){
        locations = fileEditor.getLocations()
        val distances: MutableList<Map.Entry<Int, Location>> = locations.entries.toMutableList()
        distances.sortBy { currentLocation.distanceTo(it.value) }
        if(distances.size > 0){
            compareDistance(distances[0])
        }
    }

    private fun compareDistance(closestStop: Map.Entry<Int, Location>){
        val distance = currentLocation.distanceTo(closestStop.value)
        Log.d("LocationWorker", "LocationDistance: " + distance.toString())
        Log.d("LocationWorker", "LocationStop: " + closestStop.value.toString())

        if(distance < 15){
            if(!atStop) {
                startActivity(
                    this.applicationContext,
                    Intent(applicationContext,MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("currentStopId", closestStop.key),
                    null )
                Log.d("LocationWorker", "started app")
                atStop = true
            }
            interval = 10000

        }
        else if(distance < 200){
            interval = 10000 // 10sec
            atStop = false
        }
        else if(distance < 1500){
            interval = 60000 // 1min
            atStop = false
        }
        else {
            interval = 600000 // 10min
            atStop = false
        }
    }

}