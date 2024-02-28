package com.soriano.christianjose.block6.p1.tsikottracker.auth.api

import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.ForgotPasswordRequest
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.LoginRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ForgotPasswordApi {
    @POST("/forgot-password")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ResponseBody>
}