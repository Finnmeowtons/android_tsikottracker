package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.Result
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.CompanyAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentShowCompaniesBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.CompaniesViewModel

class ShowCompaniesFragment : Fragment() {
    private var _binding: FragmentShowCompaniesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CompaniesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowCompaniesBinding.inflate(inflater, container, false)
        val view = binding.root

        val adapter = CompanyAdapter()
        binding.rvCompanies.adapter = adapter

        viewModel = ViewModelProvider(this)[CompaniesViewModel::class.java]
        viewModel.companies.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show a loading indicator (e.g., ProgressBar)
                }
                is Result.Success -> {
                    adapter.companies = result.data  // Update adapter data
                }
                is Result.Error -> {
                    // Show an error message (e.g. in a TextView or Snackbar)
                    Log.e("MyTag", "Error loading companies: ${result.message}")
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}