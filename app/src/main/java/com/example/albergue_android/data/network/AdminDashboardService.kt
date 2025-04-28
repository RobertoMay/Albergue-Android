package com.example.albergue_android.data.network

import com.example.albergue_android.data.network.DashboardDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdminService {
    @GET("admin/dashboard")
    suspend fun getDashboardData(): Response<DashboardDataResponse>
}