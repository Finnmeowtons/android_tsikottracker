package com.soriano.christianjose.block6.p1.tsikottracker.auth.api

import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.Password
import com.soriano.christianjose.block6.p1.tsikottracker.data.DeleteResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE

interface Profile {
    @DELETE
    fun deleteUser(@Body password : Password): Call<DeleteResponse>
}