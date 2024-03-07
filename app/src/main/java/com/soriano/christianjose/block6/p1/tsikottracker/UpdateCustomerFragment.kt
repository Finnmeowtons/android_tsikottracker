package com.soriano.christianjose.block6.p1.tsikottracker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.api.CustomerApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Customer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentUpdateCustomerBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdateCustomerFragment : Fragment() {
    private var _binding: FragmentUpdateCustomerBinding? = null
    private lateinit var retrofit: Retrofit
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val args: UpdateCustomerFragmentArgs by navArgs()
    private lateinit var customerApi: CustomerApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateCustomerBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Edit Customer")
        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.arrow_back)
        toolbar?.setNavigationOnClickListener {
            if (isAdded) {
                findNavController().popBackStack()
            }
        }
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )
        val authUserManager = AuthUserManager(requireContext())
        val companyId = authUserManager.getStoredCompanyId()
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        customerApi = retrofit.create(CustomerApi::class.java)

        val idArgs = args.updateIdValue
        Log.d("MyTag", idArgs.toString())
        val nameArgs = args.updateNameValue
        val plateNumberArgs = args.updatePlateNumberValue

        if (nameArgs != "null"){
            binding.etName.setText(nameArgs)
        }
        binding.etPlateNumber.setText(plateNumberArgs)

        binding.btnConfirmEdit.setOnClickListener {
            var allValid = true

            val etName = binding.etName.text
            val etPlateNumber = binding.etPlateNumber.text

            Log.d("MyTag", "$etPlateNumber || $plateNumberArgs || ${etPlateNumber.toString() == plateNumberArgs} || ${etPlateNumber.toString() == plateNumberArgs && etName.toString() == nameArgs || etName.isNullOrBlank()}")

            if(etPlateNumber.toString() == plateNumberArgs && etName.toString() == nameArgs || etName.isNullOrBlank()){
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Changes Not Submitted")
                    .setMessage("It appears you haven't made any edits. Please review your changes and try again.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            if (etPlateNumber.isNullOrBlank()){
                binding.plateNumberLayout.error = "Please input plate number"
                allValid = false
            }
            if (!allValid){
                return@setOnClickListener
            }

            val customer = Customer(idArgs, etPlateNumber.toString(), etName.toString(), companyId)
            customerApi.deleteCustomer(idArgs, customer).enqueue(object : Callback<Customer>{
                override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                    if (response.isSuccessful) {
                        val createdCustomer = response.body()
                        Log.d("MyTag", "Response: $createdCustomer")

                        if (isAdded) {
                            findNavController().popBackStack()
                            activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.VISIBLE
                            activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        }

                    } else {
                        Log.e(
                            "MyTag",
                            "API Error: ${response.code()} - ${response.message()} - $call"
                        )
                    }
                }

                override fun onFailure(call: Call<Customer>, t: Throwable) {
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
        val toolbar =
            activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.menu_fill0_wght400_grad0_opsz24)

        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val drawerLayout =
            activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
        toolbar?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }
}