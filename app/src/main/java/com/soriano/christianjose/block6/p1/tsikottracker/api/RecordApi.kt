package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.DeleteResponse
import com.soriano.christianjose.block6.p1.tsikottracker.data.GetRecord
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.data.StartEndDate
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecordApi {

    @POST("/api/records/user/{id}")
    fun getRecords(@Path("id") companyId:Int, @Body startEndDate : StartEndDate): Call<List<GetRecord>>

    @POST("/api/records/android")
    fun createRecord(@Body record: Record): Call<Record>

    @PUT("/api/records/android/{id}")
    fun updateRecord(@Path("id") recordId : Int, @Body record: Record) : Call<Record>

    @DELETE("/api/records/{id}")
    fun deleteRecord(@Path("id") recordId : Int) : Call<DeleteResponse>
}