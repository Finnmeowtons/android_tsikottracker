package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemRecordBinding

class RecordAdapter() : RecyclerView.Adapter<RecordViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Record>(){
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var records: List<Record>
        get() = differ.currentList
        set(value) { differ.submitList(value) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.binding.apply {
            val record = records[position]
        }
    }
}

class RecordViewHolder(val binding: ItemRecordBinding): RecyclerView.ViewHolder(binding.root){

}