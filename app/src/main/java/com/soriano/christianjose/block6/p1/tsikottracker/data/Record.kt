package com.soriano.christianjose.block6.p1.tsikottracker.data

import java.util.Date

data class Record(
    val id: Int,
    val customer_name: String?,
    val customer_car_plate_number: String,
    val offer: String,
    val price: Int,
    val type: String,
    val notes: String?,
    val employee_name: String,
    val employee_position: String,
    val company_id: Int
)
