package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.Call
import com.example.albergue_android.domain.models.CallResponse
import com.example.albergue_android.domain.models.CallResponse2
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CallService {
    @POST("calls/create")
    suspend fun createCall(@Body call: Call): Response<CallResponse>

    @GET("calls/all")
    suspend fun getAllCalls(): Response<CallResponse2>

    @DELETE("calls/{id}")
    suspend fun deleteCall(@Path("id") id: String): Response<Void>

    @GET("calls/status")
    suspend fun getActiveCall(): Response<CallResponse>

    @PUT("calls/{id}")
    suspend fun updateCall(@Path("id") id: String, @Body call: Call): Response<CallResponse>
}