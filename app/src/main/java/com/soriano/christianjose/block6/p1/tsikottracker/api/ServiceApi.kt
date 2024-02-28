package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ServiceApi {
    @Headers("Content-Type: application/json")
    @POST("api/offers")
    fun createOffer(@Body offer: Offer): Call<Offer>
}