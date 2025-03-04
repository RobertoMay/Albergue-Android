package com.example.albergue_android.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface StudentEnrollmentService {
    @GET("datastudents/aspirante/{aspiranteId}")
    fun getById(@Path("aspiranteId") aspiranteId: String): Call<StudentEnrollmentResponse>
}