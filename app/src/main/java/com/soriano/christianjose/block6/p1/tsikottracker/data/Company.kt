package com.soriano.christianjose.block6.p1.tsikottracker.data

data class Company(
    val id: Int,
    val name: String,
    val invitation_code: String? = null,
    val owner_id: Int?
)
