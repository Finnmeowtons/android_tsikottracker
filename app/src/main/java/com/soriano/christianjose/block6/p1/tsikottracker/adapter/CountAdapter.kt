package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.R
import com.soriano.christianjose.block6.p1.tsikottracker.data.AnalyticsOfferCount
import com.soriano.christianjose.block6.p1.tsikottracker.data.OfferCount
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemCountBinding

class CountAdapter() : RecyclerView.Adapter<CountViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<AnalyticsOfferCount>(){
        override fun areItemsTheSame(oldItem: AnalyticsOfferCount, newItem: AnalyticsOfferCount): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: AnalyticsOfferCount, newItem: AnalyticsOfferCount): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var counts: List<AnalyticsOfferCount>
        get() = differ.currentList
        set(value) { differ.submitList(value) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountViewHolder {
        return CountViewHolder(ItemCountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return counts.size
    }

    override fun onBindViewHolder(holder: CountViewHolder, position: Int) {
        val count = counts[position]
        holder.binding.apply {
            tvCount.text = count.count.toString()
            tvOfferName.text = count.name
            if (count.type == "service"){
                imgOfferType.setImageResource(R.drawable.service)
            } else if (count.type == "product"){
                imgOfferType.setImageResource(R.drawable.product)
            }
        }
    }
}

class CountViewHolder(val binding : ItemCountBinding) : RecyclerView.ViewHolder(binding.root){

}