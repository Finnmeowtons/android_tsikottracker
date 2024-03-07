package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OfferApi {
    @GET("api/offers/user/{id}")
    fun getOffer(@Path("id") companyId:Int): Call<List<Offer>>

    @GET("api/offers/{id}")
    fun getIdOffer(@Path("id") offerId : Int): Call<Offer>
    @Headers("Content-Type: application/json")
    @POST("api/offers")
    fun createOffer(@Body offer: Offer): Call<Offer>

    @POST("api/offers/android/delete/{id}")
    fun deleteOffer(@Path("id") id: Int, @Body offer:Offer): Call<Offer>
}