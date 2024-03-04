package com.soriano.christianjose.block6.p1.tsikottracker.data

import java.util.Date

data class Record(
    val id: Int,
    val customer_id: Int?,
    val service_product_id: Long,
    val price: Int?,
    val date: Date?,
    val company_id: Int,
    val notes: String?,
    val employee_id: Int?
)
