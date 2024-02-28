package com.soriano.christianjose.block6.p1.tsikottracker.retrofitclient

import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecordRetrofit {
    private const val BASE_URL = "http://146.190.111.209/"

    val api: RecordApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecordApi::class.java)
    }
}