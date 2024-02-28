package com.soriano.christianjose.block6.p1.tsikottracker.auth.api

import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.LaravelAuthenticationResponse
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.RegistrationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface RegistrationApi {
    @POST("api/register-retrofit")
    fun register(@Body registrationRequest: RegistrationRequest): Call<LaravelAuthenticationResponse>
}