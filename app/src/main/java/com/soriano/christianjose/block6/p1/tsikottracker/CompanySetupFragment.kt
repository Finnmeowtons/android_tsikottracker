package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentCompanySetupBinding
import com.soriano.christianjose.block6.p1.tsikottracker.retrofitclient.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompanySetupFragment : Fragment() {

    private var _binding: FragmentCompanySetupBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyAPI: CompanyApi

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanySetupBinding.inflate(inflater, container, false)
        val view = binding.root
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        companyAPI = retrofit.create(CompanyApi::class.java)


        binding.btnCreate.setOnClickListener {
            val companyName = binding.etCompanyName.text.toString().trim().lowercase()

            if (companyName.isNotBlank()) {
                val company = Company(id = 0, name = companyName)

                companyAPI.createCompany(company).enqueue(object : Callback<Company>{
                    override fun onResponse(call: Call<Company>, response: Response<Company>) {
                        if (response.isSuccessful) {
                            val createdCompany = response.body()
                            Log.d("MyTag", createdCompany.toString())
                            view.findNavController().navigate(R.id.action_companySetupFragment_to_addServiceFragment)
                        } else {
                            // Handle API error
                        }
                    }

                    override fun onFailure(call: Call<Company>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            } else {
                // Display error (e.g., in a TextView)
            }

        }

        binding.btnJoin.setOnClickListener {
            view.findNavController().navigate(R.id.action_companySetupFragment_to_showCompaniesFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}