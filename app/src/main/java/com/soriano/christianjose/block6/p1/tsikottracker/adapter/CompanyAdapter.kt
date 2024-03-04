package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemCompaniesBinding

class CompanyAdapter() : RecyclerView.Adapter<CompanyViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Company>(){
        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var companies: List<Company>
        get() = differ.currentList
        set(value) { differ.submitList(value) }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        return CompanyViewHolder(ItemCompaniesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return companies.size
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.binding.apply{
            val company = companies[position]
            tvId.text = company.name
        }
    }




}

class CompanyViewHolder(val binding: ItemCompaniesBinding): RecyclerView.ViewHolder(binding.root){

}