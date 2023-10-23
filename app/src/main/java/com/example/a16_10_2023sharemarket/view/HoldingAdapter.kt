package com.example.a16_10_2023sharemarket.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a16_10_2023sharemarket.databinding.ViewholdingBinding

class HoldingAdapter(private val ShareHoldingList:ArrayList<ShareHolding>):RecyclerView.Adapter<HoldingAdapter.HoldingViewHolder>() {


    class HoldingViewHolder(private val binding: ViewholdingBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(shareholdinglist: ShareHolding) {
            binding.dateOfCallTextView.text=shareholdinglist.dateOfCall
            binding.shareNameTextView.text=shareholdinglist.shareName
            binding.targetDateTextView.text=shareholdinglist.targetDate
            binding.returnPercentageTextView.text=shareholdinglist.returnPercentage.toString()
        }
    }


    override fun getItemCount()=ShareHoldingList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val binding=ViewholdingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HoldingViewHolder(binding)
    }



    override fun onBindViewHolder(holder: HoldingViewHolder, position: Int) {
        val shareholdinglist=ShareHoldingList[position]
        holder.bind(shareholdinglist)
    }


}