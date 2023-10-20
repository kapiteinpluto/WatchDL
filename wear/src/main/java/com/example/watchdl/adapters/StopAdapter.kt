package com.example.watchdl.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.watchdl.objects.BusStop
import com.example.watchdl.StopFragment

class StopAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private var fragments: MutableList<StopFragment> = mutableListOf()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addStop(stop: BusStop){
        val fragment = StopFragment(stop)
        fragments.add(fragment)
        updateArrows()
        notifyItemChanged(fragments.size - 1)
    }

    private fun updateArrows(){
        for((i, fragment) in fragments.withIndex()){
            fragment.setArrows(i != 0, i != fragments.size - 1)
        }
    }

    fun getFragmentPosition(stopId: Int): Int {
        for((i,fragment) in fragments.withIndex()){
            if(fragment.stop.id == stopId){
                return i
            }
        }
        return 0
    }

    fun updateStop(stop: BusStop){
        for(fragment in fragments){
            if(fragment.stop.id == stop.id){
                fragment.updateStop(stop)
                break
            }
        }
    }
}