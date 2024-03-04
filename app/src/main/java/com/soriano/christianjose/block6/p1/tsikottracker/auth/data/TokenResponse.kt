package com.soriano.christianjose.block6.p1.tsikottracker.auth.data

data class TokenResponse(
    val message : String? = null,
    val token : String? = null,
    val email : String? = null,
    val userId : Int? = null,
)
