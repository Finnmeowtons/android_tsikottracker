package com.soriano.christianjose.block6.p1.tsikottracker.data

import com.google.gson.annotations.Expose

data class Offer(
    val id: Int,
    var name: String,
    var price: Int,
    var type: String,
    var company_id: Int?
)
