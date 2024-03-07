package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.CustomerFragmentDirections
import com.soriano.christianjose.block6.p1.tsikottracker.R
import com.soriano.christianjose.block6.p1.tsikottracker.api.CustomerApi
import com.soriano.christianjose.block6.p1.tsikottracker.data.Customer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemCustomerBinding
import com.soriano.christianjose.block6.p1.tsikottracker.modal.ModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerAdapter(private val customerApi: CustomerApi, private val fragmentManager: FragmentManager, private val companyId: Int, private val navController: NavController) : RecyclerView.Adapter<CustomerViewHolder>() {

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


            root.setOnLongClickListener { _ ->
                Log.d("MyTag","${customer.id}")
                val modalBottomSheet = ModalBottomSheet()
                modalBottomSheet.show(fragmentManager, ModalBottomSheet.TAG)
                Handler(Looper.getMainLooper()).postDelayed({
                    modalBottomSheet.view?.findViewById<Button>(R.id.btnEdit)?.setOnClickListener {
                        Log.d("MyTag", "Edit Button Clicked")
                        val directions =
                            CustomerFragmentDirections.actionCustomerFragmentToUpdateCustomerFragment(
                                customer.id,
                                customer.name.toString(),
                                customer.car_plate_number
                            )

                        navController.navigate(directions)

                        modalBottomSheet.dismiss()
                    }
                    modalBottomSheet.view?.findViewById<Button>(R.id.btnDelete)
                        ?.setOnClickListener {
                            Log.d("MyTag", "Delete Button Clicked")
                            CoroutineScope(Dispatchers.IO).launch {  // Perform network call on IO thread
                                try {
                                    customer.company_id = null
                                    customerApi.deleteCustomer(customer.id, customer).enqueue(object : Callback<Customer>{
                                        override fun onResponse(
                                            call: Call<Customer>,
                                            response: Response<Customer>
                                        ) {
                                            Log.d("MyTag", "HEHE $call || ${response.body()}")

                                            customerApi.getCustomer(companyId).enqueue(object : Callback<List<Customer>>{
                                                override fun onResponse(
                                                    call: Call<List<Customer>>,
                                                    response: Response<List<Customer>>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        val customerResponse = response.body()
                                                        Log.d("MyTag", "$customerResponse || ${response.body()}")
//                                                        if (customerResponse != null) {
//                                                            customers = customerResponse
//                                                            notifyDataSetChanged()
//                                                        }
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<List<Customer>>,
                                                    t: Throwable
                                                ) {
                                                    Log.d("MyTag", "$call", t)
                                                }
                                            })
                                        }

                                        override fun onFailure(call: Call<Customer>, t: Throwable) {
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
}

class CustomerViewHolder(val binding :ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root){


}