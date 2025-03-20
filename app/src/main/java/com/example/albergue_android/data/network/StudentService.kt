package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.StudentDocDocument
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StudentService {
    @GET("studentdoc/not-enrolled")
    suspend fun getNotEnrolledStudents(
        @Query("page") page: Int,
        @Query("name") name: String? = null
    ): Response<List<StudentDocDocument>>

    @GET("studentdoc/enrolled")
    suspend fun getEnrolledStudents(
        @Query("page") page: Int,
        @Query("name") name: String? = null
    ): Response<List<StudentDocDocument>>
}