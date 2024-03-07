package com.soriano.christianjose.block6.p1.tsikottracker.data


data class Record(
    val id: Int,
    val customer_name: String?,
    val customer_car_plate_number: String,
    val offers: List<Offer>,
    val notes: String?,
    val employee_name: String?,
    val employee_position: String?,
    val company_id: Int
)
