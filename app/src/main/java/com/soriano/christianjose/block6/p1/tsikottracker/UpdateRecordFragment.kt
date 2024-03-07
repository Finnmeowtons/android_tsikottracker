package com.soriano.christianjose.block6.p1.tsikottracker

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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.AddServiceAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.UpdateRecordOfferAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentUpdateRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.OfferIds
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class UpdateRecordFragment : Fragment() {
    private var _binding: FragmentUpdateRecordBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var recordApi: RecordApi
    private lateinit var offerApi : OfferApi
    private lateinit var services : List<Offer>
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val offerIdsViewModel: OfferIds by activityViewModels()
    private val args : UpdateRecordFragmentArgs by navArgs()
    private var offerIds : List<Int> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateRecordBinding.inflate(inflater, container, false)
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
        offerApi = retrofit.create(OfferApi::class.java)
        val authUserManager = AuthUserManager(requireContext())
        val companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()

        val idArgs = args.updateId
        val customerNameArgs = args.updateCustomerName
        val plateNumberArgs = args.updatePlateNumber
        val employeeNameArgs = args.updateEmployeeName
        val employeePositionArgs = args.updateEmployePosition
        val notesArgs = args.updateNotes

        val adapter = UpdateRecordOfferAdapter(companyId, requireContext())
        binding.rvAddServices.adapter = adapter

        offerIdsViewModel.offerIdList.observe(viewLifecycleOwner) { offerIdsListViewModel ->
            offerIdsListViewModel.forEach { offerId ->
                offerApi.getIdOffer(offerId).enqueue(object : Callback<Offer> {
                    override fun onResponse(call: Call<Offer>, response: Response<Offer>) {
                        if (response.isSuccessful) {
                            val offer = response.body()

                            if (offer != null) {
                                val offerIdResponse = offer.id
                                val offerNameResponse = offer.name
                                val offerTypeResponse = offer.type
                                val offerPriceResponse = offer.price
                                val offerCompanyIdResponse = offer.company_id



                                adapter.addItemOffer(Offer(offerIdResponse, offerNameResponse, offerPriceResponse, offerTypeResponse, offerCompanyIdResponse))
                            }
                        } else {
                            Log.e("MyTag", "Error fetching offer: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Offer>, t: Throwable) {
                        Log.e("MyTag", "Error fetching offer: ${t.message}")
                    }
                })
            }
        }


        binding.apply {
            if(customerNameArgs != "null") {
                etCustomerName.setText(customerNameArgs)
            }
            if(employeeNameArgs != "null"){
                etEmployeeName.setText(employeeNameArgs)
            }
            if (employeePositionArgs != "null"){
                etPosition.setText(employeePositionArgs)
            }
            if (notesArgs != "null"){
                etNotes.setText(notesArgs)
            }
            etPlateNumber.setText(plateNumberArgs)
        }



        recordApi = retrofit.create(RecordApi::class.java)
        toolbar?.setOnMenuItemClickListener {menuItem ->
            var isValidated = true
            when (menuItem.itemId) {
                R.id.check ->{
                    if (adapter.validateFieldsUpdateRecord(binding, adapter)) {
                        services = adapter.getServices()
                    }
                    binding.apply {
                        if (etPlateNumber.text.isNullOrEmpty()){
                            plateNumberLayout.error = "Please enter plate number"
                            isValidated = false
                        }
                        if (etEmployeeName.text?.isNotEmpty() == true && etPosition.text.isNullOrEmpty()){
                            positionLayout.error = "Please enter position"
                            isValidated = false
                        }
                        if (!isValidated){
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Invalid Fields")
                                .setMessage("Please ensure all required fields are filled in correctly.")
                                .setPositiveButton("OK", null)
                                .show()
                            return@setOnMenuItemClickListener false
                        }
                        val customerName = etCustomerName.text.toString()
                        val plateNumber = etPlateNumber.text.toString()
                        val employeeName = etEmployeeName.text.toString()
                        val employeePosition = etPosition.text.toString()
                        val notes = etNotes.text.toString()

                        val record = Record(
                            id = idArgs,
                            customer_name= customerName,
                            customer_car_plate_number = plateNumber,
                            offers = services,
                            employee_name = employeeName,
                            employee_position = employeePosition,
                            notes = notes,
                            company_id = companyId
                        )

                        recordApi.updateRecord(idArgs, record).enqueue(object : Callback<Record>{
                            override fun onResponse(
                                call: Call<Record>,
                                response: Response<Record>
                            ) {
                                if (response.isSuccessful) {
                                    val records = response.body()
                                    Log.d("MyTag", "$records || ${response.body()}")
                                    findNavController().navigate(R.id.action_side_nav_pop_up_to_record)

                                }
                            }

                            override fun onFailure(call: Call<Record>, t: Throwable) {
                                Log.e("MyTag", "$call", t)
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