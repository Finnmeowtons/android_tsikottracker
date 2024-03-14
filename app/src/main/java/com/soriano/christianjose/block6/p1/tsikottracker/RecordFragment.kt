package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.util.Pair
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.RecordAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.GetRecord
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.data.StartEndDate
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.OfferIds
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://146.190.111.209/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private lateinit var companyApi: CompanyApi
    private lateinit var recordApi: RecordApi
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val offerIdsViewModel: OfferIds by activityViewModels()
    private lateinit var companies: List<Company>
    private lateinit var companyNames: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var getRecord: List<GetRecord>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("MyTag", "Record")
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val view = binding.root


        sharedViewModel.updateAppBarTitle("Record")
        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        if (isAdded) {
            toolbar?.menu?.clear()
            toolbar?.inflateMenu(R.menu.record_history_menu)

        }
        companyNames = mutableListOf<String>()
        companies = emptyList()
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            companyNames
        )
        binding.etCompanySelect.setAdapter(adapter)

        recordApi = retrofit.create(RecordApi::class.java)
        companyApi = retrofit.create(CompanyApi::class.java)
        val today = convertTimestampToDate(MaterialDatePicker.todayInUtcMilliseconds())
        startDate = today
        endDate = today
        var startEndDate = StartEndDate(startDate, endDate)


        Log.d("MyTag", "adapter : ${startEndDate}")

        val recyclerViewAdapter = RecordAdapter(
            recordApi,
            parentFragmentManager,
            companyId,
            findNavController(),
            offerIdsViewModel,
            requireContext(),
            startEndDate = startEndDate
        )
        binding.recyclerView.adapter = recyclerViewAdapter

        toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.history -> {

                    try {
                        binding.linearProgressIndicator.visibility = View.VISIBLE
                        val todayPicker = MaterialDatePicker.todayInUtcMilliseconds()
                        val calendar =
                            Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"))

                        calendar.timeInMillis = todayPicker
                        calendar[Calendar.MONTH] = Calendar.JANUARY
                        val janThisYear = calendar.timeInMillis

                        calendar.timeInMillis = todayPicker
                        calendar[Calendar.MONTH] = Calendar.DECEMBER
                        val decThisYear = calendar.timeInMillis

                        // Build constraints.
                        val constraintsBuilder =
                            CalendarConstraints.Builder()
                                .setStart(janThisYear)
                                .setEnd(decThisYear)
                                .setValidator(
                                    DateValidatorPointBackward.now()
                                )

                        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select date")
                            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                            .setSelection(
                                Pair(
                                    MaterialDatePicker.todayInUtcMilliseconds(),
                                    MaterialDatePicker.todayInUtcMilliseconds()
                                )
                            )
                            .setCalendarConstraints(constraintsBuilder.build())
                            .build()
                        datePicker.show(parentFragmentManager, "tag")

                        datePicker.addOnPositiveButtonClickListener {
                            startDate =
                                convertTimestampToDate(datePicker.selection?.first!!)
                            endDate = convertTimestampToDate(datePicker.selection?.second!!)
                            startEndDate = StartEndDate(startDate, endDate)
                            Log.d("MyTag", "2 ${startEndDate}")
                            fetchRecords(companyId, recyclerViewAdapter, startEndDate)
                            Log.d("MyTag", startDate)
                            Log.d("MyTag", "${ datePicker.selection?.first!! }")
                        }
                    } finally {
                        binding.linearProgressIndicator.visibility = View.GONE
                    }


                    true
                }
                R.id.search ->{
                    val searchView = menuItem.actionView as SearchView
                    searchView.isIconified = false // Expand the search view
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean = true

                        override fun onQueryTextChange(query: String?): Boolean {
                            if (query != null) {
                                val searchQuery = query.lowercase()
                                val filteredOffers = getRecord.filter { record ->
                                    record.offers.any {offer ->
                                        record.customer_car_plate_number.lowercase().contains(searchQuery) ||
                                                record.customer_name?.lowercase()?.contains(searchQuery) == true ||
                                                record.employee_name?.lowercase()?.contains(searchQuery) == true ||
                                                record.employee_position?.lowercase()?.contains(searchQuery) == true ||
                                                offer.offer.lowercase().contains(searchQuery) ||
                                                offer.offer_price.toString().contains(searchQuery)

                                    }

                                }
                                recyclerViewAdapter.records = filteredOffers
                                if (filteredOffers.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            } else {
                                recyclerViewAdapter.records = getRecord
                                if (getRecord.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            }
                            return true
                        }
                    })
                    true
                }
                else -> false
            }
        }


        lifecycleScope.launch {
            binding.linearProgressIndicator.visibility = View.VISIBLE
            try {
                fetchCompanies(storedUserId, companyId)
                Log.d("MyTag", "1 ${startEndDate}")
                fetchRecords(companyId, recyclerViewAdapter, startEndDate)
            } finally {
                binding.linearProgressIndicator.visibility = View.GONE
            }
        }




        binding.etCompanySelect.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                Log.d("MyTag", "Before getStoredCompanyId $companyId")
                val selectedCompanyName = parent.getItemAtPosition(position) as String
                val selectedCompany = companies.find { it.name == selectedCompanyName }
                Log.d("MyTag", "selectedCompany $selectedCompany")
                if (selectedCompany != null) {
                    authUserManager.storeCompanyId(selectedCompany.id)
                    companyId = authUserManager.getStoredCompanyId()
                    Log.d("MyTag", "3 ${startEndDate}")
                    fetchRecords(companyId, recyclerViewAdapter, startEndDate)
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }



        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                // Scrolling Down
                binding.floatingActionButton.hide()
            } else if (scrollY < oldScrollY) {
                // Scrolling Up
                binding.floatingActionButton.show()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
        }

        return view
    }

    fun fetchCompanies(storedUserId: Int, companyId: Int) {
        companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                Log.d("MyTag", "All Companies: ${response.body()}")
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList() // Store the companies
                    companyNames.clear()
                    companies.forEach { company -> companyNames.add(company.name) }
                    Log.d("MyTag", "All Companies: $companies")


                    val companyToSelect = companies.find { it.id == companyId }
                    if (companyToSelect != null) {



                        if (isAdded) {
                            val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
                            val searchView = toolbar?.menu?.findItem(R.id.search)?.actionView as SearchView
                            searchView.setQuery("", false) // Clear the text
                            searchView.isIconified = true
                            toolbar.menu?.findItem(R.id.search)?.collapseActionView()
                            binding.etCompanySelect.setText(
                                companyToSelect.name,
                                false
                            )
                            adapter.notifyDataSetChanged()
                        }

                    }

                }
            }

            override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                Log.e("MyTag", "$call", t)
            }
        })
    }

    fun fetchRecords(
        companyId: Int,
        recyclerViewAdapter: RecordAdapter,
        startEndDate: StartEndDate
    ) {
        recordApi.getRecords(companyId, startEndDate).enqueue(object : Callback<List<GetRecord>> {
            override fun onResponse(
                call: Call<List<GetRecord>>,
                response: Response<List<GetRecord>>
            ) {
                if (response.isSuccessful) {
                    if (isAdded) {
                        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
                        val searchView =
                            toolbar?.menu?.findItem(R.id.search)?.actionView as SearchView
                        searchView.setQuery("", false) // Clear the text
                        searchView.isIconified = true
                        toolbar.menu?.findItem(R.id.search)?.collapseActionView()

                        val records = response.body()
                        Log.d("MyTag", "$records || ${response.body()}")
                        if (records != null) {
                            recyclerViewAdapter.records = records
                            getRecord = records
                            if (getRecord.isEmpty()) {
                                binding.llNoResult.visibility = View.VISIBLE
                            } else {
                                binding.llNoResult.visibility = View.GONE
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<GetRecord>>, t: Throwable) {
                Log.e("MyTag", "$call", t)
            }
        })
    }

    fun String.toSentenceCase(): String {
        return lowercase() // Start by lowercasing the entire string
            .split("\\s+".toRegex()) // Split into words based on spaces
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
    }

    fun convertTimestampToDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Manila") // Or the appropriate timezone
        return dateFormat.format(timestamp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_recordFragment2_to_addRecordFragment)
        }
    }
}