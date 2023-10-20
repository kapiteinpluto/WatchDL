package com.example.watchdl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchdl.adapters.ItemAdapter
import com.example.watchdl.databinding.StopFragmentBinding
import com.example.watchdl.objects.BusStop

class StopFragment(var stop: BusStop) : Fragment() {
    private lateinit var binding: StopFragmentBinding

    private var arrowLeft: Boolean = false
    private var arrowRight: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StopFragmentBinding.inflate(layoutInflater)
        setStopView()
        setArrowView()

        return binding.root
    }

    private fun setStopView(){
        val bussesToUse = stop.busses.filter { stop.visibleBusses.contains(it.number) }
        binding.title.text = stop.name
        binding.recyclerView.adapter = ItemAdapter(bussesToUse)
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.setItemViewCacheSize(100)
        if (stop.busses.isEmpty()) binding.message.visibility = View.VISIBLE

    }

    private fun setArrowView(){
        binding.buttonLeft.visibility = if (arrowLeft) View.VISIBLE else View.INVISIBLE
        binding.buttonRight.visibility = if (arrowRight) View.VISIBLE else View.INVISIBLE
    }

    fun updateStop(stop: BusStop){
        this.stop = stop
        if(this::binding.isInitialized) (binding.recyclerView.adapter as ItemAdapter).updateBusses(stop.busses.filter { stop.visibleBusses.contains(it.number) })
    }

    fun setArrows(arrowLeft: Boolean, arrowRight: Boolean){
        this.arrowLeft = arrowLeft
        this.arrowRight = arrowRight
        if(this::binding.isInitialized) setArrowView()
    }

}