package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddEmployeeBinding
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemAddEmployeeBinding

class AddEmployeeAdapter(private val companyId : Int) : RecyclerView.Adapter<AddEmployeeViewHolder>() {

    private val employeeList = mutableListOf(Employee(id = 0, name ="", contact_details = "", position = "", company_id = companyId))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddEmployeeViewHolder {
        return AddEmployeeViewHolder(ItemAddEmployeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    override fun onBindViewHolder(holder: AddEmployeeViewHolder, position: Int) {
        val currentItem = employeeList[position]
        Log.e("MyTag", currentItem.toString())

        holder.binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun afterTextChanged(p0: Editable?) {
                val name = p0.toString()
                if (name.isNotBlank()) {
                    currentItem.name = name
                }
            }
        })

        holder.binding.etPosition.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun afterTextChanged(p0: Editable?) {
                val employeePosition = p0.toString().lowercase()
                if (employeePosition.isNotBlank()) {
                    currentItem.position = employeePosition
                }
            }
        })

        holder.binding.etContactDetail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun afterTextChanged(p0: Editable?) {
                val contactDetail = p0.toString()

                if (contactDetail.isNotBlank()) {
                    currentItem.contact_details = contactDetail
                }
            }
        })
    }

    fun addItem() {
        employeeList.add(Employee(id = 0, name ="", contact_details = "", position = "", company_id = companyId))
        notifyItemInserted(employeeList.size+1) // Tell the RecyclerView to update
    }

    fun getEmployees(): List<Employee> {
        Log.d("MyTag", employeeList.toString())
        return employeeList  // Assuming you filter/validate as needed
    }

    fun validateFields(binding: FragmentAddEmployeeBinding, adapter: AddEmployeeAdapter): Boolean {
        var allValid = true

        for (i in 0 until adapter.employeeList.size) {
            val viewHolder =
                binding.rvAddServices.findViewHolderForAdapterPosition(i) as? AddEmployeeViewHolder
            if (viewHolder != null) {
                val etName = viewHolder.binding.etName.text
                if (etName.isNullOrBlank()) {
                    viewHolder.binding.nameLayout.error = "Please input name"
                    allValid = false
                } else {
                    viewHolder.binding.nameLayout.error = null
                }

                val etPosition = viewHolder.binding.etPosition.text
                if (etPosition.isNullOrBlank()) {
                    viewHolder.binding.positionLayout.error = "Enter a position"
                    allValid = false
                } else {
                    viewHolder.binding.positionLayout.error = null
                }
                if (!allValid) {
                    return false
                }
            }
        }
        return allValid
    }
}

class AddEmployeeViewHolder(val binding : ItemAddEmployeeBinding) : RecyclerView.ViewHolder(binding.root){


}