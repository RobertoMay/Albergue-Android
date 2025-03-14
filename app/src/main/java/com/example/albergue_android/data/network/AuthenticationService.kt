package com.example.albergue_android.data.network


import com.example.albergue_android.domain.models.ILogin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import android.app.Service
import android.content.Intent
import android.os.IBinder

interface AuthenticationService {
    @POST("aspirante/login")
    fun login(@Body credentials: ILogin): Call<LoginResponse>
}