package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.IRegistration
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationService {
    @POST("aspirante/crearAspirante")
    fun addStudent(@Body student: IRegistration): Call<ApiResponse>
}