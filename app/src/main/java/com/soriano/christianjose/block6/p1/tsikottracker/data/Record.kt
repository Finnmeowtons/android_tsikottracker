package com.soriano.christianjose.block6.p1.tsikottracker.data

import java.util.Date

data class Record(
    val id: Int, // assuming 'id' is an integer
    val customerId: Long?,
    val serviceProductId: Long,
    val price: Double?,
    val date: Date?,
    val companyId: Long,
    val notes: String?,
    val createdAt: Date, // timestamps() likely includes creation
    val updatedAt: Date, // and update information
    val employeeId: Long?
)
