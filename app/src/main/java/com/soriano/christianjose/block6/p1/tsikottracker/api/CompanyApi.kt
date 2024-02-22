package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CompanyApi {
    @GET("/api/companies")
    fun getCompanies(): Call<List<Company>>

    @POST("api/companies")
    fun createCompany(@Body company: Company): Call<Company>
}