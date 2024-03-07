package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.DeleteResponse
import com.soriano.christianjose.block6.p1.tsikottracker.data.OwnerId
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanyApi {
    @GET("/api/companies/user/{id}")
    fun getCompanies(@Path("id") ownerId:Int): Call<List<Company>>

    @POST("api/companies")
    fun createCompany(@Body company: Company): Call<Company>

    @PUT("api/companies/{id}")
    fun updateCompanyName(@Path("id") ownerId:Int, @Body company: Company): Call<Company>

    @DELETE("api/companies/user/{id}")
    fun deleteCompany(@Path("id") id:Int, @Query("owner_id") ownerId: Int): Call<DeleteResponse>
}