package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import retrofit2.Call
import retrofit2.http.GET

interface RecordApi {

    @GET("/api/records")
    fun getCompanies(): Call<List<Record>>
}