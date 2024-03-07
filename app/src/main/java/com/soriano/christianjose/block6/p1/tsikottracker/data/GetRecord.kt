package com.soriano.christianjose.block6.p1.tsikottracker.data

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

data class GetRecord(
    val id: Int,
    val customer_name: String?,
    val customer_car_plate_number: String,
    val offers: List<GetRecordOffer>,
    val time: Date?,
    val company_id: Int,
    val notes: String?,
    val employee_id: Int?,
    val employee_name: String?,
    val employee_position: String?
)

