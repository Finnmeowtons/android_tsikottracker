package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.OfferAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentOfferBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class OfferFragment : Fragment() {
    private var _binding: FragmentOfferBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyApi: CompanyApi
    private lateinit var offerApi: OfferApi
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var companies: List<Company>
    private lateinit var companyNames: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var getOffers : List<Offer>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfferBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Offers")
        val authUserManager = AuthUserManager(requireContext())
        var companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()
        companyNames = mutableListOf<String>()
        companies = emptyList()
        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.search_menu)


        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, companyNames)
        binding.etCompanySelect.setAdapter(adapter)

        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        offerApi = retrofit.create(OfferApi::class.java)
        val recyclerViewAdapter = OfferAdapter(offerApi, parentFragmentManager, companyId, findNavController())
        binding.recyclerView.adapter = recyclerViewAdapter


        if (isAdded) {
            offerApi.getOffer(companyId).enqueue(object : Callback<List<Offer>> {
                override fun onResponse(call: Call<List<Offer>>, response: Response<List<Offer>>) {
                    if (response.isSuccessful) {
                        val offers = response.body()
                        Log.d("MyTag", "$offers || ${response.body()}")
                        if (offers != null) {
                            recyclerViewAdapter.offers = offers
                            getOffers = offers
                            if (offers.isEmpty()){
                                binding.llNoResult.visibility = View.VISIBLE
                            } else {
                                binding.llNoResult.visibility = View.GONE
                            }
                            binding.apply {
                                var click = 0
                                tvOffer.setOnClickListener {
                                    imgSortOffer.visibility = View.VISIBLE
                                    imgSortPrice.visibility = View.INVISIBLE
                                    imgSortType.visibility = View.INVISIBLE

                                    if (click == 0) {
                                        imgSortOffer.setImageResource(R.drawable.arrow_downward)
                                        recyclerViewAdapter.offers = offers.sortedByDescending { it.name }
                                        click += 1
                                    } else {
                                        imgSortOffer.setImageResource(R.drawable.arrow_upward)
                                        recyclerViewAdapter.offers = offers.sortedBy { it.name }
                                        click = 0
                                    }
                                }

                                tvPrice.setOnClickListener {
                                    imgSortOffer.visibility = View.INVISIBLE
                                    imgSortPrice.visibility = View.VISIBLE
                                    imgSortType.visibility = View.INVISIBLE

                                    if (click == 0) {
                                        imgSortPrice.setImageResource(R.drawable.arrow_downward)
                                        recyclerViewAdapter.offers = offers.sortedByDescending { it.price }
                                        click += 1
                                    } else {
                                        imgSortPrice.setImageResource(R.drawable.arrow_upward)
                                        recyclerViewAdapter.offers = offers.sortedBy { it.price }
                                        click = 0
                                    }
                                }

                                tvType.setOnClickListener {
                                    imgSortOffer.visibility = View.INVISIBLE
                                    imgSortPrice.visibility = View.INVISIBLE
                                    imgSortType.visibility = View.VISIBLE

                                    if (click == 0) {
                                        imgSortType.setImageResource(R.drawable.arrow_downward)
                                        recyclerViewAdapter.offers = offers.sortedByDescending { it.type }
                                        click += 1
                                    } else {
                                        imgSortType.setImageResource(R.drawable.arrow_upward)
                                        recyclerViewAdapter.offers = offers.sortedBy { it.type }
                                        click = 0
                                    }
                                }
                            }
                        }
                    } else {
                        // Handle API error
                    }
                }

                override fun onFailure(call: Call<List<Offer>>, t: Throwable) {
                    Log.d("MyTag", "$call", t)
                }
            })

            toolbar?.setOnMenuItemClickListener {menuItem ->
                when (menuItem.itemId) {
                    R.id.search ->{
                        val searchView = menuItem.actionView as SearchView
                        searchView.isIconified = false // Expand the search view
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean = true

                            override fun onQueryTextChange(query: String?): Boolean {
                                if (query != null) {
                                    val searchQuery = query.lowercase()
                                    val filteredOffers = getOffers.filter { offer ->
                                        offer.name.lowercase().contains(searchQuery) || offer.price.toString().contains(searchQuery)
                                    }
                                    recyclerViewAdapter.offers = filteredOffers
                                    if (filteredOffers.isEmpty()){
                                        binding.llNoResult.visibility = View.VISIBLE
                                    } else {
                                        binding.llNoResult.visibility = View.GONE
                                    }
                                } else {
                                    recyclerViewAdapter.offers = getOffers
                                    if (getOffers.isEmpty()){
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
        }

        if (isAdded) {
            companyApi = retrofit.create(CompanyApi::class.java)
            companyApi.getCompanies(storedUserId).enqueue(object : Callback<List<Company>> {
                override fun onResponse(
                    call: Call<List<Company>>,
                    response: Response<List<Company>>
                ) {
                    if (response.isSuccessful) {
                        companies = response.body() ?: emptyList() // Store the companies
                        companyNames.clear()
                        companies.forEach { company -> companyNames.add(company.name) }
                        adapter.notifyDataSetChanged()

                        val companyToSelect = companies.find { it.id == companyId }
                        if (companyToSelect != null) {
                            binding.etCompanySelect.setText(
                                companyToSelect.name,
                                false
                            )
                        }
                    } else {
                        // Handle API error
                    }
                }

                override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                    Log.e("MyTag", "$call", t)
                }
            })
        }
        binding.etCompanySelect.onItemClickListener  = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Log.d("MyTag", "Before getStoredCompanyId $companyId")
            val selectedCompanyName = parent.getItemAtPosition(position) as String
            val selectedCompany = companies.find { it.name == selectedCompanyName }
            if (selectedCompany != null) {
                authUserManager.storeCompanyId(selectedCompany.id)
                companyId = authUserManager.getStoredCompanyId()
                Log.d("MyTag", "companyId $companyId")
                Log.d("MyTag", "selectedCompany id ${selectedCompany.id}")
                offerApi.getOffer(selectedCompany.id).enqueue(object : Callback<List<Offer>> {
                    override fun onResponse(
                        call: Call<List<Offer>>,
                        response: Response<List<Offer>>
                    ) {
                        if (response.isSuccessful) {
                            val searchView = toolbar?.menu?.findItem(R.id.search)?.actionView as SearchView
                            searchView.setQuery("", false) // Clear the text
                            searchView.isIconified = true
                            toolbar.menu?.findItem(R.id.search)?.collapseActionView()

                            Log.d("MyTag", "success")
                            val offers = response.body()
                            Log.d("MyTag", "$offers || ${response.body()}")
                            if (offers != null) {
                                Log.d("MyTag", "successss")
                                recyclerViewAdapter.offers = offers
                                if (offers.isEmpty()){
                                    binding.llNoResult.visibility = View.VISIBLE
                                } else {
                                    binding.llNoResult.visibility = View.GONE
                                }
                            }
                        } else {
                            // Handle API error
                        }
                    }
                    override fun onFailure(call: Call<List<Offer>>, t: Throwable) {
                        Log.d("MyTag", "$call", t)
                    }
                })
            } else {
                Log.d("MyTag", "oh no",)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            if(isAdded) {
                val directions =
                    OfferFragmentDirections.actionOfferFragmentToAddServiceFragment(true)
                findNavController().navigate(directions)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if(isAdded) {
                findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
            }
        }

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                // Scrolling Down
                binding.floatingActionButton.hide()
            } else if (scrollY < oldScrollY ) {
                // Scrolling Up
                binding.floatingActionButton.show()
            }
        })

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}