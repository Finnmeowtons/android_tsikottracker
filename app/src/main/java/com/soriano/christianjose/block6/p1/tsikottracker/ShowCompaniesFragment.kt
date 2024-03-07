package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.CompanyAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentShowCompaniesBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ShowCompaniesFragment : Fragment() {
    private var _binding: FragmentShowCompaniesBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyApi: CompanyApi
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowCompaniesBinding.inflate(inflater, container, false)
        val view = binding.root
        val authUserManager = AuthUserManager(requireContext())
        val storedUserId = authUserManager.getStoredUserId()
        val companyId = authUserManager.getStoredCompanyId()

        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        companyApi = retrofit.create(CompanyApi::class.java)


        val adapter = CompanyAdapter(companyApi, parentFragmentManager, companyId, findNavController(), requireContext(), storedUserId, activity)
        binding.rvCompanies.adapter = adapter
        sharedViewModel.updateAppBarTitle("Company")
        Log.d("MyTag", storedUserId.toString())
        companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>>{
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    val companies = response.body()
                    Log.d("MyTag", "$companies || ${response.body()}")
                    if (companies != null) {
                        adapter.companies = companies
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    // Handle API error
                }
            }

            override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                Log.e("MyTag", "$call" , t)
            }

        })

        binding.btnJoinOrCreate.setOnClickListener {
            val directions = ShowCompaniesFragmentDirections.actionShowCompaniesFragmentToCompanySetupFragment(false)
            findNavController().navigate(directions)
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