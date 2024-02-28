package com.soriano.christianjose.block6.p1.tsikottracker.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.retrofitclient.RecordRetrofit
import com.soriano.christianjose.block6.p1.tsikottracker.retrofitclient.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.await

class RecordViewModel : ViewModel() {
    private val _records = MutableLiveData<Result<List<Record>>>()
    val records: LiveData<Result<List<Record>>> = _records

    init {
        fetchRecords()
    }

    private fun fetchRecords() {
        viewModelScope.launch {
            _records.value = Result.Loading // Indicate loading state
            try {
                val companies = RecordRetrofit.api.getCompanies().await()
                _records.value = Result.Success(companies)
                Log.e("mytag","$companies")
            } catch (e: Exception) {
                _records.value = Result.Error("Network Error: ${e.message}")
                Log.e("mytag","${e.message}")
            }
        }
    }
}