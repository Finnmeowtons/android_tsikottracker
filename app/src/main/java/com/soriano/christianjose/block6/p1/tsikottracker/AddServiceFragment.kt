package com.soriano.christianjose.block6.p1.tsikottracker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.AddServiceAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddServiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AddServiceFragment : Fragment() {

    private var _binding: FragmentAddServiceBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var offerApi: OfferApi
    private val args : AddServiceFragmentArgs by navArgs()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        val view = binding.root
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        offerApi = retrofit.create(OfferApi::class.java)

        val authUserManager = AuthUserManager(requireContext())
        val storedCompanyId = authUserManager.getStoredCompanyId()

        val adapter = AddServiceAdapter(storedCompanyId)
        binding.rvAddServices.adapter = adapter

        binding.btnAddService.setOnClickListener {
            adapter.addItem()
        }

        binding.btnSubmit.setOnClickListener {
            if (adapter.validateFields(binding, adapter)) {
                val services = adapter.getServices()
                for(service in services){
                    Log.d("MyTag", service.toString())

                    offerApi.createOffer(service).enqueue(object : Callback<Offer> {
                        override fun onResponse(call: Call<Offer>, response: Response<Offer>) {
                            if (response.isSuccessful) {
                                val createdOffer = response.body()
                                Log.d("MyTag", "Response: $createdOffer")

                                if (isAdded) { // Check if the fragment is attached
                                    if (args.myArgsAddService){
                                        findNavController().popBackStack()
                                    } else {
                                        findNavController().navigate(R.id.action_side_nav_pop_up_to_offer)
                                    }

                                    activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.VISIBLE
                                    activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                                }

                            } else {
                                Log.e(
                                    "MyTag",
                                    "API Error: ${response.code()} - ${response.message()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<Offer>, t: Throwable) {
                            Log.e("MyTag", "Network Error $call", t)
                        }
                    })
                }
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Invalid Fields")
                    .setMessage("Please correct the errors in the form.")
                    .setPositiveButton("OK", null)
                    .show()
            }

        }



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}