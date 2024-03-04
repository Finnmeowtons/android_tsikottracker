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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeBinding.inflate(inflater, container, false)
        val view = binding.root
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

        val recyclerViewAdapter = EmployeeAdapter()
        binding.rvEmployee.adapter = recyclerViewAdapter

        employeeApi = retrofit.create(EmployeeApi::class.java)
        employeeApi.getEmployee(companyId).enqueue(object : Callback<List<Employee>> {
            override fun onResponse(
                call: Call<List<Employee>>,
                response: Response<List<Employee>>
            ) {
                if (response.isSuccessful) {
                    val employees = response.body()
                    Log.d("MyTag", "$employees || ${response.body()}")
                    if (employees != null) {
                        recyclerViewAdapter.employees = employees
                    }
                } else {
                    // Handle API error
                }
            }

            override fun onFailure(call: Call<List<Employee>>, t: Throwable) {
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
            val selectedCompany = companies.find { it.name == selectedCompanyName.lowercase() }
            if (selectedCompany != null) {
                authUserManager.storeCompanyId(selectedCompany.id)
                companyId = authUserManager.getStoredCompanyId()
                employeeApi.getEmployee(selectedCompany.id).enqueue(object : Callback<List<Employee>> {
                    override fun onResponse(
                        call: Call<List<Employee>>,
                        response: Response<List<Employee>>
                    ) {
                        if (response.isSuccessful) {
                            val employees = response.body()
                            Log.d("MyTag", "$employees || ${response.body()}")
                            if (employees != null) {
                                recyclerViewAdapter.employees = employees
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
            findNavController().navigate(R.id.action_side_nav_pop_up_to_dashboard)
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