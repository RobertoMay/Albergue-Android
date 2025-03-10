package com.example.albergue_android.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://albergue-57e14.uc.r.appspot.com/api/"

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

    // Propiedad para acceder a
    val dataStudentService: DataStudentService by lazy {
        retrofit.create(DataStudentService::class.java)
    }

    val studentEnrollmentService: StudentEnrollmentService by lazy {
        retrofit.create(StudentEnrollmentService::class.java)
    }

    val studentDocService: StudentDocService by lazy {
        retrofit.create(StudentDocService::class.java)
    }

    val callService: CallService by lazy {
        retrofit.create(CallService::class.java)
    }

    val studentService: StudentService by lazy {
        retrofit.create(StudentService::class.java)
    }
}