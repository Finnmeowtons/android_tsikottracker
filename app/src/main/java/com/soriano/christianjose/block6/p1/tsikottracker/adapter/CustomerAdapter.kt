package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.Customer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemCustomerBinding

class CustomerAdapter : RecyclerView.Adapter<CustomerViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Customer>(){
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var customers: List<Customer>
        get() = differ.currentList
        set(value) { differ.submitList(value) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        return CustomerViewHolder(ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return customers.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.binding.apply {
            val customer = customers[position]
            tvCustomerEmployeeNameValue.text = customer.name
            tvRegistrationPlateValue.text = customer.car_plate_number
        }
    }
}

class CustomerViewHolder(val binding :ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root)