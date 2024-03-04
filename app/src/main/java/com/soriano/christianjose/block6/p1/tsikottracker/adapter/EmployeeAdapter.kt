package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemEmployeeBinding

class EmployeeAdapter() : RecyclerView.Adapter<EmployeeViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Employee>(){
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var employees: List<Employee>
        get() = differ.currentList
        set(value) { differ.submitList(value) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.binding.apply {
            val employee = employees[position]
            tvEmployeeNameValue.text = employee.name.toSentenceCase()
            tvPositionValue.text = employee.position.toSentenceCase()
            tvPositionValuee.text = employee.contact_details
            Log.d("MyTag" , "${employee.contact_details} || ${tvPositionTitlee.text.isNullOrEmpty()}")

            if (employee.contact_details == null){
                tvPositionValuee.visibility = View.GONE
                tvPositionTitlee.visibility = View.GONE
            }
        }
    }

    fun String.toSentenceCase(): String {
        return lowercase() // Start by lowercasing the entire string
            .split("\\s+".toRegex()) // Split into words based on spaces
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
    }
}

class EmployeeViewHolder(val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root){

}