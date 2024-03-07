package com.soriano.christianjose.block6.p1.tsikottracker.data

data class GetRecordOffer(
    val offer_id: Int,
    var offer: String,
    var offer_price: Int,
    var type: String,
    val company_id: Int
)
