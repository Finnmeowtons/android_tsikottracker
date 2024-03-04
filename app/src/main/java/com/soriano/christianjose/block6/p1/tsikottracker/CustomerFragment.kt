package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.CustomerAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.OfferAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.CustomerApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.Customer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentCustomerBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CustomerFragment : Fragment() {
    private var _binding: FragmentCustomerBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyApi: CompanyApi
    private lateinit var customerApi: CustomerApi
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var companies: List<Company>
    private lateinit var companyNames: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        val view = binding.root
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sharedViewModel.updateAppBarTitle("Customers")

        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        companyNames = mutableListOf<String>()
        companies = emptyList()

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, companyNames)
        binding.etCompanySelect.setAdapter(adapter)

        val recyclerViewAdapter = CustomerAdapter()
        binding.recyclerView.adapter = recyclerViewAdapter

        customerApi = retrofit.create(CustomerApi::class.java)
        customerApi.getCustomer(companyId).enqueue(object : Callback<List<Customer>>{
            override fun onResponse(
                call: Call<List<Customer>>,
                response: Response<List<Customer>>
            ) {
                if (response.isSuccessful) {
                    val customers = response.body()
                    Log.d("MyTag", "$customers || ${response.body()}")
                    if (customers != null) {
                        recyclerViewAdapter.customers = customers
                    }
                } else {
                    // Handle API error
                }
            }

            override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                Log.d("MyTag", "$call", t)
            }
        })


        companyApi = retrofit.create(CompanyApi::class.java)
        companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList() // Store the companies
                    companyNames.clear()
                    companies.forEach { company -> companyNames.add(company.name.toSentenceCase()) }
                    adapter.notifyDataSetChanged()

                    val companyToSelect = companies.find { it.id == companyId }
                    if (companyToSelect != null) {
                        binding.etCompanySelect.setText(companyToSelect.name.toSentenceCase(), false)
                    }

                } else {
                    // Handle API error
                }
            }
            override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                Log.e("MyTag", "$call" , t)
            }
        })

        binding.etCompanySelect.onItemClickListener  = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Log.d("MyTag", "Before getStoredCompanyId $companyId")
            val selectedCompanyName = parent.getItemAtPosition(position) as String
            Log.d("MyTag", "Before selectedCompanyName $selectedCompanyName")
            val selectedCompany = companies.find { it.name == selectedCompanyName.lowercase() }
            Log.d("MyTag", "Before selectedCompany $selectedCompany")


            if (selectedCompany != null) {
                authUserManager.storeCompanyId(selectedCompany.id)
                companyId = authUserManager.getStoredCompanyId()
                customerApi.getCustomer(companyId).enqueue(object : Callback<List<Customer>>{
                    override fun onResponse(
                        call: Call<List<Customer>>,
                        response: Response<List<Customer>>
                    ) {
                        if (response.isSuccessful) {
                            val customers = response.body()
                            Log.d("MyTag", "$customers || ${response.body()}")
                            if (customers != null) {
                                recyclerViewAdapter.customers = customers
                            }
                        } else {
                            // Handle API error
                        }
                    }

                    override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                        Log.d("MyTag", "$call", t)
                    }
                })


            }
        }



        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_side_nav_pop_up_to_dashboard)
        }

        return view
    }

    fun String.toSentenceCase(): String {
        return lowercase() // Start by lowercasing the entire string
            .split("\\s+".toRegex()) // Split into words based on spaces
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}