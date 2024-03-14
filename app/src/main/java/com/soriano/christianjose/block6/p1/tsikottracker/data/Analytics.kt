package com.soriano.christianjose.block6.p1.tsikottracker.data

import java.time.LocalDate

data class Analytics(
    val date: LocalDate,
    var totalRevenue: Int,
    var totalServices: Int,
    var totalProducts: Int
)
