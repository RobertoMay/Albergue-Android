package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.DataStudent
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DataStudentService {
    @POST("datastudents/")
    fun createDataStudent(@Body dataStudent: DataStudent): Call<StudentResponse>

    @GET("datastudents/aspirante/{aspiranteId}")
    fun getByAspiranteId(@Path("aspiranteId") aspiranteId: String): Call<DataStudent>
}