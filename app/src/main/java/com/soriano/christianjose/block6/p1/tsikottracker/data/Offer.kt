package com.soriano.christianjose.block6.p1.tsikottracker.data

import com.google.gson.annotations.Expose

data class Offer(
    @Expose(serialize = false) val id: Int,
    @Expose var name: String,
    @Expose var price: Int,
    @Expose var type: String,
    @Expose val company_id: Int


)
