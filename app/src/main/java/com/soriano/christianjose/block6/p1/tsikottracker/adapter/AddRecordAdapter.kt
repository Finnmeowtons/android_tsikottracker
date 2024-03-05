package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemAddRecordBinding

class AddRecordAdapter(private val companyId : Int) : RecyclerView.Adapter<AddRecordViewHolder>() {
    private val recordList = mutableListOf(Record(
            id = 0,
            customer_name = "",
            customer_car_plate_number = "",
            offer = "",
            price = 0,
            type = "",
            notes = null,
            employee_name = "",
            employee_position = "",
            company_id = companyId
        )
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddRecordViewHolder {
        return AddRecordViewHolder(ItemAddRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: AddRecordViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}

class AddRecordViewHolder(val binding : ItemAddRecordBinding) : RecyclerView.ViewHolder(binding.root)