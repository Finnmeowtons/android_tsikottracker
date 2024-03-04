package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Customer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CustomerApi {
    @GET("api/customers/user/{id}")
    fun getCustomer(@Path("id") companyId:Int): Call<List<Customer>>
}