package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface OfferApi {
    @GET("api/offers/user/{id}")
    fun getOffer(@Path("id") companyId:Int): Call<List<Offer>>
    @Headers("Content-Type: application/json")
    @POST("api/offers")
    fun createOffer(@Body offer: Offer): Call<Offer>
}