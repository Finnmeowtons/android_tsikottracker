package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.CountAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Analytics
import com.soriano.christianjose.block6.p1.tsikottracker.data.AnalyticsOfferCount
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.GetRecord
import com.soriano.christianjose.block6.p1.tsikottracker.data.OfferCount
import com.soriano.christianjose.block6.p1.tsikottracker.data.StartEndDate
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAnalyticBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AnalyticFragment : Fragment() {
    private lateinit var analysisList: List<AnalyticsOfferCount>
    private var _binding: FragmentAnalyticBinding? = null

    private lateinit var companies: List<Company>
    private lateinit var companyNames: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var retrofit: Retrofit
    private lateinit var companyApi: CompanyApi
    private lateinit var recordApi: RecordApi
    private lateinit var startDate: String
    private lateinit var endDate: String
    private var filterMode = "product"
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var totalRevenue = 0
    private var totalServices = 0
    private var totalProducts = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticBinding.inflate(inflater, container, false)
        val view = binding.root
        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        companyNames = mutableListOf<String>()
        companies = emptyList()
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sharedViewModel.updateAppBarTitle("Analytics")
        val today = LocalDate.now()
        startDate = today.minusDays(14).toString()
        endDate = today.toString()
        var startEndDate = StartEndDate(startDate, endDate)
        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        if (isAdded) {
            toolbar?.menu?.clear()
            toolbar?.inflateMenu(R.menu.date_range_menu)

        }
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            companyNames
        )
        binding.etCompanySelect.setAdapter(adapter)

        binding.toggleButton.check(R.id.button2)
        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            Log.d("MyTag", "checkedButtonId: ${toggleButton.checkedButtonIds}")

            val checkedIds = toggleButton.checkedButtonIds
            if (checkedIds.size == 2){
                filterMode = "both"
            } else if (checkedIds[0] == 2131296970){
                filterMode = "services"
            } else if (checkedIds[0] == 2131296969){
                filterMode = "products"
            }


            Log.d("MyTag", "filterMode is now: $filterMode")

            val filteredCounts = when (filterMode) {
                "products" -> analysisList.filter { it.type == "product" }
                "services" -> {
                    Log.d("MyTag", "Filtering for services")
                    analysisList.filter { it.type == "service" }
                }

                "none" -> listOf() // Show an empty list
                else -> analysisList
            }
            Log.d("MyTag", "filteredCounts: $filteredCounts")
            val countAdapter = CountAdapter()
            binding.recyclerView2.adapter = countAdapter
            countAdapter.counts = filteredCounts.sortedByDescending { it.count }

        }

        companyApi = retrofit.create(CompanyApi::class.java)
        companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>> {
            override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                if (response.isSuccessful) {
                    companies = response.body() ?: emptyList() // Store the companies
                    companyNames.clear()
                    companies.forEach { company -> companyNames.add(company.name) }
                    adapter.notifyDataSetChanged()

                    val companyToSelect = companies.find { it.id == companyId }
                    if (companyToSelect != null) {
                        binding.etCompanySelect.setText(companyToSelect.name, false)
                    }

                }
            }

            override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                Log.e("MyTag", "$call", t)
            }
        })

        recordApi = retrofit.create(RecordApi::class.java)
        fetchRecords(companyId, recordApi, startEndDate)

        toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dateRange -> {
                    totalRevenue = 0
                    totalProducts = 0
                    totalServices = 0
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
                        fetchRecords(companyId, recordApi, startEndDate)
                        Log.d("MyTag", startDate)
                        Log.d("MyTag", "${datePicker.selection?.first!!}")
                    }
                    true
                }

                else -> false
            }
        }

        binding.etCompanySelect.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                totalRevenue = 0
                totalProducts = 0
                totalServices = 0
                val selectedCompanyName = parent.getItemAtPosition(position) as String
                val selectedCompany = companies.find { it.name == selectedCompanyName }


                if (selectedCompany != null) {
                    authUserManager.storeCompanyId(selectedCompany.id)
                    companyId = authUserManager.getStoredCompanyId()
                    fetchRecords(companyId, recordApi, startEndDate)

                }
            }





        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
        }

        return view
    }

    fun fetchRecords(companyId: Int, recordApi: RecordApi, startEndDate: StartEndDate) {
        recordApi.getRecords(companyId, startEndDate).enqueue(object : Callback<List<GetRecord>> {
            override fun onResponse(
                call: Call<List<GetRecord>>,
                response: Response<List<GetRecord>>
            ) {
                if (response.isSuccessful) {
                    if (isAdded) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)

                            toolbar?.menu?.findItem(R.id.search)?.let { searchMenuItem ->
                                val searchView = searchMenuItem.actionView as SearchView
                                searchView.setQuery("", false) // Clear the text
                                searchView.isIconified = true
                                toolbar.menu?.findItem(R.id.search)?.collapseActionView()
                            }
                        }, 1000)


                        val records = response.body()
                        Log.d("MyTag", "$records || ${response.body()}")

                        if (records != null) {
                            val offerCounts = mutableMapOf<OfferCount, Int>()
                            for (record in records) {
                                for (offer in record.offers) {
                                    val offerKey =
                                        OfferCount(offer.offer, offer.type, record.company_id)
                                    offerCounts[offerKey] =
                                        offerCounts.getOrDefault(offerKey, 0) + 1

                                    totalRevenue += offer.offer_price
                                    if (offer.type.lowercase() == "service") {
                                        totalServices += 1
                                    } else if (offer.type.lowercase() == "product") {
                                        totalProducts += 1
                                    }
                                }
                            }

                            analysisList = offerCounts.map { (offerKey, count) ->
                                AnalyticsOfferCount(offerKey.name, count, offerKey.type)
                            }
                            Log.d("MyTag", "analysisList contents:")
                            analysisList.forEach { Log.d("MyTag", "- ${it.name} (${it.type})") }

                            val recyclerViewAdapter = CountAdapter()
                            recyclerViewAdapter.counts = analysisList.sortedByDescending { it.count }
                            binding.recyclerView2.adapter = recyclerViewAdapter


                            val startDate = LocalDate.parse(startEndDate.start_date)
                            val endDate = LocalDate.parse(startEndDate.end_date)
                            val daysDuration = ChronoUnit.DAYS.between(startDate, endDate)
                            val aggregatedData = if (daysDuration <= 14) {
                                dailyAnalytics(records, startEndDate)
                            } else if (daysDuration <= 60) {
                                weeklyAnalytics(records, startEndDate)
                            } else {
                                monthlyAnalytics(records, startEndDate)
                            }
                            Log.d("MyTag", aggregatedData.toString())

                            if (totalRevenue != 0) {
                                binding.clAnalytics.visibility = View.VISIBLE
                                binding.llNoResult.visibility = View.GONE
                                Log.d("MyTag", "Not Empty")
                                // Determine the format dynamically
                                val isDaily = daysDuration <= 14
                                val isWeekly = daysDuration <= 60 && !isDaily

                                val dateFormatter = if (isDaily) {
                                    DateTimeFormatter.ofPattern("MMM d")
                                } else if (isWeekly) {
                                    DateTimeFormatter.ofPattern("'Week' w")
                                } else {
                                    DateTimeFormatter.ofPattern("MMM ''yy")
                                }

                                val labels = aggregatedData.map { it.date.format(dateFormatter) }


                                Log.d("MyTag", labels.toString())

                                //Offers Analytics
                                val aaChartOffer = binding.aaChartViewOffers
                                val aaChartModelOffers: AAChartModel = AAChartModel()
                                    .chartType(AAChartType.Pie)
                                    .dataLabelsEnabled(false)
                                    .tooltipEnabled(false)
                                    .backgroundColor("#00FFFF00")

                                    .colorsTheme(
                                        arrayOf(
                                            "#5BDCC6",
                                            "#026E78",
                                            "#038C8C",
                                            "#31BAAE"
                                        )
                                    )
                                    .series(
                                        arrayOf(
                                            AASeriesElement()
                                                .data(
                                                    arrayOf(
                                                        arrayOf("Products", totalProducts),
                                                        arrayOf("Services", totalServices)
                                                    )
                                                )
                                        )
                                    )
                                aaChartOffer.aa_drawChartWithChartModel(aaChartModelOffers)
                                Log.d("MyTag", "RECORD RECORD ${records}")

                                //Revenue Analytics
                                val aaChartRevenue = binding.aaChartViewRevenue
                                val aaChartModelRevenue: AAChartModel = AAChartModel()
                                    .categories(labels.toTypedArray())
                                    .chartType(AAChartType.Column)
                                    .dataLabelsEnabled(true)
                                    .yAxisTitle("Revenue (₱)")
                                    .tooltipEnabled(false)
                                    .backgroundColor("#00FFFF00")

                                    .colorsTheme(
                                        arrayOf(
                                            "#5BDCC6",
                                            "#026E78",
                                            "#038C8C",
                                            "#31BAAE"
                                        )
                                    )
                                    .series(
                                        arrayOf(
                                            AASeriesElement()
                                                .name("Revenue")
                                                .data(
                                                    aggregatedData.map { it.totalRevenue }
                                                        .toTypedArray()
                                                )

                                        )
                                    )
                                aaChartRevenue.aa_drawChartWithChartModel(aaChartModelRevenue)

                                //Product Analytics
                                val aaChartProduct = binding.aaChartViewProduct
                                val aaChartProductModel: AAChartModel = AAChartModel()
                                    .categories(labels.toTypedArray())
                                    .chartType(AAChartType.Bar)
                                    .dataLabelsEnabled(false)
                                    .yAxisTitle("Performed/Bought")
                                    .tooltipEnabled(false)
                                    .backgroundColor("#00FFFF00")
                                    .colorsTheme(
                                        arrayOf(
                                            "#5BDCC6",
                                            "#026E78",
                                            "#038C8C",
                                            "#31BAAE"
                                        )
                                    )
                                    .series(
                                        arrayOf(
                                            AASeriesElement()
                                                .name("Products")
                                                .data(
                                                    aggregatedData.map { it.totalProducts }
                                                        .toTypedArray()
                                                ),
                                            AASeriesElement()
                                                .name("Services")
                                                .data(
                                                    aggregatedData.map { it.totalServices }
                                                        .toTypedArray()
                                                )
                                        )
                                    )
                                aaChartProduct.aa_drawChartWithChartModel(aaChartProductModel)
                                binding.tvTotalRevenue.text = "$totalRevenue₱"
                                binding.tvTotalServices.text = "$totalServices"
                                binding.tvTotalProducts.text = "$totalProducts"
                            } else {
                                Log.d("MyTag", "Else no data")
                                binding.clAnalytics.visibility = View.GONE
                                binding.llNoResult.visibility = View.VISIBLE
                            }

                        } else {
                            binding.tvTotalRevenue.text = "0₱"
                            binding.tvTotalServices.text = "0"
                            binding.tvTotalProducts.text = "0"
                        }

                    }
                }
            }

            override fun onFailure(call: Call<List<GetRecord>>, t: Throwable) {
                Log.e("MyTag", "$call", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Manila") // Or the appropriate timezone
        return dateFormat.format(timestamp)
    }


    fun dailyAnalytics(records: List<GetRecord>, startEndDate: StartEndDate): List<Analytics> {
        val dailyData = mutableMapOf<LocalDate, Analytics>()

        val startDate = LocalDate.parse(startEndDate.start_date) // From your previous code
        val endDate = LocalDate.parse(startEndDate.end_date)
        Log.d("MyTag", "startEndDate : $startEndDate")

        var dateBeingProcessed = startDate
        while (dateBeingProcessed <= endDate) {
            dailyData[dateBeingProcessed] = Analytics(dateBeingProcessed, 0, 0, 0)
            dateBeingProcessed = dateBeingProcessed.plusDays(1)
        }
        Log.d("MyTag", "dailyData : ${dailyData.values}")


        for (record in records) {

            val timestamp = record.time.toString()
            val parts = timestamp.split(" ")

            val monthStr = parts[1]
            val dayStr = parts[2]
            var yearStr = parts[5]
            val timeStr = parts[3]

            val timeParts = timeStr.split(":")
            val hour = timeParts[0].toInt()

            var monthNum = when (monthStr) {
                "Jan" -> "01"
                "Feb" -> "02"
                "Mar" -> "03"
                "Apr" -> "04"
                "May" -> "05"
                "Jun" -> "06"
                "Jul" -> "07"
                "Aug" -> "08"
                "Sep" -> "09"
                "Oct" -> "10"
                "Nov" -> "11"
                "Dec" -> "12"
                else -> "01" // Handle invalid month names
            }

            // Adjust the hour
            var adjustedHour = hour - 8
            var adjustedDay = dayStr.toInt()

            if (adjustedHour < 0) {
                adjustedHour += 24
                adjustedDay -= 1

                // If day becomes 0 or negative, adjust month and year
                if (adjustedDay <= 0) {
                    val prevMonthNum = monthNum.toInt() - 1
                    if (prevMonthNum <= 0) {
                        // Went back to previous year
                        monthNum = "12"
                        yearStr = (yearStr.toInt() - 1).toString()
                    } else {
                        monthNum = prevMonthNum.toString().padStart(2, '0')
                    }
                    adjustedDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
                }
            }
            val stringDay: String = if (adjustedDay <= 9) {
                "0$adjustedDay"
            } else {
                adjustedDay.toString()
            }


            val dateStr = "$yearStr-$monthNum-$stringDay"
            val recordDate = LocalDate.parse(dateStr)

            Log.d("MyTag", "recordDate : $recordDate")


            val existingData = dailyData.getOrDefault(recordDate, Analytics(recordDate, 0, 0, 0))

            for (offer in record.offers) {

                existingData.totalRevenue += offer.offer_price
                if (offer.type.lowercase() == "service") {
                    existingData.totalServices++
                } else if (offer.type.lowercase() == "product") {
                    existingData.totalProducts++
                }
            }
            Log.d("MyTag", "revenueIncrease: ${existingData.totalRevenue}")

            Log.d("MyTag", "existingData : $existingData")

        }
        Log.d("MyTag", "DailyData : ${dailyData.values}")
        return dailyData.values.toList()
    }

    fun weeklyAnalytics(records: List<GetRecord>, startEndDate: StartEndDate): List<Analytics> {
        val weeklyData = mutableMapOf<String, Analytics>() // Key will be the week identifier
        val weekFields = WeekFields.of(Locale.getDefault()) // For determining the week of the year

        val startDate = LocalDate.parse(startEndDate.start_date) // From your previous code
        val endDate = LocalDate.parse(startEndDate.end_date)
        Log.d("MyTag", "startEndDate : $startEndDate")

        var dateBeingProcessed = startDate
        while (dateBeingProcessed <= endDate) {
            val year = dateBeingProcessed.year
            val weekOfYear = dateBeingProcessed.get(weekFields.weekOfWeekBasedYear())
            val weekKey = "$year-W$weekOfYear"
            weeklyData[weekKey] =
                Analytics(dateBeingProcessed, 0, 0, 0) // Initialize with a placeholder
            dateBeingProcessed = dateBeingProcessed.plusDays(1)
        }
        Log.d("MyTag", "dailyData : ${weeklyData.values}")


        for (record in records) {

            val timestamp = record.time.toString()
            val parts = timestamp.split(" ")

            val monthStr = parts[1]
            val dayStr = parts[2]
            var yearStr = parts[5]
            val timeStr = parts[3]

            val timeParts = timeStr.split(":")
            val hour = timeParts[0].toInt()

            var monthNum = when (monthStr) {
                "Jan" -> "01"
                "Feb" -> "02"
                "Mar" -> "03"
                "Apr" -> "04"
                "May" -> "05"
                "Jun" -> "06"
                "Jul" -> "07"
                "Aug" -> "08"
                "Sep" -> "09"
                "Oct" -> "10"
                "Nov" -> "11"
                "Dec" -> "12"
                else -> "01" // Handle invalid month names
            }

            // Adjust the hour
            var adjustedHour = hour - 8
            var adjustedDay = dayStr.toInt()

            if (adjustedHour < 0) {
                adjustedHour += 24
                adjustedDay -= 1

                // If day becomes 0 or negative, adjust month and year
                if (adjustedDay <= 0) {
                    val prevMonthNum = monthNum.toInt() - 1
                    if (prevMonthNum <= 0) {
                        // Went back to previous year
                        monthNum = "12"
                        yearStr = (yearStr.toInt() - 1).toString()
                    } else {
                        monthNum = prevMonthNum.toString().padStart(2, '0')
                    }
                    adjustedDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
                }
            }
            val stringDay: String = if (adjustedDay <= 9) {
                "0$adjustedDay"
            } else {
                adjustedDay.toString()
            }


            val dateStr = "$yearStr-$monthNum-$stringDay"
            val recordDate = LocalDate.parse(dateStr)
            val year = dateBeingProcessed.year
            val weekOfYear = dateBeingProcessed.get(weekFields.weekOfWeekBasedYear())
            val weekKey = "$year-W$weekOfYear"
            Log.d("MyTag", "recordDate : $recordDate")


            val existingData = weeklyData.getOrDefault(weekKey, Analytics(recordDate, 0, 0, 0))

            for (offer in record.offers) {

                existingData.totalRevenue += offer.offer_price
                if (offer.type.lowercase() == "service") {
                    existingData.totalServices++
                } else if (offer.type.lowercase() == "product") {
                    existingData.totalProducts++
                }
            }
            Log.d("MyTag", "revenueIncrease: ${existingData.totalRevenue}")

            Log.d("MyTag", "existingData : $existingData")

        }
        Log.d("MyTag", "DailyData : ${weeklyData.values}")
        return weeklyData.values.toList()
    }

    fun monthlyAnalytics(records: List<GetRecord>, startEndDate: StartEndDate): List<Analytics> {
        val monthlyData = mutableMapOf<String, Analytics>() // Key will be the week identifier
        val monthFields = WeekFields.of(Locale.getDefault()) // For determining the week of the year

        val startDate = LocalDate.parse(startEndDate.start_date) // From your previous code
        val endDate = LocalDate.parse(startEndDate.end_date)
        Log.d("MyTag", "startEndDate : $startEndDate")

        var dateBeingProcessed = startDate
        while (dateBeingProcessed <= endDate) {
            val year = dateBeingProcessed.year
            val month = dateBeingProcessed.monthValue
            val monthKey = "$year-$month"
            monthlyData[monthKey] =
                Analytics(dateBeingProcessed, 0, 0, 0) // Initialize with a placeholder
            dateBeingProcessed = dateBeingProcessed.plusMonths(1)
        }
        Log.d("MyTag", "dailyData : ${monthlyData.values}")


        for (record in records) {

            val timestamp = record.time.toString()
            val parts = timestamp.split(" ")

            val monthStr = parts[1]
            val dayStr = parts[2]
            var yearStr = parts[5]
            val timeStr = parts[3]

            val timeParts = timeStr.split(":")
            val hour = timeParts[0].toInt()

            var monthNum = when (monthStr) {
                "Jan" -> "01"
                "Feb" -> "02"
                "Mar" -> "03"
                "Apr" -> "04"
                "May" -> "05"
                "Jun" -> "06"
                "Jul" -> "07"
                "Aug" -> "08"
                "Sep" -> "09"
                "Oct" -> "10"
                "Nov" -> "11"
                "Dec" -> "12"
                else -> "01" // Handle invalid month names
            }

            // Adjust the hour
            var adjustedHour = hour - 8
            var adjustedDay = dayStr.toInt()

            if (adjustedHour < 0) {
                adjustedHour += 24
                adjustedDay -= 1

                // If day becomes 0 or negative, adjust month and year
                if (adjustedDay <= 0) {
                    val prevMonthNum = monthNum.toInt() - 1
                    if (prevMonthNum <= 0) {
                        // Went back to previous year
                        monthNum = "12"
                        yearStr = (yearStr.toInt() - 1).toString()
                    } else {
                        monthNum = prevMonthNum.toString().padStart(2, '0')
                    }
                    adjustedDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
                }
            }
            val stringDay: String = if (adjustedDay <= 9) {
                "0$adjustedDay"
            } else {
                adjustedDay.toString()
            }


            val dateStr = "$yearStr-$monthNum-$stringDay"
            val recordDate = LocalDate.parse(dateStr)
            val year = recordDate.year
            val month = recordDate.monthValue
            val monthKey = "$year-$month"
            Log.d("MyTag", "recordDate : $recordDate")


            val existingData = monthlyData.getOrDefault(monthKey, Analytics(recordDate, 0, 0, 0))

            for (offer in record.offers) {

                existingData.totalRevenue += offer.offer_price
                if (offer.type.lowercase() == "service") {
                    existingData.totalServices++
                } else if (offer.type.lowercase() == "product") {
                    existingData.totalProducts++
                }
            }
            Log.d("MyTag", "revenueIncrease: ${existingData.totalRevenue}")

            Log.d("MyTag", "existingData : $existingData")

        }
        Log.d("MyTag", "DailyData : ${monthlyData.values}")
        return monthlyData.values.toList()
    }


}