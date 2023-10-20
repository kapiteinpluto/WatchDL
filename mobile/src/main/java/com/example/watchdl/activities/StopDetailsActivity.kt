package com.example.watchdl.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchdl.R
import com.example.watchdl.objects.BusStop
import com.example.watchdl.adapters.LineAdapter
import com.example.watchdl.databinding.StopDetailsPageBinding
import com.example.watchdl.helperClasses.FileEditor

class StopDetailsActivity : AppCompatActivity() {
    private lateinit var binding: StopDetailsPageBinding
    private lateinit var fileEditor: FileEditor
    private lateinit var stop: BusStop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StopDetailsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileEditor = FileEditor(this)

        val stopId: Int = intent.extras?.getInt("stopId").toString().toInt()
        stop = fileEditor.getStop(stopId)

        binding.pageTitle.text = stop.name

        binding.recyclerview.adapter = LineAdapter(stop.lines)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        setEventListeners()
    }

    override fun onPause() {
        fileEditor.updateStop(stop)
        super.onPause()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setEventListeners(){
        binding.buttonBack.setOnClickListener {
//            fileEditor.updateStop(stop)
//            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle())
        }
        binding.buttonDisableAll.setOnClickListener {
            for (line in stop.lines){
                line.enabled = false
            }
            binding.recyclerview.adapter?.notifyDataSetChanged()
        }
        binding.buttonRemoveStop.setOnClickListener {
            fileEditor.deleteStop(stop)
            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeCustomAnimation(this, this.taskId, this.taskId).toBundle())
        }
    }
}