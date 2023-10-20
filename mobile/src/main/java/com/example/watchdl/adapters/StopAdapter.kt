package com.example.watchdl.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.watchdl.objects.BusStop
import com.example.watchdl.activities.StopDetailsActivity
import com.example.watchdl.databinding.StopItemBinding

class StopAdapter (var items : List<BusStop>) : RecyclerView.Adapter<StopAdapter.ViewHolder>(){
    private lateinit var binding: StopItemBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater =LayoutInflater.from(context)
        binding = StopItemBinding.inflate(inflater,parent,false)
        val viewHolder = ViewHolder(binding)
        viewHolder.setIsRecyclable(false)
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount() = items.size
    inner class ViewHolder(itemView : StopItemBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(item : BusStop){
            binding.apply {
                stopId.text= item.id.toString()
                stopName.text = item.name
                buttonDetails.setOnClickListener {
                    ContextCompat.startActivity(
                        context,
                        Intent(context, StopDetailsActivity::class.java).putExtra("stopId", item.id),
                        null
                    )
                }
            }
        }
    }
}
