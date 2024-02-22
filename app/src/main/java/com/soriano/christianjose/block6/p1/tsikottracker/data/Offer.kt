package com.soriano.christianjose.block6.p1.tsikottracker.data

data class Offer(
    val id: Int, // assuming 'id' is an integer
    var name: String,
    var price: Int,
    var type: String, // Will hold either "service" or "product"
    val companyId: Long
)
