package com.soriano.christianjose.block6.p1.tsikottracker.data

import com.google.gson.annotations.Expose

data class Employee(
    val id : Int,
    var name : String,
    var contact_details : String?,
    var position : String,
    var company_id : Int?
)
