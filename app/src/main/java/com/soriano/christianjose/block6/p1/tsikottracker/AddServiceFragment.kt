package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.AddServiceAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddServiceBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AddServiceFragment : Fragment() {

    private var _binding: FragmentAddServiceBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var offerApi: OfferApi
    private val args: AddServiceFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Add Offers")
        if (args.myArgsAddService) {
            binding.textView.visibility = View.INVISIBLE
            val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
            toolbar?.setNavigationIcon(R.drawable.arrow_back)
            toolbar?.menu?.clear()
            toolbar?.setNavigationOnClickListener {
                if (isAdded) {
                    findNavController().popBackStack()
                }
            }
        } else {

            activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        }
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        offerApi = retrofit.create(OfferApi::class.java)

        val authUserManager = AuthUserManager(requireContext())
        val storedCompanyId = authUserManager.getStoredCompanyId()

        val adapter = AddServiceAdapter(storedCompanyId, requireContext())
        binding.rvAddServices.adapter = adapter

        binding.btnAddService.setOnClickListener {
            adapter.addItem()
        }

        binding.btnSubmit.setOnClickListener {
            if (adapter.validateFieldsService(binding, adapter)) {
                val services = adapter.getServices()
                for (service in services) {
                    Log.d("MyTag", service.toString())

                    offerApi.createOffer(service).enqueue(object : Callback<Offer> {
                        override fun onResponse(call: Call<Offer>, response: Response<Offer>) {
                            if (response.isSuccessful) {
                                val createdOffer = response.body()
                                Log.d("MyTag", "Response: $createdOffer")

                                if (isAdded) { // Check if the fragment is attached
                                    if (args.myArgsAddService) {
                                        findNavController().navigate(R.id.action_side_nav_pop_up_to_offer)
                                    } else {
                                        findNavController().navigate(R.id.action_side_nav_pop_up_to_offer)
                                    }

                                    activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility =
                                        View.VISIBLE
                                    activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
                                        ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
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
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Invalid Fields")
                    .setMessage("Please correct the errors in the form.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (args.myArgsAddService) {
                if (isAdded) {
                    findNavController().popBackStack(R.id.action_to_companySetupFragment, true)
                }
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Skip Offers?")
                    .setMessage("You can add offers later by going to the offers tab.")
                    .setPositiveButton("Continue") { _, _ ->
                        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility =
                            View.VISIBLE
                        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
                            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        findNavController().navigate(R.id.action_side_nav_pop_up_to_record)

                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.menu_fill0_wght400_grad0_opsz24)

        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
        toolbar?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }
}