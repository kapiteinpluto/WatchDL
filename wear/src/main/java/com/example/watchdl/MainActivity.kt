package com.example.watchdl

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.watchdl.adapters.StopAdapter
import com.example.watchdl.databinding.ActivityMainBinding
import com.example.watchdl.helperClasses.DataFetcher
import com.example.watchdl.helperClasses.FileEditor
import com.example.watchdl.helperClasses.LocationWorker
import com.example.watchdl.objects.BusStop
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class MainActivity : FragmentActivity() {
    private val locationPermissionCode = 2

    private lateinit var pagerBinding: ActivityMainBinding
    private lateinit var stopAdapter: StopAdapter

    private val dataFetcher: DataFetcher = DataFetcher()
    private lateinit var fileEditor: FileEditor

    private lateinit var infoUpdater: ScheduledFuture<*>

    private var stops: MutableList<BusStop> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(pagerBinding.root)

        fileEditor = FileEditor(this)
//        fileEditor.fillFile()
        requestPermissions()

        Log.d("Internet", internetAvailable().toString())
        if(!internetAvailable()){
            pagerBinding.offlineScreen.text = getString(R.string.you_are_offline)
            pagerBinding.offlineScreen.visibility = View.VISIBLE
        }
        else{
            pagerBinding.offlineScreen.visibility = View.INVISIBLE
            setStops()
            setEventListeners()
            if(stops.isEmpty()) {
                pagerBinding.offlineScreen.text = getString(R.string.no_busstops)
                pagerBinding.offlineScreen.visibility = View.VISIBLE
            }
        }

        pagerBinding.buttonReload.setColorFilter(Color.GRAY)



        val refreshWork = OneTimeWorkRequest.Builder(LocationWorker::class.java).setInputData(Data.Builder().putBoolean("atStop", false).build())
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("InitialLocationWorkRequest", ExistingWorkPolicy.KEEP, refreshWork.build())
    }

    override fun onStart() {
        super.onStart()

//        if(checkInternet()) pagerBinding.offlineScreen.visibility = View.INVISIBLE
//        else pagerBinding.offlineScreen.visibility = View.VISIBLE
    }

    private fun internetAvailable(): Boolean{
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setStops(){
        stops = fileEditor.getStops() as MutableList<BusStop>
        stopAdapter = StopAdapter(this)
        pagerBinding.pager.adapter = stopAdapter

        GlobalScope.launch(CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()}) {
            try {
                for ((i, stop) in stops.withIndex()) {
                    stops[i] = dataFetcher.fetchStopBusses(stop)
                    withContext(Dispatchers.Main) {
                        stopAdapter.addStop(stops[i])
                    }
                }
            } catch (e:Error){
                e.message?.let { Log.e("Error", it) }
            }
            setCurrentStop()
            println("fetch requests done")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setEventListeners(){
        pagerBinding.buttonReload.setOnClickListener {
            pagerBinding.buttonReload.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate))
            GlobalScope.launch { updateInfo() }
        }
    }

    private fun setCurrentStop(){
        val currentStopId = intent.getIntExtra("currentStopId",0)
        if(currentStopId != 0){
            pagerBinding.pager.currentItem = stopAdapter.getFragmentPosition(currentStopId)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startInfoUpdater(){
        val refreshRate = fileEditor.getAutoRefresh().toLong()
        if(refreshRate > 0){
            val service = Executors.newSingleThreadScheduledExecutor()
            val handler = Handler(Looper.getMainLooper())
            infoUpdater = service.scheduleAtFixedRate({
                handler.run {
                    GlobalScope.launch {
                        updateInfo()
                    }
                }
            }, refreshRate, refreshRate, TimeUnit.SECONDS)
        }
    }

    private suspend fun updateInfo(){
        pagerBinding.buttonReload.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate))
        for((i,stop) in stops.withIndex()){
            try {
                stops[i] = dataFetcher.fetchStopBusses(stop)
                withContext(Dispatchers.Main) {
                    stopAdapter.updateStop(stops[i])
                }
            } catch(e: Error){
                e.message?.let { Log.e("DataFetcher", it) }
            }
        }
    }

    private fun stopInfoUpdater(){
        infoUpdater.cancel(false)
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        startInfoUpdater()
    }

    override fun onPause() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        stopInfoUpdater()
        Log.d("test", "Exited app")
        super.onPause()
    }

    override fun onDestroy() {

        super.onDestroy()

    }

    private fun requestPermissions(){
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCode)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), locationPermissionCode)
            }
        }
    }
}