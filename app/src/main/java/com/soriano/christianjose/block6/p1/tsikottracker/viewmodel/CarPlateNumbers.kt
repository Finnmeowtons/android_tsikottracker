package com.soriano.christianjose.block6.p1.tsikottracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CarPlateNumbers : ViewModel() {
    private val _carPlateNumberList = MutableLiveData<List<String>>()
    val carPlateNumberList: LiveData<List<String>> = _carPlateNumberList


    fun setPlateNumberList(offerIds: List<String>) {
        _carPlateNumberList.value = offerIds
    }
}