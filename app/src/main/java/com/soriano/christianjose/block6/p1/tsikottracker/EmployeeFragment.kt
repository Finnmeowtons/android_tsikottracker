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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.EmployeeAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.EmployeeApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentEmployeeBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmployeeFragment : Fragment() {
    private var _binding: FragmentEmployeeBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyApi: CompanyApi
    private lateinit var employeeApi: EmployeeApi
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var companies: List<Company>
    private lateinit var companyNames: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var getEmployees : List<Employee>
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeBinding.inflate(inflater, container, false)
        val view = binding.root
        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.search_menu)
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sharedViewModel.updateAppBarTitle("Employee")

        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        companyNames = mutableListOf<String>()
        companies = emptyList()
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, companyNames)
        binding.etCompanySelect.setAdapter(adapter)



        employeeApi = retrofit.create(EmployeeApi::class.java)

        val recyclerViewAdapter = EmployeeAdapter(employeeApi, parentFragmentManager, companyId, findNavController() )
        binding.rvEmployee.adapter = recyclerViewAdapter


        employeeApi.getEmployee(companyId).enqueue(object : Callback<List<Employee>> {
            override fun onResponse(
                call: Call<List<Employee>>,
                response: Response<List<Employee>>
            ) {
                if (response.isSuccessful) {
                    if (isAdded) {
                        val searchView =
                            toolbar?.menu?.findItem(R.id.search)?.actionView as SearchView
                        searchView.setQuery("", false) // Clear the text
                        searchView.isIconified = true
                        toolbar.menu?.findItem(R.id.search)?.collapseActionView()
                    }
                    val employees = response.body()
                    Log.d("MyTag", "$employees || ${response.body()}")
                    if (employees != null) {
                        recyclerViewAdapter.employees = employees
                        getEmployees = employees
                        if (employees.isEmpty()){
                            binding.llNoResult.visibility = View.VISIBLE
                        } else {
                            binding.llNoResult.visibility = View.GONE
                        }
                    }
                } else {
                    // Handle API error
                }
            }

            override fun onFailure(call: Call<List<Employee>>, t: Throwable) {
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
                                val filteredOffers = getEmployees.filter { employee ->
                                    employee.name.lowercase().contains(searchQuery) || employee.position.lowercase().contains(searchQuery)
                                }
                                recyclerViewAdapter.employees = filteredOffers
                                if (filteredOffers.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            } else {
                                recyclerViewAdapter.employees = getEmployees
                                if (getEmployees.isEmpty()){
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
            val selectedCompany = companies.find { it.name == selectedCompanyName }
            if (selectedCompany != null) {
                authUserManager.storeCompanyId(selectedCompany.id)
                companyId = authUserManager.getStoredCompanyId()
                Log.d("MyTag", "StoredCompanyId $companyId")

                employeeApi.getEmployee(selectedCompany.id).enqueue(object : Callback<List<Employee>> {
                    override fun onResponse(
                        call: Call<List<Employee>>,
                        response: Response<List<Employee>>
                    ) {
                        if (response.isSuccessful) {
                            val searchView = toolbar?.menu?.findItem(R.id.search)?.actionView as SearchView
                            searchView.setQuery("", false) // Clear the text
                            searchView.isIconified = true
                            toolbar.menu?.findItem(R.id.search)?.collapseActionView()
                            val employees = response.body()
                            Log.d("MyTag", "$employees || ${response.body()}")
                            if (employees != null) {
                                recyclerViewAdapter.employees = employees
                                getEmployees = employees
                                if (employees.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            }
                        } else {
                            // Handle API error
                        }
                    }
                    override fun onFailure(call: Call<List<Employee>>, t: Throwable) {
                        Log.d("MyTag", "$call", t)
                    }
                })
            } else {
                Log.d("MyTag", "oh no",)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_employeeFragment_to_addEmployeeFragment2)
        }


        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
        }

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                // Scrolling Down
                binding.floatingActionButton.hide()
            } else if (scrollY < oldScrollY ) {
                // Scrolling Up
                binding.floatingActionButton.show()
            }
        })
        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.findViewById<MaterialToolbar>(R.id.topAppBar)?.menu?.clear()
    }
}