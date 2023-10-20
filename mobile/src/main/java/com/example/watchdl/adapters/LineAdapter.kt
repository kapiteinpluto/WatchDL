package com.example.watchdl.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.watchdl.databinding.LineItemBinding
import com.example.watchdl.objects.BusLine

class LineAdapter (var items : List<BusLine>) : RecyclerView.Adapter<LineAdapter.ViewHolder>(){
    private lateinit var binding: LineItemBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater =LayoutInflater.from(context)
        binding= LineItemBinding.inflate(inflater,parent,false)
        val viewHolder = ViewHolder(binding)
        viewHolder.setIsRecyclable(false)
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : LineItemBinding) : RecyclerView.ViewHolder(itemView.root){
        @SuppressLint("NotifyDataSetChanged")
        fun bind(item : BusLine, position: Int){
            binding.apply {
                busRoute.text = item.description
                busNumber.text = item.number
                busNumber.background.setTint(item.color.toColorInt())
                if(!item.enabled){
                    buttonDelete.visibility = View.INVISIBLE
                    buttonAdd.visibility = View.VISIBLE
                    busRoute.alpha = 0.3F
                    busNumber.background.alpha = 100
                }
                else{
                    buttonAdd.visibility = View.INVISIBLE
                    buttonDelete.visibility = View.VISIBLE
                }
                buttonDelete.setOnClickListener {
                    item.enabled = false
                    notifyDataSetChanged()
                }
                buttonAdd.setOnClickListener {
                    item.enabled = true
                    notifyDataSetChanged()
                }
            }
        }
    }
}
