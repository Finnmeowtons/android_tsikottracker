package com.soriano.christianjose.block6.p1.tsikottracker.auth.data

data class RegistrationRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)
