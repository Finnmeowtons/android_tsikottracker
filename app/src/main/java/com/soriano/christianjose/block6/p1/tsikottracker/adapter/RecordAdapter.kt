package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.GetRecord
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemRecordBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RecordAdapter() : RecyclerView.Adapter<RecordViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<GetRecord>(){
        override fun areItemsTheSame(oldItem: GetRecord, newItem: GetRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GetRecord, newItem: GetRecord): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var records: List<GetRecord>
        get() = differ.currentList
        set(value) { differ.submitList(value) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder(ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.binding.apply {
            val record = records[position]
            tvRegistrationPlateValue.text = record.customer_car_plate_number
            tvCustomerNameValue.text = record.customer_name
            tvEmployeeNameTitle.text = record.employee_name
            tvOfferValue.text = record.offer
            tvPriceValue.text = record.offer_price.toString()
            val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val formattedTime = record.time?.let { outputFormat.format(it) }
            tvTimeValue.text = formattedTime
        }
    }
}

class RecordViewHolder(val binding: ItemRecordBinding): RecyclerView.ViewHolder(binding.root){

}