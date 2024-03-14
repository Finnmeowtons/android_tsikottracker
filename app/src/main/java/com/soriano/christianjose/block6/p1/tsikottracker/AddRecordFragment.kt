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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.AddRecordAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.AddServiceAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.CustomerApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.EmployeeApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.Customer
import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
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
    private lateinit var customerApi: CustomerApi
    private var customerNameList : MutableList<String> = mutableListOf()
    private var customerPlateList : MutableList<String> = mutableListOf()
    private lateinit var employeeApi: EmployeeApi
    private var employeeNameList : MutableList<String> = mutableListOf()
    private var employeePositionList : MutableList<String> = mutableListOf()
    private lateinit var offerApi: OfferApi
    private lateinit var offersList: List<Offer>



    private lateinit var services : List<Offer>
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
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.top_menu)
        Log.d("MyTag", "Set Menu")
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
        val companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()



        val adapter = AddServiceAdapter(companyId, requireContext())

        binding.btnAddOffer.setOnClickListener {
            adapter.addItem()
        }

        offerApi = retrofit.create(OfferApi::class.java)
        offerApi.getOffer(companyId).enqueue(object: Callback<List<Offer>>{
            override fun onResponse(call: Call<List<Offer>>, response: Response<List<Offer>>) {
                if (response.isSuccessful){
                    offersList = response.body() ?: emptyList()
                    adapter.offersList = offersList

                }
                binding.rvAddServices.adapter = adapter

            }

            override fun onFailure(call: Call<List<Offer>>, t: Throwable) {
                Log.d("MyTag", "Fail")
                binding.rvAddServices.adapter = adapter

            }
        })

        customerApi = retrofit.create(CustomerApi::class.java)
        customerApi.getCustomer(companyId).enqueue(object : Callback<List<Customer>>{
            override fun onResponse(call: Call<List<Customer>>, response: Response<List<Customer>>) {
                Log.d("MyTag", "Response")
                val customerData = mutableMapOf<String, String>()

                if (response.isSuccessful){
                    for (customer in response.body()!!){
                        customer.name?.let { name ->
                            customer.car_plate_number.let { plateNumber ->
                                customerData[name] = plateNumber
                            }
                        }
                    }
                    customerNameList = customerData.keys.toMutableList()
                    customerPlateList = customerData.values.toMutableList()

                    val customerNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, customerNameList)
                    binding.etCustomerName.setAdapter(customerNameAdapter)
                    binding.etCustomerName.setOnItemClickListener { adapterView, _, i, _ ->
                        val selectedName = adapterView.adapter.getItem(i) as String
                        val plateNumber = customerData[selectedName]
                        if (plateNumber != null) {
                            binding.etPlateNumber.setText(plateNumber, false)
                        }
                    }
                    val customerPlateNumberAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, customerPlateList)
                    binding.etPlateNumber.threshold = 1
                    binding.etPlateNumber.setAdapter(customerPlateNumberAdapter)
                }
            }

            override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                Log.d("MyTag", "Fail")
            }
        })

        employeeApi = retrofit.create(EmployeeApi::class.java)
        employeeApi.getEmployee(companyId).enqueue(object : Callback<List<Employee>>{
            override fun onResponse(
                call: Call<List<Employee>>,
                response: Response<List<Employee>>
            ) {
                Log.d("MyTag", "Response")
                val employeeData = mutableMapOf<String, String>()

                if (response.isSuccessful){
                    for (employee in response.body()!!){
                        employee.name.let { name ->
                            employee.position.let { position ->
                                employeeData[name] = position
                            }
                        }
                    }
                    employeeNameList = employeeData.keys.toMutableList()
                    employeePositionList = employeeData.values.toMutableList()

                    val employeeNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, employeeNameList)
                    binding.etEmployeeName.setAdapter(employeeNameAdapter)
                    binding.etEmployeeName.setOnItemClickListener { adapterView, _, i, _ ->
                        val selectedName = adapterView.adapter.getItem(i) as String
                        val position = employeeData[selectedName]
                        if (position != null) {
                            binding.etPosition.setText(position, false)
                        }
                    }
                    val employeePositionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, employeePositionList)
                    binding.etPosition.setAdapter(employeePositionAdapter)
                }
            }

            override fun onFailure(call: Call<List<Employee>>, t: Throwable) {
                Log.d("MyTag", "Fail")
            }
        })



        recordApi = retrofit.create(RecordApi::class.java)
        toolbar?.setOnMenuItemClickListener {menuItem ->
            var isValidated = true
            when (menuItem.itemId) {
                R.id.check ->{
                    if (adapter.validateFieldsRecord(binding, adapter)) {
                        services = adapter.getServices()
                        Log.d("MyTag", "serser$services")

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
                            id = 0,
                            customer_name= customerName,
                            customer_car_plate_number = plateNumber,
                            offers = services,
                            employee_name = employeeName,
                            employee_position = employeePosition,
                            notes = notes,
                            company_id = companyId
                            )
                        Log.d("MyTag", "$record")
                        Log.d("MyTag", "before creating")

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
                                } else {
                                    Log.d("MyTag", "$call || $response")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyTag", "Set Menu")
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
        toolbar?.inflateMenu(R.menu.record_history_menu)
    }

}