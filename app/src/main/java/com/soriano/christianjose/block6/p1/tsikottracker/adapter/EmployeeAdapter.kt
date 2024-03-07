package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.EmployeeFragmentDirections
import com.soriano.christianjose.block6.p1.tsikottracker.OfferFragmentDirections
import com.soriano.christianjose.block6.p1.tsikottracker.R
import com.soriano.christianjose.block6.p1.tsikottracker.api.EmployeeApi
import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemEmployeeBinding
import com.soriano.christianjose.block6.p1.tsikottracker.modal.ModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeAdapter(private val employeeApi: EmployeeApi, private val fragmentManager: FragmentManager, private val companyId: Int, private val navController: NavController) : RecyclerView.Adapter<EmployeeViewHolder>() {
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

            root.setOnLongClickListener { _ ->
                val modalBottomSheet = ModalBottomSheet()
                modalBottomSheet.show(fragmentManager, ModalBottomSheet.TAG)
                Handler(Looper.getMainLooper()).postDelayed({
                    modalBottomSheet.view?.findViewById<Button>(R.id.btnEdit)?.setOnClickListener {
                        Log.d("MyTag", "Edit Button Clicked")
                        val directions = EmployeeFragmentDirections.actionEmployeeFragmentToUpdateEmployeeFragment(
                            employee.id,
                            employee.name,
                            employee.position,
                            employee.contact_details.toString()
                        )

                        navController.navigate(directions)

                        modalBottomSheet.dismiss()
                    }
                    modalBottomSheet.view?.findViewById<Button>(R.id.btnDelete)?.setOnClickListener {
                        Log.d("MyTag", "Delete Button Clicked")
                        CoroutineScope(Dispatchers.IO).launch {  // Perform network call on IO thread
                            try {
                                employee.company_id = null
                                employeeApi.deleteEmployee(employee.id, employee).enqueue(object :
                                    Callback<Employee> {
                                    override fun onResponse(
                                        call: Call<Employee>,
                                        response: Response<Employee>
                                    ) {
                                        Log.d("MyTag", "HEHE $call || ${response.body()}")

                                        employeeApi.getEmployee(companyId).enqueue(object : Callback<List<Employee>>{
                                            override fun onResponse(
                                                call: Call<List<Employee>>,
                                                response: Response<List<Employee>>
                                            ) {
                                                if (response.isSuccessful) {
                                                    val employeeResponse = response.body()
                                                    Log.d("MyTag", "$employeeResponse || ${response.body()}")
                                                    if (employeeResponse != null) {
                                                        employees = employeeResponse
                                                        notifyDataSetChanged()
                                                    }
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<List<Employee>>,
                                                t: Throwable
                                            ) {
                                                Log.d("MyTag", "$call", t)
                                            }
                                        })

                                    }

                                    override fun onFailure(call: Call<Employee>, t: Throwable) {
                                        Log.d("MyTag", "$call", t)
                                    }
                                })
                            } catch (e: Exception) {
                                Log.e("MyTag", "Oops $e")
                            }
                        }
                        modalBottomSheet.dismiss()
                    }
                }, 100)
                true
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