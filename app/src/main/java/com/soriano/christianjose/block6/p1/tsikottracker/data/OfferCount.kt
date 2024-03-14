package com.soriano.christianjose.block6.p1.tsikottracker.data

data class OfferCount(
    val name: String,
    val type: String,
    val companyId: Int,
    var count: Int = 0
)
