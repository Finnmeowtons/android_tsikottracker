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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.RecordAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.GetRecord
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyApi: CompanyApi
    private lateinit var recordApi: RecordApi
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var companies: List<Company>
    private lateinit var companyNames: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val view = binding.root
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sharedViewModel.updateAppBarTitle("Record")
        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        companyNames = mutableListOf<String>()
        companies = emptyList()

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, companyNames)
        binding.etCompanySelect.setAdapter(adapter)

        val recyclerViewAdapter = RecordAdapter()
        binding.recyclerView.adapter = recyclerViewAdapter

        recordApi = retrofit.create(RecordApi::class.java)
        recordApi.getRecords(companyId).enqueue(object : Callback<List<GetRecord>>{
            override fun onResponse(
                call: Call<List<GetRecord>>,
                response: Response<List<GetRecord>>
            ) {
                if (response.isSuccessful) {
                    val records = response.body()
                    Log.d("MyTag", "$records || ${response.body()}")
                    if (records != null) {
                        recyclerViewAdapter.records = records
                    }
                }
            }

            override fun onFailure(call: Call<List<GetRecord>>, t: Throwable) {
                Log.e("MyTag", "$call", t)
            }
        })

        companyApi = retrofit.create(CompanyApi::class.java)
        companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                Log.d("MyTag", "All Companies: ${response.body()}")
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList() // Store the companies
                    companyNames.clear()
                    companies.forEach { company -> companyNames.add(company.name.toSentenceCase()) }
                    Log.d("MyTag", "All Companies: $companies")


                    val companyToSelect = companies.find { it.id == companyId }
                    if (companyToSelect != null) {
                        binding.etCompanySelect.setText(companyToSelect.name.toSentenceCase(), false)
                    }

                    adapter.notifyDataSetChanged()
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
                recordApi.getRecords(companyId).enqueue(object : Callback<List<GetRecord>>{
                    override fun onResponse(
                        call: Call<List<GetRecord>>,
                        response: Response<List<GetRecord>>
                    ) {
                        if (response.isSuccessful) {
                            val records = response.body()
                            Log.d("MyTag", "$records || ${response.body()}")
                            if (records != null) {
                                recyclerViewAdapter.records = records
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<GetRecord>>, t: Throwable) {
                        Log.e("MyTag", "$call", t)
                    }
                })
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_recordFragment2_to_addRecordFragment)
        }
    }
}