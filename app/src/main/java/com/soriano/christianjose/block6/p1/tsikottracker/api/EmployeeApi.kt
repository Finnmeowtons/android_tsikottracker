package com.soriano.christianjose.block6.p1.tsikottracker.api

import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface EmployeeApi {
    @GET("api/employees/user/{id}")
    fun getEmployee(@Path("id") companyId:Int): Call<List<Employee>>

    @Headers("Content-Type: application/json")
    @POST("api/employees")
    fun createEmployee(@Body employee: Employee): Call<Employee>


    @POST("api/employees/android/delete/{id}")
    fun deleteEmployee(@Path("id") id: Int, @Body offer:Employee): Call<Employee>
}