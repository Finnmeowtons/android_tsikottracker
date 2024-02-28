package com.soriano.christianjose.block6.p1.tsikottracker.auth.api

import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.LaravelAuthenticationResponse
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.LoginRequest
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.TokenResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("/api/login-retrofit")
    fun login(@Body loginRequest: LoginRequest): Call<TokenResponse>
}