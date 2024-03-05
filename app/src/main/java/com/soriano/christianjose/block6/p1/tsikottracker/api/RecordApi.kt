package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.GetRecord
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RecordApi {

    @GET("/api/records/user/{id}")
    fun getRecords(@Path("id") companyId:Int): Call<List<GetRecord>>

    @POST("/api/records/android")
    fun createRecord(@Body record: Record): Call<Record>
}