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
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.CustomerAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.CustomerApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.Customer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentCustomerBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.CarPlateNumbers
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
    private val carPlateNUmberViewModel : CarPlateNumbers by activityViewModels()
    private lateinit var companies: List<Company>
    private lateinit var companyNames: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var getCustomers : List<Customer>
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

        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.search_menu)
        sharedViewModel.updateAppBarTitle("Customers")

        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        companyNames = mutableListOf<String>()
        companies = emptyList()

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, companyNames)
        binding.etCompanySelect.setAdapter(adapter)

        customerApi = retrofit.create(CustomerApi::class.java)
        val recyclerViewAdapter = CustomerAdapter(customerApi, parentFragmentManager, companyId, findNavController())
        binding.recyclerView.adapter = recyclerViewAdapter
        customerApi.getCustomer(companyId).enqueue(object : Callback<List<Customer>>{
            override fun onResponse(
                call: Call<List<Customer>>,
                response: Response<List<Customer>>
            ) {
                if (response.isSuccessful) {
                    val searchView = toolbar?.menu?.findItem(R.id.search)?.actionView as SearchView
                    searchView.setQuery("", false) // Clear the text
                    searchView.isIconified = true
                    toolbar.menu?.findItem(R.id.search)?.collapseActionView()
                    val customers = response.body()
                    Log.d("MyTag", "$customers || ${response.body()}")
                    if (customers != null) {
                        recyclerViewAdapter.customers = customers
                        getCustomers = customers
                        if (getCustomers.isEmpty()){
                            binding.llNoResult.visibility = View.VISIBLE
                        } else {
                            binding.llNoResult.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                Log.d("MyTag", "$call", t)
            }
        })

        toolbar?.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                R.id.search ->{
                    val searchView = menuItem.actionView as SearchView
                    searchView.isIconified = false // Expand the search view
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean = true

                        override fun onQueryTextChange(query: String?): Boolean {
                            if (query != null) {
                                val searchQuery = query.lowercase()
                                val filteredOffers = getCustomers.filter { customer ->
                                    customer.name?.lowercase()?.contains(searchQuery) == true || customer.car_plate_number.lowercase().contains(searchQuery)
                                }
                                recyclerViewAdapter.customers = filteredOffers
                                if (filteredOffers.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            } else {
                                recyclerViewAdapter.customers = getCustomers
                                if (getCustomers.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            }
                            return true
                        }
                    })
                    true
                }
                else -> false
            }
        }


        companyApi = retrofit.create(CompanyApi::class.java)
        companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList() // Store the companies
                    companyNames.clear()
                    companies.forEach { company -> companyNames.add(company.name) }
                    adapter.notifyDataSetChanged()

                    val companyToSelect = companies.find { it.id == companyId }
                    if (companyToSelect != null) {
                        binding.etCompanySelect.setText(companyToSelect.name, false)
                    }

                }
            }
            override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                Log.e("MyTag", "$call" , t)
            }
        })

        binding.etCompanySelect.onItemClickListener  = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedCompanyName = parent.getItemAtPosition(position) as String
            val selectedCompany = companies.find { it.name == selectedCompanyName }


            if (selectedCompany != null) {
                authUserManager.storeCompanyId(selectedCompany.id)
                companyId = authUserManager.getStoredCompanyId()
                customerApi.getCustomer(companyId).enqueue(object : Callback<List<Customer>>{
                    override fun onResponse(
                        call: Call<List<Customer>>,
                        response: Response<List<Customer>>
                    ) {
                        if (response.isSuccessful) {
                            val searchView = toolbar?.menu?.findItem(R.id.search)?.actionView as SearchView
                            searchView.setQuery("", false) // Clear the text
                            searchView.isIconified = true
                            toolbar.menu?.findItem(R.id.search)?.collapseActionView()
                            val customers = response.body()
                            Log.d("MyTag", "$customers || ${response.body()}")
                            if (customers != null) {
                                recyclerViewAdapter.customers = customers
                                getCustomers = customers
                                if (getCustomers.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                        Log.d("MyTag", "$call", t)
                    }
                })


            }
        }



        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}