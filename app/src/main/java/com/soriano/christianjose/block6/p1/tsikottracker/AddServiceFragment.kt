package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.AddServiceAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.ServiceApi
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddServiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class AddServiceFragment : Fragment() {

    private var _binding: FragmentAddServiceBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var serviceApi: ServiceApi

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        val view = binding.root


        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        serviceApi = retrofit.create(ServiceApi::class.java)

        val adapter = AddServiceAdapter()
        binding.rvAddServices.adapter = adapter

        binding.btnAddService.setOnClickListener {
            adapter.addItem()

        }

        binding.btnSubmit.setOnClickListener {
            val services = adapter.getServices()
            for(service in services){
                Log.d("MyTag", service.toString())
                val gson = Gson()
                val serviceJson = gson.toJson(service)
                Log.d("MyTag", "Gson JSON Output: $serviceJson")

                serviceApi.createOffer(service).enqueue(object : Callback<Offer> {
                    override fun onResponse(call: Call<Offer>, response: Response<Offer>) {
                        if (response.isSuccessful) {
                            val createdOffer = response.body()
                            Log.d("MyTag", "Response: $createdOffer")
                            findNavController().navigate(R.id.action_side_nav_pop_up_to_dashboard)
                        } else {
                            Log.e("MyTag", "API Error: ${response.code()} - ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Offer>, t: Throwable) {
                        Log.e("MyTag", "Network Error $call", t)
                    }
                })
            }





        }



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}