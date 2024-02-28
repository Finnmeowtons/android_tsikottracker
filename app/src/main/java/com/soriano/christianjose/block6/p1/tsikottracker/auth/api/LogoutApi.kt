package com.soriano.christianjose.block6.p1.tsikottracker.auth.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST

interface LogoutApi {
    @POST("/logout")
    fun logout(): Call<ResponseBody>
}