package com.soriano.christianjose.block6.p1.tsikottracker.data

import com.google.gson.annotations.Expose

data class Offer(
    @Expose(serialize = false) val id: Int, // assuming 'id' is an integer
    @Expose var name: String,
    @Expose var price: Int,
    @Expose var type: String, // Will hold either "service" or "product"
    @Expose val company_id: Long


)
