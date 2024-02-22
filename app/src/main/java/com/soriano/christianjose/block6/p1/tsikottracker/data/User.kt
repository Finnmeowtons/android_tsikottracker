package com.soriano.christianjose.block6.p1.tsikottracker.data

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String? = null // Might be null
)
