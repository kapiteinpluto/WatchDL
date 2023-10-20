package com.example.watchdl.helperClasses

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.Toast
import androidx.core.net.toUri
import com.example.watchdl.MainActivity
import com.google.android.gms.wearable.*
import java.io.File

class FileReceiver : WearableListenerService() {
    lateinit var stopSettings: File
    lateinit var appSettings: File

    override fun onCreate() {
        super.onCreate()
        stopSettings = File(applicationContext.filesDir,"settings")
        appSettings = File(applicationContext.filesDir,"settings2")
        stopSettings.appendText("")
        appSettings.appendText("")
    }

    override fun onMessageReceived(p0: MessageEvent) {
        super.onMessageReceived(p0)
        println(String(p0.data))
    }

    override fun onChannelOpened(p0: ChannelClient.Channel) {
        super.onChannelOpened(p0)
        val client = Wearable.getChannelClient(applicationContext)

        if(p0.path == "/stopSettings"){
            receiveFile(stopSettings, p0, client, false)
        }
        else if(p0.path == "/appSettings"){
            receiveFile(appSettings, p0, client, true)
        }
    }

    private fun receiveFile(file: File, p0: ChannelClient.Channel, client: ChannelClient, startActivityOnSucces: Boolean){
        file.delete()
        client.receiveFile(
            p0,
            file.toUri(),
            true
        ).apply {
            addOnSuccessListener {
                println("sending successful " + p0.path)
                if(startActivityOnSucces){
                    startActivity(Intent(applicationContext,MainActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK).addFlags(
                        FLAG_ACTIVITY_CLEAR_TASK), null )
                }
            }
            addOnFailureListener {
                println(it)
                println(it.cause)
                println(it.localizedMessage)
                println("---trace")
                for(line in it.stackTrace){
                    println(line)
                }

                Toast.makeText(applicationContext, "sending failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}