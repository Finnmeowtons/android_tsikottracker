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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyApi: CompanyApi
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
        val companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        companyNames = mutableListOf<String>()
        companies = emptyList()

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, companyNames)
        binding.etCompanySelect.setAdapter(adapter)

        companyApi = retrofit.create(CompanyApi::class.java)
        companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                Log.d("MyTag", "All Companies: ${response.body()}")
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList() // Store the companies
                    companyNames.clear()
                    companies.forEach { company -> companyNames.add(company.name) }
                    Log.d("MyTag", "All Companies: $companies")


                    val companyToSelect = companies.find { it.id == companyId }
                    if (companyToSelect != null) {
                        binding.etCompanySelect.setText(companyToSelect.name, false)
                    }

                    adapter.notifyDataSetChanged()
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

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_side_nav_pop_up_to_dashboard)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}