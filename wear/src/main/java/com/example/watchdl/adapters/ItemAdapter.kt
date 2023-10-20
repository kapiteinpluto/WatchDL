package com.example.watchdl.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.VISIBLE
import androidx.recyclerview.widget.RecyclerView
import com.example.watchdl.objects.BusItem
import com.example.watchdl.databinding.ListItemBusBinding

class ItemAdapter (var items : List<BusItem>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){
    private lateinit var binding: ListItemBusBinding
    private lateinit var viewHolder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =LayoutInflater.from(parent.context)
        binding=ListItemBusBinding.inflate(inflater,parent,false)
        viewHolder = ViewHolder(binding)
        viewHolder.setIsRecyclable(false)
        binding.busOffline.setColorFilter(Color.GRAY)
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if(items[position].)
        holder.bind(items[position])
    }
    override fun getItemViewType(position: Int): Int = position
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : ListItemBusBinding) : RecyclerView.ViewHolder(itemView.root){
        @SuppressLint("SetTextI18n")
        fun bind(item : BusItem){
            binding.apply {
                busNumber.text=item.number

                val arriveTime: String = if(item.arriveInterval < 60){
                    item.arriveInterval.toString() + "\'"
                } else {
                    (item.arriveInterval/60).toString() + "h"+ "%02d".format(item.arriveInterval%60) + "\'"
                }
                busArriveTime.text  = arriveTime
                if(item.line.length < 17) busRoute.text = item.line
                else {
                    busRoute.text = item.line.substring(0,16) + "..."
                }

                busNumber.background.setTint(item.color)
            }
            if(item.liveData) binding.busOffline.visibility = INVISIBLE
            if(item == items[items.size-1]) (binding.root.layoutParams as MarginLayoutParams).setMargins(0,0,0,100)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateBusses(updatedBusses: List<BusItem>){
        items = updatedBusses
        items = items.sortedBy { it.arriveInterval }

//        println(items.map { it.arriveInterval })
        notifyDataSetChanged()
    }
}
