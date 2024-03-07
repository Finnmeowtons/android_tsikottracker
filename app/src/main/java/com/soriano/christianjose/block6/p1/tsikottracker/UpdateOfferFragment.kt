package com.soriano.christianjose.block6.p1.tsikottracker

import android.app.AlertDialog
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
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentUpdateOfferBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UpdateOfferFragment : Fragment() {
    private var _binding: FragmentUpdateOfferBinding? = null
    private lateinit var retrofit: Retrofit
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val args : UpdateOfferFragmentArgs by navArgs()
    private lateinit var offerApi: OfferApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateOfferBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Edit Offer")

        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.arrow_back)
        toolbar?.setNavigationOnClickListener {
            if (isAdded) {
                findNavController().popBackStack()
            }
        }
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val authUserManager = AuthUserManager(requireContext())
        val companyId = authUserManager.getStoredCompanyId()

        val nameArgs = args.updateNameValue
        val priceArgs = args.updatePriceValue
        val typeArgs = args.updateTypeValue
        val idArgs = args.updateIdValue
        binding.etServiceType.setText(typeArgs.replaceFirstChar { it.uppercase() }, false)
        binding.etServiceInput.setText(nameArgs)
        binding.etServicePriceInput.setText(priceArgs)

        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        offerApi = retrofit.create(OfferApi::class.java)

        binding.btnConfirmEdit.setOnClickListener {
            var allValid = true
            val etName = binding.etServiceInput.text
            val etPrice = binding.etServicePriceInput.text
            val etType = binding.etServiceType.text

            if (etName.toString() == nameArgs && etType.toString().lowercase() == typeArgs && etPrice.toString() == priceArgs){
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Changes Not Submitted")
                    .setMessage("It appears you haven't made any edits. Please review your changes and try again.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            if (etName.isNullOrBlank()) {
                binding.offerLayout.error = "Please input an offer"
                allValid = false
            } else {
                binding.offerLayout.error = null
            }

            if (etType.isNullOrBlank()){
                binding.typeLayout.error = "Invalid"
                allValid = false
            } else {
                binding.typeLayout.error = null
            }

            if (etPrice.isNullOrBlank()){
                binding.priceLayout.error = "Invalid"
                allValid = false
            } else {
                binding.priceLayout.error = null
            }

            if (!allValid){
                return@setOnClickListener
            }

            val service = Offer(0, etName.toString(), etPrice.toString().toInt(), etType.toString(), companyId)
            offerApi.deleteOffer(idArgs, service).enqueue(object : Callback<Offer> {
                override fun onResponse(call: Call<Offer>, response: Response<Offer>) {
                    if (response.isSuccessful) {
                        val createdOffer = response.body()
                        Log.d("MyTag", "Response: $createdOffer")

                        if (isAdded) {
                            findNavController().popBackStack()
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




        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val toolbar = activity?.findViewById<MaterialToolbar>(com.soriano.christianjose.block6.p1.tsikottracker.R.id.topAppBar)
        toolbar?.setNavigationIcon(com.soriano.christianjose.block6.p1.tsikottracker.R.drawable.menu_fill0_wght400_grad0_opsz24)

        activity?.findViewById<DrawerLayout>(com.soriano.christianjose.block6.p1.tsikottracker.R.id.drawerLayout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val drawerLayout = activity?.findViewById<DrawerLayout>(com.soriano.christianjose.block6.p1.tsikottracker.R.id.drawerLayout)
        toolbar?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }
}