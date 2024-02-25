package com.soriano.christianjose.block6.p1.tsikottracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    // Holds the current app bar title
    private val _appbarTitle = MutableLiveData<String>()

    // Exposes the title for observation
    val appbarTitle: LiveData<String> get() = _appbarTitle

    // Method to update the title
    fun updateAppBarTitle(newTitle: String) {
        _appbarTitle.value = newTitle
    }
}