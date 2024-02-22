package com.soriano.christianjose.block6.p1.tsikottracker.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.retrofitclient.RetrofitClient
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.Result
import kotlinx.coroutines.launch
import retrofit2.await


class CompaniesViewModel : ViewModel() {
    private val _companies = MutableLiveData<Result<List<Company>>>()
    val companies: LiveData<Result<List<Company>>> = _companies

    init {
        fetchCompanies()
    }

    private fun fetchCompanies() {
        viewModelScope.launch {
            _companies.value = Result.Loading // Indicate loading state
            try {
                val companies = RetrofitClient.api.getCompanies().await()
                _companies.value = Result.Success(companies)
                Log.e("mytag","$companies")
            } catch (e: Exception) {
                _companies.value = Result.Error("Network Error: ${e.message}")
                Log.e("mytag","${e.message}")
            }
        }
    }
}


