package com.example.watchdl.helperClasses

import android.content.Context
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import java.io.File

class FileSender(val context: Context) {
    fun sendFilesToWatch(){
        Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener {
            if(it.size > 0) {
                val nodeId = it[0].id
                val client = Wearable.getChannelClient(context)

    //                Wearable.getMessageClient(this).sendMessage(it[0].id,"/test", "messageTest".toByteArray())
                sendFile(FileEditor(context).getFile(), "/stopSettings", client, nodeId)
                sendFile(FileEditor(context).getSettingsFile(), "/appSettings", client, nodeId)
            }
            else {
                Toast.makeText(context, "No wearable connected", Toast.LENGTH_SHORT).show()
                println("No wearable connected")
            }
        }
    }

    private fun sendFile(file: File, channelPath: String, client: ChannelClient, nodeId: String){
        val channel: Task<ChannelClient.Channel> = client.openChannel(
            nodeId,
            channelPath
        )
        channel.onSuccessTask {
            println("SEND")
            client.sendFile(channel.result, file.toUri()).apply {
                addOnSuccessListener {
                    println("succesfully send " + channelPath)
                }
                addOnFailureListener { e -> println(e.message + e.stackTrace) }
            }
        }
        channel.addOnFailureListener { e ->
            println(e.message)
        }
    }
}