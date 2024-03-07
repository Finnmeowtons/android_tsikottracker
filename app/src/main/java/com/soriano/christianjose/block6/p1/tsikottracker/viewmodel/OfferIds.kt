package com.soriano.christianjose.block6.p1.tsikottracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OfferIds: ViewModel() {
    private val _offerIdList = MutableLiveData<List<Int>>()
    val offerIdList: LiveData<List<Int>> = _offerIdList


    fun setOfferIdList(offerIds: List<Int>) {
        _offerIdList.value = offerIds
    }
}