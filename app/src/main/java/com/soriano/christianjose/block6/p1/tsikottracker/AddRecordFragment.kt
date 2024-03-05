package com.soriano.christianjose.block6.p1.tsikottracker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class AddRecordFragment : Fragment() {
    private var _binding: FragmentAddRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var recordApi: RecordApi
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRecordBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Add Data")
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.inflateMenu(R.menu.top_menu)
        toolbar?.setNavigationIcon(R.drawable.arrow_back)
        toolbar?.setNavigationOnClickListener {
            if (isAdded) {
                findNavController().popBackStack()
            }
        }
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()

        recordApi = retrofit.create(RecordApi::class.java)
        toolbar?.setOnMenuItemClickListener {menuItem ->
            var isValidated = true
            when (menuItem.itemId) {
                R.id.check ->{
                    binding.apply {
                        if (etPlateNumber.text.isNullOrEmpty()){
                            plateNumberLayout.error = "Please enter plate number"
                            isValidated = false
                        }
                        if (etOffer.text.isNullOrEmpty()){
                            offerLayout.error = "Please enter an offer"
                            isValidated = false
                        }
                        if (etServiceType.text.isNullOrEmpty()){
                            typeLayout.error = "Please select a type of offer"
                            isValidated = false
                        }
                        if (etServicePriceInput.text.isNullOrEmpty()){
                            priceLayout.error = "Enter price"
                            isValidated = false
                        } else if (etServicePriceInput.text.toString().toInt() <= 0){
                            priceLayout.error = "Must be more than 1"
                            isValidated = false
                        }
                        if (etEmployeeName.text?.isNotEmpty() == true && etPosition.text.isNullOrEmpty()){
                            positionLayout.error = "Please enter position"
                            isValidated = false
                        }
                        if (!isValidated){
                            AlertDialog.Builder(requireContext())
                                .setTitle("Invalid Fields")
                                .setMessage("Please ensure all required fields are filled in correctly.")
                                .setPositiveButton("OK", null)
                                .show()
                            return@setOnMenuItemClickListener false
                        }
                        val customerName = etCustomerName.text.toString()
                        val plateNumber = etPlateNumber.text.toString()
                        val offer = etOffer.text.toString()
                        val offerType = etServiceType.text.toString()
                        val offerPrice = etServicePriceInput.text.toString().toInt()
                        val employeeName = etEmployeeName.text.toString()
                        val employeePosition = etPosition.text.toString()
                        val notes = etNotes.text.toString()
                        val record = Record(
                            id = 0,
                            customer_name= customerName,
                            customer_car_plate_number = plateNumber,
                            offer = offer,
                            price = offerPrice,
                            type = offerType,
                            employee_name = employeeName,
                            employee_position = employeePosition,
                            notes = notes,
                            company_id = companyId
                            )
                        Log.d("MyTag", "$record")
                        recordApi.createRecord(record).enqueue(object : Callback<Record>{
                            override fun onResponse(
                                call: Call<Record>,
                                response: Response<Record>
                            ) {
                                if (response.isSuccessful) {
                                    val createdRecord = response.body()
                                    Log.d("MyTag", createdRecord.toString())

                                    if(isAdded){
                                        findNavController().popBackStack()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<Record>, t: Throwable) {
                                Log.d("MyTag", call.toString(), t)
                            }
                        })
                    }
                    true
                }
                else -> false
            }
        }




        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isAdded) {
                findNavController().popBackStack()
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
        activity?.findViewById<MaterialToolbar>(R.id.topAppBar)?.menu?.clear()

        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
        toolbar?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

}